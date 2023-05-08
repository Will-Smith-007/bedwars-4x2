package de.will_smith_007.bedwars.listeners.game.interfaces;

import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * This interface is used in all classes which needs to handle the player death.
 * It also handles the team elimination of the player, if he was the last player alive from the team.
 */
public interface IDeathHandler {

    /**
     * Can be used to handle the player death.
     *
     * @param player Player which should be handled.
     * @param iTeam  Team from the player.
     */
    void handlePlayerDeath(@NonNull Player player, @NonNull ITeam iTeam);
}
