package steve6472.standalone.interactable.ex;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Description;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.Preconditions;
import steve6472.standalone.interactable.ex.impl.IfExp;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpressionMenu
{
	private static final Mask MAIN_MASK = Mask.createMask()
		.addRow(".........", 5)
		.addRow("XXXXXUDLR")
		.addItem('X', SlotBuilder.create(ItemStackBuilder.create(Material.WHITE_STAINED_GLASS_PANE).setName("").buildItemStack()).setSticky())
		.addItem('U', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Up").setCustomModelData(8).buildItemStack(), (c, m) -> m.move(0, -1)))
		.addItem('D', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Down").setCustomModelData(9).buildItemStack(), (c, m) -> m.move(0, 1)))
		.addItem('L', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Left").setCustomModelData(10).buildItemStack(), (c, m) -> m.move(-1, 0)))
		.addItem('R', SlotBuilder.stickyButtonSlot(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Right").setCustomModelData(11).buildItemStack(), (c, m) -> m.move(1, 0)))
		;

	public static final MenuBuilder EXPRESSIONS_LIST = MenuBuilder.create(6, "Select Expression")
		.slot(0, 0, SlotBuilder.buttonSlot(ExpItems.IF_START.newItemStack(), addExpression(() -> new IfExp(null, CodeBlock.body()))))
		;

	public static final SlotBuilder PLACEHOLDER = button(ExpItems.PLACEHOLDER.newItemStack(), (c, m) -> Response.cancel());

	private static final MenuBuilder BUILDER = MenuBuilder.create(6, "Expressions").applyMask(MAIN_MASK);

	private static SlotBuilder button(ItemStack icon, BiFunction<Click, Menu, Response> click)
	{
		return SlotBuilder.create(icon)
			.allow(ClickType.LEFT)
			.allow(InventoryAction.PICKUP_ALL)
			.onClick(click);
	}

	private static BiConsumer<Click, Menu> addExpression(Supplier<Expression> expressionSupplier)
	{
		return (c, m) ->
		{
			Expression target = MetaUtil.getValue(c.player(), Expression.class, "target_exp");
			Integer targetType = MetaUtil.getValue(c.player(), Integer.class, "target_exp_type");
			Preconditions.checkNotNull(target, "Target Expression is null!");
			Preconditions.checkNotNull(targetType, "Target Expression is null!");
			target.action(target.getTypes()[targetType], c, m, expressionSupplier.get());
			showMenuToPlayer(c.player());
		};
	}

	public static void showMenuToPlayer(Player player)
	{
		Menu menu = BUILDER.build();

		CodeBlock codeBlock = MetaUtil.getValue(player, CodeBlock.class, "code_block");
		if (codeBlock == null || codeBlock.isEmpty())
		{
			codeBlock = CodeBlock.body();
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
}
