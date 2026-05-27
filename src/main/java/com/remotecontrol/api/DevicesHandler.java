package com.remotecontrol.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.validation.RequestParameter;
import io.vertx.openapi.validation.ValidatedRequest;

public class DevicesHandler extends DevicesHandlerBase {

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
                        errorResponseHandler(err, rc.response());
                });
        }

        void get(RoutingContext rc) {
                logger.debug("GET :id route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                String deviceId = req.getPathParameters().get("deviceId").getString();
                logger.debug("Dumping path param={}", deviceId);

                JsonObject reqJson = new JsonObject();
                reqJson.put("deviceId", deviceId);

                EventBus eb = vertx.eventBus();
                DeliveryOptions opts = createDeliveryOpts("getDeviceById", 3000L);

                logger.debug("Sending message to device verticle via bus address={}", eb_address_devices);
                Future<Message<JsonObject>> fut = eb.request(eb_address_devices, reqJson, opts);
                fut.onSuccess(message -> {
                        JsonObject resJson = message.body();
                        logger.debug("Reply received from={} with body={}", message.address(), resJson);

                        rc.response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(200)
                                .end(resJson.encode());
                })
                .onFailure(err -> {
                        errorResponseHandler(err, rc.response());
                });
        }

        void getAll(RoutingContext rc) {
                logger.debug("GET route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                Map<String,RequestParameter> queryMap = req.getQuery();

                JsonObject reqJson = new JsonObject();
                reqJson.put("deviceId", "*");

                if(!queryMap.isEmpty()) {
                        if(queryMap.containsKey("state")) {
                                String stateValue = queryMap.get("state").getString();
                                reqJson.put("state", stateValue);
                        }

                        if(queryMap.containsKey("brand")) {
                                String brandValue = queryMap.get("brand").getString();
                                reqJson.put("brand", brandValue);
                        }
                }

                logger.debug("Dumping request json={}", reqJson.encode());

                EventBus eb = vertx.eventBus();
                DeliveryOptions opts = createDeliveryOpts("getAllDevices", 3000L);

                logger.debug("Sending message to device verticle via bus address={}", eb_address_devices);
                Future<Message<JsonObject>> fut = eb.request(eb_address_devices, reqJson, opts);
                fut.onSuccess(message -> {
                        JsonObject resJson = message.body();
                        logger.debug("Reply received from={} with body={}", message.address(), resJson);

                        rc.response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(200)
                                .end(resJson.encode());
                })
                .onFailure(err -> {
                        errorResponseHandler(err, rc.response());
                });
        }

        void update(RoutingContext rc) {
                logger.debug("PUT route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                String deviceId = req.getPathParameters().get("deviceId").getString();
                JsonObject reqJson = req.getBody().getJsonObject();
                reqJson.put("deviceId", deviceId);
                logger.debug("Dumping path param={}", deviceId);
                logger.debug("Dumping json object={}", reqJson.encode());

                EventBus eb = vertx.eventBus();
                DeliveryOptions opts = createDeliveryOpts("updateDevice", 3000L);

                logger.debug("Sending message to device verticle via bus address={}", eb_address_devices);
                Future<Message<JsonObject>> fut = eb.request(eb_address_devices, reqJson, opts);
                fut.onSuccess(message -> {
                        JsonObject resJson = message.body();
                        logger.debug("Reply received from={} with body={}", message.address(), resJson);

                        rc.response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(200)
                                .end(resJson.encode());
                })
                .onFailure(err -> {
                        errorResponseHandler(err, rc.response());
                });
        }

        void patch(RoutingContext rc) {
                logger.debug("PATCH route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                String deviceId = req.getPathParameters().get("deviceId").getString();
                JsonObject reqJson = req.getBody().getJsonObject();
                reqJson.put("deviceId", deviceId);
                logger.debug("Dumping path param={}", deviceId);
                logger.debug("Dumping json object={}", reqJson.encode());

                EventBus eb = vertx.eventBus();
                DeliveryOptions opts = createDeliveryOpts("patchDevice", 3000L);

                logger.debug("Sending message to device verticle via bus address={}", eb_address_devices);
                Future<Message<JsonObject>> fut = eb.request(eb_address_devices, reqJson, opts);
                fut.onSuccess(message -> {
                        JsonObject resJson = message.body();
                        logger.debug("Reply received from={} with body={}", message.address(), resJson);

                        rc.response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(200)
                                .end(resJson.encode());
                })
                .onFailure(err -> {
                        errorResponseHandler(err, rc.response());
                });
        }

        void delete(RoutingContext rc) {
                logger.debug("DELETE route");

                ValidatedRequest req = rc.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
                String deviceId = req.getPathParameters().get("deviceId").getString();
                logger.debug("Dumping path param={}", deviceId);

                JsonObject reqJson = new JsonObject();
                reqJson.put("deviceId", deviceId);

                EventBus eb = vertx.eventBus();
                DeliveryOptions opts = createDeliveryOpts("deleteDevice", 3000L);

                logger.debug("Sending message to device verticle via bus address={}", eb_address_devices);
                Future<Message<JsonObject>> fut = eb.request(eb_address_devices, reqJson, opts);
                fut.onSuccess(message -> {
                        JsonObject resJson = message.body();
                        logger.debug("Reply received from={} with body={}", message.address(), resJson);

                        rc.response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(200)
                                .end(resJson.encode());
                })
                .onFailure(err -> {
                        errorResponseHandler(err, rc.response());
                });
        }
}
