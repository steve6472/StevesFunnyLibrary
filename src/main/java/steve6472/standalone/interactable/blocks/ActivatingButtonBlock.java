package steve6472.standalone.interactable.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.event.player.PlayerInteractEvent;
import steve6472.funnylib.blocks.*;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.properties.EnumProperty;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.serialize.NBT;

import java.util.List;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ActivatingButtonBlock extends CustomBlock implements IBlockData, BlockClickEvents, AdminInterface<ActivatingButtonBlock.ActivatingData>
{
	public static final EnumProperty<BlockFace> FACING = States.FACING;
	public static final EnumProperty<FaceAttachable.AttachedFace> ATTACHED = States.ATTACHED;

	public static class ActivatingData extends CustomBlockData
	{
		private Marker block;
		private Material material = Material.STONE_BUTTON;

		@Override
		public void toNBT(NBT compound)
		{
			compound.setEnum("material", material);
			NBT blockNbt = compound.createCompound();
			block.toNBT(blockNbt);
			compound.setCompound("location", blockNbt);
		}

		@Override
		public void fromNBT(NBT compound)
		{
			material = compound.getEnum(Material.class, "material");
			block = new Marker();
			block.fromNBT(compound.getOrCreateCompound("location"));
		}
	}

	@Override
	public String id()
	{
		return "activating_button";
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
		Switch switchData;
		if (context.testDataType(ActivatingData.class))
		{
			ActivatingData data = context.getBlockData(ActivatingData.class);
			switchData = (Switch) data.material.createBlockData();
		} else
		{
			switchData = (Switch) Material.STONE_BUTTON.createBlockData();
		}
		switchData.setFacing(context.getState().get(FACING));
		switchData.setAttachedFace(context.getState().get(ATTACHED));
		switchData.setPowered(false);
		return switchData;
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
		return new ActivatingData();
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		ActivatingData blockData = context.getBlockData(ActivatingData.class);
		if (blockData.block == null)
		{
			return;
		}

		Location location = blockData.block.toLocation(context.getWorld());

		State blockState = Blocks.getBlockState(location);
		if (blockState == null)
		{
			context.getPlayer().sendMessage(ChatColor.RED + "Could not find block to activate!");
			return;
		}
		if (blockState.getObject() instanceof Activable activable)
		{
			activable.activate(new PlayerBlockContext(context.playerContext(), new BlockFaceContext(location, BlockFace.SELF, blockState)));
		}
	}

	/*
	 * Menu
	 */

	@Override
	public void showInterface(ActivatingData data, PlayerBlockContext context)
	{/*
		MENU.setData("data", data);
		MENU.setData("location", context.getBlockLocation());
		MENU.build().showToPlayers(context.getPlayer());*/
	}
/*
	private static final Mask mask = Mask.createMask()
		.addRow("aVVVbVVVa")
		.addRow("bV.VaV.Vb")
		.addRow("aVVVbVVVa")
		.addItem('V', SlotBuilder.create(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "")))
		.addItem('a', SlotBuilder.create(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, "")))
		.addItem('b', SlotBuilder.create(ItemStackBuilder.quick(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "")));

	private static final MenuBuilder MENU = MenuBuilder
		.create(3, "Location")
		.allowPlayerInventory()
		.slot(2, 1, m ->
		{
			ActivatingData data = m.getData("data", ActivatingData.class);

			Marker location = data.block;
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
						data.block = Marker.fromItem(c.itemOnCursor().clone());
					} else
					{
						data.block = null;
					}
					return Response.allow();
				});
		})
		.slot(6, 1, m ->
		{
			ActivatingData data = m.getData("data", ActivatingData.class);
			Location location = m.getData("location", Location.class);

			return SlotBuilder.create(ItemStackBuilder.quick(data.material, "Material"))
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.SWAP_WITH_CURSOR)
				.allow(ClickType.LEFT)
				.onClick((c, cm) -> {

					ItemStack item = c.itemOnCursor();
					if (item.getType().isAir() || item.getType().isItem())
					{
						return Response.cancel();
					}

					BlockData blockData = item.getType().createBlockData();
					if (blockData instanceof Switch)
					{
						data.material = item.getType();
						location.getBlock().setBlockData(Interactable.ACTIVATING_BUTTON_BLOCK.getVanillaState(new BlockContext(location)));
						c.slot().setItem(ItemStackBuilder.quick(data.material, "Material"));
					}

					return Response.cancel();
				});
		})
		.applyMask(mask);*/
}
