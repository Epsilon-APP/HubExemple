package fr.epsilon.hub.inv;

import org.bukkit.entity.Player;

public abstract class InventoryFactory {
    public abstract EpsilonInventory inventory();
    protected abstract void init(Player player, InventoryContents contents, InventoryPattern pattern, InventoryPagination pagination);

    protected void close(Player player) {}
}
