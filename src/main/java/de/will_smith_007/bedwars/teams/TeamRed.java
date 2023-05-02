package de.will_smith_007.bedwars.teams;

import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class TeamRed implements ITeam {

    private static TeamRed instance;
    private final Set<Player> TEAM_PLAYERS = new HashSet<>();
    private boolean bedExists = true;

    @Override
    public void addPlayer(@NonNull Player player) {
        TEAM_PLAYERS.add(player);
    }

    @Override
    public void removePlayer(@NonNull Player player) {
        TEAM_PLAYERS.remove(player);
    }

    @Override
    public void setBedExists(boolean bedExists) {
        this.bedExists = bedExists;
    }

    @Override
    public @NonNull Team getTeam(@NonNull Scoreboard scoreboard) {
        Team team;
        if ((team = scoreboard.getTeam("Red")) == null) {
            team = scoreboard.registerNewTeam("Red");
        }
        team.color(NamedTextColor.RED);
        return team;
    }

    @Override
    public Location getTeamSpawnLocation(@NonNull World world) {
        return BED_WARS_CONFIG.getTeamSpawnLocation(BedWarsTeam.RED, world);
    }

    @Override
    public boolean bedExists() {
        return bedExists;
    }

    @Override
    public @NonNull String getTeamName() {
        return "§c" + BedWarsTeam.RED.getTeamName();
    }

    @Override
    public @NonNull Set<Player> getPlayers() {
        return TEAM_PLAYERS;
    }

    public static @NonNull ITeam getInstance() {
        return (instance == null ? (instance = new TeamRed()) : instance);
    }
}
