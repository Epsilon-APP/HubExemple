package fr.epsilon.hub.inv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EpsilonInventory {
    private final static Map<UUID, EpsilonInventory> inventoryMap = new HashMap<>();

    private final Inventory inventory;

    private final InventoryContents inventoryContents;
    private final InventoryFactory factory;

    private final boolean itemCanMove;
    private final boolean closable;

    public EpsilonInventory(String title, int size, InventoryFactory factory, boolean itemCanMove, boolean closable) {
        this.inventory = Bukkit.createInventory(null, size, title);

        this.inventoryContents = new InventoryContents(inventory);
        this.factory = factory;

        this.itemCanMove = itemCanMove;
        this.closable = closable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void init(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    public void open(Player player) {
        UUID uuid = player.getUniqueId();

        inventoryMap.put(player.getUniqueId(), this);
        inventoryMap.get(uuid).handleOpen(player);
        player.openInventory(inventory);
    }

    public void close(Player player) {
        UUID uuid = player.getUniqueId();

        if (inventoryMap.containsKey(uuid))
            inventoryMap.get(uuid).handleClose(player);

        inventoryMap.remove(uuid);
        player.closeInventory();
    }

    public InventoryContents get() {
        return inventoryContents;
    }

    protected void handleClick(InventoryClickEvent event) {
        InventoryContents.ItemSlots itemSlots = inventoryContents.getItem(event.getSlot());

        if (itemSlots != null && itemSlots.getCallback() != null) {
            itemSlots.getCallback().accept(inventoryContents);
        }
    }

    protected void handleOpen(Player player) {
        if (factory != null)
            factory.init(player, inventoryContents, inventoryContents.getFillPattern(), inventoryContents.getPagination());
    }

    protected void handleClose(Player player) {
        if (factory != null)
            factory.close(player);
    }

    public static class Builder {
        private String title;
        private int size;
        private InventoryFactory factory;

        private boolean itemCanMove;
        private boolean closable;

        public Builder() {
            this.title = "Ozyris Inventory";
            this.size = 9;
            this.factory = null;

            this.itemCanMove = false;
            this.closable = true;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder height(int height) {
            this.size = height * 9;
            return this;
        }

        public Builder factory(InventoryFactory factory) {
            this.factory = factory;
            return this;
        }

        public Builder itemCanMove(boolean canMove) {
            this.itemCanMove = canMove;
            return this;
        }

        public Builder closeable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public EpsilonInventory create() {
            return new EpsilonInventory(title, size, factory, itemCanMove, closable);
        }
    }

    private static class InventoryListener implements Listener {
        private Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            inventoryMap.remove(event.getPlayer().getUniqueId());
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            UUID uuid = event.getWhoClicked().getUniqueId();

            if (inventoryMap.containsKey(uuid)) {
                EpsilonInventory epsilonInventory = inventoryMap.get(uuid);

                epsilonInventory.handleClick(event);

                if (!epsilonInventory.itemCanMove)
                    event.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            Player player = (Player) event.getPlayer();
            UUID uuid = player.getUniqueId();

            if (inventoryMap.containsKey(uuid)) {
                EpsilonInventory epsilonInventory = inventoryMap.get(uuid);

                if (!epsilonInventory.closable) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (player.isValid())
                            player.openInventory(epsilonInventory.inventory);
                    }, 5);
                }
            }
        }
    }
}
