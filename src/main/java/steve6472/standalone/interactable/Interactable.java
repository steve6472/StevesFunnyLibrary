package steve6472.standalone.interactable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.command.AnnotationCommand;
import steve6472.funnylib.item.BlockPlacerItem;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.GenericItem;
import steve6472.funnylib.item.Items;
import steve6472.standalone.interactable.blocks.*;
import steve6472.standalone.interactable.ex.ExpItems;
import steve6472.standalone.interactable.ex.ExpressionMenu;
import steve6472.standalone.interactable.items.CheckpointItem;
import steve6472.standalone.interactable.script.ScriptCommands;
import steve6472.standalone.interactable.script.ScriptRepository;
import steve6472.standalone.interactable.worldbutton.WorldButtonListener;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Interactable
{
//	public static ScriptRepository scriptRepository;

	public static CustomBlock ELEVATOR_CONTROLLER_BLOCK;
	public static CustomBlock ACTIVATING_BUTTON_BLOCK;
	public static CustomBlock CODE_BLOCK;
	public static CustomBlock CHECKPOINT_BLOCK;
	public static ElevatorEditorBlock ELEVATOR_EDITOR_BLOCK;
	public static CustomBlock LABEL_BLOCK;

	public static CustomItem ELEVATOR_CONTROLLER_ITEM;
	public static CustomItem ACTIVATING_BUTTON_ITEM;
	public static CustomItem ELEVATOR_EDITOR_ITEM;
	public static CustomItem ELEVATOR_DATA_ITEM;
	public static CustomItem CODE_BLOCK_ITEM;
	public static CustomItem CHECKPOINT_BLOCK_ITEM;
	public static CustomItem CHECKPOINT_ITEM;
	public static CustomItem LABEL_BLOCK_ITEM;

	public static void init()
	{
		Bukkit.getPluginManager().registerEvents(new WorldButtonListener(), FunnyLib.getPlugin());

		Blocks.registerBlock(ELEVATOR_CONTROLLER_BLOCK = new ElevatorControllerBlock());
		Blocks.registerBlock(ACTIVATING_BUTTON_BLOCK = new ActivatingButtonBlock());
		Blocks.registerBlock(ELEVATOR_EDITOR_BLOCK = new ElevatorEditorBlock());
		Blocks.registerBlock(CODE_BLOCK = new CodeBlock());
		Blocks.registerBlock(CHECKPOINT_BLOCK = new CheckpointBlock());
		Blocks.registerBlock(LABEL_BLOCK = new LabelBlock());

		Items.registerAdminItem(ELEVATOR_CONTROLLER_ITEM = new BlockPlacerItem(ELEVATOR_CONTROLLER_BLOCK, "elevator_controller", Material.REPEATING_COMMAND_BLOCK, "Elevator Controller", 0));
		Items.registerAdminItem(ACTIVATING_BUTTON_ITEM = new BlockPlacerItem(ACTIVATING_BUTTON_BLOCK, "activating_button", Material.WARPED_BUTTON, "Activating Button", 0));
		Items.registerAdminItem(ELEVATOR_EDITOR_ITEM = new BlockPlacerItem(ELEVATOR_EDITOR_BLOCK, "elevator_editor", Material.CHAIN_COMMAND_BLOCK, "Elevator Editor", 0));
		Items.registerAdminItem(CODE_BLOCK_ITEM = new BlockPlacerItem(CODE_BLOCK, "code_block", Material.COMMAND_BLOCK, "Code Block", 0));
		Items.registerAdminItem(ELEVATOR_DATA_ITEM = new GenericItem("elevator_data", Material.ENCHANTED_BOOK, "Elevator Data", 0));
		Items.registerAdminItem(CHECKPOINT_BLOCK_ITEM = new BlockPlacerItem(CHECKPOINT_BLOCK, "checkpoint", Material.LIGHT_WEIGHTED_PRESSURE_PLATE, "Checkpoint", 0));
		Items.registerItem(CHECKPOINT_ITEM = new CheckpointItem());
		Items.registerAdminItem(LABEL_BLOCK_ITEM = new BlockPlacerItem(LABEL_BLOCK, "label_block", Material.NAME_TAG, "Label Block", 0));

		ExpItems.init();
		AnnotationCommand.registerCommands(ExpressionMenu.class);

		FunnyLib.getPlugin().saveResource("transformers/minecraft.txt", true);
//		scriptRepository = new ScriptRepository();
//		Bukkit.getPluginManager().registerEvents(scriptRepository, FunnyLib.getPlugin());
		AnnotationCommand.registerCommands(ScriptCommands.class);
	}
}
