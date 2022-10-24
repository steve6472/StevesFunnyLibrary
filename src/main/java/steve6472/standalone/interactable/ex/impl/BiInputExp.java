package steve6472.standalone.interactable.ex.impl;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.ex.*;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class BiInputExp extends Expression
{
	private final Type returnType;
	private final OperatorIcon icon;
	public CodeBlockExp left, right;

	public BiInputExp(Type returnType, Type leftType, Type rightType, Expression left, Expression right)
	{
		this.returnType = returnType;
		this.left = CodeBlockExp.executor(this, leftType, left);
		this.right = CodeBlockExp.executor(this, rightType, right);
		this.icon = new OperatorIcon();
	}

	protected abstract ItemStack getMiddleItem();
	protected abstract String sign();

	@Override
	public int getHeight()
	{
		return 1;
	}

	@Override
	public int getWidth()
	{
		return 1 + left.getWidth() + right.getWidth();
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		builder.setSlot(x + left.getWidth(), y, icon);
		builder.build(left, x, y);
		builder.build(right, x + left.getWidth() + 1, y);
	}

	@Override
	public void save(JSONObject json)
	{
		json.put("left", Expressions.saveExpression(left));
		json.put("right", Expressions.saveExpression(right));
	}

	@FunctionalInterface
	public interface TriFunction<T>
	{
		Expression construct(T operator, Expression left, Expression right);
	}

	public static <T> Expression load(JSONObject json, T operator, TriFunction<T> constructor)
	{
		Expression leftExpr = Expressions.loadExpression(json.optJSONObject("left"));
		Expression rightExpr = Expressions.loadExpression(json.optJSONObject("right"));

		return constructor.construct(operator, leftExpr, rightExpr);
	}

	protected boolean runBoth(ExpContext context)
	{
		return left.execute(context) != ExpResult.DELAY && right.execute(context) != ExpResult.DELAY;
	}

	protected void reset()
	{
		left.reset();
		right.reset();
	}

	@Override
	public IElementType[] getTypes()
	{
		return new IElementType[] {icon};
	}

	@Override
	public String stringify(boolean flag)
	{
		// (X || X)
		return
			boldify("(", flag) +
			left.stringify(false) +
			" " +
			boldify(sign(), flag) +
			" " +
			right.stringify(false) +
			boldify(")", flag);
	}

	private static String boldify(String s, boolean flag)
	{
		if (flag)
		{
			return ChatColor.WHITE + s + ChatColor.DARK_GRAY;
		} else
		{
			return ChatColor.DARK_GRAY + s;
		}
	}

	@Override
	public Type getType()
	{
		return returnType;
	}

	public class OperatorIcon implements IElementType
	{
		@Override
		public String label()
		{
			return "operator";
		}

		@Override
		public int ordinal()
		{
			return 0;
		}

		@Override
		public ItemStack item()
		{
			ItemStack item = getMiddleItem();
			ItemStackBuilder edit = ItemStackBuilder.edit(item.clone());
			if (parent instanceof BiInputExp bix)
			{
				edit.addLoreWithLines(recursion(bix));
			} else
			{
				edit.addLoreWithLines(stringify(true));
			}
			edit.addLore("");
			return edit.buildItemStack();
		}

		private String recursion(BiInputExp bix)
		{
			if (bix.parent instanceof BiInputExp bix_)
			{
				return recursion(bix_);
			} else
			{
				return bix.stringify(false);
			}
		}
	}
}
