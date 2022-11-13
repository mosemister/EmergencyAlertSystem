package com.colonelkai.emergencyalertsystem.commands;

import com.colonelkai.emergencyalertsystem.EmergencyAlertSystem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mose.command.ArgumentCommand;
import org.mose.command.CommandArgument;
import org.mose.command.arguments.operation.ExactArgument;
import org.mose.command.arguments.operation.RemainingArgument;
import org.mose.command.arguments.simple.text.StringArgument;
import org.mose.command.context.CommandContext;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Deprecated
public class CivilProtectionEASCommand implements ArgumentCommand {
    private final ExactArgument ISSUE_ARGUMENT = new ExactArgument("issue");
    private final ExactArgument CIVILPROTECTION_ARGUMENT = new ExactArgument("civil-protection");
    private final RemainingArgument<String> REMAINING_ARGUMENT = new RemainingArgument<>(new StringArgument("message"));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(
                ISSUE_ARGUMENT,
                CIVILPROTECTION_ARGUMENT,
                REMAINING_ARGUMENT
        );
    }

    @Override
    public @NotNull String getDescription() {
        return "Runs an EAS warning about Civil Protection.";
    }

    @Override
    public @NotNull Optional<String> getPermissionNode() {
        return Optional.of("eas.civil-protection");
    }

    @Override
    public boolean run(CommandContext commandContext, String... args) {
        List<String> list = commandContext.getArgument(this, REMAINING_ARGUMENT);
        String string = String.join(" ", list);

        // CHAT BROADCAST

        String broadcastMessage =
                ChatColor.RED +
                "------------ RNET ------------" + "\n" +
                "The Royal Chancellery of the Internal Affairs have issued a Civil Protection Warning. Please seek shelter."
                + "\n" + ChatColor.ITALIC +
                string;

        EmergencyAlertSystem.getPlugin().getServer().broadcastMessage(broadcastMessage);

        // SHORT MESSAGE

        String broadcastMessageShort =
                ChatColor.RED +
                        "Civil Protection Warning. "
                        + ChatColor.ITALIC +
                        string;

        // Sound Player

        int task1 = EmergencyAlertSystem.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(EmergencyAlertSystem.getPlugin(), new Runnable(){
            public void run(){
                EmergencyAlertSystem.getPlugin().getServer().getOnlinePlayers()
                        .parallelStream()
                        .forEach(p -> {
                            p.playSound(p.getLocation(), "minecraft:eas1", 10, 1);
                        });
            }
        }, 0, 280);

// 560
        int task2 = EmergencyAlertSystem.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(EmergencyAlertSystem.getPlugin(), new Runnable(){
            public void run(){
                EmergencyAlertSystem.getPlugin().getServer().getOnlinePlayers()
                        .parallelStream()
                        .forEach(p -> {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(broadcastMessageShort));
                        });
            }
        }, 0, 20);

        EmergencyAlertSystem.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(EmergencyAlertSystem.getPlugin(), new Runnable() {
            @Override
            public void run() {
                EmergencyAlertSystem.getPlugin().getServer().getScheduler().cancelTask(task1);
                EmergencyAlertSystem.getPlugin().getServer().getScheduler().cancelTask(task2);
            }
        }, 560);


        return true;
    }
}