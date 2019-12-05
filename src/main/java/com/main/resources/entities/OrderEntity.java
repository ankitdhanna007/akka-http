package com.main.resources.entities;

import com.main.DBConfiguration;
import com.main.jooq.model.tables.records.OrdersRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OrderEntity {

    private final OrdersRecord order;

    public enum States {
        CREATED,
        PAID,
        IN_FULFILLMENT,
        CLOSED
    }

    public static String mapState(final States state) {
        switch (state){
            case CREATED:
                return "I";
            case PAID:
                return "P";
            case IN_FULFILLMENT:
                return "F";
            case CLOSED:
                return "C";
            default:
                new IllegalStateException("Illegal state encountered");
        }
        return null;
    }


    OrderEntity(final OrdersRecord ordersRecord) {
        this.order = ordersRecord;
    }

    public int getId() {
        return this.order.getId();
    }

    public OrderEntity(final int userId, final int productId, final String state) {
        this(
                new OrdersRecord(
                        null,
                        userId,
                        productId,
                        new Timestamp(System.currentTimeMillis()),
                        state,
                        null,
                        null));
    }

    public OrderEntity store() throws Exception {
            DBConfiguration.initiateDB().attach(order);
            order.store();
            return this;
    }
}
