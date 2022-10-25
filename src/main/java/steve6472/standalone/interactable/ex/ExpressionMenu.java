package steve6472.standalone.interactable.ex;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Description;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.json.JsonPrettify;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.Preconditions;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpressionMenu
{
	public static final Mask MAIN_MASK = Mask.createMask()
		.addRow(".........", 5)
		.addRow("XXXXXUDLR")
		.addItem('X', SlotBuilder.create(ItemStackBuilder.create(Material.WHITE_STAINED_GLASS_PANE).setName("").buildItemStack()).setSticky())
		.addItem('U', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Up").setCustomModelData(8).buildItemStack(), (c, m) -> m.move(0, -1)))
		.addItem('D', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Down").setCustomModelData(9).buildItemStack(), (c, m) -> m.move(0, 1)))
		.addItem('L', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Left").setCustomModelData(10).buildItemStack(), (c, m) -> m.move(-1, 0)))
		.addItem('R', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Right").setCustomModelData(11).buildItemStack(), (c, m) -> m.move(1, 0)))
		;

	public static final Mask POPUP = Mask.createMask()
		.addRow("...___...")
		.addRow(".______X.")
		.addRow("._______.")
		.addRow(".______U.")
		.addRow(".______D.")
		.addRow("____P____")
		.addItem('_', SlotBuilder.create(MiscUtil.AIR).setSticky())
		.addItem('P', SlotBuilder.create(ExpItems.POPUP_TEST.newItemStack()).setSticky())
		.addItem('X', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_CLOSE.newItemStack(), (c, m) ->
		{
			showMenuToPlayer(c.player());
			m.removeMetadata("popup");
		}))
		.addItem('U', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_UP.newItemStack(), (c, m) ->
		{
			Menu popupMenu = m.getMetadata("popup", Menu.class);
			popupMenu.move(0, -1);
			popupMenu.overlay(m, 1, 1, 6, 4);
		}))
		.addItem('D', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_DOWN.newItemStack(), (c, m) ->
		{
			Menu popupMenu = m.getMetadata("popup", Menu.class);
			popupMenu.move(0, 1);
			popupMenu.overlay(m, 1, 1, 6, 4);
		}))
		;

	public static final MenuBuilder EXPRESSIONS_LIST = MenuBuilder.create(6, "Select Control Expression")
		.customBuilder(mb -> {
			List<Expressions.ExpressionEntry> expressions = Expressions.getExpressions(Expression.Type.CONTROL);
			for (int i = 0; i < expressions.size(); i++)
			{
				Expressions.ExpressionEntry entry = expressions.get(i);
				mb.slot(i, SlotBuilder.buttonSlot(entry.icon().newItemStack(), addExpression(entry.constructor())));
			}
		});

	public static final MenuBuilder BOOL_LIST = MenuBuilder.create(6, "Select Bool Expression")
		.customBuilder(mb -> {
			List<Expressions.ExpressionEntry> expressions = Expressions.getExpressions(Expression.Type.BOOL);
			for (int i = 0; i < expressions.size(); i++)
			{
				Expressions.ExpressionEntry entry = expressions.get(i);
				mb.slot(i, SlotBuilder.buttonSlot(entry.icon().newItemStack(), addExpression(entry.constructor())));
			}
		});

	public static final MenuBuilder INT_LIST = MenuBuilder.create(6, "Select Int Expression")
		.customBuilder(mb -> {
			List<Expressions.ExpressionEntry> expressions = Expressions.getExpressions(Expression.Type.INT);
			for (int i = 0; i < expressions.size(); i++)
			{
				Expressions.ExpressionEntry entry = expressions.get(i);
				mb.slot(i, SlotBuilder.buttonSlot(entry.icon().newItemStack(), addExpression(entry.constructor())));
			}
		});

	public static final MenuBuilder STRING_LIST = MenuBuilder.create(6, "Select String Expression")
		.customBuilder(mb -> {
			List<Expressions.ExpressionEntry> expressions = Expressions.getExpressions(Expression.Type.STRING);
			for (int i = 0; i < expressions.size(); i++)
			{
				Expressions.ExpressionEntry entry = expressions.get(i);
				mb.slot(i, SlotBuilder.buttonSlot(entry.icon().newItemStack(), addExpression(entry.constructor())));
			}
		});

	public static final SlotBuilder PLACEHOLDER = button(ExpItems.PLACEHOLDER.newItemStack(), (c, m) -> Response.cancel());

	private static final MenuBuilder BUILDER = MenuBuilder.create(6, "Expressions").applyMask(MAIN_MASK);

	private static SlotBuilder button(ItemStack icon, BiFunction<Click, Menu, Response> click)
	{
		return SlotBuilder.create(icon)
			.allow(ClickType.LEFT)
			.allow(InventoryAction.PICKUP_ALL)
			.onClick(click);
	}

	private static BiConsumer<Click, Menu> addExpression(Function<JSONObject, Expression> expressionSupplier)
	{
		return (c, m) ->
		{
			Expression target = MetaUtil.getValue(c.player(), Expression.class, "target_exp");
			Integer targetType = MetaUtil.getValue(c.player(), Integer.class, "target_exp_type");
			Preconditions.checkNotNull(target, "Target Expression is null!");
			Preconditions.checkNotNull(targetType, "Target Expression is null!");
			target.action(target.getTypes()[targetType], c, m, expressionSupplier.apply(new JSONObject()));
			showMenuToPlayer(c.player());
		};
	}

	public static void showMenuToPlayer(Player player)
	{
		Menu menu = BUILDER.build();

		CodeBlockExp codeBlock = MetaUtil.getValue(player, CodeBlockExp.class, "code_block");
		if (codeBlock == null || codeBlock.isEmpty())
		{
			codeBlock = CodeBlockExp.body(null);
			MetaUtil.setMeta(player, "code_block", codeBlock);
		}

		ExpBuilder builder = new ExpBuilder(menu);
		builder.build(codeBlock, 0, 0);

		menu.reload();

		menu.showToPlayer(player);
	}

	@Command
	@Description("Deletes current expression builder")
	@Usage("/clearExpMenu")
	public static boolean clearExpMenu(@NotNull Player player, @NotNull String[] args)
	{
		MetaUtil.removeMeta(player, "code_block");
		return true;
	}

	@Command
	@Description("Opens expression builder Menu")
	@Usage("/expMenu")
	public static boolean expMenu(@NotNull Player player, @NotNull String[] args)
	{
		showMenuToPlayer(player);
		return true;
	}

	@Command
	@Description("JSONifies expressions")
	@Usage("/saveExpMenu")
	public static boolean saveExpMenu(@NotNull Player player, @NotNull String[] args)
	{
		CodeBlockExp codeBlock = MetaUtil.getValue(player, CodeBlockExp.class, "code_block");
		if (codeBlock == null)
		{
			player.sendMessage(ChatColor.RED + "no expressions exist");
			return false;
		}
		JSONObject json = new JSONObject();
		codeBlock.save(json);
		System.out.println("\n" + JsonPrettify.prettify(json));
		return true;
	}
}
