package steve6472.standalone.interactable.ex;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Description;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.json.JsonNBT;
import steve6472.funnylib.json.JsonPrettify;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.*;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpressionMenu
{
	/*
	public static final Mask MAIN_MASK = Mask.createMask()
		.addRow(".........", 5)
		.addRow("XXXXXUDLR")
		.addItem('X', SlotBuilder.create(ItemStackBuilder.create(Material.WHITE_STAINED_GLASS_PANE).setName("").buildItemStack()).setSticky())
		.addItem('U', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Up").setCustomModelData(8).buildItemStack(), (c, m) -> move(c, m,0, -1)))
		.addItem('D', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Down").setCustomModelData(9).buildItemStack(), (c, m) -> move(c, m,0, 1)))
		.addItem('L', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Left").setCustomModelData(10).buildItemStack(), (c, m) -> move(c, m,-1, 0)))
		.addItem('R', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Right").setCustomModelData(11).buildItemStack(), (c, m) -> move(c, m,1, 0)))
		;

	private static void move(Click click, Menu menu, int x, int y)
	{
		menu.move(x, y);
		MetaUtil.setMeta(click.player(), "exp_menu_x", menu.getOffsetX());
		MetaUtil.setMeta(click.player(), "exp_menu_y", menu.getOffsetY());
	}

	public static final Mask POPUP_BACKGROUND = Mask.createMask()
		.addRow("", 5)
		.addRow("....P....")
		.addItem('P', SlotBuilder.create(ExpItems.POPUP_BACKGROUND.newItemStack()).setSticky());

	public static final Mask POPUP_NO_BACKGROUND = Mask.createMask()
		.addRow("", 5)
		.addRow("....P....")
		.addItem('P', SlotBuilder.create(ExpItems.POPUP_NO_BACKGROUND.newItemStack()).setSticky());

	public static final Mask POPUP_NO_BACKGROUND_TOP = Mask.createMask()
		.addRow("", 5)
		.addRow("....P....")
		.addItem('P', SlotBuilder.create(ExpItems.POPUP_NO_BACKGROUND_TOP.newItemStack()).setSticky());

	public static final Mask POPUP = Mask.createMask()
		.addRow("...___...")
		.addRow(".______X.")
		.addRow(".______R.")
		.addRow(".______U.")
		.addRow(".______D.")
		.addRow("____P____")
		.addItem('_', SlotBuilder.create(MiscUtil.AIR).setSticky())
		.addItem('X', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_CLOSE.newItemStack(), (c, m) ->
		{
			MetaUtil.removeMeta(c.player(), "target_exp");
			MetaUtil.removeMeta(c.player(), "target_exp_type");
			construct(c.player(), m);
			m.applyMask(MAIN_MASK);
		}))
		.addItem('U', SlotBuilder.stickyButtonSlot_(MiscUtil.AIR, (c, m) ->
		{
			Menu popupMenu = m.getMetadata("popup", Menu.class);
			popupMenu.move(0, -1);
			popupMenu.overlay(m, 1, 1, 6, 4);
			c.slot().setItem(popupMenu.getOffsetY() <= 0 ? MiscUtil.AIR : ExpItems.POPUP_UP.newItemStack());
		}))
		.addItem('D', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_DOWN.newItemStack(), (c, m) ->
		{
			Menu popupMenu = m.getMetadata("popup", Menu.class);
			popupMenu.move(0, 1);
			popupMenu.overlay(m, 1, 1, 6, 4);

			m.getSlot(c.slot().getX(), c.slot().getY() - 1).setItem(popupMenu.getOffsetY() <= 0 ? MiscUtil.AIR : ExpItems.POPUP_UP.newItemStack());
		}))
		.addItem('R', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_REMOVE_EXP.newItemStack(), (c, m) ->
		{
			Expression target = MetaUtil.getValue(c.player(), Expression.class, "target_exp");
			Integer targetType = MetaUtil.getValue(c.player(), Integer.class, "target_exp_type");

			if (target != null && targetType != null)
			{
				if (target.parent != null)
				{
					if (target.parent instanceof CodeBlockExp block)
					{
						block.removeExpression(target);
					} else
					{
						c.player().sendMessage("Type was: " + target.parent.getClass().getSimpleName());
					}
				}
			}

			MetaUtil.removeMeta(c.player(), "target_exp");
			MetaUtil.removeMeta(c.player(), "target_exp_type");
			construct(c.player(), m);
			m.applyMask(MAIN_MASK);
		}))
		;

	private static final MenuBuilder BUILDER = MenuBuilder
		.create(6, "Expressions")
		.allowPlayerInventory()
		.setOnClose((m, p) ->
		{
			MetaUtil.removeMeta(p, "code_block");
			return Response.allow();
		})
		.applyMask(MAIN_MASK);

	public static BiConsumer<Click, Menu> addExpression(Supplier<Expression> expressionSupplier)
	{
		return (c, m) ->
		{
			Expression target = MetaUtil.getValue(c.player(), Expression.class, "target_exp");
			Integer targetType = MetaUtil.getValue(c.player(), Integer.class, "target_exp_type");
			Preconditions.checkNotNull(target, "Target Expression is null!");
			Preconditions.checkNotNull(targetType, "Target Expression is null!");
			target.action(target.getTypes()[targetType], c, m, expressionSupplier.get());
			construct(c.player(), m);
			m.applyMask(MAIN_MASK);
		};
	}

	public static void construct(Player player, Menu menu)
	{
		Expression exp = MetaUtil.getValue(player, Expression.class, "code_block");
		Preconditions.checkNotNull(exp);

		menu.clear();

		ExpBuilder builder = new ExpBuilder(menu);
		builder.build(exp, 0, 0);

		Integer x = MetaUtil.getValue(player, Integer.class, "exp_menu_x");
		Integer y = MetaUtil.getValue(player, Integer.class, "exp_menu_y");

		// move calls reload()
		menu.move(x == null ? 0 : x, y == null ? 0 : y);
	}

	public static CodeBlockExp showMenuToPlayer(Player player, CodeBlockExp code)
	{
		Menu menu = BUILDER.build();

		if (code == null)
			code = CodeBlockExp.body(null);

		MetaUtil.setMeta(player, "code_block", code);
		MetaUtil.setMeta(player, "exp_menu_x", 0);
		MetaUtil.setMeta(player, "exp_menu_y", 0);
		MetaUtil.removeMeta(player, "target_exp");
		MetaUtil.removeMeta(player, "target_exp_type");

		construct(player, menu);
		menu.applyMask(MAIN_MASK);

		menu.showToPlayer(player);
		return code;
	}*/

	@Command
	@Description("Deletes current expression builder")
	@Usage("/clearExpMenu")
	public static boolean clearExpMenu(@NotNull Player player, @NotNull String[] args)
	{
		MetaUtil.removeMeta(player, "code_block");
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
		NBT nbt = PdcNBT.fromPDC(NMS.newCraftContainer());
		codeBlock.toNBT(nbt);
		System.out.println(JsonPrettify.prettify(JsonNBT.NBTtoJSON(nbt)));
//		JSONObject json = new JSONObject();
//		codeBlock.save(json);
//		System.out.println("\n" + JsonPrettify.prettify(json));
		return true;
	}
}
