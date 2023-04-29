package de.will_smith_007.bedwars.teams.helper;

import de.will_smith_007.bedwars.enums.Team;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamHelper implements ITeamHelper {

    private final Team[] TEAMS = Team.values();

    @Override
    public void removeBedWarsTeam(@NonNull Player player) {
        getTeam(player).ifPresent(iTeam -> iTeam.removePlayer(player));
    }

    @Override
    public void selectBedWarsTeam(@NonNull Player player) {
        final Optional<ITeam> optionalMinITeam = Arrays.stream(TEAMS)
                .map(Team::getTeam)
                .filter(iTeam -> iTeam.getPlayers().size() < 2)
                .min(Comparator.comparingInt(iTeam -> iTeam.getPlayers().size()));

        for (Team team : TEAMS) {
            final ITeam iTeam = team.getTeam();
            final Set<Player> teamPlayers = iTeam.getPlayers();

            if (teamPlayers.isEmpty()) {
                iTeam.addPlayer(player);
                return;
            }
        }

        optionalMinITeam.ifPresent(iTeam -> iTeam.addPlayer(player));
    }

    @Override
    public boolean setBedWarsTeam(@NonNull Player player, @NonNull Team team) {
        if (!canTeamJoined(team)) return false;
        final ITeam iTeam = team.getTeam();
        iTeam.addPlayer(player);
        return true;
    }

    @Override
    public boolean canTeamJoined(@NonNull Team team) {
        final ITeam iTeam = team.getTeam();
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int minPlayersPerTeam = (players.size() / TEAMS.length);
        final Set<Player> teamPlayers = iTeam.getPlayers();

        if (teamPlayers.isEmpty()) return true;

        return (teamPlayers.size() < minPlayersPerTeam);
    }

    @Override
    public Optional<ITeam> getTeam(@NonNull Player player) {
        return Arrays.stream(TEAMS)
                .map(Team::getTeam)
                .filter(iTeam -> iTeam.getPlayers().contains(player))
                .findAny();
    }
}
