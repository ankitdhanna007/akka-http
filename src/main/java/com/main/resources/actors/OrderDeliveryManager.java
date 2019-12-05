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

import static com.main.jooq.model.tables.Orders.ORDERS;
import static com.main.resources.entities.OrderEntity.States.CLOSED;
import static com.main.resources.entities.OrderEntity.mapState;
import static java.lang.Thread.sleep;

public class OrderDeliveryManager extends AbstractBehavior<OrderDeliveryManager.OrderDeliveryManagement> {

    public interface OrderDeliveryManagement{};

    public static class Start implements OrderDeliveryManagement {

        public final int orderId;

        public Start(int orderId) {
            this.orderId = orderId;
        }
    }

    public static final class Fulfilled implements OrderDeliveryManagement {
        public final int orderId;
        public final boolean isFulfilled;

        public Fulfilled(final int orderId, final boolean isFulfilled) {
            this.orderId = orderId;
            this.isFulfilled = isFulfilled;
        }
    }

    public static Behavior<OrderDeliveryManager.OrderDeliveryManagement> create() {
        return Behaviors.setup(OrderDeliveryManager::new);
    }

    private final ActorRef<FulfillmentProvider.DeliverOrder> fulfillmentProvider;

    private OrderDeliveryManager(ActorContext<OrderDeliveryManagement> context) {
        super(context);
        fulfillmentProvider = context.spawn(FulfillmentProvider.create(), "fulfillmentProvider");
    }

    @Override
    public Receive<OrderDeliveryManagement> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .onMessage(Fulfilled.class, this::onOrderFulfilled)
                .build();
    }

    private Behavior<OrderDeliveryManagement> onStart(final Start command) throws InterruptedException {
        sleep(2000); // to simulate the gap before the delivery
        fulfillmentProvider.tell(new FulfillmentProvider.DeliverOrder(command.orderId, null));
        return this;
    }

    private Behavior<OrderDeliveryManagement> onOrderFulfilled(final Fulfilled command) throws Exception {


        final Optional<OrdersRecord> ordersRecordOptional = DBConfiguration.initiateDB().fetchOptional(ORDERS, ORDERS.ID.eq(command.orderId));
        if (ordersRecordOptional.isPresent()) {
            final OrdersRecord ordersRecord = ordersRecordOptional.get();

            //preserving idempotency
            if(ordersRecord.getIsFulfilled() != null) {
                getContext().getLog().info(String.format("Order %s already fulfilled with status: %s.", command.orderId, ordersRecord.getIsFulfilled()));
                return this;
            }

            ordersRecord.setState(mapState(CLOSED));
            ordersRecord.setIsFulfilled(command.isFulfilled ? "1" : "0");
            ordersRecord.store();

            getContext().getLog().info(String.format("Order %s fulfillment status: %s.", command.orderId, command.isFulfilled));

        } else {
            throw new EntityNotFoundException(String.format("Order %s not found.", command.orderId));
        }
        return this;
    }

    }