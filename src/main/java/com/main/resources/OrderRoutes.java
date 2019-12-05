package com.main.resources;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import com.main.exceptions.EntityNotFoundException;
import com.main.resources.actors.OrderRegistry;
import com.main.resources.actors.OrderRegistry.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class OrderRoutes {
    private final static Logger log = LoggerFactory.getLogger(OrderRoutes.class);
    private final ActorRef<OrderRegistry.Command> orderRegistryActor;
    private final Duration askTimeout;
    private final Scheduler scheduler;

    public OrderRoutes(ActorSystem<?> system, ActorRef<OrderRegistry.Command> orderRegistryActor) {
        this.orderRegistryActor = orderRegistryActor;
        scheduler = system.scheduler();
        askTimeout = system.settings().config().getDuration("my-app.routes.ask-timeout");
    }

    private CompletionStage<OrderRegistry.ActionPerformed> createOrder(Order order) {
        return AskPattern.ask(orderRegistryActor, ref -> new OrderRegistry.CreateOrder(order, ref), askTimeout, scheduler);
    }

    private CompletionStage<OrderRegistry.ActionPerformed> updateOrder(String orderId, Order order) {
        return AskPattern.ask(orderRegistryActor, ref -> new OrderRegistry.ConfirmPayment(orderId, order, ref), askTimeout, scheduler);
    }

    public Route orderRoutes() {

        final ExceptionHandler exceptionHandler = ExceptionHandler.newBuilder()
                .match(EntityNotFoundException.class, x ->
                        complete(StatusCodes.NOT_FOUND, "Order not found"))
                .build();

        return pathPrefix("orders", () ->
                concat(
                        pathEnd(() ->
                                concat(
                                        post(() ->
                                                entity(
                                                        Jackson.unmarshaller(Order.class),
                                                        order ->
                                                                onSuccess(createOrder(order), performed -> {
                                                                    log.info("Create result: {}", performed.description);
                                                                    return complete(StatusCodes.CREATED, performed, Jackson.marshaller());
                                                                })
                                                )
                                        )
                                )
                        ),
                        path(PathMatchers.segment(), (String orderId) ->
                                handleExceptions(exceptionHandler, () ->
                                        concat(
                                                put(() ->
                                                        entity(
                                                                Jackson.unmarshaller(Order.class),
                                                                order ->
                                                                        onSuccess(updateOrder(orderId, order), performed -> {
                                                                                    log.info("Order paid[", performed.description);
                                                                                    return complete(StatusCodes.OK, performed, Jackson.marshaller());
                                                                                }
                                                                        )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
