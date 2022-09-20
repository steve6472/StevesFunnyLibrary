package steve6472.standalone.interactable.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveBool;
import steve6472.funnylib.json.codec.ann.SaveDouble;
import steve6472.funnylib.json.codec.ann.SaveInt;
import steve6472.funnylib.json.codec.codecs.EntityCodec;
import steve6472.funnylib.json.codec.codecs.MarkerCodec;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.*;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ElevatorControllerBlock extends CustomBlock implements IBlockData, BlockTick, BlockClickEvents, AdminInterface<ElevatorControllerBlock.ElevatorControllerData>, Activable
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

		if (data.movingDirection == 1)
		{
			data.progress -= data.speed * (1.0 / data.pointA.distance(data.pointB) / 20.0);
			if (data.progress < 0)
			{
				data.progress = 0;
				data.movingDirection = 0;
			}
		} else if (data.movingDirection == 2)
		{
			data.progress += data.speed * (1.0 / data.pointA.distance(data.pointB) / 20.0);
			if (data.progress > 1)
			{
				data.progress = 1;
				data.movingDirection = 0;
			}
		}

		double x = lerp(data.pointA.getX(), data.pointB.getX(), data.progress);
		double y = lerp(data.pointA.getY(), data.pointB.getY(), data.progress);
		double z = lerp(data.pointA.getZ(), data.pointB.getZ(), data.progress);
		context.getWorld().spawnParticle(Particle.END_ROD, x + 0.5, y + 0.5, z + 0.5, 0);
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
	}

	@Override
	public void activate(BlockContext context)
	{
		ElevatorControllerData data = context.getBlockData(ElevatorControllerData.class);
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
			data.movingDirection = 1;
		} else if (data.progress == 0.0)
		{
			data.movingDirection = 2;
		}
	}

	public static class ElevatorControllerData extends CustomBlockData
	{
		@Save(type = MarkerCodec.class)
		public Vector pointA, pointB;

		@SaveDouble
		public double speed = 1d, progress;

		@SaveBool
		public boolean showPoints, dirChange;

		@SaveInt
		public int movingDirection; // 0 - none, 1 - to point A, 2 - to point B

		@Save(type = EntityCodec.class)
		ArmorStand dataLabel;
	}

	@Override
	public void showInterface(ElevatorControllerData data, PlayerBlockContext context)
	{
		MENU.setData("data", data);
		MENU.build().showToPlayers(context.getPlayer());
	}

	private static final Mask mask = Mask.createMask()
		.addRow("PPPaaaa..")
		.addRow("P.Paaaaaa")
		.addRow("aaaaa...a")
		.addRow("O.Oaaaaaa")
		.addRow("OOOaaaaaa")
		.addItem('V', SlotBuilder.create(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "")))
		.addItem('P', SlotBuilder.create(ItemStackBuilder.quick(Material.PINK_STAINED_GLASS_PANE, "")))
		.addItem('O', SlotBuilder.create(ItemStackBuilder.quick(Material.ORANGE_STAINED_GLASS_PANE, "")))
		.addItem('a', SlotBuilder.create(ItemStackBuilder.quick(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "")));

	private static final MenuBuilder MENU = MenuBuilder
		.create(5, "Elevator Controller")
		.allowPlayerInventory()
		.slot(1, 1, m ->
		{
			ElevatorControllerData data = m.getData("data", ElevatorControllerData.class);
			return MarkerCodec.slotBuilder(data.pointA, v -> data.pointA = v);
		})
		.slot(1, 3, m ->
		{
			ElevatorControllerData data = m.getData("data", ElevatorControllerData.class);
			return MarkerCodec.slotBuilder(data.pointB, v -> data.pointB = v);
		})
		.toggleSlot(8, 0, "Show Points", d -> d.getData("data", ElevatorControllerData.class).showPoints, (d, b) -> d.getData("data", ElevatorControllerData.class).showPoints = b)
		.toggleSlot(7, 0, "Allow changing direction when moving", d -> d.getData("data", ElevatorControllerData.class).dirChange, (d, b) -> d.getData("data", ElevatorControllerData.class).dirChange = b)
		.applyMask(mask);
}
