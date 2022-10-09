package steve6472.standalone.interactable.ex.impl;

import org.bukkit.inventory.ItemStack;
import steve6472.standalone.interactable.ex.ExpContext;
import steve6472.standalone.interactable.ex.ExpResult;
import steve6472.standalone.interactable.ex.Expression;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class BoolExpr extends BiInputExp
{
	public Type type;

	public BoolExpr(Expression left, Expression right)
	{
		super(left, right);
	}

	@Override
	protected ItemStack getMiddleItem()
	{
		return null;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (!runBoth(context)) return ExpResult.DELAY;

		boolean r = switch (type)
			{
				case AND -> left.getResult().asBoolean() && right.getResult().asBoolean();
				case OR -> left.getResult().asBoolean() || right.getResult().asBoolean();
				case EQUALS -> left.getResult().asBoolean() == right.getResult().asBoolean();
				case XOR -> left.getResult().asBoolean() ^ right.getResult().asBoolean();
			};

		reset();
		return new ExpResult(r);
	}

	public enum Type
	{
		AND("&&"),
		OR("||"),
		EQUALS("=="),
		XOR("^"),
		;

		private final String label;

		Type(String label)
		{
			this.label = label;
		}

		public String getLabel()
		{
			return label;
		}
	}
}
