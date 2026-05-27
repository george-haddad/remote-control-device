package com.remotecontrol.device;

import java.time.LocalDateTime;
import java.util.UUID;

public record Device(UUID deviceId, String deviceName, String deviceBrand, String deviceState, LocalDateTime deviceCreationTime) {

}
