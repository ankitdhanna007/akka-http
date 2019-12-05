/**
 * This class is generated by jOOQ
 */
package com.main.jooq.model.tables;


import com.main.jooq.model.Keys;
import com.main.jooq.model.Store;
import com.main.jooq.model.tables.records.OrdersRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class Orders extends TableImpl<OrdersRecord> {

    private static final long serialVersionUID = -1718349900;

    /**
     * The reference instance of <code>store.orders</code>
     */
    public static final Orders ORDERS = new Orders();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrdersRecord> getRecordType() {
        return OrdersRecord.class;
    }

    /**
     * The column <code>store.orders.id</code>.
     */
    public final TableField<OrdersRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>store.orders.user_id</code>.
     */
    public final TableField<OrdersRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>store.orders.product_id</code>.
     */
    public final TableField<OrdersRecord, Integer> PRODUCT_ID = createField("product_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>store.orders.date</code>.
     */
    public final TableField<OrdersRecord, Timestamp> DATE = createField("date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>store.orders.state</code>.
     */
    public final TableField<OrdersRecord, String> STATE = createField("state", org.jooq.impl.SQLDataType.CHAR.length(2).nullable(false), this, "");

    /**
     * The column <code>store.orders.is_fulfilled</code>.
     */
    public final TableField<OrdersRecord, String> IS_FULFILLED = createField("is_fulfilled", org.jooq.impl.SQLDataType.CHAR.length(1), this, "");

    /**
     * The column <code>store.orders.is_paid</code>.
     */
    public final TableField<OrdersRecord, String> IS_PAID = createField("is_paid", org.jooq.impl.SQLDataType.CHAR.length(1), this, "");

    /**
     * Create a <code>store.orders</code> table reference
     */
    public Orders() {
        this("orders", null);
    }

    /**
     * Create an aliased <code>store.orders</code> table reference
     */
    public Orders(String alias) {
        this(alias, ORDERS);
    }

    private Orders(String alias, Table<OrdersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Orders(String alias, Table<OrdersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Store.STORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<OrdersRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ORDERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<OrdersRecord> getPrimaryKey() {
        return Keys.KEY_ORDERS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<OrdersRecord>> getKeys() {
        return Arrays.<UniqueKey<OrdersRecord>>asList(Keys.KEY_ORDERS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Orders as(String alias) {
        return new Orders(alias, this);
    }

    /**
     * Rename this table
     */
    public Orders rename(String name) {
        return new Orders(name, null);
    }
}
