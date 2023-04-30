package de.will_smith_007.bedwars.teams.helper.interfaces;

import de.will_smith_007.bedwars.teams.enums.Team;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface ITeamHelper {

    void removeBedWarsTeam(@NonNull Player player);

    void selectBedWarsTeam(@NonNull Player player);

    boolean setBedWarsTeam(@NonNull Player player, @NonNull Team team);

    boolean canTeamJoined(@NonNull Team team);

    Optional<ITeam> getTeam(@NonNull Player player);
}
