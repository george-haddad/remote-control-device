package com.remotecontrol.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.MessageConsumerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;

public class DevicesVerticle extends VerticleBase {

        private final Logger logger = LoggerFactory.getLogger("devices");
        private final String eb_address_devices = "remotecontrol.devices";
        private final String eb_address_devices_health = "remotecontrol.devices.health";

        private DeviceService service;

        public DevicesVerticle(Pool sharedPool) {
                this.service = new DeviceService(sharedPool);
        }

        @Override
        public Future<?> start() throws Exception {
                return createHealthCheckConsumer()
                        .compose(hcConsumer -> createMessageConsumer())
                        .onSuccess(msgConsumer -> {
                                logger.info("Successfully registered all consumers on DevicesVerticle event-bus");
                        })
                        .onFailure(err -> {
                                logger.info("Failed to register all consumers on DevicesVerticle event-bus, err={}", err.getMessage(), err);
                        });
        }

        private Future<MessageConsumer<String>> createHealthCheckConsumer() {
                logger.info("Creating health-check consumer on event-bus address={} ...", eb_address_devices_health);

                EventBus eb = vertx.eventBus();
                MessageConsumerOptions opts = new MessageConsumerOptions();
                opts.setLocalOnly(false);
                opts.setAddress(eb_address_devices_health);
                opts.setMaxBufferedMessages(1000);
                MessageConsumer<String> consumer = eb.consumer(opts, new DevicesHealthCheckHandler<>());
                return Future.succeededFuture(consumer);
        }

        private Future<MessageConsumer<JsonObject>> createMessageConsumer() {
                logger.info("Creating message consumer on event-bus address={} ...", eb_address_devices);

                EventBus eb = vertx.eventBus();
                MessageConsumerOptions opts = new MessageConsumerOptions();
                opts.setLocalOnly(false);
                opts.setAddress(eb_address_devices);
                opts.setMaxBufferedMessages(1000);
                MessageConsumer<JsonObject> consumer = eb.consumer(opts, new DevicesMessageHandler<>(service));
                return Future.succeededFuture(consumer);
        }
}
