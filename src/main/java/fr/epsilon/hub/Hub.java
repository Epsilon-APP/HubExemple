package fr.epsilon.hub;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.epsilon.api.EpsilonAPI;
import fr.epsilon.api.informer.EInstanceInformer;
import fr.epsilon.hub.commands.JoinQueueCommand;
import fr.epsilon.hub.inv.EpsilonInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Hub extends JavaPlugin implements Listener {
    private final ItemStack COMPASS = new ItemBuilder(Material.COMPASS)
            .name(ChatColor.GOLD + "Serveur Mini-jeux")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .build();

    private EInstanceInformer instanceInformer;

    @Override
    public void onEnable() {
        getServer().getLogger().info("Epsilon exporter !.");

        this.instanceInformer = EpsilonAPI.get().runInstanceInformer();

        EpsilonInventory.init(this);

        getServer().getWorlds().get(0).setDifficulty(Difficulty.PEACEFUL);
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getPluginCommand("queue").setExecutor(new JoinQueueCommand());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public EInstanceInformer getInstanceInformer() {
        return instanceInformer;
    }

    public void connect(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();

        player.getInventory().addItem(COMPASS);

        player.teleport(new Location(player.getWorld(), -145, 70, 70));
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action clickType = event.getAction();

        if ((clickType == Action.RIGHT_CLICK_AIR || clickType == Action.LEFT_CLICK_BLOCK) && item.equals(COMPASS)) {
            new InventoryCompass(this).inventory().open(player);
        }
    }
}
