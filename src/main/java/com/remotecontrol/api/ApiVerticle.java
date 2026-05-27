package com.remotecontrol.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
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
                        .compose(router -> createHttpServer(router));
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
                        route.addFailureHandler(devicesHandler::validationError);
                }

                Router router = routerBuilder.createRouter();
                return Future.succeededFuture(router);
        }

        private Future<Router> createHealthChecks(Router router) {
                HealthChecks hc = HealthChecks.create(vertx);
                hc.register(
                        "api",
                        1000L,
                        promise -> promise.complete(Status.OK())
                );

                hc.register(
                        "database",
                        2000L,
                        promise -> sharedPool.getConnection()
                                .compose(SqlConnection::close)
                                .<Status>mapEmpty()
                                .onComplete(promise)
                );

                router.get("/health")
                        .handler(HealthCheckHandler.createWithHealthChecks(hc));

                return Future.succeededFuture(router);
        }

        private Future<HttpServer> createHttpServer(Router router) {
                int port = config().getInteger("http.port").intValue();
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
