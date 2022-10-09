package steve6472.standalone.interactable.ex;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.MetaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeBlock extends Expression
{
	private final List<Expression> expressions;
	private final boolean isBody;
	private int lastExecuted = 0;
	private ExpResult lastResult;

	public static CodeBlock body(Expression... expressions)
	{
		return new CodeBlock(true, expressions);
	}

	public static CodeBlock executor(Expression... expressions)
	{
		return new CodeBlock(false, expressions);
	}

	private CodeBlock(boolean isBody, Expression... expressions)
	{
		this.isBody = isBody;
		if (expressions == null || (expressions.length == 1 && expressions[0] == null))
			this.expressions = new ArrayList<>();
		else
			this.expressions = new ArrayList<>(List.of(expressions));
	}

	public void addExpression(Expression expression)
	{
		if (lastExecuted != 0)
			throw new RuntimeException("Can not add expression while block is iterating");
		expressions.add(expression);
	}

	public void removeExpression(Expression expression)
	{
		if (lastExecuted != 0)
			throw new RuntimeException("Can not remove expression while block is iterating");
		expressions.remove(expression);
	}

	public boolean isEmpty()
	{
		return expressions.isEmpty();
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		while (context.getDelay() == 0 && lastExecuted < expressions.size())
		{
			Expression expression = expressions.get(lastExecuted);
			lastResult = expression.execute(context);

			if (lastResult == ExpResult.DELAY)
			{
				return ExpResult.DELAY;
			}

			lastExecuted++;
		}

		return ExpResult.PASS;
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		if (isBody && expressions.isEmpty())
		{
			builder.setSlot(x, y, ElementType.NEW_EXP);
			return;
		}

		int yOffset = 0;
		for (Expression expression : expressions)
		{
			builder.build(expression, x, y + yOffset);
			yOffset += expression.getHeight();
		}
		builder.setCurrentExpression(this);
		if (isBody)
		{
			builder.setSlot(x, y + yOffset, ElementType.NEW_EXP);
		} else if (expressions.isEmpty())
		{
			builder.setSlot(x, y, ElementType.PLACEHOLDER);
		}
	}

	@Override
	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		if (type == ElementType.NEW_EXP)
		{
			if (expression == null)
			{
				MetaUtil.setMeta(click.player(), "target_exp", this);
				MetaUtil.setMeta(click.player(), "target_exp_type", type.ordinal());
				return Response.redirect(ExpressionMenu.EXPRESSIONS_LIST);
			} else
			{
				addExpression(expression);
			}
		}

		return Response.cancel();
	}

	@Override
	public int getHeight()
	{
		int height = 1;
		for (Expression expression : expressions)
		{
			height += expression.getHeight();
		}
		return height;
	}

	@Override
	public int getWidth()
	{
		int maxWidth = 1;
		for (Expression expression : expressions)
		{
			maxWidth = Math.max(maxWidth, expression.getWidth());
		}
		return maxWidth;
	}

	public ExpResult getResult()
	{
		return lastResult;
	}

	public void reset()
	{
		lastExecuted = 0;
	}

	@Override
	public IElementType[] getTypes()
	{
		return ElementType.values();
	}

	public enum ElementType implements IElementType
	{
		PLACEHOLDER("placeholder", ExpItems.PLACEHOLDER.newItemStack()),
		NEW_EXP("new_exp", ExpItems.ADD_EXPRESSION.newItemStack()),
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
