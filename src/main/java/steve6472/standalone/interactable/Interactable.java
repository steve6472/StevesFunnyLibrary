package steve6472.standalone.interactable;

import org.bukkit.Material;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.item.BlockPlacerItem;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.standalone.interactable.blocks.ActivatingButtonBlock;
import steve6472.standalone.interactable.blocks.ElevatorControllerBlock;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Interactable
{
	public static CustomBlock ELEVATOR_CONTROLLER_BLOCK;
	public static CustomBlock ACTIVATING_BUTTON_BLOCK;

	public static CustomItem ELEVATOR_CONTROLLER_ITEM;
	public static CustomItem ACTIVATING_BUTTON_ITEM;

	public static void init()
	{
		Blocks.registerBlock(ELEVATOR_CONTROLLER_BLOCK = new ElevatorControllerBlock());
		Blocks.registerBlock(ACTIVATING_BUTTON_BLOCK = new ActivatingButtonBlock());

		Items.registerAdminItem(ELEVATOR_CONTROLLER_ITEM = new BlockPlacerItem(ELEVATOR_CONTROLLER_BLOCK, "elevator_controller", Material.REPEATING_COMMAND_BLOCK, "Elevator Controller", 0));
		Items.registerAdminItem(ACTIVATING_BUTTON_ITEM = new BlockPlacerItem(ACTIVATING_BUTTON_BLOCK, "activating_button", Material.WARPED_BUTTON, "Activating Button", 0));
	}
}
