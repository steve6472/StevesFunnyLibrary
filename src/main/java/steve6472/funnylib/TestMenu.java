package steve6472.funnylib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.menu.slots.buttons.MoveButtonSlot;
import steve6472.funnylib.menu.windows.Popup;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 6/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class TestMenu extends Menu
{
	Player player;

	public TestMenu(Player player)
	{
		super(5, "Main Test Menu v2", true);
		this.player = player;
	}

	@Override
	protected void setup()
	{
		Mask mask = new Mask();
		mask.addRow("xxxxxxxxx");
		mask.addRow("xm_____cx");
		mask.addRow("x___f___x");
		mask.addRow("x_______x");
		mask.addRow("xxxxxxxxx");
		mask.addItem('x', () -> new IconSlot(ItemStackBuilder.quick(Material.ORANGE_STAINED_GLASS_PANE, ""), true));
		mask.addItem('_', () -> new IconSlot(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""), true));
		mask.addItem('c', () -> new ButtonSlot(JSONMessage.create("Close").color(ChatColor.RED), Material.BARRIER, true).setClick(click -> Response.exit()));
		mask.addItem('f', () -> new ButtonSlot(JSONMessage.create("Enter the ").then("FUN ZONE", ChatColor.GREEN), Material.LIME_DYE, false).setClick(click -> Response.redirect(new FancyMenu(player))));
		mask.addItem('m', () -> new ButtonSlot(createGamemodeItem(player.getGameMode()), true).setClick(click ->
			{
				player.setGameMode(player.getGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE);
				((ButtonSlot) click.slot()).setIcon(createGamemodeItem(player.getGameMode()));
				return Response.cancel();
			}));
		applyMask(mask);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (getInventory().getViewers().isEmpty())
					cancel();

				for (HumanEntity viewer : getInventory().getViewers())
				{
					InventoryView openInventory = viewer.getOpenInventory();
					openInventory.setTitle(openInventory.getOriginalTitle() + " " + ChatColor.GRAY + "[" + FunnyLib.getUptimeTicks() + "]");
				}
			}
		}.runTaskTimer(FunnyLib.getPlugin(), 1, 0);
	}

	private ItemStack createGamemodeItem(GameMode currentMode)
	{
		Material mat = currentMode == GameMode.CREATIVE ? Material.WOODEN_PICKAXE : Material.COMMAND_BLOCK;
		String text = currentMode == GameMode.CREATIVE ? "Switch to Survival" : "Switch to Creative";
		return ItemStackBuilder.create(mat).setName(JSONMessage.create(text)).buildItemStack();
	}

	@Override
	public Response onClose(Player player)
	{
		player.sendMessage(ChatColor.GREEN + "Goodbye");
		return super.onClose(player);
	}

	private static class FancyMenu extends Menu
	{
		Player player;

		public FancyMenu(Player player)
		{
			super(6, "Fancy Menu", true);
			this.player = player;
		}

		@Override
		protected void setup()
		{
			setSlot(0, 0, new IconSlot(ItemStackBuilder.quick(Material.STICK, "Fancy Stick"), false));
			setSlot(0, 1, new ButtonSlot(ItemStackBuilder.quick(Material.OAK_SIGN, "Popup"), false)
				.setClick(c ->
				{
					Popup popup = new Popup(false, 1);
					addWindow(popup);
					reload();
					return Response.cancel();
				}));

			Mask mask = new Mask();
			mask.addRow(".........", 5);
			mask.addRow("lro<X>oud");
			mask.addItem('<', () -> new ButtonSlot(JSONMessage.create("Back"), Material.ARROW, true).setClick(c -> c.menu().goBackIntoThePast()));
			mask.addItem('X', () -> new ButtonSlot(JSONMessage.create("Close").color(ChatColor.RED), Material.BARRIER, true).setClick(c -> Response.exit()));
			mask.addItem('>', () -> new ButtonSlot(JSONMessage.create("Forward"), Material.ARROW, true));
			mask.addItem('o', () -> new IconSlot(ItemStackBuilder.quick(Material.BLACK_STAINED_GLASS_PANE, ""), true));
			mask.addItem('l', () -> new MoveButtonSlot(ItemStackBuilder.quick(Material.ARROW, "Left"), -1, 0, true));
			mask.addItem('r', () -> new MoveButtonSlot(ItemStackBuilder.quick(Material.ARROW, "Right"), 1, 0, true));
			mask.addItem('u', () -> new MoveButtonSlot(ItemStackBuilder.quick(Material.ARROW, "Up"), 0, -1, true));
			mask.addItem('d', () -> new MoveButtonSlot(ItemStackBuilder.quick(Material.ARROW, "Down"), 0, 1, true));
			applyMask(mask);

//			Menu window = new TestMenu(player);
//			window.setWindowPosition(2, 2);
//			addWindow(window);
		}
	}
}
