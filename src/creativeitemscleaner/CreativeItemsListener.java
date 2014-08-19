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

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class CreativeItemsListener implements Listener {

	@EventHandler(ignoreCancelled=true)
	public void onCreativeItemGet(InventoryCreativeEvent event) {
		if (event.getWhoClicked().hasPermission("creativeitemscleaner.ignore")) {
			return;
		}
		ItemStack item = event.getCursor();
		if ((item != null) && (item.getType() != Material.AIR)) {
			event.setCursor(ItemStackCleaner.generateCleanItem(item));
		}
	}

}