package steve6472.funnylib.item.builtin;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.builtin.IMultiBlock;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.GenericItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/25/2022
 * Project: StevesFunnyLibrary <br>
 */
public class MultiBlockPlacerItem extends GenericItem implements TickInHandEvent
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.BLACK, 0.5f);

	private final CustomBlock block;
	private final IMultiBlock multiBlock;

	public MultiBlockPlacerItem(IMultiBlock block, String id, Material material, String name, int customModelId)
	{
		super(id, material, name, customModelId);

		if (block instanceof CustomBlock cb)
			this.block = cb;
		else
			throw new RuntimeException("block is not an instance of CustomBlock");
		multiBlock = block;
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (useType != UseType.RIGHT)
			return;

		Location location = context.getBlockLocation().clone().add(context.getFace().getDirection());
		if (location.getBlock().getType().isAir())
		{
			State stateForPlacement = Items.callWithItemContextR(context.getPlayer(), EquipmentSlot.HAND, context.getHandItem(), ic -> block.getStateForPlacement(new PlayerBlockContext(ic, new BlockFaceContext(location, context.getFace()))));
			Blocks.setBlockState(location, stateForPlacement);
			if (context.getHandItem() != null && context.isSurvival())
			{
				context.getHandItem().setAmount(context.getHandItem().getAmount() - 1);
			}

			Vector vector = multiBlock.multiblockSize();
			for (int i = 0; i < vector.getBlockX(); i++)
			{
				for (int j = 0; j < vector.getBlockY(); j++)
				{
					for (int k = 0; k < vector.getBlockZ(); k++)
					{
						if (i == 0 && j == 0 && k == 0)
							continue;

						Blocks.setBlockState(location.clone().add(i, j, k), FunnyLib.MULTI_BLOCK.getDefaultState());
					}
				}
			}
		}
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0)
			return;

		Vector vector = multiBlock.multiblockSize();

		RayTraceResult rayTraceResult = context.getPlayer().rayTraceBlocks(6);
		if (rayTraceResult == null || rayTraceResult.getHitBlock() == null || rayTraceResult.getHitBlockFace() == null)
			return;
		Location location = rayTraceResult.getHitBlock().getLocation().add(rayTraceResult.getHitBlockFace().getDirection());

		int x0 = location.getBlockX();
		int y0 = location.getBlockY();
		int z0 = location.getBlockZ();
		int x1 = vector.getBlockX() + location.getBlockX();
		int y1 = vector.getBlockY() + location.getBlockY();
		int z1 = vector.getBlockZ() + location.getBlockZ();

		ParticleUtil.boxAbsolute(context.getPlayer(), Particle.COMPOSTER, x0, y0, z0, x1, y1, z1, 0, 0.25);

	}
}
