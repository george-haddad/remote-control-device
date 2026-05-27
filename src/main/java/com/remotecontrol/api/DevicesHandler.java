package com.remotecontrol.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.http.HttpServerResponse;
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

        void validationError(RoutingContext rc) {
                int code = rc.statusCode();
                String message = null;
                String details = null;

                if(rc.failure() != null) {
                        message = rc.failure().getMessage();
                        details = rc.failure().getCause().getMessage();
                }
                else {
                        message = "Validation exception";
                }

                logger.warn("Validation error on {} {}", rc.request().method().name(), rc.request().path());

                JsonObject errJson = new JsonObject()
                        .put("code", code)
                        .put("message", message);

                if(details != null) {
                        errJson.put("details", details);
                }

                rc.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(code)
                        .end(errJson.encode());
        }

        void create(RoutingContext rc) {
                logger.debug("POST route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                JsonObject reqJson = req.getBody().getJsonObject();
                logger.debug("Dumping json object={}", reqJson.encode());

                EventBus eb = vertx.eventBus();
                DeliveryOptions opts = createDeliveryOpts("addDevice", 3000L);

                logger.debug("Sending message to device verticle via bus address={}", eb_address_devices);
                Future<Message<JsonObject>> fut = eb.request(eb_address_devices, reqJson, opts);
                fut.onSuccess(message -> {
                        JsonObject resJson = message.body();
                        logger.debug("Reply received from={} with body={}", message.address(), resJson);

                        rc.response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(201)
                                .end(resJson.encode());
                })
                .onFailure(err -> {
                        HttpServerResponse res = rc.response();
                        res.putHeader("Content-Type", "application/json");
                        JsonObject errJson = new JsonObject();

                        if(err instanceof ReplyException replyEx) {
                                int code = replyEx.failureCode();
                                ReplyFailure type = replyEx.failureType();
                                String msg = replyEx.getMessage();

                                logger.error("Received reply failure type={} code={} msg={}", type, code, msg);

                                switch(type)
                                {
                                        case RECIPIENT_FAILURE: {
                                                errJson.put("code", code);
                                                errJson.put("message", msg);
                                                res.setStatusCode(code);
                                                break;
                                        }

                                        case NO_HANDLERS:
                                        case TIMEOUT:
                                        case ERROR:
                                        default: {
                                                errJson.put("code", 500);
                                                errJson.put("message", "Could not complete operation=" + opts.getHeaders().get("action") + " due to an unknown error");
                                                res.setStatusCode(500);
                                                break;
                                        }
                                }

                                res.end(errJson.encode());
                        }
                        else {
                                errJson.put("code", 500);
                                errJson.put("message", "Internal server error");
                                res.end(errJson.encode());
                        }
                });
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

        private DeliveryOptions createDeliveryOpts(String action, long timeout) {
                DeliveryOptions opts = new DeliveryOptions();
                opts.setLocalOnly(false);
                opts.setSendTimeout(timeout);
                opts.addHeader("action", action);
                opts.addHeader("domain", "device");
                return opts;
        }
}
