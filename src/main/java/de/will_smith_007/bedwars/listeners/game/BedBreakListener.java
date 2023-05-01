package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.events.BedBreakEvent;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.Optional;

public class BedBreakListener implements Listener {

    private final IScoreboardManager SCOREBOARD_MANAGER;

    public BedBreakListener(@NonNull IScoreboardManager scoreboardManager) {
        SCOREBOARD_MANAGER = scoreboardManager;
    }

    @EventHandler
    public void onBedBreak(@NonNull BedBreakEvent bedBreakEvent) {
        final Optional<ITeam> optionalITeam = bedBreakEvent.getTeamFromBed();
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        optionalITeam.ifPresent(iTeam -> {
            final String teamName = iTeam.getTeamName();

            for (Player player : players) {
                player.sendPlainMessage(Message.PREFIX + "§cThe bed from " + teamName + "§c was §4destroyed§c!");
                final Location playerLocation = player.getLocation();
                player.playSound(playerLocation, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
                SCOREBOARD_MANAGER.updateScoreboard(player);
            }
        });
    }
}
