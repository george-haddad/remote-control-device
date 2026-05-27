package com.remotecontrol.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class DevicesMessageHandler<E> implements Handler<Message<JsonObject>> {

        private final Logger logger = LoggerFactory.getLogger("devices");

        public DevicesMessageHandler() {

        }

        @Override
        public void handle(Message<JsonObject> event) {

        }
}
