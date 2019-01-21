package com.republicasmp.listener;

import com.republicasmp.Main;
import com.republicasmp.database.BlockHelper;
import com.republicasmp.model.BlockState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.republicasmp.Main.TAG;

public class BlockListener implements Listener {

    private BlockHelper blockHelper;
    private Set<UUID> inspectors;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public BlockListener(Main plugin) {
        this.blockHelper = plugin.blockHelper;
        this.inspectors = plugin.inspectors;
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (inspectors.contains(uuid)) {
            if (event.getBlockPlaced().getType() == Material.BEDROCK) {
                inspect(player, event.getBlockPlaced().getLocation());
                event.setCancelled(true);
                return;
            }
        }

        Location loc = event.getBlock().getLocation();
        Material prev = event.getBlockReplacedState().getType();
        Material placed = event.getBlock().getType();

        String playerName = event.getPlayer().getName();

        BlockState state = new BlockState(playerName, loc, prev, placed, BlockState.EVENT.PLACED);
        blockHelper.insert(state);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (inspectors.contains(uuid)) {
            if (player.getItemInHand().getType() == Material.BEDROCK) {
                inspect(player, event.getBlock().getLocation());
                event.setCancelled(true);
                return;
            }
        }

        Location loc = event.getBlock().getLocation();
        Material prev = event.getBlock().getType();
        Material now = Material.AIR;

        String playerName = event.getPlayer().getName();

        BlockState state = new BlockState(playerName, loc, prev, now, BlockState.EVENT.BROKE);
        blockHelper.insert(state);
    }

    private void inspect(Player player, Location loc) {
        List<BlockState> results = blockHelper.findByLoc(loc);
        if (results.size() == 0) {
            player.sendMessage(TAG + "No results found");

        } else {
            player.sendMessage(TAG + "Results for " +
                    ChatColor.RED + loc.getBlockX() + ChatColor.RESET + ", " +
                    ChatColor.RED + loc.getBlockY() + ChatColor.RESET + ", " +
                    ChatColor.RED + loc.getBlockZ() + ChatColor.RESET + ": ");

            for (BlockState state : results) {
                    Material affected = state.getBefore();

                    if (state.getEventType() == BlockState.EVENT.PLACED)
                        affected = state.getAfter();

                    player.sendMessage(TAG + state.getDate().format(dateFormatter) + " " +
                        ChatColor.AQUA + state.getEntityName() + " " +
                        ChatColor.RESET + state.getEventType().toString().toLowerCase() + " " +
                        ChatColor.RED + affected.toString().toLowerCase());
            }
        }
    }
}
