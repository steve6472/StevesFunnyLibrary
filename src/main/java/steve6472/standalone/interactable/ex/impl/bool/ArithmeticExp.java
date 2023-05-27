package steve6472.standalone.interactable.ex.impl.bool;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.ExpContext;
import steve6472.standalone.interactable.ex.ExpResult;
import steve6472.standalone.interactable.ex.Expression;
import steve6472.standalone.interactable.ex.impl.BiInputExp;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ArithmeticExp extends BiInputExp
{
	public Operator operator;

	public ArithmeticExp(Operator operator)
	{
		super(Type.INT, Type.INT, Type.INT);
		this.operator = operator;
	}

	@Override
	protected ItemStack getMiddleItem()
	{
		return null;
	}

	@Override
	protected String sign()
	{
		return operator.label;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (!runBoth(context)) return ExpResult.DELAY;

		int r = switch (operator)
		{
			case ADD -> left.getResult().asInt() + right.getResult().asInt();
			case SUB -> left.getResult().asInt() - right.getResult().asInt();
			case MUL -> left.getResult().asInt() * right.getResult().asInt();
			case DIV -> left.getResult().asInt() / right.getResult().asInt();
			case LSH -> left.getResult().asInt() << right.getResult().asInt();
			case RSH -> left.getResult().asInt() >> right.getResult().asInt();
			case MOD -> Math.floorMod(left.getResult().asInt(), right.getResult().asInt());
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
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
		LSH("<<"),
		RSH(">>"),
		MOD("%")
		;

		private final String label;

		Operator(String label)
		{
			this.label = label;
		}

		public String getLabel()
		{
			return label;
		}
	}
}
