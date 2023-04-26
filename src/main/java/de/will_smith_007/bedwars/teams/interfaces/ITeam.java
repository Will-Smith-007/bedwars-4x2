package de.will_smith_007.bedwars.teams.interfaces;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public interface ITeam {

    void addPlayer(@NonNull Player player);

    void removePlayer(@NonNull Player player);

    @NonNull Team getTeam(@NonNull Scoreboard scoreboard);

    @NonNull Set<Player> getPlayers();
}
