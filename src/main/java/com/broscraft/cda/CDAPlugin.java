package com.broscraft.cda;

import java.util.Objects;

import com.broscraft.cda.commands.NewOrderCommand;
import com.broscraft.cda.commands.OpenMenuCommand;
import com.broscraft.cda.commands.SearchMarketCommand;
import com.broscraft.cda.gui.MarketGui;
import com.broscraft.cda.gui.utils.OverviewIconsManager;
import com.broscraft.cda.repositories.ItemRepository;
import com.broscraft.cda.repositories.OrderRepository;
import com.broscraft.cda.services.ItemOverviewService;
import com.broscraft.cda.services.ItemService;
import com.broscraft.cda.services.OrderService;

import org.bukkit.Bukkit;
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

    private ItemService itemService;
    private OrderService orderService;
    private ItemOverviewService itemOverviewService;
    private OverviewIconsManager overviewIconsManager;

    private MarketGui marketGui;

    private static TaskChainFactory taskChainFactory;
    
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    public static void runTask(Runnable r) {
        Bukkit.getServer().getScheduler().runTask(instance, r);
    }

    private static CDAPlugin instance;
    
    @Override
    public void onEnable() {
        instance = this;

        taskChainFactory = BukkitTaskChainFactory.create(this);
        
        overviewIconsManager = new OverviewIconsManager();

        itemRepository = new ItemRepository();
        orderRepository = new OrderRepository();

        itemService = new ItemService(itemRepository);
        itemOverviewService = new ItemOverviewService(itemService, overviewIconsManager);
        orderService = new OrderService(orderRepository, itemService, itemOverviewService);

        marketGui = new MarketGui(
            overviewIconsManager,
            orderService
        );

        openMenuCommand = new OpenMenuCommand(marketGui);
        searchMarketCommand = new SearchMarketCommand(marketGui);
        newOrderCommand = new NewOrderCommand(orderService, itemService, marketGui);
        
        getLogger().info("Enabled CDA!");
        this.setupCommands();
    }

    private void setupCommands() {
        Objects.requireNonNull(this.getCommand("market")).setExecutor(openMenuCommand);
        Objects.requireNonNull(this.getCommand("searchmarket")).setExecutor(searchMarketCommand);
        Objects.requireNonNull(this.getCommand("neworder")).setExecutor(newOrderCommand);
    }

}
