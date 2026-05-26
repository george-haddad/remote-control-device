package com.remotecontrol.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.validation.ValidatedRequest;

public class DevicesHandler {

        private final Logger logger = LoggerFactory.getLogger("api");
        private final String eb_address_devices = "remotecontrol.devices";
        private Vertx vertx;

        public DevicesHandler(Vertx vertx) {
                this.vertx = vertx;
        }

        void create(RoutingContext rc) {
                logger.debug("POST route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                JsonObject reqJson = req.getBody().getJsonObject();
                logger.debug("Dumping json object={}", reqJson.encode());

                JsonObject device = new JsonObject()
                                        .put("id", "550e8400-e29b-41d4-a716-446655440000")
                                        .put("name", "whatever")
                                        .put("brand", "samsung")
                                        .put("state", "available")
                                        .put("creation-time", "2026-05-26T18:00:00.000000Z");
                rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(201)
                        .end(device.encode());
        }

        void get(RoutingContext rc) {
                logger.debug("GET :id route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                String deviceId = req.getPathParameters().get("deviceId").getString();
                logger.debug("Dumping path param={}", deviceId);

                JsonObject device = new JsonObject()
                                        .put("id", "550e8400-e29b-41d4-a716-446655440000")
                                        .put("name", "whatever")
                                        .put("brand", "samsung")
                                        .put("state", "available")
                                        .put("creation-time", "2026-05-26T18:00:00.000000Z");
                rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(device.encode());
        }

        void getAll(RoutingContext rc) {
                logger.debug("GET route");
                JsonArray arr = new JsonArray()
                                .add(new JsonObject()
                                        .put("id", "550e8400-e29b-41d4-a716-446655440000")
                                        .put("name", "whatever")
                                        .put("brand", "samsung")
                                        .put("state", "available")
                                        .put("creation-time", "2026-05-26T18:00:00.000000Z")
                        );

                rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(arr.encode());
        }

        void update(RoutingContext rc) {
                logger.debug("PUT route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                JsonObject reqJson = req.getBody().getJsonObject();
                String deviceId = req.getPathParameters().get("deviceId").getString();
                logger.debug("Dumping path param={}", deviceId);
                logger.debug("Dumping json object={}", reqJson.encode());

                JsonObject device = new JsonObject()
                                        .put("id", "550e8400-e29b-41d4-a716-446655440000")
                                        .put("name", "whatever")
                                        .put("brand", "samsung")
                                        .put("state", "available")
                                        .put("creation-time", "2026-05-26T18:00:00.000000Z");
                rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(device.encode());
        }

        void patch(RoutingContext rc) {
                logger.debug("PATCH route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                JsonObject reqJson = req.getBody().getJsonObject();
                String deviceId = req.getPathParameters().get("deviceId").getString();
                logger.debug("Dumping path param={}", deviceId);
                logger.debug("Dumping json object={}", reqJson.encode());

                JsonObject device = new JsonObject()
                                        .put("id", "550e8400-e29b-41d4-a716-446655440000")
                                        .put("name", "whatever")
                                        .put("brand", "samsung")
                                        .put("state", "available")
                                        .put("creation-time", "2026-05-26T18:00:00.000000Z");
                rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(device.encode());
        }

        void delete(RoutingContext rc) {
                logger.debug("DELETE route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                final String deviceId = req.getPathParameters().get("deviceId").getString();
                logger.debug("Dumping path param={}", deviceId);

                JsonObject device = new JsonObject()
                                        .put("id", "550e8400-e29b-41d4-a716-446655440000")
                                        .put("name", "whatever")
                                        .put("brand", "samsung")
                                        .put("state", "available")
                                        .put("creation-time", "2026-05-26T18:00:00.000000Z");
                rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(device.encode());
        }
}
