package com.notsatvrn.hurricane.util;

import com.notsatvrn.hurricane.accel.DeviceUtil;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class EntryPoint {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void start() {
        LOGGER.info("[Hurricane] Starting...");
        LOGGER.info("[Hurricane] Populating devices...");
        DeviceUtil.populateDevices();
        LOGGER.info("[Hurricane] Started.");
    }
}

