package com.remotecontrol.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.healthchecks.HealthCheckHandler;
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
                return createHealthChecks()
                        .compose(hc -> createHttpServer(hc));
        }

        private Future<HttpServer> createHttpServer(HealthChecks hc) {
                Router router = Router.router(vertx);
                router.get("/health").handler(HealthCheckHandler.createWithHealthChecks(hc));

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

        private Future<HealthChecks> createHealthChecks() {
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

                return Future.succeededFuture(hc);
        }
}
