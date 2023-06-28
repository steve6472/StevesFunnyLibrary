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

	public static CustomItem POPUP_BACKGROUND = reg(new GenericItem("popup_background", Material.COMMAND_BLOCK, "Popup", 12));
	public static CustomItem POPUP_NO_BACKGROUND = reg(new GenericItem("popup_no_background", Material.COMMAND_BLOCK, "Popup", 36));
	public static CustomItem POPUP_CLOSE = reg(new GenericItem("popup_close", Material.COMMAND_BLOCK, ChatColor.DARK_RED + "Close", 27));
	public static CustomItem POPUP_UP = reg(new GenericItem("popup_up", Material.COMMAND_BLOCK, ChatColor.WHITE + "Up", 28));
	public static CustomItem POPUP_DOWN = reg(new GenericItem("popup_down", Material.COMMAND_BLOCK, ChatColor.WHITE + "Down", 29));
	public static CustomItem POPUP_NO_BACKGROUND_TOP = reg(new GenericItem("popup_no_background_top", Material.COMMAND_BLOCK, "Popup", 40));
	public static CustomItem POPUP_REMOVE_EXP = reg(new GenericItem("popup_remove_exp", Material.COMMAND_BLOCK, ChatColor.DARK_RED + "Remove Expression", 27));

	public static CustomItem IF_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "if_icon").customModel(13).name(ChatColor.DARK_AQUA + "If").build());
	public static CustomItem IF_START = reg(new GenericItem("if_start", Material.COMMAND_BLOCK, "If", 6));
	public static CustomItem IF_THEN = reg(new GenericItem("if_end", Material.COMMAND_BLOCK, "Then", 8));
	public static CustomItem IF_EXTENSION = reg(GenericItem.builder(Material.COMMAND_BLOCK, "if_extension").name("If Block").customModel(11).addLore(ChatColor.GRAY + "Left click to go to start of If Block").addLore(ChatColor.GRAY + "Right click to go to end of If Block").build());
	public static CustomItem IF_END = reg(new GenericItem("if_end", Material.COMMAND_BLOCK, "If End", 7));

	public static CustomItem LOGIC_AND = reg(GenericItem.builder(Material.COMMAND_BLOCK, "logic_and").customModel(14).name(ChatColor.DARK_AQUA + "Logic And").build());
	public static CustomItem LOGIC_AND_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "logic_and_icon").customModel(15).name(ChatColor.DARK_AQUA + "Logic And").build());
	public static CustomItem LOGIC_OR = reg(GenericItem.builder(Material.COMMAND_BLOCK, "logic_or").customModel(16).name(ChatColor.DARK_AQUA + "Logic Or").build());
	public static CustomItem LOGIC_OR_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "logic_or_icon").customModel(17).name(ChatColor.DARK_AQUA + "Logic Or").build());

	public static CustomItem EQUALITY_EQUALS = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_equals").customModel(23).name(ChatColor.DARK_AQUA + "Equals").build());
	public static CustomItem EQUALITY_EQUALS_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_equals_icon").customModel(22).name(ChatColor.DARK_AQUA + "Equals").build());
	public static CustomItem EQUALITY_LESS = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_less").customModel(24).name(ChatColor.DARK_AQUA + "Less").build());
	public static CustomItem EQUALITY_LESS_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_less_icon").customModel(25).name(ChatColor.DARK_AQUA + "Less").build());
	public static CustomItem EQUALITY_LESS_EQUAL = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_less_equal").customModel(30).name(ChatColor.DARK_AQUA + "Less Equal").build());
	public static CustomItem EQUALITY_LESS_EQUAL_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_less_equal_icon").customModel(31).name(ChatColor.DARK_AQUA + "Less Equal").build());
	public static CustomItem EQUALITY_GREATER = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_greater").customModel(32).name(ChatColor.DARK_AQUA + "Greater").build());
	public static CustomItem EQUALITY_GREATER_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_greater_icon").customModel(33).name(ChatColor.DARK_AQUA + "Greater").build());
	public static CustomItem EQUALITY_GREATER_EQUAL = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_greater_equal").customModel(34).name(ChatColor.DARK_AQUA + "Greater Equal").build());
	public static CustomItem EQUALITY_GREATER_EQUAL_ICON = reg(GenericItem.builder(Material.COMMAND_BLOCK, "equality_greater_equal_icon").customModel(35).name(ChatColor.DARK_AQUA + "Greater Equal").build());

	public static CustomItem ADD_EXPRESSION = reg(new GenericItem("add_expression", Material.COMMAND_BLOCK, "Add Expression", 10));
	public static CustomItem PLACEHOLDER = reg(new GenericItem("placeholder", Material.LEATHER_HORSE_ARMOR, "Placeholder", 1));

	public static CustomItem LEFT = reg(new GenericItem("left", Material.COMMAND_BLOCK, "Left", 7));
	public static CustomItem OPERATOR = reg(new GenericItem("operator", Material.COMMAND_BLOCK, "Operator", 7));
	public static CustomItem RIGHT = reg(new GenericItem("right", Material.COMMAND_BLOCK, "Right", 7));

	public static CustomItem DEBUG_HERE = reg(new GenericItem("debug_here", Material.COMMAND_BLOCK, "Debug Here", 18));
	public static CustomItem DEBUG_HERE_ICON = reg(new GenericItem("debug_here_icon", Material.COMMAND_BLOCK, "Debug Here", 19));
	public static CustomItem DEBUG_HERE_INC = reg(new GenericItem("debug_here_inc", Material.LEATHER_HORSE_ARMOR, "Debug ID++", 1));
	public static CustomItem DEBUG_HERE_DEC = reg(new GenericItem("debug_here_dec", Material.LEATHER_HORSE_ARMOR, "Debug ID--", 1));
	public static CustomItem DEBUG_HERE_ID = reg(new GenericItem("debug_here_id", Material.LEATHER_HORSE_ARMOR, "Debug ID: 0", 1));

	public static CustomItem DELAY = reg(new GenericItem("delay", Material.COMMAND_BLOCK, "Delay", 20));
	public static CustomItem DELAY_ICON = reg(new GenericItem("delay_icon", Material.COMMAND_BLOCK, "Delay", 21));
	public static CustomItem DELAY_INC = reg(new GenericItem("delay_inc", Material.LEATHER_HORSE_ARMOR, "Delay ticks ++", 1));
	public static CustomItem DELAY_DEC = reg(new GenericItem("delay_dec", Material.LEATHER_HORSE_ARMOR, "Delay ticks --", 1));
	public static CustomItem DELAY_ID = reg(new GenericItem("delay_id", Material.LEATHER_HORSE_ARMOR, "Current Delay: 0 ticks", 1));

	public static CustomItem CONSTANT_NUMBER = reg(new GenericItem("const_int", Material.COMMAND_BLOCK, "Number: 0", 26));
	public static CustomItem AREA_LOCATION_EMPTY = reg(new GenericItem("area_location_empty", Material.COMMAND_BLOCK, "Area Location (empty)", 38));
	public static CustomItem AREA_LOCATION_FULL = reg(new GenericItem("area_location_full", Material.COMMAND_BLOCK, "Area Location (full)", 39));

	public static CustomItem PLAYER_IN_AREA = reg(new GenericItem("player_in_area", Material.COMMAND_BLOCK, "Player in Area", 37));

	public static CustomItem GIVE_ITEM = reg(new GenericItem("give_item", Material.COMMAND_BLOCK, "Give Item", 0));

	private static CustomItem reg(CustomItem customItem)
	{
		return Items.registerAdminItem(customItem, true);
	}

	// (a && c) || b
	// a || (b && c)

	public static void init() {}
}
