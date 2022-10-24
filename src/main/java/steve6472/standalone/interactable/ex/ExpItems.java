package steve6472.standalone.interactable.ex;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.GenericItem;
import steve6472.funnylib.item.Items;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpItems
{
	public static CustomItem TEST = Items.registerAdminItem(new GenericItem("ui_texture_test", Material.COMMAND_BLOCK, "Test", 9));

	public static CustomItem POPUP_TEST = Items.registerAdminItem(new GenericItem("popup_test", Material.COMMAND_BLOCK, "Popup", 12));
	public static CustomItem POPUP_CLOSE = Items.registerAdminItem(new GenericItem("popup_close", Material.COMMAND_BLOCK, ChatColor.DARK_RED + "Close", 27));
	public static CustomItem POPUP_UP = Items.registerAdminItem(new GenericItem("popup_up", Material.COMMAND_BLOCK, ChatColor.WHITE + "Up", 28));
	public static CustomItem POPUP_DOWN = Items.registerAdminItem(new GenericItem("popup_down", Material.COMMAND_BLOCK, ChatColor.WHITE + "Down", 29));

	public static CustomItem IF_ICON = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "if_icon").customModel(13).name(ChatColor.DARK_AQUA + "If").build());
	public static CustomItem IF_START = Items.registerAdminItem(new GenericItem("if_start", Material.COMMAND_BLOCK, "If", 6));
	public static CustomItem IF_THEN = Items.registerAdminItem(new GenericItem("if_end", Material.COMMAND_BLOCK, "Then", 8));
	public static CustomItem IF_EXTENSION = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "if_extension").name("If Block").customModel(11).addLore(ChatColor.GRAY + "Left click to go to start of If Block").addLore(ChatColor.GRAY + "Right click to go to end of If Block").build());
	public static CustomItem IF_END = Items.registerAdminItem(new GenericItem("if_end", Material.COMMAND_BLOCK, "If End", 7));

	public static CustomItem LOGIC_AND = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "logic_and").customModel(14).name(ChatColor.DARK_AQUA + "Logic And").build());
	public static CustomItem LOGIC_AND_ICON = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "logic_and_icon").customModel(15).name(ChatColor.DARK_AQUA + "Logic And").build());
	public static CustomItem LOGIC_OR = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "logic_or").customModel(16).name(ChatColor.DARK_AQUA + "Logic Or").build());
	public static CustomItem LOGIC_OR_ICON = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "logic_or_icon").customModel(17).name(ChatColor.DARK_AQUA + "Logic Or").build());

	public static CustomItem EQUALITY_EQUALS = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "equality_equals").customModel(23).name(ChatColor.DARK_AQUA + "Equals").build());
	public static CustomItem EQUALITY_EQUALS_ICON = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "equality_equals_icon").customModel(22).name(ChatColor.DARK_AQUA + "Equals").build());
	public static CustomItem EQUALITY_LESS = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "equality_less").customModel(24).name(ChatColor.DARK_AQUA + "Less").build());
	public static CustomItem EQUALITY_LESS_ICON = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "equality_less_icon").customModel(25).name(ChatColor.DARK_AQUA + "Less").build());

	public static CustomItem ADD_EXPRESSION = Items.registerAdminItem(new GenericItem("add_expression", Material.COMMAND_BLOCK, "Add Expression", 10));
	public static CustomItem PLACEHOLDER = Items.registerAdminItem(new GenericItem("placeholder", Material.LEATHER_HORSE_ARMOR, "Placeholder", 1));

	public static CustomItem LEFT = Items.registerAdminItem(new GenericItem("left", Material.COMMAND_BLOCK, "Left", 7));
	public static CustomItem OPERATOR = Items.registerAdminItem(new GenericItem("operator", Material.COMMAND_BLOCK, "Operator", 7));
	public static CustomItem RIGHT = Items.registerAdminItem(new GenericItem("right", Material.COMMAND_BLOCK, "Right", 7));

	public static CustomItem DEBUG_HERE = Items.registerAdminItem(new GenericItem("debug_here", Material.COMMAND_BLOCK, "Debug Here", 18));
	public static CustomItem DEBUG_HERE_ICON = Items.registerAdminItem(new GenericItem("debug_here_icon", Material.COMMAND_BLOCK, "Debug Here", 19));
	public static CustomItem DEBUG_HERE_INC = Items.registerAdminItem(new GenericItem("debug_here_inc", Material.LEATHER_HORSE_ARMOR, "Debug ID++", 1));
	public static CustomItem DEBUG_HERE_DEC = Items.registerAdminItem(new GenericItem("debug_here_dec", Material.LEATHER_HORSE_ARMOR, "Debug ID--", 1));
	public static CustomItem DEBUG_HERE_ID = Items.registerAdminItem(new GenericItem("debug_here_id", Material.LEATHER_HORSE_ARMOR, "Debug ID: 0", 1));

	public static CustomItem DELAY = Items.registerAdminItem(new GenericItem("delay", Material.COMMAND_BLOCK, "Delay", 20));
	public static CustomItem DELAY_ICON = Items.registerAdminItem(new GenericItem("delay_icon", Material.COMMAND_BLOCK, "Delay", 21));
	public static CustomItem DELAY_INC = Items.registerAdminItem(new GenericItem("delay_inc", Material.LEATHER_HORSE_ARMOR, "Delay ticks ++", 1));
	public static CustomItem DELAY_DEC = Items.registerAdminItem(new GenericItem("delay_dec", Material.LEATHER_HORSE_ARMOR, "Delay ticks --", 1));
	public static CustomItem DELAY_ID = Items.registerAdminItem(new GenericItem("delay_id", Material.LEATHER_HORSE_ARMOR, "Current Delay: 0 ticks", 1));

	public static CustomItem CONSTANT_NUMBER = Items.registerAdminItem(new GenericItem("const_int", Material.COMMAND_BLOCK, "Number: 0", 26));

	// (a && c) || b
	// a || (b && c)

	public static void init() {}
}
