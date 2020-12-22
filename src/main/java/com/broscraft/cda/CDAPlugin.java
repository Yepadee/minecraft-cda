package com.broscraft.cda;

import java.util.Objects;

import com.broscraft.cda.commands.NewOrderCommand;
import com.broscraft.cda.commands.OpenMenuCommand;
import com.broscraft.cda.commands.SearchMarketCommand;
import com.broscraft.cda.gui.MarketGui;
import com.broscraft.cda.gui.utils.OverviewIconsManager;
import com.broscraft.cda.repositories.ItemOverviewRepository;
import com.broscraft.cda.repositories.ItemRepository;
import com.broscraft.cda.repositories.OrderRepository;

import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

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

    private MarketGui marketGui;

    private static TaskChainFactory taskChainFactory;
    
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }
    
    @Override
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        
        itemRepository = new ItemRepository();
        orderRepository = new OrderRepository(itemRepository);

        overviewIconsManager = new OverviewIconsManager();
        itemOverviewRepository = new ItemOverviewRepository(overviewIconsManager);
        
        orderRepository.addObserver(itemOverviewRepository);

        marketGui = new MarketGui(overviewIconsManager);

        openMenuCommand = new OpenMenuCommand(marketGui);
        searchMarketCommand = new SearchMarketCommand(marketGui);
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
