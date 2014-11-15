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

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class CreativeItemsListener implements Listener {

	private HashMap<UUID, ItemStack> lastTakenItem = new HashMap<UUID, ItemStack>();

	@EventHandler(ignoreCancelled=true)
	public void onCreativeItemGet(InventoryCreativeEvent event) {
		if (event.getWhoClicked().hasPermission("creativeitemscleaner.ignore")) {
			return;
		}
		final Player player = (Player) event.getWhoClicked();
		UUID uuid = player.getUniqueId();
		ItemStack item = event.getCursor();
		if (item != null) {
			try {
				ItemStack slotItem = player.getInventory().getItem(event.getSlot());
				//discard 0 and negative count items or items over maxstacksize (othwersize logic in next checks will break)
				if (item.getType() != Material.AIR && (item.getAmount() <= 0 || item.getAmount() > item.getMaxStackSize())) {
					event.setCursor(new ItemStack(Material.AIR));
					return;
				}
				//discard 0 and negative count lastTakenItem in case i screwed up somewhere
				if (lastTakenItem.containsKey(uuid)) {
					if (lastTakenItem.get(uuid).getAmount() <= 0) {
						lastTakenItem.remove(uuid);
					}
				}
				//if item is air than player probably taken the item from inventory to cursor
				if (item.getType() == Material.AIR) {
					lastTakenItem.put(uuid, slotItem);
					return;
				}
				//if there is already an item in the slot than there may be numerous actions
				if (slotItem != null) {
					if (slotItem.isSimilar(item)) {//split or stack
						if (slotItem.getAmount() > item.getAmount()) {//split
							ItemStack cursor = slotItem.clone();
							cursor.setAmount(slotItem.getAmount() - item.getAmount());
							lastTakenItem.put(uuid, cursor);
							return;
						} else {//stack
							if (item.getAmount() < item.getMaxStackSize()) {//taken all items from cursor or maybe not, but we don't care about it
								lastTakenItem.remove(uuid);
								return;
							} else {//still some left in player cursor or maybe not
								if (lastTakenItem.containsKey(uuid)) {
									lastTakenItem.get(uuid).setAmount(lastTakenItem.get(uuid).getAmount() - (item.getMaxStackSize() - slotItem.getAmount()));
								}
								return;
							}
						}
					} else {//replace item
						if (item.equals(lastTakenItem.get(player.getUniqueId()))) {//from cursor
							lastTakenItem.put(uuid, slotItem);
							return;
						}
					}
				}
				//if there wasn't any item in slot than player put the one from cursor or unknown one
				if (item.equals(lastTakenItem.get(uuid))) {//from cursor
					lastTakenItem.remove(uuid);
					return;
				}
				//unknown item, replace with clean version
				event.setCursor(ItemStackCleaner.generateCleanItem(item));
				lastTakenItem.remove(player.getUniqueId());
			} catch (Throwable t) {
				//in case i crewed up somewhere just clear cache and clean item
				event.setCursor(ItemStackCleaner.generateCleanItem(item));
				lastTakenItem.remove(player.getUniqueId());
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onQuit(PlayerQuitEvent event) {
		lastTakenItem.remove(event.getPlayer().getUniqueId());
	}

}