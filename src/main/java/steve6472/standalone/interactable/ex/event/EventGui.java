package steve6472.standalone.interactable.ex.event;

import org.bukkit.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.util.*;
import steve6472.standalone.interactable.blocks.CodeBlockData;
import steve6472.standalone.interactable.ex.*;
import steve6472.standalone.interactable.ex.elements.IElementType;

import java.util.List;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 * DO NOT LOOK HERE<br>
 * SAVE YOUR SOUL<br>
 */
public class EventGui
{/*
	public static final Mask MAIN_MASK = Mask.createMask()
		.addRow(".........", 5)
		.addRow("XXXXXXXUD")
		.addItem('X', SlotBuilder.create(ItemStackBuilder.create(Material.WHITE_STAINED_GLASS_PANE).setName("").buildItemStack()).setSticky())
		.addItem('U', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Up").setCustomModelData(8).buildItemStack(), (c, m) -> move(c, m, -1)))
		.addItem('D', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Down").setCustomModelData(9).buildItemStack(), (c, m) -> move(c, m, 1)))
		;

	private static void move(Click click, Menu menu, int y)
	{
		menu.move(0, y);
		MetaUtil.setMeta(click.player(), "events_menu_y", menu.getOffsetY());
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

	public static MenuBuilder getEventsMenu() {  MenuBuilder EVENTS = MenuBuilder.create(6, "Events")
		.applyMask(MAIN_MASK)
		.customBuilder(b ->
		{
			CodeBlockData blockData = b.getData("data", CodeBlockData.class);

			List<ExpressionEvent> events = blockData.events;
			for (int i = 0; i < events.size(); i++)
			{
				ExpressionEvent event = events.get(i);
				ExpressionEvents.EventEntry eventEntry = ExpressionEvents.ENTRY_BY_CLASS.get(event.getClass());

				// Might wanna create a popupt to remove/move the event idk
				ItemStackBuilder itemStackBuilder = ItemStackBuilder.editNonStatic(eventEntry.icon().clone());
				itemStackBuilder.addLore(JSONMessage.create());

				for (Pair<String, InputType<?>> output : event.getOutputs())
				{
					itemStackBuilder.addLore(JSONMessage.create(output.a()).color(ChatColor.GOLD).then(" : ", ChatColor.GRAY).then(output.b().getId()).color(ChatColor.WHITE));
				}

				b.slot(0, i, SlotBuilder.create(itemStackBuilder.buildItemStack()).allow(InventoryAction.values()).allow(ClickType.values()).onClick((c, m) ->
				{
					Expression expression = event.getExpression();
					if (expression == null)
					{
						expression = CodeBlockExp.body(null);
					}
					event.setExpression(expression);

					ExpressionMenu.showMenuToPlayer(c.player(), (CodeBlockExp) expression);
					return Response.cancel();
				}));

				IElementType[] types = event.getTypes();
				for (int j = 0; j < types.length; j++)
				{
					IElementType type = types[j];
					ItemStack item = ItemStackBuilder
						.editNonStatic(type.item())
						.addLore(JSONMessage.create(""))
						.addLore(JSONMessage.create(type.label()).color(ChatColor.GRAY))
						.buildItemStack();
					b.slot(1 + j, i, SlotBuilder.create(item).allow(InventoryAction.values()).allow(ClickType.values()).onClick((c, m) -> event.action(type, c)));
				}
			}
			b.slot(0, events.size(), SlotBuilder.create(ItemStackBuilder.create(Material.NETHER_STAR).setName(JSONMessage.create("Add Event").color(ChatColor.DARK_AQUA)).setCustomModelData(10).buildItemStack())
				.allow(InventoryAction.values())
				.allow(ClickType.values())
				.onClick(((click, menu) ->
				{
					menu.applyMask(getPopupMask());
					menu.applyMask(POPUP_NO_BACKGROUND);

					MenuBuilder builder = MenuBuilder.create(3, "POPUP_MENU_EVENTS");
					builder.limitOffset(0, 0, 0, Integer.MAX_VALUE);

					int i = 0;
					for (ExpressionEvents.EventEntry value : ExpressionEvents.ENTRY_BY_CLASS.values())
					{
						ItemStack icon = value.icon();
						builder.slot(i, SlotBuilder.buttonSlotResponse_(icon, (c, m) ->
						{
							events.add(value.constructor().get());
							return Response.redirect(getEventsMenu(), b.getArbitraryData().copy());
						}));
						i++;
					}

					Menu popupMenu = builder.build();
					menu.setMetadata("popup", popupMenu);
//					MetaUtil.setMeta(click.player(), "target_event", null);
//					MetaUtil.setMeta(click.player(), "target_event_type", 0);

					popupMenu.overlay(menu, 1, 1, 6, 4);

					return Response.cancel();
				}))
			);
		})
		.allowPlayerInventory();
		return EVENTS;
	}

	// I don't care, I'm too lazy to fix this now, will fix when I get better menu library
	public static Mask getPopupMask() { Mask POPUP = Mask.createMask()
		.addRow("...___...")
		.addRow(".______X.")
		.addRow(".______R.")
		.addRow(".______U.")
		.addRow(".______D.")
		.addRow("____P____")
		.addItem('_', SlotBuilder.create(MiscUtil.AIR).setSticky())
		.addItem('X', SlotBuilder.buttonSlotResponse_(ExpItems.POPUP_CLOSE.newItemStack(), (c, m) ->
		{
			MetaUtil.removeMeta(c.player(), "target_event");
			MetaUtil.removeMeta(c.player(), "target_event_type");
			return Response.redirect(getEventsMenu(), m.getPassedData().copy());
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
		.addItem('R', SlotBuilder.buttonSlotResponse_(ExpItems.POPUP_REMOVE_EXP.newItemStack(), (c, m) ->
		{
			ExpressionEvent target = MetaUtil.getValue(c.player(), ExpressionEvent.class, "target_event");
			Integer targetType = MetaUtil.getValue(c.player(), Integer.class, "target_event_type");

			CodeBlockData blockData = m.getPassedData().getData("data", CodeBlockData.class);
			blockData.events.remove(target);

			MetaUtil.removeMeta(c.player(), "target_event");
			MetaUtil.removeMeta(c.player(), "target_event_type");
			return Response.redirect(getEventsMenu(), m.getPassedData().copy());
		}))
		;
		return POPUP;
	}*/
}

