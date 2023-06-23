package fr.epsilon.hub.inv;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryPagination {
    private int currentPage;
    private int[] availableSlots;

    private final List<Item> itemList;
    private final InventoryContents inventory;

    private ItemPage before;
    private ItemPage next;

    public InventoryPagination(InventoryContents inventory) {
        this.currentPage = -1;
        this.itemList = new ArrayList<>();
        this.inventory = inventory;
    }

    public void availableSlots(int[] slots) {
        this.availableSlots = slots;
    }

    public void page(int index) {
        if (index > 0) {
            inventory.set(before.itemStack, before.slot, before.callback);
        }else {
            inventory.set(new ItemStack(Material.AIR), before.slot);
        }

        if (index < itemList.size() / availableSlots.length) {
            inventory.set(next.itemStack, next.slot, next.callback);
        }else {
            inventory.set(new ItemStack(Material.AIR), next.slot);
        }

        for (int i = index * availableSlots.length; i < (index + 1) * availableSlots.length; i++) {
            if (i < itemList.size()) {
                Item item = itemList.get(i);
                inventory.set(item.itemStack, availableSlots[i%availableSlots.length], item.callback);
            }else {
                inventory.set(new ItemStack(Material.AIR), availableSlots[i%availableSlots.length]);
            }
        }
    }

    public void init(ItemPage before, ItemPage next) {
        this.currentPage = 0;

        this.before = before;
        before.callback((inventory) -> beforePage());

        this.next = next;
        next.callback((inventory) -> nextPage());

        page(currentPage);
    }

    public void nextPage() {
        System.out.println("NEXT PAGE");

        if (currentPage < itemList.size() / availableSlots.length) {
            this.currentPage++;
            page(currentPage);
        }
    }

    public void beforePage() {
        System.out.println("BEFORE PAGE");

        if (currentPage > 0) {
            this.currentPage--;
            page(currentPage);
        }
    }

    public void add(ItemStack itemStack, Consumer<InventoryContents> callback) {
        itemList.add(new Item(itemStack, callback));
    }

    public void add(ItemStack itemStack) {
        itemList.add(new Item(itemStack));
    }

    public void remove(ItemStack itemStack) {
        itemList.removeIf(item -> item.itemStack.equals(itemStack));
    }

    public static class ItemPage {
        private ItemStack itemStack;
        private int slot;
        private Consumer<InventoryContents> callback;

        public ItemPage(ItemStack itemStack, int slot) {
            this.itemStack = itemStack;
            this.slot = slot;
        }

        public void callback(Consumer<InventoryContents> callback) {
            this.callback = callback;
        }
    }

    private static class Item {
        private ItemStack itemStack;
        private Consumer<InventoryContents> callback;

        public Item(ItemStack itemStack, Consumer<InventoryContents> callback) {
            this.itemStack = itemStack;
            this.callback = callback;
        }

        public Item(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }
}
