package steve6472.standalone.interactable.blocks;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.builtin.IMultiBlock;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.StructureItem;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.BlockGen;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ElevatorEditorBlock extends CustomBlock implements IBlockData, AdminInterface<ElevatorEditorData>, IMultiBlock, BlockTick
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.AQUA, 1f);

	@Override
	public String id()
	{
		return "elevator_editor";
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return BlockGen.ChainCommandBlock(false, BlockFace.DOWN);
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new ElevatorEditorData();
	}

	@Override
	public void showInterface(ElevatorEditorData data, PlayerBlockContext context)
	{
		MENU.setData("data", data);
		MENU.build().showToPlayers(context.getPlayer());
	}

	@Override
	public Vector multiblockSize()
	{
		return new Vector(6, 1, 1);
	}

	@Override
	public void tick(BlockContext context)
	{
		ElevatorEditorData blockData = context.getBlockData(ElevatorEditorData.class);
		if (blockData == null) return;

		if (FunnyLib.getUptimeTicks() % 7 == 0)
		{
			Vector size = StructureItem.getSize(blockData.structure);

			Location location = context.getLocation().clone().add(1, 0, 2);

			int x0 = location.getBlockX();
			int y0 = location.getBlockY();
			int z0 = location.getBlockZ();
			int x1 = size.getBlockX() + location.getBlockX();
			int y1 = size.getBlockY() + location.getBlockY();
			int z1 = size.getBlockZ() + location.getBlockZ();

			ParticleUtil.boxAbsolute(context.getWorld(), Particle.REDSTONE, x0, y0, z0, x1 + 1, y1 + 1, z1 + 1, 0, 1.0, OPTIONS);
		}

		for (Player player : context.getWorld().getPlayers())
		{
			if (player.getLocation().distance(context.getLocation()) < 16)
			{
				JSONMessage.create("Offset: ")
					.then("X: ").color(ChatColor.RED).then("" + blockData.offsetX).color(ChatColor.RED)
					.then(" Y: ").color(ChatColor.GREEN).then("" + blockData.offsetY).color(ChatColor.GREEN)
					.then(" Z: ").color(ChatColor.BLUE).then("" + blockData.offsetZ).color(ChatColor.BLUE)
					.actionbar(player);
			}
		}
	}

	@Override
	public void onRemove(BlockContext context)
	{
		breakMultiblock(context);
	}

	private static final Mask mask = Mask.createMask()
		.addRow("VVVaaaaaa")
		.addRow("S.Vaaaaaa")
		.addRow("VPVaaaaaa")
		.addRow("E.Paaaaaa")
		.addRow("PPPaaaaaa")
		.addItem('V', SlotBuilder.create(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "")))
		.addItem('S', SlotBuilder.create(ItemStackBuilder.quick(Material.STRUCTURE_BLOCK, "Structure File")))
		.addItem('E', SlotBuilder.create(ItemStackBuilder.quick(Material.RESPAWN_ANCHOR, "Elevator Data")))
		.addItem('P', SlotBuilder.create(ItemStackBuilder.quick(Material.PINK_STAINED_GLASS_PANE, "")))
		.addItem('O', SlotBuilder.create(ItemStackBuilder.quick(Material.ORANGE_STAINED_GLASS_PANE, "")))
		.addItem('a', SlotBuilder.create(ItemStackBuilder.quick(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "")));

	private static final MenuBuilder MENU = MenuBuilder
		.create(5, "Elevator Editor")
		.allowPlayerInventory()
		.itemSlot(1, 1, d -> d.getData("data", ElevatorEditorData.class).structure, (d, i) -> d.getData("data", ElevatorEditorData.class).structure = i, i -> Items.getCustomItem(i) == FunnyLib.STRUCTURE)
		.applyMask(mask);
}
