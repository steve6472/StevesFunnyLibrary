package steve6472.standalone.interactable.ex;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.util.MetaUtil;
import steve6472.standalone.interactable.ex.impl.IfExp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeBlockExp extends Expression
{
	private final List<Expression> expressions;
	private final boolean isBody;
	private final Type type;
	private int lastExecuted = 0;
	private ExpResult lastResult = ExpResult.STOP;

	public static Expression loadJson(JSONObject json)
	{
		if (json.isEmpty())
			return new CodeBlockExp(null, false, null);

		if (!json.getBoolean("isBody"))
		{
			List<Expression> expressionList = Expressions.loadExpressions(json.getJSONArray("expressions"));
			if (expressionList.isEmpty())
				return null;
			return expressionList.get(0);
		} else
		{
			return body(null, Expressions.loadExpressions(json.getJSONArray("expressions")).toArray(new Expression[0]));
		}
	}

	public static CodeBlockExp body(Expression parent, Expression... expressions)
	{
		return new CodeBlockExp(parent, true, Type.CONTROL, expressions);
	}

	public static CodeBlockExp executor(Expression parent, Type type, Expression... expressions)
	{
		return new CodeBlockExp(parent, false, type, expressions);
	}

	private CodeBlockExp(Expression parent, boolean isBody, Type type, Expression... expressions)
	{
		this.parent = parent;
		this.isBody = isBody;
		this.type = type;
		this.expressions = new ArrayList<>();
		if (expressions != null && expressions.length > 0)
		{
			for (Expression expression : expressions)
			{
				if (expression == null)
					continue;

				expression.parent = this;
				this.expressions.add(expression);
			}
		}
	}

	public void addExpression(Expression expression)
	{
		if (lastExecuted != 0)
			throw new RuntimeException("Can not add expression while block is iterating");
		expression.parent = this;
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

	public List<Expression> getExpressions()
	{
		return expressions;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (context.getDelay() > 0)
			return ExpResult.DELAY;

		while (context.getDelay() == 0 && lastExecuted < expressions.size())
		{
			Expression expression = expressions.get(lastExecuted);
			lastResult = expression.execute(context);
			lastExecuted++;

			if (lastResult == ExpResult.DELAY)
			{
				return ExpResult.DELAY;
			}
		}

		return ExpResult.STOP;
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
//		if (type == ElementType.NEW_EXP)
//		{
			if (expression == null)
			{
				MetaUtil.setMeta(click.player(), "target_exp", this);
				MetaUtil.setMeta(click.player(), "target_exp_type", type.ordinal());
				return switch (this.type)
					{
						case CONTROL -> Response.redirect(ExpressionMenu.EXPRESSIONS_LIST);
						case BOOL -> Response.redirect(ExpressionMenu.BOOL_LIST);
						case INT -> Response.redirect(ExpressionMenu.INT_LIST);
						case STRING -> Response.redirect(ExpressionMenu.STRING_LIST);
						case HIDDEN -> throw new RuntimeException("These expressions are hidden for a reason...");
					};
			} else
			{
				addExpression(expression);
			}
//		}

		return Response.cancel();
	}

	@Override
	public Type getType()
	{
		return type;
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

	@Override
	public String stringify(boolean flag)
	{
		StringBuilder builder = new StringBuilder();
		for (Expression expression : expressions)
		{
			builder.append(expression.stringify(false));
			if (isBody)
				builder.append("\n");
		}
		String s = builder.toString();
		if (s.isBlank())
			return ChatColor.RED + "X";
		return s;
	}

	@Override
	public void save(JSONObject json)
	{
		json.put("isBody", isBody);
		json.put("type", type);
		json.put("expressions", Expressions.saveExpressions(expressions));
	}

	public boolean isBody()
	{
		return isBody;
	}

	public void setParent(Expression parent)
	{
		this.parent = parent;
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