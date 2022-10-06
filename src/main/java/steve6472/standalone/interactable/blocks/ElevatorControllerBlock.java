package steve6472.standalone.interactable.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.player.PlayerInteractEvent;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.json.codec.codecs.MarkerCodec;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.*;
import steve6472.standalone.interactable.Interactable;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ElevatorControllerBlock extends CustomBlock implements IBlockData, BlockTick, BlockClickEvents, AdminInterface<ElevatorControllerData>, Activable
{
	@Override
	public String id()
	{
		return "elevator_controller";
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return BlockGen.RepeatingCommandBlock(false, BlockFace.DOWN);
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (!context.isPlayerSneaking())
			e.setCancelled(true);
	}

	@Override
	public void tick(BlockContext context)
	{
		ElevatorControllerData data = context.getBlockData(ElevatorControllerData.class);
		data.dataLabel.setCustomName("dir= " + data.movingDirection + ", progress= " + ("%.4f".formatted(data.progress)) + ", speed= " + data.speed);

		if (data.showPoints && data.pointA != null && FunnyLib.getUptimeTicks() % 10 == 0)
		{
			Location location = data.pointA.toLocation(context.getWorld());
			ParticleUtil.block(location.getBlock(), Particle.WAX_OFF, 0, 0.2);
		}
		if (data.showPoints && data.pointB != null && FunnyLib.getUptimeTicks() % 10 == 0)
		{
			Location location = data.pointB.toLocation(context.getWorld());
			ParticleUtil.block(location.getBlock(), Particle.WAX_ON, 0, 0.2);
		}

		if (data.pointA == null || data.pointB == null)
		{
			return;
		}

		if (data.sbe != null && FunnyLib.getUptimeTicks() % 41 == 0)
		{
			data.sbe.update();
		}

		if (data.enabled && data.movingDirection != 0)
		{
			data.updatePosition();
		}

		if (data.movingDirection == 1)
		{
			data.progress -= data.speed * (1.0 / data.pointA.distance(data.pointB) / 20.0);
			if (data.progress < 0)
			{
				data.progress = 0;
				data.movingDirection = 0;
				data.solidify();
			}
		} else if (data.movingDirection == 2)
		{
			data.progress += data.speed * (1.0 / data.pointA.distance(data.pointB) / 20.0);
			if (data.progress > 1)
			{
				data.progress = 1;
				data.movingDirection = 0;
				data.solidify();
			}
		}
//
//		double x = lerp(data.pointA.getX(), data.pointB.getX(), data.progress);
//		double y = lerp(data.pointA.getY(), data.pointB.getY(), data.progress);
//		double z = lerp(data.pointA.getZ(), data.pointB.getZ(), data.progress);
//		context.getWorld().spawnParticle(Particle.END_ROD, x + 0.5, y + 0.5, z + 0.5, 0);
	}

	private static double lerp(double a, double b, double percentage)
	{
		return a + percentage * (b - a);
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new ElevatorControllerData();
	}

	@Override
	public void onPlace(BlockContext context)
	{
		if (!(context.getBlockData() instanceof ElevatorControllerData data)) return;

		ArmorStand dataLabel = context.getWorld().spawn(context.getLocation().clone().add(0.5, 1.5, 0.5), ArmorStand.class);
		dataLabel.setInvisible(true);
		dataLabel.setMarker(true);
		dataLabel.setGravity(false);
		dataLabel.setCustomNameVisible(true);
		data.dataLabel = dataLabel;
	}

	@Override
	public void onRemove(BlockContext context)
	{
		ElevatorControllerData data = context.getBlockData(ElevatorControllerData.class);
		data.dataLabel.remove();
		if (!data.elevatorData.getType().isAir())
		{
			context.getWorld().dropItem(context.getLocation(), data.elevatorData);
		}
	}

	@Override
	public void activate(PlayerBlockContext context)
	{
		ElevatorControllerData data = context.getBlockData(ElevatorControllerData.class);
		if (data.solidifyProtection)
			return;

		if (data.dirChange)
		{
			if (data.movingDirection == 1)
			{
				data.movingDirection = 2;
				return;
			} else if (data.movingDirection == 2)
			{
				data.movingDirection = 1;
				return;
			}
		}

		if (data.movingDirection != 0 && !data.dirChange)
		{
			return;
		}

		if (data.progress == 1.0)
		{
			data.activate(context, 1);
		} else if (data.progress == 0.0)
		{
			data.activate(context, 2);
		}
	}

	@Override
	public boolean canPlayerBreak(PlayerBlockContext context)
	{
		return false;
	}

	@Override
	public void showInterface(ElevatorControllerData data, PlayerBlockContext context)
	{
		MENU.setData("data", data);
		MENU.setData("location", context.getBlockLocation());
		MENU.build().showToPlayers(context.getPlayer());
	}

	private static final Mask mask = Mask.createMask()
		.addRow("P.Paaa...")
		.addRow("PPPaaaaRa")
		.addRow("G.GaaaR.R")
		.addRow("OOOaaaaRa")
		.addRow("O.Oaaa...")
		.addItem('V', SlotBuilder.create(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "")))
		.addItem('P', SlotBuilder.create(ItemStackBuilder.quick(Material.PINK_STAINED_GLASS_PANE, "")))
		.addItem('O', SlotBuilder.create(ItemStackBuilder.quick(Material.ORANGE_STAINED_GLASS_PANE, "")))
		.addItem('G', SlotBuilder.create(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "")))
		.addItem('R', SlotBuilder.create(ItemStackBuilder.quick(Material.RED_STAINED_GLASS_PANE, "")))
		.addItem('a', SlotBuilder.create(ItemStackBuilder.quick(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "")));

	private static final MenuBuilder MENU = MenuBuilder
		.create(5, "Elevator Controller")
		.allowPlayerInventory()
		.slot(1, 0, m ->
		{
			ElevatorControllerData data = m.getData("data", ElevatorControllerData.class);
			return MarkerCodec.slotBuilder(data.pointA, v -> data.pointA = v);
		})
		.slot(1, 4, m ->
		{
			ElevatorControllerData data = m.getData("data", ElevatorControllerData.class);
			return MarkerCodec.slotBuilder(data.pointB, v -> data.pointB = v);
		})
		.buttonSlot(6, 4, Material.ARROW, "Reduce speed", (c, cm) -> cm.getPassedData().getData("data", ElevatorControllerData.class).speed -= 0.05)
		.buttonSlot(8, 4, Material.ARROW, "Add speed", (c, cm) -> cm.getPassedData().getData("data", ElevatorControllerData.class).speed += 0.05)
		.buttonSlotResponse(7, 2, Material.BARRIER, ChatColor.RED + "Remove Controller", (c, cm) -> { Blocks.setBlockState(cm.getPassedData().getData("location", Location.class), null); return Response.exit(); })
		.itemSlot(1, 2, d -> d.getData("data", ElevatorControllerData.class).elevatorData, (d, b) -> d.getData("data", ElevatorControllerData.class).elevatorData = b, is -> Items.getCustomItem(is) == Interactable.ELEVATOR_DATA_ITEM)
		.toggleSlot(8, 0, "Show Points", d -> d.getData("data", ElevatorControllerData.class).showPoints, (d, b) -> d.getData("data", ElevatorControllerData.class).showPoints = b)
		.toggleSlot(7, 0, "Allow changing direction when moving", d -> d.getData("data", ElevatorControllerData.class).dirChange, (d, b) -> d.getData("data", ElevatorControllerData.class).dirChange = b)
		.toggleSlot(6, 0, "Seat Activator", d -> d.getData("data", ElevatorControllerData.class).seatActivator, (d, b) -> d.getData("data", ElevatorControllerData.class).seatActivator = b)
		.toggleSlot(3, 2, "Enabled", d -> d.getData("data", ElevatorControllerData.class).enabled, (d, b) -> d.getData("data", ElevatorControllerData.class).enabled = b)
		.applyMask(mask);
}
