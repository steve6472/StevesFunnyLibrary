package steve6472.funnylib.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.MetaUtil;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary <br>
 */
public class MenuListener implements Listener
{
	public static final String MENU_META_KEY = "current_menu";

	@EventHandler
	public void close(InventoryCloseEvent e)
	{
		if (!(e.getPlayer() instanceof Player player)) return;
		Menu menu = MetaUtil.getValue(player, Menu.class, MENU_META_KEY);
		if (menu == null) return;
		if (menu.redirected)
		{
			// Clear in case player goes back to the menu via history and then closes it
			menu.redirected = false;
			return;
		}

		Response response = menu.onClose(player);

		if (response == Response.allow())
		{
			MetaUtil.removeMeta(player, MENU_META_KEY);
			return;
		}

		if (response == Response.cancel())
		{
			e.getPlayer().openInventory(menu.getInventory());
			return;
		}
	}

	@EventHandler
	public void drag(InventoryDragEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player player))
			return;
		Menu menu = MetaUtil.getValue(player, Menu.class, MENU_META_KEY);
		if (menu == null)
			return;

		e.setCancelled(true);
	}

	@EventHandler
	public void click(InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player player))
			return;
		Menu menu = MetaUtil.getValue(player, Menu.class, MENU_META_KEY);
		if (menu == null)
			return;

		if (!menu.allowPlayerInventory)
		{
			if (e.getClickedInventory() != menu.getInventory())
			{
				e.setCancelled(true);
				return;
			}
		}

		// Can click in player inventory & clicked inventory is player inventory
		if (menu.allowPlayerInventory && e.getClickedInventory() != menu.getInventory())
		{
			// Shift clicking hard
			// Probably some handler for it
			if (e.isShiftClick())
			{
				e.setCancelled(true);
				return;
			}

			return;
		}

		Slot slot = menu.getSlot(e.getSlot() % 9, e.getSlot() / 9);

		Click click = new Click();
		click.player = player;
		click.slot = slot;
		click.itemOnCursor = e.getCursor();
		click.type = e.getClick();
		click.action = e.getAction();

		if (slot == null || !slot.canBeInteractedWith(click))
		{
			e.setCancelled(true);
			return;
		}

		Response response = slot.onClick(click);

		if (response == Response.allow())
		{
			return;
		}

		if (response.getSetItemToCursor() != null)
		{
			e.setCursor(response.getSetItemToCursor().clone());
			e.setCancelled(true);
			return;
		}

		if (response == Response.clearItemFromCursor())
		{
			e.setCursor(new ItemStack(Material.AIR));
			return;
		}

		if (response == Response.exit())
		{
			e.setCancelled(true);
//			MetaUtil.removeMeta(player, MENU_META_KEY);
			player.closeInventory();
			return;
		}

		if (response.getRedirect() != null)
		{
			e.setCancelled(true);
			menu.redirected = true;

			Menu redirect = response.getRedirect();
			redirect.getHistory().ifPresent(list -> list.add(menu));

			redirect.showToPlayer(player);
			return;
		}

		if (response == Response.cancel())
		{
			e.setCancelled(true);
			return;
		}
	}
}
