package com.remotecontrol.device;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class DeviceService {

        private final Logger logger = LoggerFactory.getLogger("devices");
        private final Pool sharedPool;

        public DeviceService(Pool sharedPool) {
                this.sharedPool = sharedPool;
        }

        Future<Device> createOne(Device device) {
                final String sql = "INSERT INTO app.devices (device_name, device_brand, device_state, device_creation_time) VALUES($1, $2, $3, now()) RETURNING *";
                final Tuple tuple = Tuple.of(
                        device.deviceName(),
                        device.deviceBrand(),
                        device.deviceState()
                );

                return sharedPool.withTransaction(client -> client
                        .preparedQuery(sql)
                        .execute(tuple)
                        .map(RowSet::iterator)
                        .map(iter -> {
                                Device dev = null;
                                if(iter.hasNext()) {
                                        Row row = iter.next();
                                        dev = new Device(
                                                row.getUUID(0),
                                                row.getString(1),
                                                row.getString(2),
                                                row.getString(3),
                                                row.getLocalDateTime(4)
                                        );
                                }
                                else {
                                        // Technically we should not enter here, but just in case
                                        new NoSuchElementException("Query yielded no result without exception");
                                }

                                return dev;
                        })
                        .onSuccess(dev -> logger.info("Successfully inserted device with deviceId={}", dev.deviceId()))
                        .onFailure(err -> logger.error("Error inserting device", err.getMessage())));
        }
}
