package de.will_smith_007.bedwars.commands;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartCommand implements CommandExecutor {

    private final ILobbyCountdownHelper lobbyCountdownHelper;

    @Inject
    public StartCommand(@NonNull ILobbyCountdownHelper lobbyCountdownHelper) {
        this.lobbyCountdownHelper = lobbyCountdownHelper;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendPlainMessage(Message.PREFIX + "§cYou need to be a player to execute this command.");
            return true;
        }

        if (!player.hasPermission("bedwars.start")) {
            player.sendPlainMessage(Message.PREFIX + "§cYou don't have permission to execute this command.");
            return true;
        }

        if (args.length != 0) {
            player.sendPlainMessage(Message.PREFIX + "§cPlease only use the command §e/start");
            return true;
        }

        final boolean canShortenCountdown = lobbyCountdownHelper.shortenCountdown();

        if (!canShortenCountdown) {
            player.sendPlainMessage(Message.PREFIX + "§cYou can't shorten the§e lobby countdown§c.");
            player.sendPlainMessage(Message.PREFIX + "§cThere aren't enough players to shorten or the countdown " +
                    "is already running at less than§e 10 seconds§c.");
            return true;
        }

        player.sendPlainMessage(Message.PREFIX + "§aYou've set the countdown to§e 10 seconds§a.");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.0f);

        return false;
    }
}
