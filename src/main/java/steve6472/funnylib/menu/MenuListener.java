package steve6472.funnylib.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
		if (menu.redirected) return;

		Response response = menu.callOnClose(player);

		if (response == Response.allow())
		{
			MetaUtil.removeMeta(player, MENU_META_KEY);
			return;
		}

		if (response == Response.cancel())
		{
			e.getPlayer().openInventory(menu.inventory);
			return;
		}
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
			if (e.getClickedInventory() != menu.inventory)
			{
				e.setCancelled(true);
				return;
			}
		}

		Slot slot = menu.getSlot(e.getSlot() % 9, e.getSlot() / 9);

		if (slot == null)
		{
			e.setCancelled(true);
			return;
		}

		if (!slot.allowedClickTypes.contains(e.getClick()))
		{
			e.setCancelled(true);
			return;
		}

		if (!slot.allowedInventoryActions.contains(e.getAction()))
		{
			e.setCancelled(true);
			return;
		}

		Click click = new Click();
		click.slot = slot;
		click.itemOnCursor = e.getCursor();
		click.type = e.getClick();
		click.action = e.getAction();

		Response response = slot.callOnClick(click, player);

		if (response == Response.allow())
		{
			return;
		}

		if (response == Response.exit())
		{
			e.setCancelled(true);
			MetaUtil.removeMeta(player, MENU_META_KEY);
			player.closeInventory();
			return;
		}

		if (response.getRedirect() != null)
		{
			e.setCancelled(true);
			menu.redirected = true;
			response.getRedirect().build().showToPlayer(player);
			return;
		}

		if (response == Response.cancel())
		{
			e.setCancelled(true);
			return;
		}
	}
}
