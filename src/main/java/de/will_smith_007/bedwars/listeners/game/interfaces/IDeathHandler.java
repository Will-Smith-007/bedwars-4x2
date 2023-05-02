package de.will_smith_007.bedwars.listeners.game.interfaces;

import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.entity.Player;

public interface IDeathHandler {

    void handlePlayerDeath(@NonNull Player player, @NonNull ITeam iTeam);
}
