/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package creativeitemscleaner;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackCleaner {

	public static ItemStack generateCleanItem(ItemStack oldItem) {
		//handle base
		ItemStack newItem = new ItemStack(oldItem.getType());
		//handle possibly invalid count (never seen this, but who knows what can possibly happen?)
		if (oldItem.getAmount() > oldItem.getMaxStackSize()) {
			newItem.setAmount(oldItem.getMaxStackSize());
		} else {
			newItem.setAmount(oldItem.getAmount());
		}
		newItem.setDurability(oldItem.getDurability());
		//handle books
		if (oldItem.getType() == Material.ENCHANTED_BOOK) {
			//handle enchants
			if (oldItem.hasItemMeta()) {
				EnchantmentStorageMeta oldEnchBookMeta = (EnchantmentStorageMeta) oldItem.getItemMeta();
				if (oldEnchBookMeta.hasStoredEnchants()) {
					//just add the first found enchants if it is valid
					Map<Enchantment, Integer> enchants = oldEnchBookMeta.getStoredEnchants();
					if (enchants.size() > 0) {
						Entry<Enchantment, Integer> entry = enchants.entrySet().iterator().next();
						if (entry.getValue() <= entry.getKey().getMaxLevel()) {
							EnchantmentStorageMeta newEnchBookMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
							newEnchBookMeta.addStoredEnchant(entry.getKey(), entry.getValue(), false);
							newItem.setItemMeta(newEnchBookMeta);
						}
					}
				}
			}
			//now handle name
			ItemMeta newItemMeta = null;
			if (newItem.hasItemMeta()) {
				newItemMeta = newItem.getItemMeta();
			} else {
				newItemMeta = Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
			}
			newItemMeta.setDisplayName(ChatColor.YELLOW + "Enchanted Book");
			newItem.setItemMeta(newItemMeta);
		}
		//return newly generated item
		return newItem;
	}

}