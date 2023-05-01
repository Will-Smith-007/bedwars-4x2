package de.will_smith_007.bedwars.scoreboard.interfaces;

import lombok.NonNull;
import org.bukkit.entity.Player;

public interface IScoreboardManager {

    void createScoreboard(@NonNull Player player);

    void updateScoreboard(@NonNull Player player);

    void setTablist(@NonNull Player player);

    void setScoreboardAndTablist(@NonNull Player player);
}
