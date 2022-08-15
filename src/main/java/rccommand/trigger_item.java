package rccommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class trigger_item extends JavaPlugin implements Listener {
    private static trigger_item instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("create_trigger").setExecutor(new create_trigger());
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static trigger_item get_instance() {
        return instance;
    }

    @EventHandler
    public void right_click(PlayerInteractEvent e) {
        if(e.getItem() == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(trigger_item.get_instance(), "trigger_item");
        NamespacedKey cooldown_len = new NamespacedKey(trigger_item.get_instance(), "cooldown_len");
        NamespacedKey cooldown_data = new NamespacedKey(trigger_item.get_instance(), "cooldown_data");

        if(!e.getItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return;
        }

        if(!e.getItem().getItemMeta().getPersistentDataContainer().has(cooldown_len, PersistentDataType.LONG)) {
            return;
        }

        if(!e.getItem().getItemMeta().getPersistentDataContainer().has(cooldown_data, PersistentDataType.LONG)) {
            return;
        }
        long len = e.getItem().getItemMeta().getPersistentDataContainer().get(cooldown_len, PersistentDataType.LONG);
        long data = e.getItem().getItemMeta().getPersistentDataContainer().get(cooldown_data, PersistentDataType.LONG);


        if(len > 0) {
            long diff = System.currentTimeMillis() - data;
            long secs = diff / 1000;

            if(secs < len) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.0F, 1.0F);
                e.getPlayer().sendMessage(ChatColor.RED + "Wait " + (len - secs) + " seconds");
                return;
            }
        }

        ItemMeta i = e.getItem().getItemMeta();
        i.getPersistentDataContainer().set(cooldown_data, PersistentDataType.LONG, System.currentTimeMillis());
        e.getItem().setItemMeta(i);

        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
            if(e.getItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + e.getPlayer().getName() + " at @s run " + e.getItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
            }
    }
}
