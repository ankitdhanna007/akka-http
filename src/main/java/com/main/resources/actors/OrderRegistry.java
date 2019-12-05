package com.main.resources.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.main.exceptions.EntityNotFoundException;
import com.main.jooq.model.tables.records.OrdersRecord;
import com.main.DBConfiguration;
import com.main.resources.entities.OrderEntity;

import java.util.Optional;

import static com.main.jooq.model.tables.Orders.ORDERS;
import static com.main.resources.entities.OrderEntity.States.CREATED;
import static com.main.resources.entities.OrderEntity.States.PAID;
import static com.main.resources.entities.OrderEntity.mapState;

public class OrderRegistry extends AbstractBehavior<OrderRegistry.Command> {

    public interface Command {
    }

    public final static class CreateOrder implements Command {
        public final Order order;
        public final ActorRef<ActionPerformed> replyTo;

        public CreateOrder(Order order, ActorRef<ActionPerformed> replyTo) {
            this.order = order;
            this.replyTo = replyTo;
        }
    }

    public final static class ConfirmPayment implements Command {
        public final Order order;
        public final int orderId;
        public final ActorRef<ActionPerformed> replyTo;

        public ConfirmPayment(String orderId, Order order, ActorRef<ActionPerformed> replyTo) {
            this.order = order;
            this.orderId = Integer.parseInt(orderId); //todo - add exception
            this.replyTo = replyTo;
        }
    }

    public final static class ActionPerformed implements Command {
        public final String description;

        public ActionPerformed(String description) {
            this.description = description;
        }
    }

    public final static class Order {
        public final int userId;
        public final int productId;

        @JsonCreator
        public Order(@JsonProperty("user_id") int userId, @JsonProperty("product_id") int productId) {
            this.userId = userId;
            this.productId = productId;
        }
    }

    private OrderRegistry(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(OrderRegistry::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(CreateOrder.class, this::onCreateOrder)
                .onMessage(ConfirmPayment.class, this::onConfirmPayment)
                .build();
    }

    private Behavior<Command> onCreateOrder(final CreateOrder command) throws Exception {
        final Order order = command.order;
        final OrderEntity orderEntity = new OrderEntity(order.userId, order.productId, mapState(CREATED));
        orderEntity.store();
        command.replyTo.tell(new ActionPerformed(String.format("Order %s created.", orderEntity.getId())));
        return this;
    }

    private Behavior<Command> onConfirmPayment(final ConfirmPayment command) throws Exception {
        final Optional<OrdersRecord> ordersRecordOptional = DBConfiguration.initiateDB().fetchOptional(ORDERS, ORDERS.ID.eq(command.orderId));
        if (ordersRecordOptional.isPresent()) {
            final OrdersRecord ordersRecord = ordersRecordOptional.get();
            if (ordersRecord.getIsPaid() == null) {
                ordersRecord.setState(mapState(PAID));
                ordersRecord.setIsPaid("1");
                ordersRecord.store();
            }

            // initiate delivery manager
            final ActorSystem<OrderDeliveryManager.OrderDeliveryManagement> system =
                    ActorSystem.create(OrderDeliveryManager.create(), "OrderDeliveryManager");
            system.tell(new OrderDeliveryManager.Start(command.orderId));

            command.replyTo.tell(new ActionPerformed(String.format("Order %s paid.", command.orderId)));
        } else {
            command.replyTo.tell(new ActionPerformed(String.format("Order with id %s not found.", command.orderId)));
            // todo - investgate how to handle these in route, ideally raise a 404
//      throw new EntityNotFoundException(String.format("Order %s not found.", command.order.userId));
        }
        return this;
    }
}
