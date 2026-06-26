package com.remotecontrol.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.healthchecks.HealthCheckHandler;
import io.vertx.ext.web.openapi.router.OpenAPIRoute;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;

public class ApiVerticle extends VerticleBase {

        private final Logger logger = LoggerFactory.getLogger("api");
        private final Pool sharedPool;

        public ApiVerticle(Pool sharedPool) {
                this.sharedPool = sharedPool;
        }

        @Override
        public Future<?> start() {
                return getOpenApiContract("device-spec.yaml")
                        .compose(contract -> createRouter(contract))
                        .compose(router -> createHealthChecks(router))
                        .compose(router -> createReadinessCheck(router))
                        .compose(router -> createHttpServer(router))
                        .onSuccess(server -> {
                                logger.info("Successfully initialized ApiVerticle with event-bus, routers, health and readiness checks");
                        })
                        .onFailure(err -> {
                                logger.error("Failed to initialize ApiVerticle err={}", err.getMessage(), err);
                        });

        }

        private Future<OpenAPIContract> getOpenApiContract(String path) {
                Future<OpenAPIContract> contract = OpenAPIContract.from(vertx, path);
                return contract;
        }

        private Future<Router> createRouter(OpenAPIContract contract) {
                DevicesHandler devicesHandler = new DevicesHandler(vertx);
                RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract);
                routerBuilder.getRoute("addDevice").addHandler(devicesHandler::create);
                routerBuilder.getRoute("getAllDevices").addHandler(devicesHandler::getAll);
                routerBuilder.getRoute("updateDevice").addHandler(devicesHandler::update);
                routerBuilder.getRoute("patchDevice").addHandler(devicesHandler::patch);
                routerBuilder.getRoute("getDeviceById").addHandler(devicesHandler::get);
                routerBuilder.getRoute("deleteDevice").addHandler(devicesHandler::delete);

                List<OpenAPIRoute> routes = routerBuilder.getRoutes();
                for (OpenAPIRoute route : routes) {
                        route.addFailureHandler(devicesHandler::validationFailure);
                }

                Router router = routerBuilder.createRouter();
                return Future.succeededFuture(router);
        }

        private Future<Router> createHealthChecks(Router router) {
                logger.info("Creating health-check on api route ...");

                HealthChecks hc = HealthChecks.create(vertx);
                hc.register(
                        "api",
                        1000L,
                        promise -> promise.complete(Status.OK())
                );

                // logger.info("Creating health-check on database server ...");

                // hc.register(
                //         "database",
                //         2000L,
                //         promise -> sharedPool.getConnection()
                //                 .compose(SqlConnection::close)
                //                 .<Status>mapEmpty()
                //                 .onComplete(promise)
                // );

                logger.info("Creating health-check on event-bus address={} ...", "remotecontrol.devices");

                DeliveryOptions opts = new DeliveryOptions();
                opts.setLocalOnly(false);
                opts.setSendTimeout(1000L);
                opts.addHeader("health", "health");

                hc.register(
                        "event-bus/remotecontrol.devices",
                        1000L,
                        promise -> vertx.eventBus().request("remotecontrol.devices.health", "ping", opts)
                                .onSuccess(msg -> {
                                        promise.complete(Status.OK());
                                })
                                .onFailure(err -> {
                                        logger.error(err.getMessage());
                                        promise.complete(Status.KO());
                                })
                );

                router.get("/health").handler(HealthCheckHandler.createWithHealthChecks(hc));

                logger.info("Successfully registered all health-checks");

                return Future.succeededFuture(router);
        }

        private Future<Router> createReadinessCheck(Router router) {
                HealthChecks hc = HealthChecks.create(vertx);
                hc.register(
                        "ready",
                        1000L,
                        promise -> promise.complete(Status.OK())
                );

                router.get("/readiness").handler(HealthCheckHandler.createWithHealthChecks(hc));
                return Future.succeededFuture(router);
        }

        private Future<HttpServer> createHttpServer(Router router) {
                int port = config().getInteger("HTTP_PORT").intValue();
                HttpServer server = vertx.createHttpServer();
                return server.requestHandler(router).listen(port)
                        .onSuccess(httpServer -> {
                                logger.info("HTTP server started on port {}", httpServer.actualPort());
                        })
                        .onFailure(throwable -> {
                                logger.error("HTTP server could not start", throwable);
                        });
        }
}
