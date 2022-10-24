package steve6472.standalone.interactable.ex.impl;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.ex.*;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class IfExp extends Expression
{
	private final CodeBlockExp body, condition;

	public IfExp(Expression condition, Expression body)
	{
		this.condition = CodeBlockExp.executor(this, Type.BOOL, condition);
		if (body instanceof CodeBlockExp cb)
		{
			this.body = cb;
			this.body.setParent(this);
		} else
		{
			this.body = CodeBlockExp.body(body);
		}
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (condition.execute(context) == ExpResult.DELAY) return ExpResult.DELAY;

		if (condition.getResult().asBoolean())
		{
			if (body.execute(context) == ExpResult.DELAY) return ExpResult.DELAY;
		}

		condition.reset();
		body.reset();
		return ExpResult.PASS;
	}

	@Override
	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		if (type == ElementType.EXTENSION)
		{
			if (click.type() == ClickType.LEFT)
			{
				for (int i = 0; i < 64; i++)
				{
					Slot slot = menu.getSlot(click.slot().getX(), click.slot().getY() - i);
					if (slot == null) break;
					if (Items.getCustomItem(slot.item()) == ExpItems.IF_START)
					{
						menu.setOffset(click.slot().getX(), slot.getY());
						break;
					}
				}
			}
			if (click.type() == ClickType.RIGHT)
			{
				for (int i = 0; i < 64; i++)
				{
					Slot slot = menu.getSlot(click.slot().getX(), click.slot().getY() + i);
					if (slot == null) break;
					if (Items.getCustomItem(slot.item()) == ExpItems.IF_END)
					{
						menu.setOffset(click.slot().getX(), slot.getY() - 4);
						break;
					}
				}
			}
		}

		return Response.cancel();
	}

	@Override
	public void createPopup(MenuBuilder builder)
	{
		builder.slot(0, 0, SlotBuilder.buttonSlot(ItemStackBuilder.edit(ExpItems.POPUP_CLOSE.newItemStack()).setName("Clear Condition").buildItemStack(), (c, m) -> condition.getExpressions().clear()));
		builder.slot(0, 1, SlotBuilder.buttonSlot(ItemStackBuilder.edit(ExpItems.POPUP_CLOSE.newItemStack()).setName("Clear Body").buildItemStack(), (c, m) -> body.getExpressions().clear()));
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		builder.setSlot(x, y, ElementType.START);
		builder.setSlot(x + condition.getWidth() + 1, y, ElementType.THEN);
		builder.setSlot(x, y + body.getHeight() + 1, ElementType.END);
		for (int i = 0; i < body.getHeight(); i++)
		{
			builder.setSlot(x, y + 1 + i, ElementType.EXTENSION);
		}

		builder.build(condition, x + 1, y);
		builder.build(body, x + 1, y + 1);
	}

	@Override
	public int getHeight()
	{
		return 2 + body.getHeight();
	}

	@Override
	public int getWidth()
	{
		return 3 + Math.max(body.getWidth(), condition.getWidth());
	}

	@Override
	public IElementType[] getTypes()
	{
		return ElementType.values();
	}

	@Override
	public String stringify(boolean flag)
	{
		return "if " + condition.stringify(false) + " then\n" + body.stringify(false) + "\nend";
	}

	@Override
	public void save(JSONObject json)
	{
		json.put("body", Expressions.saveExpression(body));
		json.put("condition", Expressions.saveExpression(condition));
	}

	@Override
	public Type getType()
	{
		return Type.CONTROL;
	}

	public enum ElementType implements IElementType
	{
		START("start", ExpItems.IF_START.newItemStack()),
		THEN("then", ExpItems.IF_THEN.newItemStack()),
		END("end", ExpItems.IF_END.newItemStack()),
		EXTENSION("if extension", ExpItems.IF_EXTENSION.newItemStack())
		;

		private final String label;
		private final ItemStack item;

		ElementType(String label, ItemStack item)
		{
			this.label = label;
			this.item = item;
		}

		@Override
		public String label()
		{
			return label;
		}

		@Override
		public ItemStack item()
		{
			return item;
		}
	}
}
