package com.test;

import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {

    private BrewManager brewManager;
    private BrewMenu brewMenu;

    @Override
    public void onEnable() {
        this.brewManager = new BrewManager(this);
        this.brewMenu = new BrewMenu(this.brewManager);
        
        getServer().getPluginManager().registerEvents(brewMenu, this);

        if (getCommand("dbre") != null) {
            getCommand("dbre").setExecutor(new DbreCommand(this, brewManager, brewMenu));
        }
        getLogger().info("MyPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyPlugin disabled!");
    }
}
