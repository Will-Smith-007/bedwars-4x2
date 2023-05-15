package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.listeners.game.interfaces.IDeathHandler;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Optional;

/**
 * This {@link Listener} handles the {@link EntityDamageByEntityEvent} to handle player deaths
 * caused by an entity such as an arrow or another player.
 */
public class TeamAndPlayerDamageListener implements Listener, IDeathHandler {

    private final GameAssets gameAssets;
    private final ITeamHelper teamHelper;
    private final BedWarsConfig bedWarsConfig;
    private final IScoreboardManager scoreboardManager;
    private final String prefix = Message.PREFIX.toString();

    @Inject
    public TeamAndPlayerDamageListener(@NonNull GameAssets gameAssets,
                                       @NonNull ITeamHelper teamHelper,
                                       @NonNull IScoreboardManager scoreboardManager,
                                       @NonNull BedWarsConfig bedWarsConfig) {
        this.gameAssets = gameAssets;
        this.teamHelper = teamHelper;
        this.scoreboardManager = scoreboardManager;
        this.bedWarsConfig = bedWarsConfig;
    }

    @EventHandler
    public void onTeamDamage(@NonNull EntityDamageByEntityEvent entityDamageByEntityEvent) {
        final GameState gameState = gameAssets.getGameState();

        // If the game state is lobby, then this event must be cancelled
        if (gameState == GameState.LOBBY) {
            entityDamageByEntityEvent.setCancelled(true);
            return;
        }

        final Entity damageEntity = entityDamageByEntityEvent.getDamager();
        final Entity victimEntity = entityDamageByEntityEvent.getEntity();

        // If the victim entity is a villager, then this event must be cancelled
        if (victimEntity.getType() == EntityType.VILLAGER) {
            entityDamageByEntityEvent.setCancelled(true);
            return;
        }

        // If player damages another player
        if (damageEntity instanceof final Player damagePlayer &&
                victimEntity instanceof final Player victimPlayer) {

            // If damage player is in spectator mode (dead), then cancel the event
            if (damagePlayer.getGameMode() == GameMode.SPECTATOR) {
                entityDamageByEntityEvent.setCancelled(true);
                return;
            }

            // Gets the teams of the players which are involved in this event
            final Optional<ITeam> optionalDamagePlayerTeam = teamHelper.getTeam(damagePlayer);
            final Optional<ITeam> optionalVictimPlayerTeam = teamHelper.getTeam(victimPlayer);

            if (optionalDamagePlayerTeam.isEmpty() || optionalVictimPlayerTeam.isEmpty()) return;

            final ITeam damagePlayerITeam = optionalDamagePlayerTeam.get();
            final ITeam victimPlayerITeam = optionalVictimPlayerTeam.get();

            // If these players are in the same team, cancel the event
            if (damagePlayerITeam == victimPlayerITeam) {
                entityDamageByEntityEvent.setCancelled(true);
            } else {
                final double dealtDamage = entityDamageByEntityEvent.getDamage();
                final double victimHealth = victimPlayer.getHealth();

                // If damage isn't causing death, return
                if (dealtDamage < victimHealth) return;

                entityDamageByEntityEvent.setDamage(0.00d);
                handlePlayerDeath(victimPlayer, victimPlayerITeam);

                final Location damagePlayerLocation = damagePlayer.getLocation();
                final Scoreboard scoreboard = victimPlayer.getScoreboard();
                final Team victimPlayerTeam = scoreboard.getPlayerTeam(victimPlayer);
                final Team damagePlayerTeam = scoreboard.getPlayerTeam(damagePlayer);

                if (victimPlayerTeam == null || damagePlayerTeam == null) return;

                final TextColor victimTextColor = victimPlayerTeam.color();
                final TextColor damageTextColor = damagePlayerTeam.color();
                final String victimPlayerName = victimPlayer.getName();
                final String damagePlayerName = damagePlayer.getName();

                // Player kill handling
                damagePlayer.sendMessage(Component.text(Message.PREFIX + "§aYou killed ")
                        .append(Component.text(victimPlayerName).color(victimTextColor))
                        .append(Component.text("§a.")));
                damagePlayer.playSound(damagePlayerLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage(Component.text(prefix)
                            .append(Component.text(victimPlayerName).color(victimTextColor))
                            .append(Component.text(" §7was killed by "))
                            .append(Component.text(damagePlayerName).color(damageTextColor)));
                }

                entityDamageByEntityEvent.setCancelled(true);
            }
            // If the damage entity is an arrow
        } else if (damageEntity instanceof final Arrow arrow) {
            handleArrowDamage(arrow, victimEntity, entityDamageByEntityEvent);
        }
    }

    @Override
    public void handlePlayerDeath(@NonNull Player player, @NonNull ITeam iTeam) {
        final World playerWorld = player.getWorld();
        final boolean bedExists = iTeam.bedExists();
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        player.getInventory().clear();

        if (bedExists) {
            player.setHealth(20.0d);
            player.setFoodLevel(20);
            final Location teamSpawnLocation = iTeam.getTeamSpawnLocation(playerWorld, bedWarsConfig);
            player.teleport(teamSpawnLocation);
        } else {
            iTeam.removePlayer(player);
            final Location spectatorLocation = bedWarsConfig.getSpectatorLocation(playerWorld);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(spectatorLocation);
            teamHelper.handleTeamElimination(iTeam, players);

            players.forEach(scoreboardManager::updateScoreboard);
        }
    }

    /**
     * Handles the damage caused by an arrow entity.
     *
     * @param arrow                     Arrow entity which was shot.
     * @param victimEntity              Entity which was hit by the arrow entity.
     * @param entityDamageByEntityEvent The event which was called on entity by entity damage event.
     */
    private void handleArrowDamage(@NonNull Arrow arrow,
                                   @NonNull Entity victimEntity,
                                   @NonNull EntityDamageByEntityEvent entityDamageByEntityEvent) {
        final ProjectileSource projectileSource = arrow.getShooter();

        // If the shooter wasn't a player, return
        if (!(projectileSource instanceof final Player damagePlayer)) return;
        // If the entity which was hit by the arrow wasn't a player, return
        if (!(victimEntity instanceof final Player victimPlayer)) {
            entityDamageByEntityEvent.setCancelled(true);
            return;
        }

        final double dealtDamage = entityDamageByEntityEvent.getDamage();
        final double victimHealth = victimPlayer.getHealth();

        // If the arrow isn't causing a death, return
        if (dealtDamage < victimHealth) return;

        // Gets the teams of the players which are involved in this event
        final Optional<ITeam> optionalDamagePlayerTeam = teamHelper.getTeam(damagePlayer);
        final Optional<ITeam> optionalVictimPlayerTeam = teamHelper.getTeam(victimPlayer);

        if (optionalDamagePlayerTeam.isEmpty() || optionalVictimPlayerTeam.isEmpty()) return;

        final ITeam damagePlayerITeam = optionalDamagePlayerTeam.get();
        final ITeam victimPlayerITeam = optionalVictimPlayerTeam.get();

        // If the players are in the same team, cancel the event and return
        if (damagePlayerITeam == victimPlayerITeam) {
            entityDamageByEntityEvent.setCancelled(true);
            return;
        }

        // Set the damage to 0 because player can spawn with health lower than 20
        entityDamageByEntityEvent.setDamage(0.00d);
        handlePlayerDeath(victimPlayer, victimPlayerITeam);

        final Location damagePlayerLocation = damagePlayer.getLocation();
        final Scoreboard scoreboard = victimPlayer.getScoreboard();
        final Team victimPlayerTeam = scoreboard.getPlayerTeam(victimPlayer);
        final Team damagePlayerTeam = scoreboard.getPlayerTeam(damagePlayer);

        if (victimPlayerTeam == null || damagePlayerTeam == null) return;

        final TextColor victimTextColor = victimPlayerTeam.color();
        final TextColor damageTextColor = damagePlayerTeam.color();
        final String victimPlayerName = victimPlayer.getName();
        final String damagePlayerName = damagePlayer.getName();

        // Player kill handling caused by an arrow entity
        damagePlayer.sendMessage(Component.text(Message.PREFIX + "§aYou killed ")
                .append(Component.text(victimPlayerName).color(victimTextColor))
                .append(Component.text("§a.")));
        damagePlayer.playSound(damagePlayerLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(Component.text(prefix)
                    .append(Component.text(victimPlayerName).color(victimTextColor))
                    .append(Component.text(" §7was killed by "))
                    .append(Component.text(damagePlayerName).color(damageTextColor)));
        }

        // Event must be cancelled after this, otherwise the player gets knockback after respawn
        entityDamageByEntityEvent.setCancelled(true);
    }
}
