package steve6472.standalone.interactable.ex.impl;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.*;
import steve6472.standalone.interactable.ex.*;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class IfExp extends Expression
{
	private final CodeBlock body, condition;

	public IfExp(Expression condition, Expression body)
	{
		this.condition = CodeBlock.executor(condition);
		if (body instanceof CodeBlock cb)
			this.body = cb;
		else
			this.body = CodeBlock.body(body);
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
