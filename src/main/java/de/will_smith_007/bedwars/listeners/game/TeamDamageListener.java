package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

public class TeamDamageListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;

    public TeamDamageListener(@NonNull GameAssets gameAssets,
                              @NonNull ITeamHelper teamHelper) {
        GAME_ASSETS = gameAssets;
        TEAM_HELPER = teamHelper;
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
                final Component displayName = victimPlayer.displayName();
                final Location damagePlayerLocation = damagePlayer.getLocation();

                damagePlayer.sendMessage(Component.text(Message.PREFIX + "§aYou killed ")
                        .append(displayName)
                        .append(Component.text("§a.")));
                damagePlayer.playSound(damagePlayerLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        } else {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }
}
