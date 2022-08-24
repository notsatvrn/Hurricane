package com.notsatvrn.hurricane.util;

import com.notsatvrn.hurricane.accel.DeviceUtil;

import org.bukkit.Bukkit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntryPoint {
    public static final Logger LOGGER = Bukkit.getLogger();

    public static void start() {
        LOGGER.log(Level.INFO, "[Hurricane] Starting...");
        LOGGER.log(Level.INFO, "[Hurricane] Populating devices...");
        DeviceUtil.populateDevices();
        LOGGER.log(Level.INFO, "[Hurricane] Configuring devices...");
        DeviceUtil.configureDevices();
        LOGGER.log(Level.INFO, "[Hurricane] Started.");
    }
}

