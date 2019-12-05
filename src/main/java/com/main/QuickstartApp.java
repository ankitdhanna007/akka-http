package com.main;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.ActorSystem;
import com.main.resources.actors.OrderRegistry;
import com.main.resources.OrderRoutes;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

public class QuickstartApp {
    static void startHttpServer(Route route, ActorSystem<?> system) {
        akka.actor.ActorSystem classicSystem = Adapter.toClassic(system);
        final Http http = Http.get(classicSystem);
        final Materializer materializer = Materializer.matFromSystem(system);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.flow(classicSystem, materializer);
        CompletionStage<ServerBinding> futureBinding =
            http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8080), materializer);

        futureBinding.whenComplete((binding, exception) -> {
            if (binding != null) {
                InetSocketAddress address = binding.localAddress();
                system.log().info("Server online at http://{}:{}/",
                    address.getHostString(),
                    address.getPort());
            } else {
                system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
                system.terminate();
            }
        });
    }

    public static void main(String[] args) {

        Behavior<NotUsed> rootBehavior = Behaviors.setup(context -> {
            ActorRef<OrderRegistry.Command> userRegistryActor =
                context.spawn(OrderRegistry.create(), "OrderRegistry");

            OrderRoutes orderRoutes = new OrderRoutes(context.getSystem(), userRegistryActor);
            startHttpServer(orderRoutes.orderRoutes(), context.getSystem());

            return Behaviors.empty();
        });

        // boot up server using the route as defined below
        ActorSystem.create(rootBehavior, "HttpServer");
    }

}


