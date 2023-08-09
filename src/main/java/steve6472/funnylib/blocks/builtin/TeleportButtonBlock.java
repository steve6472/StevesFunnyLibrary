package steve6472.funnylib.blocks.builtin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;
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
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.generated.BlockGen;
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
		private Marker location;

		@Override
		public void toNBT(NBT compound)
		{
			if (location == null)
				return;
			location.toNBT(compound);
		}

		@Override
		public void fromNBT(NBT compound)
		{
			if (!compound.hasCompound("location"))
				return;

			location = new Marker(0, 0, 0, null);
			location.fromNBT(compound);
		}

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
	public BlockData getVanillaState(BlockContext context)
	{
		return BlockGen.StoneButton(context.getState().get(FACING), context.getState().get(ATTACHED), false);
	}

	@Override
	public State getStateForPlacement(PlayerBlockContext context)
	{
		FaceAttachable.AttachedFace face = switch (context.getFace())
		{
			case NORTH, EAST, SOUTH, WEST -> FaceAttachable.AttachedFace.WALL;
			case UP -> FaceAttachable.AttachedFace.FLOOR;
			case DOWN -> FaceAttachable.AttachedFace.CEILING;
			default -> throw new IllegalStateException("Unexpected value: " + context.getFace());
		};

		if (context.getFace() == BlockFace.UP || context.getFace() == BlockFace.DOWN)
		{
			return getDefaultState().with(FACING, context.getPlayer().getFacing()).with(ATTACHED, face);
		} else
		{
			return getDefaultState().with(FACING, context.getFace()).with(ATTACHED, face);
		}
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

		Marker loc = blockData.location;
		if (loc == null)
			return;

		context.getPlayer().teleport(new Location(context.getWorld(), loc.x() + 0.5, loc.y(), loc.z() + 0.5, context.getPlayerLocation().getYaw(), context.getPlayerLocation().getPitch()));
	}

	@Override
	public void getDrops(BlockContext blockContext, List<ItemStack> drops)
	{
		drops.add(FunnyLib.TELEPORT_BUTTON_ITEM.newItemStack());
		Marker location = blockContext.getBlockData(TeleportButtonData.class).location;
		if (location != null)
			drops.add(location.toItem());
	}

	@Override
	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		if (!context.isCreative())
			drops.add(FunnyLib.TELEPORT_BUTTON_ITEM.newItemStack());
		Marker location = context.getBlockData(TeleportButtonData.class).location;
		if (location != null)
			drops.add(location.toItem());
	}

	/*
	 * Menu
	 */

	@Override
	public void showInterface(TeleportButtonData data, PlayerBlockContext context)
	{/*
		MENU.setData("data", data);
		MENU.build().showToPlayers(context.getPlayer());*/
	}
/*
	private static final Mask mask = Mask.createask()
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
			TeleportButtonData data = m.getData("data", TeleportButtonData.class);

			Marker location = data.location;
			ItemStack item;
			if (location == null)
				item = MiscUtil.AIR;
			else
				item = location.toItem();

			return SlotBuilder
				.create(item)
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL)
				.allow(ClickType.LEFT)
				.onClick((c, cm) ->
				{
					boolean markerInHand = Items.getCustomItem(c.itemOnCursor()) == FunnyLib.LOCATION_MARKER;

					// Cancel if item in hand is not Air and is not Marker
					if (!markerInHand && !c.itemOnCursor().getType().isAir())
						return Response.cancel();

					if (markerInHand && !c.itemOnCursor().getType().isAir())
					{
						data.location = Marker.fromItem(c.itemOnCursor().clone());
					} else
					{
						data.location = null;
					}
					c.slot().setItem(data.location == null ? new ItemStack(Material.AIR) : data.location.toItem());
					return Response.cancel();
				});
		})
		.applyMask(mask);*/
}
