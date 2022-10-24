package steve6472.standalone.interactable.ex;

import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/24/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ElementType implements IElementType
{
	private final String label;
	private final int ordinal;
	private final Supplier<ItemStack> item;

	public ElementType(String label, int ordinal, Supplier<ItemStack> item)
	{
		this.label = label;
		this.ordinal = ordinal;
		this.item = item;
	}

	@Override
	public String label()
	{
		return label;
	}

	@Override
	public int ordinal()
	{
		return ordinal;
	}

	@Override
	public ItemStack item()
	{
		return item.get();
	}
}
