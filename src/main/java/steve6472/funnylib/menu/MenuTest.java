package steve6472.funnylib.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Description;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public class MenuTest
{
	private static final MenuBuilder FANCY_MENU = MenuBuilder
		.create(6, "Fancy Menu")
		.slot(0, SlotBuilder.create(ItemStackBuilder.quick(Material.STICK, "Fancy Stick", "#966f33")));

	private static final Mask BIOME_MAP_MASK = Mask.createMask()
		.addRow("......XXX")
		.addRow("......XXX")
		.addRow("......XXX")
		.addRow("......X.X")
		.addRow(".......X.")
		.addRow("......X.X")
		.addItem('X', SlotBuilder.create(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, "")).setSticky())
		;

	private static final Mask BIOME_BORDER_MASK = Mask.createMask()
		.addRow("X".repeat(66))
		.addRow("X" + ".".repeat(64) + "X", 64)
		.addRow("X".repeat(66))
		.addItem('X', SlotBuilder.create(ItemStackBuilder.quick(Material.RED_STAINED_GLASS_PANE, "")))
		.setOffset(-33, -33);

	private static final MenuBuilder BIOME_MAP = MenuBuilder
		.create(6, "Biome Map 16x16")
		.customBuilder(b -> {
			World world = Bukkit.getWorld("world");
			if (world == null)
				return;

			for (int x = -32; x < 32; x++)
			{
				for (int y = -32; y < 32; y++)
				{
					Material mat = Material.BLACK_STAINED_GLASS_PANE;

					Biome biome = world.getBiome(x * 16, 64, y * 16);
					if (biome == Biome.RIVER)
						mat = Material.BLUE_STAINED_GLASS_PANE;
					if (biome == Biome.FOREST)
						mat = Material.GREEN_STAINED_GLASS_PANE;
					if (biome == Biome.PLAINS)
						mat = Material.LIME_STAINED_GLASS_PANE;
					if (biome == Biome.GROVE)
						mat = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					if (biome == Biome.TAIGA)
						mat = Material.WHITE_STAINED_GLASS_PANE;

					ItemStack item = ItemStackBuilder.create(mat).setName(biome.name()).addLore("C: " + x + "/" + y).addLore("B: " + (x * 16) + "/" + (y * 16)).buildItemStack();
					b.slot(x, y, SlotBuilder.create(item));
				}
			}
		})
		.slot(7, 3,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "North", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, p) -> { c.getMenu().move(0, -1); return Response.cancel(); })
				.onClick(ClickType.RIGHT, (c, p) -> { c.getMenu().move(0, -6); return Response.cancel(); }))
		.slot(8, 4,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "East", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, p) -> { c.getMenu().move(1, 0); return Response.cancel(); })
				.onClick(ClickType.RIGHT, (c, p) -> { c.getMenu().move(6, 0); return Response.cancel(); }))
		.slot(7, 5,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "South", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, p) -> { c.getMenu().move(0, 1); return Response.cancel(); })
				.onClick(ClickType.RIGHT, (c, p) -> { c.getMenu().move(0, 6); return Response.cancel(); }))
		.slot(6, 4,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "West", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, p) -> { c.getMenu().move(-1, 0); return Response.cancel(); })
				.onClick(ClickType.RIGHT, (c, p) -> { c.getMenu().move(-6, 0); return Response.cancel(); }))
		.applyMask(BIOME_MAP_MASK)
		.applyMask(BIOME_BORDER_MASK);

	private static final MenuBuilder MAIN_BUILDER = MenuBuilder
		.create(3, "Main Test Menu")
		.slot(12, SlotBuilder
			.create(ItemStackBuilder
				.create(Material.LIME_DYE)
				.setName(JSONMessage.create("Enter the ").then("FUN ZONE").color(ChatColor.GREEN))
				.buildItemStack())
			.allow(InventoryAction.PICKUP_ALL)
			.onClick(ClickType.LEFT, (c, p) ->
			{
				p.sendMessage(ChatColor.GREEN + "Welcome to the GUI");
				return Response.redirect(FANCY_MENU);
			}))
		.slot(14, SlotBuilder
			.create(ItemStackBuilder
				.create(Material.GRASS_BLOCK)
				.setName(JSONMessage.create("Biome Map").color(ChatColor.DARK_GREEN))
				.buildItemStack())
			.allow(InventoryAction.PICKUP_ALL)
			.onClick(ClickType.LEFT, (c, p) ->
			{
				return Response.redirect(BIOME_MAP);
			}))
		.slot(8, SlotBuilder
			.create(ItemStackBuilder.quick(Material.BARRIER, "Close", ChatColor.RED))
			.allow(ClickType.LEFT)
			.allow(InventoryAction.PICKUP_ALL)
			.onClick((c, p) -> Response.exit()))
		.setOnClose((m, p) ->
		{
			p.sendMessage(ChatColor.GREEN + "Goodbye");
			return Response.allow();
		});

	@Command
	@Description("Opens test Menu")
	@Usage("/testMenu")
	public static boolean testMenu(@NotNull Player player, @NotNull String[] args)
	{
		MAIN_BUILDER.build().showToPlayer(player);

		return true;
	}
}
