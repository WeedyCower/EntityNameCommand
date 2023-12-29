package com.weedycow.entityNameCommand;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin
{
    public static Main main;
    @Override
    public void onEnable()
    {
        main = this;
        saveDefaultConfig();
        Bukkit.getPluginCommand("enc").setExecutor(new EntityCommand());
        Bukkit.getPluginManager().registerEvents(new EventListener(),this);
    }

    @Override
    public void onDisable() {}
}
