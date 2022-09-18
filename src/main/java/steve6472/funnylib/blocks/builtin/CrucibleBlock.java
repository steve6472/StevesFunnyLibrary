package steve6472.funnylib.blocks.builtin;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveInt;
import steve6472.funnylib.json.codec.codecs.EntityCodec;
import steve6472.funnylib.util.Checks;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.List;

/**
 * Created by steve6472
 * Date: 9/18/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CrucibleBlock extends CustomBlock implements IBlockData, BlockClickEvents, BlockTick
{
	public static class CrucibleBlockData extends CustomBlockData
	{
		@SaveInt
		int cobblestoneAmount, lavaAmount;
		@Save(type = EntityCodec.class)
		Entity frame;
		@Save(type = EntityCodec.class)
		ArmorStand indicator, dataLabel;

		boolean zeroedOut = false;
	}

	@Override
	public void tick(BlockContext context)
	{
		CrucibleBlockData data = context.getBlockData(CrucibleBlockData.class);
		Material heatSource = context.getLocation().clone().add(0, -1, 0).getBlock().getType();

		data.dataLabel.setCustomName("cobble= " + data.cobblestoneAmount + ", lava= " + data.lavaAmount + ", zero= " + data.zeroedOut);

		int change = switch (heatSource)
			{
				case LAVA -> 5;
				case FIRE -> 4;
				case SOUL_FIRE -> 3;
				case TORCH -> 2;
				case SOUL_TORCH -> 1;
				default -> 0;
			};

		if (data.cobblestoneAmount > 0 && data.lavaAmount <= 2000)
		{
			data.zeroedOut = false;
			data.cobblestoneAmount -= change;
			data.lavaAmount += change;

			ArmorStand indicator = data.indicator;
			EntityEquipment equipment = indicator.getEquipment();

			if (data.lavaAmount >= 1000)
			{
				equipment.setHelmet(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(6).buildItemStack());
			} else if (data.cobblestoneAmount > 0)
			{
				equipment.setHelmet(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(7).buildItemStack());
			}

			indicator.teleport(context.getLocation().clone().add(0.5, 4d / 16d + lerp(0, 10d / 16d, (data.cobblestoneAmount + data.lavaAmount) / 2000d), 0.5));
		}

		if (data.cobblestoneAmount == 0 && data.lavaAmount == 0 && !data.zeroedOut)
		{
			ArmorStand indicator = data.indicator;
			indicator.teleport(context.getLocation().clone().add(0.5, 0.0, 0.5));
			data.zeroedOut = true;

			EntityEquipment equipment = indicator.getEquipment();
			equipment.setHelmet(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(1).buildItemStack());
		}
	}

	private static double lerp(double a, double b, double percentage)
	{
		return a + percentage * (b - a);
	}

	@Override
	public void leftClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (Checks.isPickaxeMaterial(context.getHandItem().getType()))
		{
			ItemStackBuilder.edit(context.getHandItem()).dealDamage(1).buildItemStack();
			Blocks.callBlockBreak(context);
		}
	}

	@Override
	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		if (context.isCreative())
			return;

		getDrops(context.blockContext(), drops);
	}

	@Override
	public void getDrops(BlockContext blockContext, List<ItemStack> drops)
	{
		drops.add(FunnyLib.CRUCIBLE_ITEM.newItemStack());
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (context.isPlayerSneaking())
		{
			return;
		}

		e.setCancelled(true);

		CrucibleBlockData data = context.getBlockData(CrucibleBlockData.class);

		if (context.getHandItem().getType() == Material.COBBLESTONE)
		{
			if (data.cobblestoneAmount + data.lavaAmount <= 1750)
			{
				data.cobblestoneAmount += 250;
				context.getHandItem().setAmount(context.getHandItem().getAmount() - 1);
				e.setUseItemInHand(Event.Result.ALLOW);
			}
		}

		if (context.getHandItem().getType() == Material.BUCKET)
		{
			if (data.lavaAmount >= 1000)
			{
				if (context.getHandItem().getAmount() == 1)
				{
					context.getHandItem().setType(Material.LAVA_BUCKET);
				}
				 else
				{
					context.getHandItem().setAmount(context.getHandItem().getAmount() - 1);
					context.getPlayer().getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
				}
				e.setUseItemInHand(Event.Result.ALLOW);

				data.lavaAmount -= 1000;
			}
		}
	}

	@Override
	public void onPlace(BlockContext context)
	{
		if (!(context.getBlockData() instanceof CrucibleBlockData data)) return;

		ItemFrame frame = context.getWorld().spawn(context.getLocation(), ItemFrame.class);
		frame.setFixed(true);
		frame.setFacingDirection(BlockFace.UP);
		frame.setVisible(false);
		frame.setInvulnerable(true);
		frame.setItem(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(2).buildItemStack());

		data.frame = frame;

		ArmorStand indicator = context.getWorld().spawn(context.getLocation().clone().add(0.5, 0.0, 0.5), ArmorStand.class);
		indicator.setInvisible(true);
		indicator.setMarker(true);
		indicator.setGravity(false);
		indicator.getEquipment().setHelmet(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(1).buildItemStack());
		data.indicator = indicator;

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
		CrucibleBlockData data = context.getBlockData(CrucibleBlockData.class);
		data.frame.remove();
		data.indicator.remove();
		data.dataLabel.remove();
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new CrucibleBlockData();
	}

	@Override
	public String id()
	{
		return "crucible";
	}

	@Override
	public BlockData getVanillaState(State state)
	{
		return Material.BARRIER.createBlockData();
	}
}
