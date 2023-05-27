package steve6472.standalone.machinal;

import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.item.*;
import steve6472.funnylib.util.Checks;
import steve6472.standalone.Skins;
import steve6472.standalone.interactable.blocks.ElevatorControllerBlock;
import steve6472.standalone.machinal.blocks.GenericFullHeadBlock;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Machinal
{
	public static CustomBlock HEAVY_MACHINE_BLOCK;
	public static CustomBlock LIGHT_MACHINE_BLOCK;

	public static CustomItem HEAVY_MACHINE_BLOCK_ITEM;
	public static CustomItem LIGHT_MACHINE_BLOCK_ITEM;

	public static void init()
	{
		Blocks.registerBlock(LIGHT_MACHINE_BLOCK = new GenericFullHeadBlock("light_machine_block", (c) -> Checks.isPickaxeMaterial(c.getHandItem().getType()), (c) -> 1)
			.setPlayerDrops((p, l) -> {if (p.isSurvival()) l.add(LIGHT_MACHINE_BLOCK_ITEM.newItemStack());})
			.setBlockDrops((b, l) -> l.add(LIGHT_MACHINE_BLOCK_ITEM.newItemStack()))
		);

		Blocks.registerBlock(HEAVY_MACHINE_BLOCK = new GenericFullHeadBlock("heavy_machine_block", (c) -> Checks.isPickaxeMaterial(c.getHandItem().getType()), (c) -> 1)
			.setPlayerDrops((p, l) -> {if (p.isSurvival()) l.add(HEAVY_MACHINE_BLOCK_ITEM.newItemStack());})
			.setBlockDrops((b, l) -> l.add(HEAVY_MACHINE_BLOCK_ITEM.newItemStack()))
			.rotate()

		);

		Items.registerItem(HEAVY_MACHINE_BLOCK_ITEM = new PlacerHeadItem(HEAVY_MACHINE_BLOCK, Skins.Items.HEAVY_MACHINE_BLOCK, "heavy_machine_block", "Heavy Machine Block"));
		Items.registerItem(LIGHT_MACHINE_BLOCK_ITEM = new PlacerHeadItem(LIGHT_MACHINE_BLOCK, Skins.Items.LIGHT_MACHINE_BLOCK, "light_machine_block", "Light Machine Block"));
	}
}
