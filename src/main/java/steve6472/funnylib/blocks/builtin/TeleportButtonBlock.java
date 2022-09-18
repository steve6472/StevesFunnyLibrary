package steve6472.funnylib.blocks.builtin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.blocks.*;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.properties.EnumProperty;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.codecs.ItemStackCodec;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.BlockGen;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;

import java.util.List;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class TeleportButtonBlock extends CustomBlock implements IBlockData, BlockClickEvents, AdminInterface<TeleportButtonBlock.TeleportButtonData>
{
	public static final EnumProperty<BlockFace> FACING = States.FACING;
	public static final EnumProperty<FaceAttachable.AttachedFace> ATTACHED = States.ATTACHED;

	public static class TeleportButtonData extends CustomBlockData
	{
		@Save(type = ItemStackCodec.class)
		private ItemStack item = MiscUtil.AIR;
	}

	@Override
	public String id()
	{
		return "teleport_button";
	}

	@Override
	public void fillStates(List<IProperty<?>> properties)
	{
		properties.add(FACING);
		properties.add(ATTACHED);
	}

	@Override
	public BlockData getVanillaState(State state)
	{
		return BlockGen.StoneButton(state.get(FACING), state.get(ATTACHED), false);
	}

	@Override
	public State getStateForPlacement(Player player, Block clickedBlock, BlockFace clickedFace)
	{
		FaceAttachable.AttachedFace face = switch (clickedFace)
		{
			case NORTH, EAST, SOUTH, WEST -> FaceAttachable.AttachedFace.WALL;
			case UP -> FaceAttachable.AttachedFace.FLOOR;
			case DOWN -> FaceAttachable.AttachedFace.CEILING;
			default -> throw new IllegalStateException("Unexpected value: " + clickedFace);
		};
		return getDefaultState().with(FACING, clickedFace).with(ATTACHED, face);
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new TeleportButtonData();
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		TeleportButtonData blockData = context.getBlockData(TeleportButtonData.class);

		ItemStack item = blockData.item;
		if (Items.getCustomItem(item) != FunnyLib.LOCATION_MARKER)
			return;

		ItemStackBuilder edit = ItemStackBuilder.edit(item);
		int x = edit.getCustomTagInt("x");
		int y = edit.getCustomTagInt("y");
		int z = edit.getCustomTagInt("z");
		context.getPlayer().teleport(new Location(context.getWorld(), x + 0.5, y, z + 0.5, context.getPlayerLocation().getYaw(), context.getPlayerLocation().getPitch()));
	}

	@Override
	public void getDrops(BlockContext blockContext, List<ItemStack> drops)
	{
		drops.add(FunnyLib.TELEPORT_BUTTON_ITEM.newItemStack());
		drops.add(blockContext.getBlockData(TeleportButtonData.class).item);
	}

	@Override
	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		if (!context.isCreative())
			drops.add(FunnyLib.TELEPORT_BUTTON_ITEM.newItemStack());
		drops.add(context.getBlockData(TeleportButtonData.class).item);
	}

	/*
	 * Menu
	 */

	@Override
	public void showInterface(TeleportButtonData data, Player player)
	{
		MENU.setData("location", data.item);
		MENU.setData("data", data);
		MENU.build().showToPlayers(player);
	}

	private static final Mask mask = Mask.createMask()
		.addRow("abaVVVaba")
		.addRow("babV.Vbab")
		.addRow("abaVVVaba")
		.addItem('V', SlotBuilder.create(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "")))
		.addItem('a', SlotBuilder.create(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, "")))
		.addItem('b', SlotBuilder.create(ItemStackBuilder.quick(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "")));

	private static final MenuBuilder MENU = MenuBuilder
		.create(3, "Location")
		.allowPlayerInventory()
		.slot(4, 1, m ->
		{
			ItemStack item = m.getData("location", ItemStack.class);
			if (item == null) item = MiscUtil.AIR;

			return SlotBuilder
				.create(item)
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL)
				.allow(ClickType.LEFT)
				.onClick((c, cm) ->
				{
					TeleportButtonData data = cm.getPassedData().getData("data", TeleportButtonData.class);
					if (c.itemOnCursor() != null && !c.itemOnCursor().getType().isAir())
					{
						data.item = c.itemOnCursor().clone();
					} else
					{
						data.item = MiscUtil.AIR;
					}
					return Response.allow();
				});
		})
		.applyMask(mask);
}
