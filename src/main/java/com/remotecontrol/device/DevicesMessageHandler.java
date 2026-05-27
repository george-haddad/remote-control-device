package com.remotecontrol.device;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DevicesMessageHandler<E> implements Handler<Message<JsonObject>> {

        private final Logger logger = LoggerFactory.getLogger("devices");
        private DeviceService service;

        public DevicesMessageHandler(DeviceService service) {
                this.service = service;
        }

        @Override
        public void handle(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");

                if (action == null) {
                        event.fail(1, "No action header specified");
                        return;
                }

                if (domain == null) {
                        event.fail(1, "No domain header specified");
                        return;
                }

                switch (action) {
                        case "addDevice":
                                createOne(event);
                                break;

                        case "getAllDevices":
                                fetchMany(event);
                                break;

                        case "updateDevice":
                                updateOne(event);
                                break;

                        case "patchDevice":
                                patchOne(event);
                                break;

                        case "getDeviceById":
                                fetchOne(event);
                                break;

                        case "deleteDevice":
                                removeOne(event);
                                break;

                        default:
                                logger.debug("Received on default case message={} on address={} with headers[action={},domain={}]", event.body().encode(), event.address(), action, domain);
                                event.fail(400, "Invalid request");
                                break;
                }
        }

        private void createOne(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();

                logger.debug("Received message={} on address={} with headers[action={},domain={}]", body.encode(), event.address(), action, domain);

                Device device = new Device(
                        null,
                        body.getString("name"),
                        body.getString("brand"),
                        body.getString("state").toLowerCase(),
                        null
                );

                service.createOne(device)
                        .compose(dev -> createResponse(dev))
                        .onSuccess(json -> {
                                event.reply(json, createDeliveryOpts(action, 3000L));
                        })
                        .onFailure(err -> {
                                if (err instanceof NoSuchElementException) {
                                        event.fail(404, err.getMessage());
                                }
                                else {
                                        event.fail(500, err.getMessage());
                                }
                        });
        }

        private void fetchMany(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();
                String deviceId = body.getString("deviceId");

                logger.debug("Received message={} on address={} with headers[action={},domain={}]", body.encode(), event.address(), action, domain);

                if (!deviceId.equals("*")) {
                        event.fail(500, "Cannot fetch all devices due to invalid id");
                        return;
                }

                service.fetchAll()
                        .compose(devices -> createResponseMany(devices))
                        .onSuccess(json -> {
                                event.reply(json, createDeliveryOpts(action, 3000L));
                        })
                        .onFailure(err -> {
                                event.fail(500, err.getMessage());
                        });
        }

        private void updateOne(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();
                String deviceId = body.getString("deviceId");

                logger.debug("Received message={} on address={} with headers[action={},domain={}]", body.encode(), event.address(), action, domain);

                Device device = new Device(
                        null,
                        body.getString("name"),
                        body.getString("brand"),
                        body.getString("state").toLowerCase(),
                        null
                );

                service.updateOne(deviceId, device)
                        .compose(res -> createResponse(res))
                        .onSuccess(json -> {
                                event.reply(json, createDeliveryOpts(action, 3000L));
                        })
                        .onFailure(err -> {
                                if (err instanceof NoSuchElementException) {
                                        event.fail(404, err.getMessage());
                                }
                                else if (err instanceof IllegalStateException) {
                                        event.fail(409, err.getMessage());
                                }
                                else {
                                        event.fail(500, err.getMessage());
                                }
                        });
        }

        private void patchOne(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();
                String deviceId = body.getString("deviceId");

                logger.debug("Received message={} on address={} with headers[action={},domain={}]", body.encode(), event.address(), action, domain);

                // TODO business logic code here aka "service" layer
        }

        private void fetchOne(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();
                String deviceId = body.getString("deviceId");

                logger.debug("Received message={} on address={} with headers[action={},domain={}]", body.encode(), event.address(), action, domain);

                service.fetchOne(deviceId)
                        .compose(device -> createResponse(device))
                        .onSuccess(json -> {
                                event.reply(json, createDeliveryOpts(action, 3000L));
                        })
                        .onFailure(err -> {
                                if (err instanceof NoSuchElementException) {
                                        event.fail(404, err.getMessage());
                                }
                                else {
                                        event.fail(500, err.getMessage());
                                }
                        });
        }

        private void removeOne(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();
                String deviceId = body.getString("deviceId");

                logger.debug("Received message={} on address={} with headers[action={},domain={}]",  body.encode(), event.address(), action, domain);

                service.removeOne(deviceId)
                        .compose(device -> createResponse(device))
                        .onSuccess(json -> {
                                event.reply(json, createDeliveryOpts(action, 3000L));
                        })
                        .onFailure(err -> {
                                if (err instanceof NoSuchElementException) {
                                        event.fail(404, err.getMessage());
                                }
                                else if (err instanceof IllegalStateException) {
                                        event.fail(409, err.getMessage());
                                }
                                else {
                                        event.fail(500, err.getMessage());
                                }
                        });
        }

        //--------------

        private DeliveryOptions createDeliveryOpts(String action, long timeout) {
                DeliveryOptions opts = new DeliveryOptions();
                opts.setLocalOnly(false);
                opts.setSendTimeout(timeout);
                opts.addHeader("action", action);
                opts.addHeader("domain", "device");
                return opts;
        }

        private Future<JsonObject> createResponse(Device device) {
                JsonObject json = new JsonObject()
                        .put("id", device.deviceId().toString())
                        .put("name", device.deviceName().toString())
                        .put("brand", device.deviceBrand())
                        .put("state", device.deviceState())
                        .put("creation-time", device.deviceCreationTime().toString());
                return Future.succeededFuture(json);
        }

        private Future<JsonArray> createResponseMany(Device[] devices) {
                JsonArray jarray = new JsonArray();
                for (Device device : devices) {
                        jarray.add(new JsonObject()
                                .put("id", device.deviceId().toString())
                                .put("name", device.deviceName().toString())
                                .put("brand", device.deviceBrand())
                                .put("state", device.deviceState())
                                .put("creation-time", device.deviceCreationTime().toString())
                        );
                }

                return Future.succeededFuture(jarray);
        }
}
