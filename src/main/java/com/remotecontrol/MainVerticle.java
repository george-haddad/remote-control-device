package com.remotecontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.remotecontrol.api.ApiVerticle;
import com.remotecontrol.device.DevicesVerticle;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class MainVerticle extends VerticleBase {

        private static final Logger logger = LoggerFactory.getLogger("main");
        private final String configPath = "config.json";
        private Pool sharedPool;

        @Override
        public Future<?> start() {
                return loadConfig()
                        .compose(config -> createPool())
                        .compose(pool -> deployApiVerticle())
                        .compose(id -> deployDevicesVerticle())
                        .compose(id -> {
                                logger.info("Successfully deployed all verticles");
                                return Future.succeededFuture();
                        });
        }

        private Future<String> deployApiVerticle() {
                logger.info("Deploying ApiVerticle");

                DeploymentOptions opts = new DeploymentOptions()
                        .setInstances(3)
                        .setConfig(config().copy());

                return vertx.deployVerticle(() -> new ApiVerticle(sharedPool), opts)
                        .onSuccess(id -> {
                                logger.info("Deployed {} instances of ApiVerticle with deploymentId={}", opts.getInstances(), id);
                        })
                        .onFailure(err -> {
                                logger.error("Failed to deploy ApiVerticle err={}", err.getMessage(), err.getCause());
                        });
        }

        private Future<String> deployDevicesVerticle() {
                logger.info("Deploying DevicesVerticle");

                DeploymentOptions opts = new DeploymentOptions()
                        .setInstances(2)
                        .setConfig(config().copy());

                return vertx.deployVerticle(() -> new DevicesVerticle(sharedPool), opts)
                        .onSuccess(id -> {
                                logger.info("Deployed {} instances of DevicesVerticle with deploymentId={}", opts.getInstances(), id);
                        })
                        .onFailure(err -> {
                                logger.error("Failed to deploy DevicesVerticle err={}", err.getMessage(), err.getCause());
                        });
        }

        private Future<Pool> createPool() {
                PgConnectOptions connectOptions = new PgConnectOptions()
                                .setPort(config().getInteger("DB_PORT").intValue())
                                .setHost(config().getString("DB_HOST"))
                                .setDatabase(config().getString("DB_NAME"))
                                .setUser(config().getString("DB_USER"))
                                .setPassword(config().getString("DB_PASS"));

                final int availableCores = Runtime.getRuntime().availableProcessors() / 2;
                PoolOptions poolOptions = new PoolOptions().setMaxSize(availableCores);

                // pool operations are not pipelined, only connections acquired from the pool are pipelined
                this.sharedPool = PgBuilder.pool()
                        .with(poolOptions)
                        .connectingTo(connectOptions)
                        .using(vertx)
                        .build();

                logger.info("Built shared pgPool to host={} on database={} as user={}",
                        config().getString("DB_HOST"),
                        config().getString("DB_NAME"),
                        config().getString("DB_USER"));

                return Future.succeededFuture(sharedPool);
        }

        private Future<JsonObject> loadConfig() {
                JsonObject configJson = new JsonObject().put("path", configPath);
                ConfigStoreOptions fileStore = new ConfigStoreOptions().setType("file").setConfig(configJson);

                ConfigStoreOptions envStore = new ConfigStoreOptions()
                        .setType("env")
                        .setConfig(new JsonObject()
                                .put("keys", new JsonArray()
                                        .add("NAME")
                                        .add("DB_HOST")
                                        .add("DB_PORT")
                                        .add("DB_NAME")
                                        .add("DB_USER")
                                        .add("DB_PASS")
                                        .add("HTTP_PORT")));

                ConfigRetrieverOptions retrieverOpts = new ConfigRetrieverOptions()
                        .addStore(fileStore)  // load first
                        .addStore(envStore);  // overrides fileStore if env-vars exist (we are in a docker)

                ConfigRetriever retriever = ConfigRetriever.create(vertx, retrieverOpts);
                return retriever.getConfig()
                        .onSuccess(json -> {
                                config().mergeIn(json);
                                logger.info("Successfully loaded config for {}", config().getString("NAME"));
                        })
                        .onFailure(err -> {
                                logger.error("Could not load config from {}", configJson.encode(), err);
                        });
        }
}
