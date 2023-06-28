package steve6472.standalone.interactable.ex.impl;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.interactable.ex.*;
import steve6472.standalone.interactable.ex.elements.IElementType;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class BiInputExp extends Expression
{
	private final OperatorIcon icon;
	public CodeBlockExp left, right;

	public BiInputExp(Type returnType, Type leftType, Type rightType)
	{
		super(returnType, 0, 1);
		this.left = CodeBlockExp.executor(this, leftType);
		this.right = CodeBlockExp.executor(this, rightType);
		this.icon = new OperatorIcon();
	}

	protected abstract ItemStack getMiddleItem();
	protected abstract String sign();

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
	public void toNBT(NBT compound)
	{
		compound.setCompound("left", Expressions.saveExpression(compound.createCompound(), left));
		compound.setCompound("right", Expressions.saveExpression(compound.createCompound(), right));
	}

	@Override
	public void fromNBT(NBT compound)
	{
		left = Expressions.loadExpression(compound.getOrCreateCompound("left"), this, CodeBlockExp.class);
		right = Expressions.loadExpression(compound.getOrCreateCompound("right"), this, CodeBlockExp.class);
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
//				edit.addLoreWithLines(recursion(bix));
				edit.addLore(JSONMessage.create(recursion(bix)).setItalic(JSONMessage.ItalicType.FALSE));
			} else
			{
//				edit.addLoreWithLines(stringify(true));
				edit.addLore(JSONMessage.create(stringify(true)).setItalic(JSONMessage.ItalicType.FALSE));
			}
			edit.addLore(JSONMessage.create(""));
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
