package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.events.BedBreakEvent;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

public class BedBreakListener implements Listener {

    private final IScoreboardManager SCOREBOARD_MANAGER;
    private final String PREFIX = Message.PREFIX.toString();

    public BedBreakListener(@NonNull IScoreboardManager scoreboardManager) {
        SCOREBOARD_MANAGER = scoreboardManager;
    }

    @EventHandler
    public void onBedBreak(@NonNull BedBreakEvent bedBreakEvent) {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final ITeam iTeam = bedBreakEvent.getBED_TEAM();
        final String teamName = iTeam.getTeamName();
        final Player bedBreakPlayer = bedBreakEvent.getPLAYER();
        final Scoreboard scoreboard = bedBreakPlayer.getScoreboard();
        final Team team = scoreboard.getPlayerTeam(bedBreakPlayer);

        iTeam.setBedExists(false);

        if (team == null) return;

        final TextColor textColor = team.color();
        final String playerName = bedBreakPlayer.getName();

        for (Player player : players) {
            player.sendMessage(Component.text(PREFIX)
                    .append(Component.text("§4The bed from " + teamName + "§4 was destroyed by "))
                    .append(Component.text(playerName).color(textColor))
                    .append(Component.text("§c!")));
            final Location playerLocation = player.getLocation();
            player.playSound(playerLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            SCOREBOARD_MANAGER.updateScoreboard(player);
        }
    }
}
