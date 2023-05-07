package de.will_smith_007.bedwars.events;

import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BedBreakEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter
    private final Location bedLocation;
    @Getter
    private final Player player;
    @Getter
    private final ITeam bedTeam;

    public BedBreakEvent(@NonNull Player player, @NonNull Location bedLocation, @NonNull ITeam bedTeam) {
        this.bedLocation = bedLocation;
        this.player = player;
        this.bedTeam = bedTeam;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
