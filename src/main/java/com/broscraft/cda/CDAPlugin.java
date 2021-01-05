package com.broscraft.cda;

import java.util.Objects;

import com.broscraft.cda.commands.MyOrdersCommand;
import com.broscraft.cda.commands.NewOrderCommand;
import com.broscraft.cda.commands.OpenMenuCommand;
import com.broscraft.cda.commands.SearchMarketCommand;
import com.broscraft.cda.database.DB;
import com.broscraft.cda.gui.MarketGui;
import com.broscraft.cda.gui.utils.IconsManager;
import com.broscraft.cda.repositories.ItemRepository;
import com.broscraft.cda.repositories.OrderRepository;
import com.broscraft.cda.services.ItemOverviewService;
import com.broscraft.cda.services.ItemService;
import com.broscraft.cda.services.OrderService;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

/**
 * Continuous Double Action Plugin
 *
 */
public class CDAPlugin extends JavaPlugin {
    private FileConfiguration config = getConfig();

    private OpenMenuCommand openMenuCommand;
    private SearchMarketCommand searchMarketCommand;
    private NewOrderCommand newOrderCommand;
    private MyOrdersCommand myOrdersCommand;

    private ItemRepository itemRepository;
    private OrderRepository orderRepository;

    private ItemService itemService;
    private OrderService orderService;
    private ItemOverviewService itemOverviewService;
    private IconsManager iconsManager;

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
        setUpConfig();
        setUpDB();

        instance = this;

        taskChainFactory = BukkitTaskChainFactory.create(this);

        iconsManager = new IconsManager();

        itemRepository = new ItemRepository();
        orderRepository = new OrderRepository();

        itemService = new ItemService(itemRepository);
        ItemUtils.init(itemService);
        itemOverviewService = new ItemOverviewService(itemService, iconsManager);
        orderService = new OrderService(orderRepository, itemService, itemOverviewService);

        marketGui = new MarketGui(iconsManager, orderService, itemService);

        openMenuCommand = new OpenMenuCommand(marketGui);
        searchMarketCommand = new SearchMarketCommand(marketGui);
        newOrderCommand = new NewOrderCommand(marketGui);
        myOrdersCommand = new MyOrdersCommand(marketGui);

        setupCommands();

        getLogger().info("Enabled CDA!");
    }

    @Override
    public void onDisable() {
        DB.close();
        getLogger().info("Disabled CDA!");
    }

    private void setupCommands() {
        Objects.requireNonNull(this.getCommand("market")).setExecutor(openMenuCommand);
        Objects.requireNonNull(this.getCommand("searchmarket")).setExecutor(searchMarketCommand);
        Objects.requireNonNull(this.getCommand("neworder")).setExecutor(newOrderCommand);
        Objects.requireNonNull(this.getCommand("myorders")).setExecutor(myOrdersCommand);
    }

    private void setUpDB() {
        DB.init(config);
    }

    private void setUpConfig() {
        config.addDefault("db.driverclassname", "org.mariadb.jdbc.Driver");
        config.addDefault("db.dialect", "jdbc:mariadb");
        config.addDefault("db.host", "localhost");
        config.addDefault("db.port", 3306);
        config.addDefault("db.database", "MinecraftCDA");
        config.addDefault("db.user", "root");
        config.addDefault("db.password", null);

        config.addDefault("db.pool.maxlifetime", 60000);
        config.addDefault("db.pool.minpoolsize", 0);
        config.addDefault("db.pool.maxpoolsize", 1);
        config.addDefault("db.pool.idletimeout", 5000);

        config.options().copyDefaults(true);
        saveConfig();
    }
}
