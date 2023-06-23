package fr.epsilon.hub.inv;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryPattern {
    private final InventoryContents inventory;
    private final List<Integer> blacklist;

    public InventoryPattern(InventoryContents inventory) {
        this.inventory = inventory;
        this.blacklist = new ArrayList<>();
    }

    public void blacklist(Integer... slotsBlacklist) {
        Collections.addAll(blacklist, slotsBlacklist);
    }

    public Fill fill(ItemStack item) {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < inventory.getInventorySize(); i++) {
            if (blacklist.contains(i)) continue;

            slots.add(i);
        }

        return new Fill(slots.stream().mapToInt(Integer::intValue).toArray(), item, inventory);
    }

    public Fill fillRow(int y, ItemStack item) {
        List<Integer> slots = new ArrayList<>();

        for (int i = y * 9; i < (y + 1) * 9; i++) {
            if (blacklist.contains(i)) continue;

            slots.add(i);
        }

        return new Fill(slots.stream().mapToInt(Integer::intValue).toArray(), item, inventory);
    }

    public Fill fillColumn(int x, ItemStack item) {
        List<Integer> slots = new ArrayList<>();

        for (int i = x; i < inventory.getInventorySize() - 9; i += 9) {
            if (blacklist.contains(i)) continue;

            slots.add(i);
        }

        return new Fill(slots.stream().mapToInt(Integer::intValue).toArray(), item, inventory);
    }

    public Fill fillBorder(ItemStack item) {
        return new Fill(mergeArray(fillColumn(0, item), fillColumn(8, item), fillRow(0, item), fillRow(inventory.getHeight() - 1, item)), item, inventory);
    }

    private int[] mergeArray(Fill... arrays) {
        List<Integer> slots = new ArrayList<>();

        for (Fill array : arrays) {
            for (int i : array.slots) {
                if (!slots.contains(i))
                    slots.add(i);
            }
        }

        return slots.stream().mapToInt(Integer::intValue).toArray();
    }

    public static class Fill {
        private int[] slots;
        private ItemStack item;

        private InventoryContents inventory;

        public Fill(int[] slots, ItemStack item, InventoryContents inventory) {
            this.slots = slots;
            this.item = item;
            this.inventory = inventory;
        }

        public void generate() {
            for (int slot : slots) {
                inventory.set(item, slot);
            }
        }

        public int[] getSlots() {
            return slots;
        }
    }
}
