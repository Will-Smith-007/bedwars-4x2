package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
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

    private final IScoreboardManager scoreboardManager;
    private final String prefix = Message.PREFIX.toString();

    @Inject
    public BedBreakListener(@NonNull IScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onBedBreak(@NonNull BedBreakEvent bedBreakEvent) {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final ITeam iTeam = bedBreakEvent.getBedTeam();
        final String teamName = iTeam.getTeamName();
        final Player bedBreakPlayer = bedBreakEvent.getPlayer();
        final Scoreboard scoreboard = bedBreakPlayer.getScoreboard();
        final Team team = scoreboard.getPlayerTeam(bedBreakPlayer);

        iTeam.setBedExists(false);

        if (team == null) return;

        final TextColor textColor = team.color();
        final String playerName = bedBreakPlayer.getName();

        for (Player player : players) {
            player.sendMessage(Component.text(prefix)
                    .append(Component.text("§4The bed from " + teamName + "§4 was destroyed by "))
                    .append(Component.text(playerName).color(textColor))
                    .append(Component.text("§c!")));
            final Location playerLocation = player.getLocation();
            player.playSound(playerLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            scoreboardManager.updateScoreboard(player);
        }
    }
}
