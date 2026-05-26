package com.remotecontrol;

import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

        void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
                testContext.completeNow();
        }
}
