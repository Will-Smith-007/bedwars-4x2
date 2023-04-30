package de.will_smith_007.bedwars.teams.helper;

import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamHelper implements ITeamHelper {

    private final BedWarsTeam[] TEAMS = BedWarsTeam.values();

    @Override
    public void removeBedWarsTeam(@NonNull Player player) {
        getTeam(player).ifPresent(iTeam -> iTeam.removePlayer(player));
    }

    @Override
    public ITeam selectBedWarsTeam(@NonNull Player player) {
        final Optional<ITeam> optionalMinITeam = Arrays.stream(TEAMS)
                .map(BedWarsTeam::getTeam)
                .filter(iTeam -> iTeam.getPlayers().size() < 2)
                .min(Comparator.comparingInt(iTeam -> iTeam.getPlayers().size()));

        for (BedWarsTeam bedWarsTeam : TEAMS) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final Set<Player> teamPlayers = iTeam.getPlayers();

            if (teamPlayers.isEmpty()) {
                iTeam.addPlayer(player);
                return iTeam;
            }
        }

        optionalMinITeam.ifPresent(iTeam -> iTeam.addPlayer(player));
        return (optionalMinITeam.orElse(null));
    }

    @Override
    public boolean setBedWarsTeam(@NonNull Player player, @NonNull BedWarsTeam bedWarsTeam) {
        if (!canTeamJoined(bedWarsTeam)) return false;

        final ITeam iTeam = bedWarsTeam.getTeam();
        iTeam.addPlayer(player);

        return true;
    }

    @Override
    public boolean canTeamJoined(@NonNull BedWarsTeam team) {
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
                .map(BedWarsTeam::getTeam)
                .filter(iTeam -> iTeam.getPlayers().contains(player))
                .findAny();
    }
}
