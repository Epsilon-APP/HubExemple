package fr.epsilon.hub;

import fr.epsilon.api.EpsilonAPI;
import fr.epsilon.api.instance.EInstance;
import fr.epsilon.hub.inv.*;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class InventoryCompass extends InventoryFactory {
    private Hub plugin;

    @Override
    public EpsilonInventory inventory() {
        return EpsilonInventory.builder()
                .title(ChatColor.GOLD + "Serveur Mini-jeux")
                .height(5)
                .factory(this)
                .create();
    }

    public InventoryCompass(Hub plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void init(Player player, InventoryContents contents, InventoryPattern pattern, InventoryPagination pagination) {
        pattern.fillRow(1, new ItemBuilder(Material.STAINED_GLASS_PANE).data(6).name("").build()).generate();
        pattern.fillRow(3, new ItemBuilder(Material.STAINED_GLASS_PANE).data(6).name("").build()).generate();
        pattern.fillBorder(new ItemBuilder(Material.STAINED_GLASS_PANE).data(2).name("").build()).generate();

        contents.set(new ItemBuilder(Material.ENDER_CHEST).name(ChatColor.LIGHT_PURPLE + "Liste des HUBS").build(), 3, 2, (parent) -> hubList(parent, player));
        contents.set(new ItemBuilder(Material.ENDER_PORTAL_FRAME).name(ChatColor.AQUA + "Liste des Serveurs").build(), 4, 2, (parent) -> serverList(parent, player));
        contents.set(new ItemBuilder(Material.SIGN).name(ChatColor.GOLD + "Gestion des Serveurs").build(), 5, 2, (parent) -> serverManage(parent, player));
    }

    private void hubList(InventoryContents parent, Player player) {
        EpsilonInventory inventory = EpsilonInventory.builder()
                .title(ChatColor.LIGHT_PURPLE + "Liste des HUBS")
                .height(2)
                .create();

        int[] slots = inventory.get().getFillPattern().fillRow(0, null).getSlots();
        inventory.get().getPagination().availableSlots(slots);

        for (EInstance server : plugin.getInstanceInformer().getInstances()) {
            if (server.isHub())
                inventory.get().getPagination().add(new ItemBuilder(Material.WOOL).name(server.getName()).lore(ChatColor.GREEN + "State: " + server.getState(), ChatColor.GOLD + "" + server.getOnlineCount() + "/" + server.getSlots()).build(), (contents) -> {
                    plugin.connect(player, server.getName());
                    inventory.close(player);
                });
        }

        inventory.get().getPagination().init(
                new InventoryPagination.ItemPage(new ItemBuilder(Material.PAPER).name("Précédent").build(), 9),
                new InventoryPagination.ItemPage(new ItemBuilder(Material.PAPER).name("Suivant").build(), 17)
        );

        player.closeInventory();
        inventory.open(player);
    }

    private void serverList(InventoryContents parent, Player player) {
        serverList(parent, player, (server) -> plugin.connect(player, server.getName()));
    }

    private void serverList(InventoryContents parent, Player player, Consumer<EInstance> onClick) {
        EpsilonInventory inventory = EpsilonInventory.builder()
                .title(ChatColor.LIGHT_PURPLE + "Liste des Servers")
                .height(2)
                .create();

        int[] slots = inventory.get().getFillPattern().fillRow(0, null).getSlots();
        inventory.get().getPagination().availableSlots(slots);

        for (EInstance server : plugin.getInstanceInformer().getInstances()) {
            inventory.get().getPagination().add(new ItemBuilder(Material.WOOL).name(server.getName()).lore(ChatColor.GREEN + "State: " + server.getState(), ChatColor.GOLD + "" + server.getOnlineCount() + "/" + server.getSlots()).build(), (contents) -> {
                onClick.accept(server);
                inventory.close(player);
            });
        }

        inventory.get().getPagination().init(
                new InventoryPagination.ItemPage(new ItemBuilder(Material.PAPER).name("Précédent").build(), 9),
                new InventoryPagination.ItemPage(new ItemBuilder(Material.PAPER).name("Suivant").build(), 17)
        );

        player.closeInventory();
        inventory.open(player);
    }

    private void serverManage(InventoryContents parent, Player player) {
        EpsilonInventory inventory = EpsilonInventory.builder()
                .title(ChatColor.LIGHT_PURPLE + "Liste des Servers")
                .height(2)
                .create();

        int[] slots = inventory.get().getFillPattern().fillRow(0, null).getSlots();
        inventory.get().getPagination().availableSlots(slots);

        inventory.get().add(new ItemBuilder(Material.WOOD_DOOR).name(ChatColor.YELLOW + "Ouvrir un serveur").build(), (contents) -> {
            new AnvilGUI.Builder()
                    .onComplete((anvilPlayer, text) -> {
                        EpsilonAPI.get().instanceModule().openInstance(text);
                        return AnvilGUI.Response.close();
                    })
                    .text("Type du serveur")
                    .plugin(plugin)
                    .open(player);
        });

        inventory.get().add(new ItemBuilder(Material.BARRIER).name(ChatColor.YELLOW + "Fermé un serveur").build(), (contents) -> {
            serverList(inventory.get(), player, EInstance::close);
        });

        player.closeInventory();
        inventory.open(player);
    }
}
