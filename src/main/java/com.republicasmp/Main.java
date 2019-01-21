package com.republicasmp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.republicasmp.commands.InspectExecutor;
import com.republicasmp.database.BlockHelper;
import com.republicasmp.listener.BlockListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static final String TAG = ChatColor.GOLD + "[Tempora] " +ChatColor.RESET;
    public static Logger logger = Bukkit.getLogger();

    // Not sure if I have to keep these in scope or not to avoid GC..
    // Shouldn't need to, but fuck it.
    private MongoClient mongoClient;
    private MongoDatabase database;

    // Database helpers
    public BlockHelper blockHelper;


    public Set<UUID> inspectors = new HashSet<>();

    @Override
    public void onEnable() {
        logger.info("Tempora enabled");

        mongoClient = new MongoClient();
        database = mongoClient.getDatabase("tempora");
        this.blockHelper = new BlockHelper(database);
        logger.info("Connected to Mongo client");

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new BlockListener(this), this);

        this.getCommand("inspect").setExecutor(new InspectExecutor(this));
    }

    @Override
    public void onDisable(){
        logger.info("Tempora disabled");
    }
}
