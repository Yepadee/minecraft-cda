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
        setUpTables();

        instance = this;

        taskChainFactory = BukkitTaskChainFactory.create(this);
        
        iconsManager = new IconsManager();

        itemRepository = new ItemRepository();
        orderRepository = new OrderRepository();

        itemService = new ItemService(itemRepository);
        itemOverviewService = new ItemOverviewService(itemService, iconsManager);
        orderService = new OrderService(orderRepository, itemService, itemOverviewService);

        marketGui = new MarketGui(
            iconsManager,
            orderService
        );

        openMenuCommand = new OpenMenuCommand(marketGui);
        searchMarketCommand = new SearchMarketCommand(marketGui);
        newOrderCommand = new NewOrderCommand(marketGui);
        myOrdersCommand = new MyOrdersCommand(marketGui);
        //TODO: stop negative input
        getLogger().info("Enabled CDA!");
        this.setupCommands();
    }

    @Override
    public void onDisable() {
        DB.close();
    }

    private void setupCommands() {
        Objects.requireNonNull(this.getCommand("market")).setExecutor(openMenuCommand);
        Objects.requireNonNull(this.getCommand("searchmarket")).setExecutor(searchMarketCommand);
        Objects.requireNonNull(this.getCommand("neworder")).setExecutor(newOrderCommand);
        Objects.requireNonNull(this.getCommand("myorders")).setExecutor(myOrdersCommand);
    }

    private void setUpTables() {
        this.getDataFolder().mkdir();
        DB.init(this.getDataFolder());

        if (!DB.tableExists("Orders")) {
            DB.update(
                "CREATE TABLE Orders " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " type CHAR(3) NOT NULL, " + 
                " player_uuid CHAR(36) NOT NULL, " + 
                " item_id INTEGER NOT NULL, " + 
                " price DECIMAL(8,2) NOT NULL, " + 
                " quantity INTEGER NOT NULL, " +
                " quantity_filled INTEGER DEFAULT 0, " +
                " quantity_uncollected INTEGER DEFAULT 0, " +
                " created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                " FOREIGN KEY (item_id) REFERENCES Items(id) " +
                ")"
            );
        }

        if (!DB.tableExists("Items")) {
            DB.update(
                "CREATE TABLE Items " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " material VARCHAR(64), " + 
                " potion_type VARCHAR(64), " + 
                " is_upgraded TINYINT, " + 
                " is_extended TINYINT" + 
                ")"
            );
        }

        if (!DB.tableExists("Enchantments")) {
            DB.update(
                "CREATE TABLE Enchantments " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " item_id INTEGER NOT NULL, " +
                " enchantment VARCHAR(64) NOT NULL, " + 
                " level TINYINT NOT NULL, " + 

                " FOREIGN KEY (item_id) REFERENCES Items(id) " +
                ")"
            );
        }

        if (!DB.tableExists("Transactions")) {
            
        }

        DB.commit();
    }
}
