package steve6472.funnylib.blocks.builtin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.*;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.properties.EnumProperty;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.MarkerItem;
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
public class TeleportButtonBlock extends CustomBlock implements IBlockData, BlockClickEvents, AdminInterface
{
	public static final EnumProperty<BlockFace> FACING = States.FACING;
	public static final EnumProperty<FaceAttachable.AttachedFace> ATTACHED = States.ATTACHED;

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
	public org.bukkit.block.data.BlockData getVanillaState(State state)
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
		return getDefaultState().with(FACING, player.getFacing().getOppositeFace()).with(ATTACHED, face);
	}

	@Override
	public BlockData createBlockData()
	{
		return new TeleportButtonData();
	}

	@Override
	public void rightClick(State state, ItemStack itemInHand, Player player, PlayerInteractEvent e)
	{
		TeleportButtonData blockData = Blocks.getBlockData(e.getClickedBlock().getLocation(), TeleportButtonData.class);
		if (blockData.item.getType().isAir())
			return;
		if (Items.getCustomItem(blockData.item) != FunnyLib.LOCATION_MARKER)
			return;

		ItemStackBuilder edit = ItemStackBuilder.edit(blockData.item);
		int x = edit.getCustomTagInt(MarkerItem.X);
		int y = edit.getCustomTagInt(MarkerItem.Y);
		int z = edit.getCustomTagInt(MarkerItem.Z);
		player.teleport(new Location(player.getWorld(), x + 0.5, y, z + 0.5));
	}

	/*
	 * Menu
	 */

	@Override
	public void showInterface(BlockData blockData, Player player)
	{
		TeleportButtonData data = (TeleportButtonData) blockData;
		MENU.setData("location", data.item).setData("data", data).build().showToPlayers(player);
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
						data.item = c.itemOnCursor();
					} else
					{
						data.item = null;
					}
					data.save();
					return Response.allow();
				});
		})
		.applyMask(mask);
}
