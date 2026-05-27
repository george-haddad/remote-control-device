package com.remotecontrol.device;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
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

        Future<Device> fetchOne(String deviceId) {
                final String sql = "SELECT device_id, device_name, device_brand, device_state, device_creation_time FROM app.devices WHERE device_id = $1";
                final Tuple tuple = Tuple.of(deviceId);

                return sharedPool.preparedQuery(sql)
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
                                        throw new NoSuchElementException("Device with id does not exist");
                                }

                                return dev;
                        })
                        .onSuccess(dev -> logger.info("Successfully fetched device with deviceId={}", dev.deviceId()))
                        .onFailure(err -> logger.error("Error fetching device with deviceId={}, message={}", deviceId, err.getMessage()));
        }

        Future<Device[]> fetchAll() {
                final String sql = "SELECT device_id, device_name, device_brand, device_state, device_creation_time FROM app.devices";

                return sharedPool.preparedQuery(sql)
                        .execute()
                        .map(rows -> {
                                RowIterator<Row> iter  = rows.iterator();
                                List<Device> list = new ArrayList<>(rows.size());

                                while(iter.hasNext()) {
                                        Row row = iter.next();
                                        Device dev = new Device(
                                                row.getUUID(0),
                                                row.getString(1),
                                                row.getString(2),
                                                row.getString(3),
                                                row.getLocalDateTime(4)
                                        );

                                        list.add(dev);
                                }

                                Device[] devices = list.toArray(new Device[list.size()]);
                                return devices;
                        })
                        .onSuccess(devices -> logger.info("Successfully fetched all devices"))
                        .onFailure(err -> logger.error("Error fetching all devices message={}", err.getMessage()));
        }

        Future<Device> removeOne(String deviceId) {
                final String sqlExists = "SELECT device_id FROM app.devices WHERE device_id = $1";
                final String sqlDelete = "DELETE FROM app.devices WHERE device_id = $1 AND device_state <> 'in-use' RETURNING *";
                final Tuple tuple = Tuple.of(deviceId);

                return sharedPool.withTransaction(client -> client
                        .preparedQuery(sqlExists)
                        .execute(tuple)
                        .compose(rows -> {
                                if(rows.size() < 1) {
                                        return Future.failedFuture(new NoSuchElementException("Device with id does not exist"));
                                }

                                return client.preparedQuery(sqlDelete)
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
                                                        throw new IllegalStateException("Device with id has a state of 'in-use' and cannot be deleted");
                                                }

                                                return dev;
                                        });
                        })
                        .onSuccess(dev -> logger.info("Successfully deleted device with deviceId={}", deviceId))
                        .onFailure(err -> logger.error("Error deleting device with deviceId={}, message={}", deviceId, err.getMessage())));
        }
}
