package de.will_smith_007.bedwars.game_config;

import lombok.NonNull;
import org.bukkit.World;

/**
 * Currently this class only stores the world in which the players play.
 *
 * @param gameWorld World in which the players play.
 */
public record GameConfiguration(@NonNull World gameWorld) {
}
