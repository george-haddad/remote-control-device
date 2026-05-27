package com.remotecontrol.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class DevicesHandlerBase {

        private final Logger logger = LoggerFactory.getLogger("api");

        public DevicesHandlerBase() {

        }

        protected void validationFailure(RoutingContext rc) {
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

        protected void errorResponseHandler(Throwable err, HttpServerResponse res) {
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
                                        errJson.put("message", "Could not complete operation due to an unknown error");
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
        }

        protected DeliveryOptions createDeliveryOpts(String action, long timeout) {
                DeliveryOptions opts = new DeliveryOptions();
                opts.setLocalOnly(false);
                opts.setSendTimeout(timeout);
                opts.addHeader("action", action);
                opts.addHeader("domain", "device");
                return opts;
        }
}
