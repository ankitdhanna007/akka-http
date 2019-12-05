/**
 * This class is generated by jOOQ
 */
package com.main.jooq.model;


import com.main.jooq.model.tables.FlywaySchemaHistory;
import com.main.jooq.model.tables.Orders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Store extends SchemaImpl {

    private static final long serialVersionUID = 1122247490;

    /**
     * The reference instance of <code>store</code>
     */
    public static final Store STORE = new Store();

    /**
     * The table <code>store.flyway_schema_history</code>.
     */
    public final FlywaySchemaHistory FLYWAY_SCHEMA_HISTORY = com.main.jooq.model.tables.FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY;

    /**
     * The table <code>store.orders</code>.
     */
    public final Orders ORDERS = com.main.jooq.model.tables.Orders.ORDERS;

    /**
     * No further instances allowed
     */
    private Store() {
        super("store", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY,
            Orders.ORDERS);
    }
}
