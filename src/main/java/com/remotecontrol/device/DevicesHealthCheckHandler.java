package com.remotecontrol.device;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

public class DevicesHealthCheckHandler<E> implements Handler<Message<String>> {

        @Override
        public void handle(Message<String> event) {
                if (event.headers().contains("health")) {
                        String msg = event.body();
                        if ("ping".equals(msg)) {
                                DeliveryOptions opts = new DeliveryOptions();
                                opts.setLocalOnly(false);
                                opts.setSendTimeout(1000L);
                                event.reply("pong", opts);
                                return;
                        }
                }

                event.fail(0, "dead");
        }
}
