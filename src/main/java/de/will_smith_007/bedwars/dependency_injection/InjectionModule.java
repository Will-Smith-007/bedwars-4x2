package de.will_smith_007.bedwars.dependency_injection;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import de.will_smith_007.bedwars.inventories.game.BedWarsShopInventory;
import de.will_smith_007.bedwars.inventories.interfaces.IBedWarsInventory;
import de.will_smith_007.bedwars.inventories.lobby.TeamSelectorInventory;
import de.will_smith_007.bedwars.lobby_countdown.LobbyCountdownHelper;
import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import de.will_smith_007.bedwars.scoreboard.ScoreboardManager;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.shop.parser.ShopParser;
import de.will_smith_007.bedwars.shop.parser.interfaces.IShopParser;
import de.will_smith_007.bedwars.spawner.provider.SpawnerProvider;
import de.will_smith_007.bedwars.spawner.provider.interfaces.ISpawnerProvider;
import de.will_smith_007.bedwars.teams.helper.TeamHelper;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

public class InjectionModule extends AbstractModule {

    private final JavaPlugin javaPlugin;

    public InjectionModule(@NonNull JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(javaPlugin);
        bind(ISpawnerProvider.class).to(SpawnerProvider.class);
        bind(ITeamHelper.class).to(TeamHelper.class);
        bind(IScoreboardManager.class).to(ScoreboardManager.class);
        bind(ILobbyCountdownHelper.class).to(LobbyCountdownHelper.class);
        bind(IBedWarsInventory.class).annotatedWith(Names.named("ShopInventory")).to(BedWarsShopInventory.class);
        bind(IBedWarsInventory.class).annotatedWith(Names.named("TeamSelectorInventory")).to(TeamSelectorInventory.class);
        bind(IShopParser.class).to(ShopParser.class);
    }
}
