package com.notsatvrn.hurricane.config;

import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.notsatvrn.hurricane.config.HurricaneConfig.log;

@SuppressWarnings("unused")
public class HurricaneWorldConfig {
    private final String worldName;

    public HurricaneWorldConfig(String worldName) {
        this.worldName = worldName;
        init();
    }

    public void init() {
        log("-------- World Settings For [" + worldName + "] --------");
        HurricaneConfig.readConfig(HurricaneWorldConfig.class, this);
    }

    private void set(String path, Object val) {
        HurricaneConfig.config.addDefault("world-settings.default." + path, val);
        HurricaneConfig.config.set("world-settings.default." + path, val);
        if (HurricaneConfig.config.get("world-settings." + worldName + "." + path) != null) {
            HurricaneConfig.config.addDefault("world-settings." + worldName + "." + path, val);
            HurricaneConfig.config.set("world-settings." + worldName + "." + path, val);
        }
    }

    private ConfigurationSection getConfigurationSection(String path) {
        ConfigurationSection section = HurricaneConfig.config.getConfigurationSection("world-settings." + worldName + "." + path);
        return section != null ? section : HurricaneConfig.config.getConfigurationSection("world-settings.default." + path);
    }

    private String getString(String path, String def) {
        HurricaneConfig.config.addDefault("world-settings.default." + path, def);
        return HurricaneConfig.config.getString("world-settings." + worldName + "." + path, HurricaneConfig.config.getString("world-settings.default." + path));
    }

    private boolean getBoolean(String path, boolean def) {
        HurricaneConfig.config.addDefault("world-settings.default." + path, def);
        return HurricaneConfig.config.getBoolean("world-settings." + worldName + "." + path, HurricaneConfig.config.getBoolean("world-settings.default." + path));
    }

    private boolean getBoolean(String path, Predicate<Boolean> predicate) {
        String val = getString(path, "default").toLowerCase();
        Boolean bool = BooleanUtils.toBooleanObject(val, "true", "false", "default");
        return predicate.test(bool);
    }

    private double getDouble(String path, double def) {
        HurricaneConfig.config.addDefault("world-settings.default." + path, def);
        return HurricaneConfig.config.getDouble("world-settings." + worldName + "." + path, HurricaneConfig.config.getDouble("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        HurricaneConfig.config.addDefault("world-settings.default." + path, def);
        return HurricaneConfig.config.getInt("world-settings." + worldName + "." + path, HurricaneConfig.config.getInt("world-settings.default." + path));
    }

    private <T> List<?> getList(String path, T def) {
        HurricaneConfig.config.addDefault("world-settings.default." + path, def);
        return HurricaneConfig.config.getList("world-settings." + worldName + "." + path, HurricaneConfig.config.getList("world-settings.default." + path));
    }

    private Map<String, Object> getMap(String path, Map<String, Object> def) {
        final Map<String, Object> fallback = HurricaneConfig.getMap("world-settings.default." + path, def);
        final Map<String, Object> value = HurricaneConfig.getMap("world-settings." + worldName + "." + path, null);
        return value.isEmpty() ? fallback : value;
    }
}

