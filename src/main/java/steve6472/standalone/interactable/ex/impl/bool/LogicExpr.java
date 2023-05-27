package steve6472.standalone.interactable.ex.impl.bool;

import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.ExpContext;
import steve6472.standalone.interactable.ex.ExpItems;
import steve6472.standalone.interactable.ex.ExpResult;
import steve6472.standalone.interactable.ex.Expression;
import steve6472.standalone.interactable.ex.impl.BiInputExp;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class LogicExpr extends BiInputExp
{
	public Operator operator;

	public LogicExpr(Operator operator)
	{
		super(Type.BOOL, Type.BOOL, Type.BOOL);
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
				case AND -> left.getResult().asBoolean() && right.getResult().asBoolean();
				case OR -> left.getResult().asBoolean() || right.getResult().asBoolean();
				case EQUALS -> left.getResult().asBoolean() == right.getResult().asBoolean();
				case XOR -> left.getResult().asBoolean() ^ right.getResult().asBoolean();
			};

		reset();
		return new ExpResult(r);
	}

	@Override
	public void toNBT(NBT compound)
	{
		super.toNBT(compound);
		compound.setEnum("operator", operator);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		super.fromNBT(compound);
		operator = compound.getEnum(Operator.class, "operator");
	}

	public enum Operator
	{
		AND("&&", ExpItems.LOGIC_AND),
		OR("||", ExpItems.LOGIC_OR),
		EQUALS("==", ExpItems.PLACEHOLDER),
		XOR("^", ExpItems.PLACEHOLDER),
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
