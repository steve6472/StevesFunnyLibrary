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
public class EqualityExp extends BiInputExp
{
	public Type type;

	public EqualityExp(Expression left, Expression right)
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
				case EQUALS -> left.getResult().asInt() == right.getResult().asInt();
				case BIGGER -> left.getResult().asInt() > right.getResult().asInt();
				case BIGGER_EQUAL -> left.getResult().asInt() >= right.getResult().asInt();
				case SMALLER -> left.getResult().asInt() < right.getResult().asInt();
				case SMALLER_EQUAL -> left.getResult().asInt() <= right.getResult().asInt();
			};

		reset();
		return new ExpResult(r);
	}

	public enum Type
	{
		EQUALS("=="),
		BIGGER(">"),
		BIGGER_EQUAL(">="),
		SMALLER("<"),
		SMALLER_EQUAL("<="),
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
