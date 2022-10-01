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
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveInt;
import steve6472.funnylib.json.codec.codecs.EntityCodec;
import steve6472.funnylib.util.Checks;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.exnulla.ExNulla;

import java.util.List;

/**
 * Created by steve6472
 * Date: 9/18/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class ItemFrameBlock extends CustomBlock implements IBlockData, BlockClickEvents
{
	public static class Data extends CustomBlockData
	{
		@Save(type = EntityCodec.class)
		Entity frame;
	}

	@Override
	public void leftClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (canBeBroken(context))
		{
			ItemStackBuilder.edit(context.getHandItem()).dealDamage(damageToolOnBlockBreak(context)).buildItemStack();
			Blocks.callBlockBreak(context);
		}
	}

	public abstract boolean canBeBroken(PlayerBlockContext context);
	public abstract int damageToolOnBlockBreak(PlayerBlockContext context);

	@Override
	public void onPlace(BlockContext context)
	{
		if (!(context.getBlockData() instanceof Data data)) return;

		ItemFrame frame = context.getWorld().spawn(context.getLocation(), ItemFrame.class);
		frame.setFixed(true);
		frame.setFacingDirection(BlockFace.UP);
		frame.setVisible(false);
		frame.setInvulnerable(true);
		frame.setItem(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(2).buildItemStack());

		data.frame = frame;
	}

	@Override
	public void onRemove(BlockContext context)
	{
		Data data = context.getBlockData(Data.class);
		data.frame.remove();
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new Data();
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return Material.BARRIER.createBlockData();
	}
}
