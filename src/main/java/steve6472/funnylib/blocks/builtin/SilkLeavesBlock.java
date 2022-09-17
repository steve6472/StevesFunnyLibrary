package steve6472.funnylib.blocks.builtin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.*;
import steve6472.funnylib.blocks.events.BreakBlockEvent;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveDouble;
import steve6472.funnylib.json.codec.codecs.EntityCodec;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.RandomUtil;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class SilkLeavesBlock extends CustomBlock implements IBlockData, BlockTick, BreakBlockEvent
{
	public static class SilkLeavesBlockData extends CustomBlockData
	{
		@SaveDouble
		public double percentage;

		@Save(type = EntityCodec.class)
		public ItemFrame itemFrame;
	}

	@Override
	public String id()
	{
		return "silk_leaves";
	}

	@Override
	public org.bukkit.block.data.BlockData getVanillaState(State state)
	{
		return Material.COBWEB.createBlockData();
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new SilkLeavesBlockData();
	}

	@Override
	public void tick(State state, Location location, CustomBlockData data)
	{
		if (!(data instanceof SilkLeavesBlockData silkData)) return;

		if (RandomUtil.decide(20))
		{
			silkData.percentage += RandomUtil.randomDouble(1, 5);
			if (silkData.percentage > 100)
				silkData.percentage = 100;
			silkData.itemFrame.setItem(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(4).setArmorColor(blend(0x77AB2F, 0xffffff, (float) (silkData.percentage / 100f))).buildItemStack());
		}
	}

	@Override
	public void onPlace(Location location, State state, CustomBlockData data)
	{
		if (!(data instanceof SilkLeavesBlockData silkData)) return;

		ItemFrame frame = location.getWorld().spawn(location, ItemFrame.class);
		frame.setFixed(true);
		frame.setFacingDirection(States.FACING_HORIZONTAL.getPossibleValues()[RandomUtil.randomInt(0, 3)]);
		frame.setVisible(false);
		frame.setInvulnerable(true);
		frame.setItem(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setCustomModelData(4).setArmorColor(0x77AB2F).buildItemStack());

		silkData.itemFrame = frame;
	}

	@Override
	public void onRemove(Location location, State state, CustomBlockData data)
	{
		if (!(data instanceof SilkLeavesBlockData silkData)) return;
		silkData.itemFrame.remove();

		if (silkData.percentage == 100)
		{
			int amount = RandomUtil.randomInt(0, 4);
			if (amount > 0)
			{
				ItemStack silk = new ItemStack(Material.STRING);
				silk.setAmount(amount);
				location.getWorld().dropItemNaturally(location, silk);
			}
		}
	}

	@Override
	public void breakBlock(ItemStack item, State state, CustomBlockData data, BlockBreakEvent e)
	{
		e.setCancelled(true);
		e.getBlock().setType(Material.AIR);
	}

	int blend(int c1, int c2, float ratio)
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
