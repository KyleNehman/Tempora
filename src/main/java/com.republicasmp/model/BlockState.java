package com.republicasmp.model;

import org.bukkit.Location;
import org.bukkit.Material;

import java.time.LocalDateTime;
import java.util.UUID;

public class BlockState {

    public enum EVENT {PLACED, BROKE, NONE}

    private LocalDateTime date;
    private EVENT eventType;

    private String worldName;
    private int x, y, z;

    private Material before;
    private Material after;

    private String entityName;

    // Avoid using default constructor,
    // It's here for reflective NoSQL references
    public BlockState() {
    }

    public BlockState(String name, Location loc, Material before, Material after, EVENT event) {
        this.entityName = name;

        this.date = LocalDateTime.now();
        this.before = before;
        this.after = after;

        this.eventType = event;

        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.worldName = loc.getWorld().getName();
    }

    public String getEntityName() {
        return entityName;
    }

    public Material getAfter() {
        return after;
    }

    public Material getBefore() {
        return before;
    }

    public EVENT getEventType() {
        return eventType;
    }

    public LocalDateTime getDate() {
        return date;
    }


    @Override
    public String toString() {
        return "BlockState: {" +
                "\"worldName\": " + worldName +
                "\"x\": " + x +
                "\"y\": " + y +
                "\"z\": " + z +
                "\"entityName\": " + entityName +
                "}";
    }
}
