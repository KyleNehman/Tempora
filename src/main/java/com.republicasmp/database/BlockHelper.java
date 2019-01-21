package com.republicasmp.database;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.republicasmp.Main;
import com.republicasmp.model.BlockState;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class BlockHelper {

    private MongoCollection<Document> collection;
    private Logger logger;

    public BlockHelper(MongoDatabase db) {
        this.collection = db.getCollection("blocks");
        this.logger = Main.logger;
    }

    public void insert(BlockState blockState) {
        Document doc = new Document();
        Field[] fields = BlockState.class.getDeclaredFields();

        for (Field f : fields) {
            try {
                f.setAccessible(true);
                String fieldName = f.getName();

                if (!fieldName.contains("meta") && !fieldName.contains("$")) {
                    Object current = f.get(blockState);
                    if (current.getClass().isEnum()) {
                        doc.put(fieldName, current.toString());

                    } else {
                        doc.put(fieldName, current);
                    }
                }
            } catch (Exception e) {
                // Should never occur
                e.printStackTrace();
            }
        }

        collection.insertOne(doc);
    }

    public List<BlockState> findByLoc(Location loc) {
        String worldName = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        Bson filter = and(eq("x", x),
                eq("y", y),
                eq("z", z),
                eq("worldName", worldName));

        List<BlockState> returnVals = new ArrayList<>();
        FindIterable<Document> results = collection.find(filter);
        results.sort(Sorts.ascending("date"));

        results.forEach((Block<Document>) document -> {
            BlockState thisState = new BlockState();
            Field[] fields = BlockState.class.getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.contains("$") || fieldName.contains("meta"))
                        continue;

                    Class clazz = field.getType();
                    Object val = document.get(fieldName);

                    if (clazz.isEnum()) {
                        Enum enumVal = Enum.valueOf(clazz, val.toString());
                        field.set(thisState, enumVal);

                    } else if (clazz == LocalDateTime.class) {
                        Date date = (Date) document.get(fieldName);
                        field.set(thisState, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                    } else {
                        field.set(thisState, document.get(fieldName));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            returnVals.add(thisState);
        });

        return returnVals;
    }
}
