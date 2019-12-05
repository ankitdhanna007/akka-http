package com.main.resources.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.main.DBConfiguration;
import com.main.exceptions.EntityNotFoundException;
import com.main.jooq.model.tables.records.OrdersRecord;

import java.util.Optional;
import java.util.Random;

import static com.main.jooq.model.tables.Orders.ORDERS;
import static com.main.resources.entities.OrderEntity.States.IN_FULFILLMENT;
import static com.main.resources.entities.OrderEntity.mapState;

public class FulfillmentProvider extends AbstractBehavior<FulfillmentProvider.DeliverOrder> {

    public static final class DeliverOrder {
        public final int orderId;
        public final ActorRef<OrderDeliveryManager.Fulfilled> replyTo;

        public DeliverOrder(int orderId, ActorRef<OrderDeliveryManager.Fulfilled> replyTo) {
            this.orderId = orderId;
            this.replyTo = replyTo;
        }
    }

    public static Behavior<DeliverOrder> create() {
        return Behaviors.setup(FulfillmentProvider::new);
    }

    private FulfillmentProvider(ActorContext<DeliverOrder> context) {
        super(context);
    }

    @Override
    public Receive<DeliverOrder> createReceive() {
        return newReceiveBuilder().onMessage(DeliverOrder.class, this::startDelivery).build();
    }

    private Behavior<DeliverOrder> startDelivery(DeliverOrder command) throws Exception {
        getContext().getLog().info("Order getting ready to deliver");

        final Optional<OrdersRecord> ordersRecordOptional = DBConfiguration.initiateDB().fetchOptional(ORDERS, ORDERS.ID.eq(command.orderId));
        if (ordersRecordOptional.isPresent()) {

            final OrdersRecord ordersRecord = ordersRecordOptional.get();
            boolean fulfillmentStatus;
            //preserving idempotency
            if(ordersRecord.getIsFulfilled() == null) {
                fulfillmentStatus = new Random().nextBoolean();
                ordersRecord.setState(mapState(IN_FULFILLMENT));
                ordersRecord.store();
            } else {
                fulfillmentStatus = ordersRecord.getIsFulfilled() == "1" ? true : false;
            }

            final ActorRef<OrderDeliveryManager.OrderDeliveryManagement> fulfilled = getContext().spawn(OrderDeliveryManager.create(), "Fulfilled");
            fulfilled.tell(new OrderDeliveryManager.Fulfilled(command.orderId, fulfillmentStatus));

        } else {
            throw new EntityNotFoundException(String.format("Order %s not found.", command.orderId));
        }
        return this;
    }


}