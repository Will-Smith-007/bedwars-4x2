package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Optional;

public class TeamDamageListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;
    private final IScoreboardManager SCOREBOARD_MANAGER;
    private final BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();

    public TeamDamageListener(@NonNull GameAssets gameAssets,
                              @NonNull ITeamHelper teamHelper,
                              @NonNull IScoreboardManager scoreboardManager) {
        GAME_ASSETS = gameAssets;
        TEAM_HELPER = teamHelper;
        SCOREBOARD_MANAGER = scoreboardManager;
    }

    @EventHandler
    public void onTeamDamage(@NonNull EntityDamageByEntityEvent entityDamageByEntityEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();

        if (gameState == GameState.LOBBY) {
            entityDamageByEntityEvent.setCancelled(true);
            return;
        }

        final Entity damageEntity = entityDamageByEntityEvent.getDamager();
        final Entity victimEntity = entityDamageByEntityEvent.getEntity();

        if (damageEntity instanceof final Player damagePlayer &&
                victimEntity instanceof final Player victimPlayer) {

            if (damagePlayer.getGameMode() == GameMode.SPECTATOR) {
                entityDamageByEntityEvent.setCancelled(true);
                return;
            }

            final Optional<ITeam> optionalDamagePlayerTeam = TEAM_HELPER.getTeam(damagePlayer);
            final Optional<ITeam> optionalVictimPlayerTeam = TEAM_HELPER.getTeam(victimPlayer);

            if (optionalDamagePlayerTeam.isEmpty()) return;
            if (optionalVictimPlayerTeam.isEmpty()) return;

            final ITeam damagePlayerTeam = optionalDamagePlayerTeam.get();
            final ITeam victimPlayerTeam = optionalVictimPlayerTeam.get();

            if (damagePlayerTeam == victimPlayerTeam) {
                entityDamageByEntityEvent.setCancelled(true);
            } else {
                final double dealtDamage = entityDamageByEntityEvent.getDamage();
                final double victimHealth = victimPlayer.getHealth();

                if (dealtDamage < victimHealth) return;

                handlePlayerDeath(victimPlayer, victimPlayerTeam);

                final Location damagePlayerLocation = damagePlayer.getLocation();
                final Scoreboard scoreboard = victimPlayer.getScoreboard();
                final Team playerTeam = scoreboard.getPlayerTeam(victimPlayer);

                if (playerTeam == null) return;

                final TextColor textColor = playerTeam.color();
                final String victimPlayerName = victimPlayer.getName();

                damagePlayer.sendMessage(Component.text(Message.PREFIX + "§aYou killed ")
                        .append(Component.text(victimPlayerName).color(textColor))
                        .append(Component.text("§a.")));
                damagePlayer.playSound(damagePlayerLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        } else {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }

    private void handlePlayerDeath(@NonNull Player player, @NonNull ITeam iTeam) {
        final World playerWorld = player.getWorld();
        final boolean bedExists = iTeam.bedExists();
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        player.getInventory().clear();

        if (bedExists) {
            final Location teamSpawnLocation = iTeam.getTeamSpawnLocation(playerWorld);
            player.teleport(teamSpawnLocation);
        } else {
            iTeam.removePlayer(player);
            final Location spectatorLocation = BED_WARS_CONFIG.getSpectatorLocation(playerWorld);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(spectatorLocation);
            TEAM_HELPER.handleTeamElimination(iTeam, players);

            players.forEach(SCOREBOARD_MANAGER::updateScoreboard);
            //TODO: Hide from other game players
        }
    }
}
