package steve6472.funnylib.menu.slots.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.components.Disable;
import steve6472.funnylib.menu.components.DisableComponent;
import steve6472.funnylib.menu.slots.ItemSlot;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 2/7/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ClickNumberScroller extends ItemSlot implements Disable<ClickNumberScroller>
{
	private final DisableComponent disabled = new DisableComponent(this, ItemStackBuilder.quick(Material.GRAY_DYE, "Disabled Click Number Scroller", ChatColor.DARK_GRAY));
	private ItemStack displayItem;

	private final Supplier<Integer> minBound;
	private final Supplier<Integer> maxBound;

	private final Supplier<Integer> getValue;
	private final Consumer<Integer> setValue;

	public ClickNumberScroller(ItemStack icon, Supplier<Integer> minBound, Supplier<Integer> maxBound, Supplier<Integer> getValue, Consumer<Integer> setValue)
	{
		super(null, false);
		this.displayItem = icon;

		this.minBound = minBound;
		this.maxBound = maxBound;
		this.getValue = getValue;
		this.setValue = setValue;

		super.setIcon(editItem());
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
		setValue.accept(clampValue(value));
		super.setIcon(editItem());
	}

	@Override
	public void setIcon(ItemStack icon)
	{
		this.displayItem = icon;
		super.setIcon(editItem());
	}

	public void update()
	{
		setValue(getValue());
	}

	@Override
	public boolean canBeInteractedWith(Click click)
	{
		return ButtonSlot.interactionCheckWithDisable(this, click);
	}

	@Override
	public ItemStack getIcon()
	{
		if (isDisabled())
			return getDisabledIcon();

		return super.getIcon();
	}

	@Override
	public Response onClick(Click click)
	{
		if (isDisabled())
			return getDisabledResponse();

		if (click.type().isLeftClick())
			setValue(getValue() + 1);
		else if (click.type().isRightClick())
			setValue(getValue() - 1);

		return Response.cancel();
	}



	@Override
	public DisableComponent getDisableComponent()
	{
		return disabled;
	}

	@Override
	public ClickNumberScroller getThis()
	{
		return this;
	}

	private int clampValue(int value)
	{
		return clamp(value, minBound.get(), maxBound.get());
	}

	private static int clamp(int value, int min, int max)
	{
		return Math.max(min, Math.min(max, value));
	}

	private ItemStack editItem()
	{
		if (displayItem == null || displayItem.getType().isAir())
			return displayItem;

		ItemStack itemStack = displayItem.clone();

		ItemStackBuilder.modify(itemStack, builder ->
		{
			builder.addLore();
			builder.addLore(JSONMessage.create("Current Value: ", ChatColor.GRAY).then("" + getValue(), ChatColor.GOLD).setItalic(false));
			builder.addLore();
			builder.addLore(JSONMessage.create("Left Click to Increase Value", ChatColor.GREEN).setItalic(false));
			builder.addLore(JSONMessage.create("Right Click to Decrease Value", ChatColor.RED).setItalic(false));
			builder.addLore(JSONMessage.create("" + getMin(), ChatColor.RED).then(" - ", ChatColor.WHITE).then("" + getMax(), ChatColor.GREEN).setItalic(false));
		});
		return itemStack;
	}
}
