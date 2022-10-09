package steve6472.standalone.interactable.ex.impl;

import org.bukkit.inventory.ItemStack;
import steve6472.standalone.interactable.ex.*;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class BiInputExp extends Expression
{
	public CodeBlock left, right;

	public BiInputExp(Expression left, Expression right)
	{
		this.left = CodeBlock.executor(left);
		this.right = CodeBlock.executor(right);
	}

	protected abstract ItemStack getMiddleItem();

	@Override
	public int getHeight()
	{
		return 1;
	}

	@Override
	public int getWidth()
	{
		return 3;
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{

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
		return new IElementType[0];
	}

	public enum ElementType implements IElementType
	{
		LEFT("left", ExpItems.LEFT.newItemStack()),
		RIGHT("right", ExpItems.RIGHT.newItemStack()),
		MIDDLE("operator", ExpItems.OPERATOR.newItemStack()),
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
