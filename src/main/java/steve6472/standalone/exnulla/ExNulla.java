package steve6472.standalone.exnulla;

import org.bukkit.Material;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.item.BlockPlacerItem;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.standalone.exnulla.blocks.CrucibleBlock;
import steve6472.standalone.exnulla.blocks.SilkLeavesBlock;
import steve6472.standalone.exnulla.items.PocketFurnaceItem;
import steve6472.standalone.exnulla.items.SilkwormItem;
import steve6472.standalone.exnulla.items.WoodenCroockItem;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExNulla
{
	public static CustomBlock SILK_LEAVES;
	public static CustomBlock CRUCIBLE_BLOCK;

	public static CustomItem WOODEN_CROOCK;
	public static CustomItem SILKWORM;
	public static CustomItem CRUCIBLE_ITEM;
	public static CustomItem POCKET_FURNACE;

	public static void init()
	{
		Blocks.registerBlock(SILK_LEAVES = new SilkLeavesBlock());
		Blocks.registerBlock(CRUCIBLE_BLOCK = new CrucibleBlock());

		Items.registerItem(WOODEN_CROOCK = new WoodenCroockItem());
		Items.registerItem(SILKWORM = new SilkwormItem());
		Items.registerItem(POCKET_FURNACE = new PocketFurnaceItem());
		Items.registerItem(CRUCIBLE_ITEM = new BlockPlacerItem(CRUCIBLE_BLOCK, "crucible", Material.COMMAND_BLOCK, "Crucible", 2));
	}
}
