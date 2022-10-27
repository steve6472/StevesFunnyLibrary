package steve6472.standalone.interactable.ex.impl;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.item.CustomItem;
import steve6472.standalone.interactable.ex.ExpContext;
import steve6472.standalone.interactable.ex.ExpItems;
import steve6472.standalone.interactable.ex.ExpResult;
import steve6472.standalone.interactable.ex.Expression;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class EqualityExp extends BiInputExp
{
	public Operator operator = Operator.EQUALS;

	public EqualityExp(Expression left, Expression right)
	{
		super(Type.BOOL, Type.INT, Type.INT, left, right);
	}

	public EqualityExp(Operator operator, Expression left, Expression right)
	{
		super(Type.BOOL, Type.INT, Type.INT, left, right);
		this.operator = operator;
	}

	@Override
	protected ItemStack getMiddleItem()
	{
		return operator.getIcon().newItemStack();
	}

	@Override
	protected String sign()
	{
		return operator.getLabel();
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (!runBoth(context)) return ExpResult.DELAY;

		boolean r = switch (operator)
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

	public enum Operator
	{
		EQUALS("==", ExpItems.EQUALITY_EQUALS),
		BIGGER(">", ExpItems.EQUALITY_GREATER),
		BIGGER_EQUAL(">=", ExpItems.EQUALITY_GREATER_EQUAL),
		SMALLER("<", ExpItems.EQUALITY_LESS),
		SMALLER_EQUAL("<=", ExpItems.EQUALITY_LESS_EQUAL),
		;

		private final String label;
		private final CustomItem icon;

		Operator(String label, CustomItem icon)
		{
			this.label = label;
			this.icon = icon;
		}

		public String getLabel()
		{
			return label;
		}

		public CustomItem getIcon()
		{
			return icon;
		}
	}
}
