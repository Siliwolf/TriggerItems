package rccommand;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.logging.Level;

public class create_trigger implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = ((Player) sender).getPlayer();

            if(!p.isOp()) {
                p.sendMessage(ChatColor.RED + "You don't have the required permissions!");
                return false;
            }

            if(args[0] == null) {
                p.sendMessage(ChatColor.RED + "Pass in arguments! See /help create_trigger for more info");
                return false;
            }

            if(args[0].isEmpty()) {
                p.sendMessage(ChatColor.RED + "Pass in arguments! See /help create_trigger for more info");
                return false;
            }

            long cooldown = Integer.parseInt(args[0]);

            if(cooldown < 0) {
                p.sendMessage(ChatColor.RED + "Cooldown must be greater than zero");
                return false;
            }

            ItemStack i = p.getInventory().getItemInMainHand();

            String[] string_args = Arrays.copyOfRange(args, 1, args.length);

            String cmd = new String();
            for(String s : string_args) {
                cmd = cmd + " " + s;
            }
            cmd.replace("/", "");

            ItemMeta meta = i.getItemMeta();

            NamespacedKey key = new NamespacedKey(trigger_item.get_instance(), "trigger_item");
            NamespacedKey cooldown_len = new NamespacedKey(trigger_item.get_instance(), "cooldown_len");
            NamespacedKey cooldown_data = new NamespacedKey(trigger_item.get_instance(), "cooldown_data");

            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, cmd);
            meta.getPersistentDataContainer().set(cooldown_len, PersistentDataType.LONG, cooldown);
            meta.getPersistentDataContainer().set(cooldown_data, PersistentDataType.LONG, System.currentTimeMillis());

            Bukkit.getLogger().log(Level.INFO, "Gave " + p.getDisplayName() + " trigger for command " + cmd);
            i.setItemMeta(meta);
            p.getInventory().setItemInMainHand(i);

            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
            p.sendMessage(ChatColor.GREEN + "Item in mainhand converted to a command trigger!");
        }
        return true;
    }
}
