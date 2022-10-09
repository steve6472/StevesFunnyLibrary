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

	public static CustomItem IF_START = Items.registerAdminItem(new GenericItem("if_start", Material.COMMAND_BLOCK, "If", 6));
	public static CustomItem IF_THEN = Items.registerAdminItem(new GenericItem("if_end", Material.COMMAND_BLOCK, "Then", 8));
	public static CustomItem IF_EXTENSION = Items.registerAdminItem(GenericItem.builder(Material.COMMAND_BLOCK, "if_extension").name("If Block").customModel(11).addLore(ChatColor.GRAY + "Left click to go to start of If Block").addLore(ChatColor.GRAY + "Right click to go to end of If Block").build());
	public static CustomItem IF_END = Items.registerAdminItem(new GenericItem("if_end", Material.COMMAND_BLOCK, "If End", 7));

	public static CustomItem ADD_EXPRESSION = Items.registerAdminItem(new GenericItem("add_expression", Material.COMMAND_BLOCK, "Add Expression", 10));
	public static CustomItem PLACEHOLDER = Items.registerAdminItem(new GenericItem("placeholder", Material.STONE, "Placeholder", 0));

	public static CustomItem LEFT = Items.registerAdminItem(new GenericItem("left", Material.COMMAND_BLOCK, "Left", 7));
	public static CustomItem OPERATOR = Items.registerAdminItem(new GenericItem("operator", Material.COMMAND_BLOCK, "Operator", 7));
	public static CustomItem RIGHT = Items.registerAdminItem(new GenericItem("right", Material.COMMAND_BLOCK, "Right", 7));

	public static void init() {}
}
