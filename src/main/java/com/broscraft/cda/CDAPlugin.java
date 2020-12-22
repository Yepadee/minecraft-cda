package com.broscraft.cda;

import java.util.Objects;

import com.broscraft.cda.commands.NewOrderCommand;
import com.broscraft.cda.commands.OpenMenuCommand;
import com.broscraft.cda.commands.SearchMarketCommand;
import com.broscraft.cda.gui.utils.OverviewIconsManager;
import com.broscraft.cda.repositories.ItemOverviewRepository;
import com.broscraft.cda.repositories.ItemRepository;
import com.broscraft.cda.repositories.OrderRepository;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Continuous Double Action Plugin
 *
 */
public class CDAPlugin extends JavaPlugin
{
    private OpenMenuCommand openMenuCommand;
    private SearchMarketCommand searchMarketCommand;
    private NewOrderCommand newOrderCommand;

    private ItemRepository itemRepository;
    private OrderRepository orderRepository;
    private ItemOverviewRepository itemOverviewRepository;
    private OverviewIconsManager overviewIconsManager;
    
    @Override
    public void onEnable() {
        itemRepository = new ItemRepository();
        orderRepository = new OrderRepository(itemRepository);

        overviewIconsManager = new OverviewIconsManager();
        itemOverviewRepository = new ItemOverviewRepository(overviewIconsManager);
        
        orderRepository.addObserver(itemOverviewRepository);

        openMenuCommand = new OpenMenuCommand(overviewIconsManager);
        searchMarketCommand = new SearchMarketCommand(overviewIconsManager);
        newOrderCommand = new NewOrderCommand(orderRepository);

        itemOverviewRepository.loadItemOverviews();
        
        
        getLogger().info("Enabled CDA!");
        this.setupCommands();
    }

    public void setupCommands() {
        Objects.requireNonNull(this.getCommand("market")).setExecutor(openMenuCommand);
        Objects.requireNonNull(this.getCommand("searchmarket")).setExecutor(searchMarketCommand);
        Objects.requireNonNull(this.getCommand("neworder")).setExecutor(newOrderCommand);
    }
}
