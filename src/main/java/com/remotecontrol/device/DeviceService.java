package com.remotecontrol.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.sqlclient.Pool;

public class DeviceService {

        private final Logger logger = LoggerFactory.getLogger("devices");
        private final Pool sharedPool;

        public DeviceService(Pool sharedPool) {
                this.sharedPool = sharedPool;
        }


}
