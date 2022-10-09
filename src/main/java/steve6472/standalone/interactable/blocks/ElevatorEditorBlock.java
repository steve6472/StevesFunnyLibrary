package steve6472.standalone.interactable.blocks;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.StructureItem;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.BlockGen;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ElevatorEditorBlock extends CustomBlock implements IBlockData, AdminInterface<ElevatorEditorData>, BlockTick
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.AQUA, 1f);

	@Override
	public String id()
	{
		return "elevator_editor";
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return BlockGen.ChainCommandBlock(false, BlockFace.DOWN);
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new ElevatorEditorData();
	}

	@Override
	public void showInterface(ElevatorEditorData data, PlayerBlockContext context)
	{
		MENU.setData("data", data);
		MENU.setData("location", context.getBlockLocation());
		MENU.build().showToPlayers(context.getPlayer());
	}

	@Override
	public void tick(BlockContext context)
	{
		ElevatorEditorData blockData = context.getBlockData(ElevatorEditorData.class);
		if (blockData == null) return;

		if (FunnyLib.getUptimeTicks() % 7 == 0)
		{
			Vector size = StructureItem.getSize(blockData.structure);

			Location location = context.getLocation().clone().add(1, 0, 2);

			int x0 = location.getBlockX();
			int y0 = location.getBlockY();
			int z0 = location.getBlockZ();
			int x1 = size.getBlockX() + location.getBlockX();
			int y1 = size.getBlockY() + location.getBlockY();
			int z1 = size.getBlockZ() + location.getBlockZ();

			ParticleUtil.boxAbsolute(context.getWorld(), Particle.REDSTONE, x0, y0, z0, x1 + 1, y1 + 1, z1 + 1, 0, 1.0, OPTIONS);
		}

		for (Player player : context.getWorld().getPlayers())
		{
			if (player.getLocation().distance(context.getLocation()) < 16)
			{
				if (blockData.collisionsMode)
				{
					JSONMessage.create("Collision Offset: ")
						.then("X: ").color(ChatColor.RED).then("" + blockData.editingCollision.offsetX).color(ChatColor.DARK_RED)
						.then(" Y: ").color(ChatColor.GREEN).then("" + blockData.editingCollision.offsetY).color(ChatColor.DARK_GREEN)
						.then(" Z: ").color(ChatColor.BLUE).then("" + blockData.editingCollision.offsetZ).color(ChatColor.DARK_BLUE)
						.actionbar(player);
				} else
				{
					JSONMessage.create("Seat Offset: ")
						.then("X: ").color(ChatColor.RED).then("" + blockData.editingSeat.offsetX).color(ChatColor.DARK_RED)
						.then(" Y: ").color(ChatColor.GREEN).then("" + blockData.editingSeat.offsetY).color(ChatColor.DARK_GREEN)
						.then(" Z: ").color(ChatColor.BLUE).then("" + blockData.editingSeat.offsetZ).color(ChatColor.DARK_BLUE)
						.actionbar(player);
				}
			}
		}
	}

	private static final Mask mask = Mask.createMask()
		.addRow("VVVaaaaaa")
		.addRow("..Vaaaaaa")
		.addRow("VPVaaaaaa")
		.addRow("..Paaaaaa")
		.addRow("PPPaaaaaa")
		.addItem('V', SlotBuilder.create(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "")))
		.addItem('P', SlotBuilder.create(ItemStackBuilder.quick(Material.PINK_STAINED_GLASS_PANE, "")))
		.addItem('O', SlotBuilder.create(ItemStackBuilder.quick(Material.ORANGE_STAINED_GLASS_PANE, "")))
		.addItem('a', SlotBuilder.create(ItemStackBuilder.quick(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "")));

	private static final MenuBuilder MENU = MenuBuilder
		.create(5, "Elevator Editor")
		.allowPlayerInventory()
		.slot(0, 1, SlotBuilder.create(ItemStackBuilder
				.create(Material.STRUCTURE_BLOCK)
				.setName("Structure File")
				.addLore("Click to load!")
				.buildItemStack())
			.allow(InventoryAction.PICKUP_ALL)
			.allow(ClickType.LEFT)
			.onClick((c, cm) ->
			{
				cm.getPassedData().getData("data", ElevatorEditorData.class).loadStructure(cm.getPassedData().getData("location", Location.class), c.player());
				return Response.cancel();
			}))
		.slot(1, 3, SlotBuilder.create()
			.allow(ClickType.LEFT)
			.allow(InventoryAction.PICKUP_ALL)
			.onClick((c, cm) -> Response.allow()))
		.slot(0, 3, SlotBuilder.create(ItemStackBuilder
				.create(Material.RESPAWN_ANCHOR)
				.setName("Elevator Data")
				.addLore("Click to generate!")
				.buildItemStack())
			.allow(InventoryAction.PICKUP_ALL)
			.allow(ClickType.LEFT)
			.onClick((c, cm) ->
			{
				ItemStack data = cm.getPassedData().getData("data", ElevatorEditorData.class).createElevatorDataItem();
				if (data == null)
				{
					c.player().sendMessage(ChatColor.RED + "Data could not be generated");
					c.player().sendMessage(ChatColor.RED + "Check if a valid Structure item is present");
					return Response.cancel();
				}
				cm.getSlot(1, 3).setItem(data);

				return Response.cancel();
			}))
		.slot(1, 1, SlotBuilder.itemSlot(d -> d.getData("data", ElevatorEditorData.class).structure, (d, i) -> d.getData("data", ElevatorEditorData.class).structure = i, i -> Items.getCustomItem(i) == FunnyLib.STRUCTURE))
		.applyMask(mask);
}
