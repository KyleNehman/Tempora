package com.republicasmp.commands;

import com.republicasmp.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class InspectExecutor implements CommandExecutor {

    private Set<UUID> inspectors;

    public InspectExecutor(Main plugin) {
        this.inspectors = plugin.inspectors;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (player.isOp()) {
                if (!inspectors.contains(uuid)) {
                    inspectors.add(uuid);
                    player.sendMessage(Main.TAG + "You are now in inspection mode (Bedrock)");

                } else {
                    inspectors.remove(uuid);
                    player.sendMessage(Main.TAG + "No longer inspecting");
                }
            }
        }

        return true;
    }
}
