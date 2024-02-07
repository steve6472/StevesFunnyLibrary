package steve6472.funnylib.menu.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 1/31/2024
 * Project: StevesFunnyLibrary <br>
 */
public class NumberScroller extends Menu
{
	private final Supplier<Integer> minBound;
	private final Supplier<Integer> maxBound;

	private final Supplier<Integer> getValue;
	private final Consumer<Integer> setValue;

	public NumberScroller(Supplier<Integer> minBound, Supplier<Integer> maxBound, Supplier<Integer> getValue, Consumer<Integer> setValue)
	{
		super(1, "number_scroller", false);
		this.minBound = minBound;
		this.maxBound = maxBound;
		this.getValue = getValue;
		this.setValue = setValue;

		setWindowBounds(3, 1);
		limitOffset(0, 0, 0, 0);
	}

	@Override
	protected void setup()
	{
		setSlot(0, 0, new ButtonSlot(createDecreaseItem(), false).setClick(click ->
		{
			if (click.type().isShiftClick() && click.type().isRightClick())
			{
				setValue(getMin());
			} else
			{
				setValue(clampValue(getValue() - 1));
			}
			return Response.cancel();
		}));

		setSlot(1, 0, new IconSlot(createCurrentItem(getValue()), false));

		setSlot(2, 0, new ButtonSlot(createIncreaseItem(), false).setClick(click ->
		{
			if (click.type().isShiftClick() && click.type().isRightClick())
			{
				setValue(getMax());
			} else
			{
				setValue(clampValue(getValue() + 1));
			}
			return Response.cancel();
		}));
	}

	public int getMin()
	{
		return minBound.get();
	}

	public int getMax()
	{
		return maxBound.get();
	}

	public int getValue()
	{
		return clampValue(getValue.get());
	}

	/**
	 * Sets value and updates slots
	 * @param value value to set
	 */
	public void setValue(int value)
	{
		setValue.accept(value);
		getSlot(0, 0).updateSlot(createDecreaseItem());
		getSlot(1, 0).updateSlot(createCurrentItem(value));
		getSlot(2, 0).updateSlot(createIncreaseItem());
	}

	/*
	 * Item helpers
	 */

	protected ItemStack createCurrentItem(int value)
	{
		return ItemStackBuilder.quick(Material.HEART_OF_THE_SEA, "Current Size: " + clampValue(value));
	}

	protected ItemStack createIncreaseItem()
	{
		int value = getValue();
		int max = getMax();

		if (value >= max)
		{
			return ItemStackBuilder.quick(Material.FIREWORK_STAR, "Maximum Radius Reached (" + max + ")");
		} else
		{
			return ItemStackBuilder.quick(Material.EMERALD, "Increase Size to: " + clampValue(value + 1));
		}
	}

	protected ItemStack createDecreaseItem()
	{
		int value = getValue();
		int min = getMin();

		if (value <= min)
		{
			return ItemStackBuilder.quick(Material.FIREWORK_STAR, "Minimum Radius Reached (" + min + ")");
		} else
		{
			return ItemStackBuilder.quick(Material.REDSTONE, "Decrease Size to: " + clampValue(value - 1));
		}
	}

	private int clampValue(int value)
	{
		return clamp(value, minBound.get(), maxBound.get());
	}

	private static int clamp(int value, int min, int max)
	{
		return Math.max(min, Math.min(max, value));
	}
}
