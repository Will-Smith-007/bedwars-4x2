package de.will_smith_007.bedwars.listeners.game;

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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Optional;

public class EntityDamageAndDeathListener implements Listener, IDeathHandler {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;
    private final BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();
    private final IScoreboardManager SCOREBOARD_MANAGER;

    public EntityDamageAndDeathListener(@NonNull GameAssets gameAssets,
                                        @NonNull ITeamHelper teamHelper,
                                        @NonNull IScoreboardManager scoreboardManager) {
        GAME_ASSETS = gameAssets;
        TEAM_HELPER = teamHelper;
        SCOREBOARD_MANAGER = scoreboardManager;
    }

    @EventHandler
    public void onPlayerDeath(@NonNull PlayerDeathEvent playerDeathEvent) {
        playerDeathEvent.setCancelled(true);
        playerDeathEvent.deathMessage(null);

        final Player player = playerDeathEvent.getPlayer();
        final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        final Scoreboard scoreboard = player.getScoreboard();
        final Team playerTeam = scoreboard.getPlayerTeam(player);

        if (playerTeam == null) return;

        final TextColor textColor = playerTeam.color();
        final String playerName = player.getName();
        final String prefix = Message.PREFIX.toString();

        for (Player onlinePlayer : onlinePlayers) {
            onlinePlayer.sendMessage(Component.text(prefix)
                    .append(Component.text(playerName).color(textColor))
                    .append(Component.text("§7 died.")));
        }
    }

    @EventHandler
    public void onEntityDamage(@NonNull EntityDamageEvent entityDamageEvent) {
        final Entity entity = entityDamageEvent.getEntity();
        final GameState gameState = GAME_ASSETS.getGameState();

        if (!(entity instanceof final Player player)) return;

        if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        switch (gameState) {
            case LOBBY, ENDING -> entityDamageEvent.setCancelled(true);
            case INGAME, PROTECTION -> {
                final double damage = entityDamageEvent.getDamage();
                final double playerHealth = player.getHealth();

                if (damage < playerHealth) return;

                final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);

                optionalITeam.ifPresent(iTeam -> handlePlayerDeath(player, iTeam));
            }
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
