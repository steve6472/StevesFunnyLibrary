package steve6472.funnylib.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.util.MetaUtil;

import javax.annotation.Nullable;

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

		/*
		 * This is terrible
		 */
		if (menu.anvilRedirectState == AnvilRedirectState.ANVIL_OPEN_1)
		{
			menu.anvilRedirectState = AnvilRedirectState.ANVIL_OPEN_2;
			return;
		}
		else if (menu.anvilRedirectState == AnvilRedirectState.ANVIL_OPEN_2)
		{
			menu.anvilRedirectState = AnvilRedirectState.AWAITING_ANVIL_CLOSE;
			return;
		} else if (menu.anvilRedirectState == AnvilRedirectState.AWAITING_ANVIL_CLOSE && e.getInventory().getType() == InventoryType.ANVIL)
		{
			menu.anvilRedirectState = AnvilRedirectState.ANVIL_CLOSE;
			Bukkit.getScheduler().runTaskLater(FunnyLib.getPlugin(), () -> {
				menu.showToPlayer(player);
			}, 0);
			return;
		} else if (menu.anvilRedirectState == AnvilRedirectState.ANVIL_CLOSE)
		{
			menu.anvilRedirectState = AnvilRedirectState.NONE;
			return;
		}

		Response response = menu.onClose(player);

		if (back(response, menu, player, null, false))
		{
			return;
		}

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

		if (!FunnyLib.getSettings().enableFakeClickFromDrag)
		{
			e.setCancelled(true);
			return;
		}

		// I'm lazy
		if (e.getInventory().getType() != InventoryType.CHEST)
		{
			e.setCancelled(true);
			return;
		}

		if (e.getInventorySlots().size() == 1)
		{
			DragType type = e.getType();

			int slot = e.getRawSlots().stream().findFirst().get();
			if (slot > menu.rows * 9)
			{
				return;
			}

			ItemStack cursor = e.getOldCursor();

			FakeInventoryClickEvent fakeClick = new FakeInventoryClickEvent(
				e.getView(),
				InventoryType.SlotType.CONTAINER,
				slot,
				type == DragType.SINGLE ? ClickType.RIGHT : ClickType.LEFT,
				type == DragType.SINGLE ? InventoryAction.PICKUP_HALF : InventoryAction.PLACE_ALL,
				cursor);

			click(fakeClick);

			if (fakeClick.isCancelled())
				e.setCancelled(true);

		} else
		{
			e.setCancelled(true);
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

		if (back(response, menu, player, e, true))
			return;

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

	/**
	 * @param response
	 * @param menu
	 * @param player
	 * @param event
	 * @return true if back was hit
	 */
	private boolean back(Response response, Menu menu, Player player, @Nullable Cancellable event, boolean shouldCloseInventory)
	{
		if (response != Response.back() && response != Response.backReload())
		{
			return false;
		}

		if (event != null)
			event.setCancelled(true);

		if (menu.hasHistory())
		{
			menu.redirected = true;
			Menu last = menu.getHistory().get().pollLast();

			if (response == Response.backReload())
				last.reload();

			last.showToPlayer(player);
		} else
		{
			if (shouldCloseInventory)
				player.closeInventory();
		}
		return true;
	}
}
