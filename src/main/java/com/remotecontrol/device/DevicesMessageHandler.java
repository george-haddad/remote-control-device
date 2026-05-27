package com.remotecontrol.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
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

                // TODO business logic code here aka "service" layer
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

                // TODO business logic code here aka "service" layer
        }

        private void updateOne(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();
                String deviceId = body.getString("deviceId");

                logger.debug("Received message={} on address={} with headers[action={},domain={}]", body.encode(), event.address(), action, domain);

                // TODO business logic code here aka "service" layer
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

                // TODO business logic code here aka "service" layer
        }

        private void removeOne(Message<JsonObject> event) {
                String action = event.headers().get("action");
                String domain = event.headers().get("domain");
                JsonObject body = event.body();
                String deviceId = body.getString("deviceId");

                logger.debug("Received message={} on address={} with headers[action={},domain={}]",  body.encode(), event.address(), action, domain);

                // TODO business logic code here aka "service" layer
        }
}
