package steve6472.funnylib;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Description;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public class MenuTest
{
	/*
	private static final MenuBuilder FANCY_MENU = MenuBuilder
		.create(6, "Fancy Menu")
		.slot(0, SlotBuilder.create(ItemStackBuilder.quick(Material.STICK, "Fancy Stick", "#966f33")));

	private static final Mask BIOME_MAP_MASK = Mask.createMask()
		.addRow("......XXX")
		.addRow("......XXX")
		.addRow("......XXX")
		.addRow("......X.X")
		.addRow(".........")
		.addRow("......X.X")
		.addItem('X', SlotBuilder.create(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, "")).setSticky())
		;

	private static final Mask BIOME_BORDER_MASK = Mask.createMask()
		.addRow("X".repeat(66))
		.addRow("X" + ".".repeat(64) + "X", 64)
		.addRow("X".repeat(66))
		.addItem('X', SlotBuilder.create(ItemStackBuilder.quick(Material.RED_STAINED_GLASS_PANE, "")))
		.setOffset(-33, -33);

	private static Response moveBiomeMap(Menu menu, int x, int y)
	{
		menu.move(x, y);
		menu.getSlot(7, 4).setItem(ItemStackBuilder.quick(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "Offset: " + menu.getOffsetX() + "/" + menu.getOffsetY()));
		return Response.cancel();
	}

	private static final MenuBuilder BIOME_MAP = MenuBuilder
		.create(6, "Biome Map 16x16")
		.customBuilder(b ->
		{
			Player player = b.getData("player", Player.class);
			World world = player.getWorld();

			int ox = player.getLocation().getChunk().getX();
			int oz = player.getLocation().getChunk().getZ();

			for (int x = -32 + ox; x < 32 + ox; x++)
			{
				for (int z = -32 + oz; z < 32 + oz; z++)
				{
					Material mat = Material.BLACK_STAINED_GLASS_PANE;

					Biome biome = world.getBiome(x * 16, player.getLocation().getBlockY(), z * 16);
					mat = switch (biome)
					{
						case RIVER -> Material.BLUE_STAINED_GLASS_PANE;
						case FOREST -> Material.GREEN_STAINED_GLASS_PANE;
						case PLAINS -> Material.LIME_STAINED_GLASS_PANE;
						case GROVE -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
						case TAIGA -> Material.WHITE_STAINED_GLASS_PANE;
						case LUSH_CAVES -> Material.MOSS_BLOCK;
						case FROZEN_RIVER -> Material.ICE;
						case SNOWY_TAIGA -> Material.SNOW_BLOCK;
						case BEACH -> Material.SMOOTH_SANDSTONE_SLAB;
						case SNOWY_PLAINS -> Material.SNOW;
						case COLD_OCEAN -> Material.PACKED_ICE;
						default -> mat;
					};

					ItemStack item = ItemStackBuilder.create(mat).setName(biome.name()).addLore("C: " + x + "/" + z).addLore("B: " + (x * 16) + "/" + (z * 16)).buildItemStack();
					SlotBuilder slot = SlotBuilder.create(item);
					slot.allow(InventoryAction.PICKUP_ALL);
					int finalX = x;
					int finalZ = z;
					slot.onClick(ClickType.LEFT, (c, m) ->
					{
						Location location = new Location(world, finalX * 16 + 8, 0, finalZ * 16 + 8);
						location.setY(world.getHighestBlockAt(location).getY() + 1.5);
						c.player().teleport(location);
						return Response.exit();
					});
					b.slot(x - ox, z - oz, slot);
				}
			}
		})
		.slot(7, 3,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "North", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, m) -> moveBiomeMap(c.menu(), 0, -1))
				.onClick(ClickType.RIGHT, (c, m) -> moveBiomeMap(c.menu(), 0, -6)))
		.slot(8, 4,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "East", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, m) -> moveBiomeMap(c.menu(), 1, 0))
				.onClick(ClickType.RIGHT, (c, m) -> moveBiomeMap(c.menu(), 6, 0)))
		.slot(7, 5,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "South", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, m) -> moveBiomeMap(c.menu(), 0, 1))
				.onClick(ClickType.RIGHT, (c, m) -> moveBiomeMap(c.menu(), 0, 6)))
		.slot(6, 4,
			SlotBuilder.create(ItemStackBuilder.quick(Material.ARROW, "West", ChatColor.GREEN))
				.setSticky()
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
				.onClick(ClickType.LEFT, (c, m) -> moveBiomeMap(c.menu(), -1, 0))
				.onClick(ClickType.RIGHT, (c, m) -> moveBiomeMap(c.menu(), -6, 0)))
		.slot(7, 4, SlotBuilder.create(ItemStackBuilder.quick(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "Offset: 0/0")).setSticky())
		.applyMask(BIOME_MAP_MASK)
		.applyMask(BIOME_BORDER_MASK)
		.limitOffset(-33, 27, -33, 27);

	private static final MenuBuilder MAIN_BUILDER = MenuBuilder
		.create(3, "Main Test Menu")
		.slot(12, SlotBuilder
			.create(ItemStackBuilder
				.create(Material.LIME_DYE)
				.setName(JSONMessage.create("Enter the ").then("FUN ZONE").color(ChatColor.GREEN))
				.buildItemStack())
			.allow(InventoryAction.PICKUP_ALL)
			.onClick(ClickType.LEFT, (c, m) ->
			{
				c.player().sendMessage(ChatColor.GREEN + "Welcome to the GUI");
				return Response.redirect(FANCY_MENU);
			}))
		.slot(14, SlotBuilder
			.create(ItemStackBuilder
				.create(Material.GRASS_BLOCK)
				.setName(JSONMessage.create("Biome Map").color(ChatColor.DARK_GREEN))
				.buildItemStack())
			.allow(InventoryAction.PICKUP_ALL)
			.onClick(ClickType.LEFT, (c, m) ->
			{
				return Response.redirect(BIOME_MAP, m.getPassedData());
			}))
		.slot(8, SlotBuilder
			.create(ItemStackBuilder.quick(Material.BARRIER, "Close", ChatColor.RED))
			.allow(ClickType.LEFT)
			.allow(InventoryAction.PICKUP_ALL)
			.onClick((c, m) -> Response.exit()))
		.slot(0, 0, m ->
		{
			Player player = m.getData("player", Player.class);
			Material mat = player.getGameMode() == GameMode.CREATIVE ? Material.WOODEN_PICKAXE : Material.COMMAND_BLOCK;
			String text = player.getGameMode() == GameMode.CREATIVE ? "Switch to Survival" : "Switch to Creative";

			return SlotBuilder
				.create(ItemStackBuilder.quick(mat, text))
				.allow(InventoryAction.PICKUP_ALL)
				.onClick(ClickType.LEFT, (c, cm) -> {
					player.setGameMode(player.getGameMode() == GameMode.CREATIVE ? GameMode.ADVENTURE : GameMode.CREATIVE);
					return Response.exit();
				});
		})
		.setOnClose((m, p) ->
		{
			p.sendMessage(ChatColor.GREEN + "Goodbye");
			return Response.allow();
		});
*/
	@Command
	@Description("Opens test Menu")
	@Usage("/testMenu")
	public static boolean testMenu(@NotNull Player player, @NotNull String[] args)
	{
//		MAIN_BUILDER.setData("player", player).build().showToPlayer(player);
		new TestMenu(player).showToPlayer(player);

		return true;
	}
}
