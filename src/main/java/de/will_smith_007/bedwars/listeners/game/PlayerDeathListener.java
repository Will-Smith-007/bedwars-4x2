package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.schedulers.EndingCountdownScheduler;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;

public class PlayerDeathListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;
    private final EndingCountdownScheduler ENDING_COUNTDOWN_SCHEDULER;
    private final BedWarsConfig BEDWARS_CONFIG = BedWarsConfig.getInstance();
    private final BedWarsTeam[] BEDWARS_TEAMS = BedWarsTeam.values();

    public PlayerDeathListener(@NonNull GameAssets gameAssets,
                               @NonNull ITeamHelper teamHelper,
                               @NonNull EndingCountdownScheduler endingCountdownScheduler) {
        GAME_ASSETS = gameAssets;
        TEAM_HELPER = teamHelper;
        ENDING_COUNTDOWN_SCHEDULER = endingCountdownScheduler;
    }

    @EventHandler
    public void onPlayerDeath(@NonNull PlayerDeathEvent playerDeathEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();

        if (gameState == GameState.LOBBY) return;

        playerDeathEvent.setCancelled(true);

        final Player player = playerDeathEvent.getPlayer();
        final World playerWorld = player.getWorld();
        final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);

        optionalITeam.ifPresent(iTeam -> {
            final boolean bedExists = iTeam.bedExists(playerWorld);
            final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            for (Player onlinePlayer : onlinePlayers) {
                onlinePlayer.sendMessage(Component.text(Message.PREFIX + " ")
                        .append(player.displayName())
                        .append(Component.text("§7 died.")));
            }

            if (bedExists) {
                final Location teamSpawnLocation = iTeam.getTeamSpawnLocation(playerWorld);
                player.teleport(teamSpawnLocation);
            } else {
                iTeam.removePlayer(player);
                final Location spectatorLocation = BEDWARS_CONFIG.getSpectatorLocation(playerWorld);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(spectatorLocation);
                handleTeamElimination(iTeam, onlinePlayers);
            }
        });
    }

    @SuppressWarnings("all")
    private void handleTeamElimination(@NonNull ITeam team,
                                       @NonNull Collection<? extends Player> players) {
        final Set<Player> teamPlayers = team.getPlayers();

        if (players.size() > 0) return;

        for (Player player : players) {
            player.sendPlainMessage(Message.PREFIX + team.getTeamName() + "§c was§4 eliminated§c!");
        }

        final List<ITeam> aliveTeams = Arrays.stream(BEDWARS_TEAMS)
                .map(BedWarsTeam::getTeam)
                .filter(iTeam -> iTeam.getPlayers().size() > 0)
                .toList();

        if (aliveTeams.size() == 1) {
            final ITeam winningTeam = aliveTeams.get(0);
            final String winningTeamName = winningTeam.getTeamName();

            for (Player player : players) {
                player.sendPlainMessage(Message.PREFIX + winningTeamName + "§a has won the game!");
                final Location playerLocation = player.getLocation();
                player.playSound(playerLocation, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.0f, 1.0f);
            }

            GAME_ASSETS.setGameState(GameState.ENDING);
            ENDING_COUNTDOWN_SCHEDULER.start();
        }
        //TODO: Update Scoreboard
    }
}
