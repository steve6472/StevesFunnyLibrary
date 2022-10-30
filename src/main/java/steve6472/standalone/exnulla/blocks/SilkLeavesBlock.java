package steve6472.standalone.exnulla.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.*;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveDouble;
import steve6472.funnylib.json.codec.codecs.EntityCodec;
import steve6472.funnylib.util.Checks;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.RandomUtil;
import steve6472.standalone.exnulla.ExNulla;

import java.util.List;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class SilkLeavesBlock extends CustomBlock implements IBlockData, BlockTick
{
	public static class SilkLeavesBlockData extends CustomBlockData
	{
		@SaveDouble
		public double percentage;

		@Save(value = EntityCodec.class)
		public ItemFrame itemFrame;
	}

	@Override
	public String id()
	{
		return "silk_leaves";
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return Material.COBWEB.createBlockData();
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new SilkLeavesBlockData();
	}

	@Override
	public void tick(BlockContext context)
	{
		SilkLeavesBlockData silkData = context.getBlockData(SilkLeavesBlockData.class);

		if (silkData.percentage != 100 && RandomUtil.decide(20))
		{
			silkData.percentage += RandomUtil.randomDouble(1, 5);
			if (silkData.percentage > 100)
				silkData.percentage = 100;
			silkData.itemFrame.setItem(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(4).setArmorColor(blend(0x77AB2F, 0xffffff, (float) (silkData.percentage / 100f))).buildItemStack());
		}

		if (RandomUtil.decide(40) && silkData.percentage > 25)
		{
			Block block = context.getLocation()
				.clone()
				.add(MiscUtil.DIRECTIONS[RandomUtil.randomInt(0, 5)].getDirection())
				.getBlock();

			if (Checks.isLeavesMaterial(block.getType()))
			{
				Blocks.setBlockState(block.getLocation(), ExNulla.SILK_LEAVES.getDefaultState());
			}
		}
	}

	@Override
	public void onPlace(BlockContext context)
	{
		if (!(context.getBlockData() instanceof SilkLeavesBlockData silkData)) return;

		ItemFrame frame = context.getWorld().spawn(context.getLocation(), ItemFrame.class);
		frame.setFixed(true);
		frame.setFacingDirection(States.FACING_HORIZONTAL.getPossibleValues()[RandomUtil.randomInt(0, 3)]);
		frame.setVisible(false);
		frame.setInvulnerable(true);
		frame.setItem(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(4).setArmorColor(0x77AB2F).buildItemStack());

		silkData.itemFrame = frame;
	}

	@Override
	public void onRemove(BlockContext context)
	{
		context.getBlockData(SilkLeavesBlockData.class).itemFrame.remove();
	}

	@Override
	public void getDrops(BlockContext blockContext, List<ItemStack> drops)
	{
		SilkLeavesBlockData silkData = blockContext.getBlockData(SilkLeavesBlockData.class);

		if (silkData.percentage < 95)
		{
			int amount = RandomUtil.randomInt(0, 4);
			if (amount == 0)
			{
				ItemStack silk = new ItemStack(Material.STRING);
				drops.add(silk);
			}
		}
	}

	@Override
	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		if (context.isCreative())
			return;

		SilkLeavesBlockData silkData = context.getBlockData(SilkLeavesBlockData.class);

		if (silkData.percentage >= 95)
		{
			int amount = RandomUtil.randomInt(0, 4);

			if (context.holdsCustomItem(ExNulla.WOODEN_CROOCK))
			{
				amount += RandomUtil.randomInt(0, 3);

				if (RandomUtil.randomDouble(0, 1) <= 0.15)
				{
					drops.add(ExNulla.SILKWORM.newItemStack());
				}
			}

			if (amount > 0)
			{
				ItemStack silk = new ItemStack(Material.STRING);
				silk.setAmount(amount);
				drops.add(silk);
			}
		} else
		{
			getDrops(context.blockContext(), drops);
		}
	}

	public static int blend(int c1, int c2, float ratio)
	{
		if ( ratio > 1f ) ratio = 1f;
		else if ( ratio < 0f ) ratio = 0f;
		float iRatio = 1.0f - ratio;

		int r1 = ((c1 & 0xff0000) >> 16);
		int g1 = ((c1 & 0xff00) >> 8);
		int b1 = (c1 & 0xff);

		int r2 = ((c2 & 0xff0000) >> 16);
		int g2 = ((c2 & 0xff00) >> 8);
		int b2 = (c2 & 0xff);

		int r = (int)((r1 * iRatio) + (r2 * ratio));
		int g = (int)((g1 * iRatio) + (g2 * ratio));
		int b = (int)((b1 * iRatio) + (b2 * ratio));

		return r << 16 | g << 8 | b;
	}
}
