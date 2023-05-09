package de.will_smith_007.bedwars.scoreboard.interfaces;

import lombok.NonNull;
import org.bukkit.entity.Player;

public interface IScoreboardManager {

    /**
     * Creates a scoreboard for the specified {@link Player}.
     *
     * @param player The player to which the scoreboard should be set.
     */
    void createScoreboard(@NonNull Player player);

    /**
     * Updates the scoreboard for the specified {@link Player}.
     *
     * @param player The player from which the scoreboard should be updated.
     */
    void updateScoreboard(@NonNull Player player);

    /**
     * Sets the tablist of the specified {@link Player}.
     *
     * @param player The player to which the tablist should be set.
     */
    void setTablist(@NonNull Player player);

    /**
     * Performs the {@link #createScoreboard(Player)} and {@link #setTablist(Player)}.
     *
     * @param player The player which should be affected by these methods.
     */
    void setScoreboardAndTablist(@NonNull Player player);
}
