package fr.epsilon.hub.inv;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryContents {
    private final List<ItemSlots> itemList;
    private final InventoryPattern inventoryPattern;
    private final InventoryPagination inventoryPagination;
    private final Inventory inventory;

    public InventoryContents(Inventory inventory) {
        this.itemList = new ArrayList<>();
        this.inventoryPattern = new InventoryPattern(this);
        this.inventoryPagination = new InventoryPagination(this);
        this.inventory = inventory;
    }

    public void add(ItemStack item, Consumer<InventoryContents> callback) {
        for (int i = 0; i <= itemList.size(); i++) {
            if (getItem(i) == null) {
                set(item, i, callback);
                return;
            }
        }
    }

    public void add(ItemStack item) {
        for (int i = 0; i <= itemList.size(); i++) {
            if (getItem(i) == null) {
                set(item, i);
                return;
            }
        }
    }

    public void set(ItemStack item, int slot, Consumer<InventoryContents> callback) {
        ItemSlots itemSlots = new ItemSlots(item, slot % 9, slot / 9, callback);
        itemList.add(itemSlots);
        inventory.setItem(itemSlots.getRealSlot(), itemSlots.getItem());
    }

    public void set(ItemStack item, int slot) {
        ItemSlots itemSlots = new ItemSlots(item, slot % 9, slot / 9);
        itemList.add(itemSlots);
        inventory.setItem(itemSlots.getRealSlot(), itemSlots.getItem());
    }

    public void set(ItemStack item, int x, int y, Consumer<InventoryContents> callback) {
        ItemSlots itemSlots = new ItemSlots(item, x, y, callback);
        itemList.add(itemSlots);
        inventory.setItem(itemSlots.getRealSlot(), itemSlots.getItem());
    }

    public void set(ItemStack item, int x, int y) {
        ItemSlots itemSlots = new ItemSlots(item, x, y);
        itemList.add(itemSlots);
        inventory.setItem(itemSlots.getRealSlot(), itemSlots.getItem());
    }

    public void remove(int slot) {
        for (ItemSlots itemSlots : itemList) {
            if (itemSlots.getRealSlot() == slot) {
                inventory.remove(itemSlots.getItem());
                itemList.remove(itemSlots);
            }
        }
    }

    public void remove(int x, int y) {
        for (ItemSlots itemSlots : itemList) {
            if (itemSlots.getX() == x && itemSlots.getY() == y) {
                inventory.remove(itemSlots.getItem());
                itemList.remove(itemSlots);
            }
        }
    }

    public ItemSlots getItem(int slot) {
        for (ItemSlots itemSlots : itemList) {
            if (itemSlots.getRealSlot() == slot)
                return itemSlots;
        }

        return null;
    }

    public ItemSlots getItem(int x, int y) {
        for (ItemSlots itemSlots : itemList) {
            if (itemSlots.getX() == x && itemSlots.getY() == y)
                return itemSlots;
        }

        return null;
    }

    public List<ItemSlots> getItems() {
        return itemList;
    }

    public int getSizeContent() {
        return itemList.size();
    }

    public int getInventorySize() {
        return inventory.getSize();
    }

    public int getHeight() {
        return inventory.getSize() / 9;
    }

    public InventoryPattern getFillPattern() {
        return inventoryPattern;
    }

    public InventoryPagination getPagination() {
        return inventoryPagination;
    }

    public static class ItemSlots {
        private ItemStack item;
        private int x;
        private int y;

        private Consumer<InventoryContents> callback;

        public ItemSlots(ItemStack item, int x, int y, Consumer<InventoryContents> callback) {
            this.item = item;
            this.x = x;
            this.y = y;

            this.callback = callback;
        }

        public ItemSlots(ItemStack item, int x, int y) {
            this.item = item;
            this.x = x;
            this.y = y;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getRealSlot() {
            return y * 9 + x;
        }

        public Consumer<InventoryContents> getCallback() {
            return callback;
        }
    }
}
