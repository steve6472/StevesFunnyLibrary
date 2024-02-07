package steve6472.funnylib.menu.slots.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.Slot;
import steve6472.funnylib.menu.components.Disable;
import steve6472.funnylib.menu.components.DisableComponent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 6/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ToggleButtonSlot extends Slot implements Disable<ToggleButtonSlot>
{
	private final DisableComponent disabled = new DisableComponent(this, ItemStackBuilder.quick(Material.GRAY_DYE, "Disabled button", ChatColor.DARK_GRAY));
	public BiFunction<Click, Boolean, Response> click;
	ItemStack iconOn, iconOff;
	private boolean isToggled;

	public ToggleButtonSlot(ItemStack iconOn, ItemStack iconOff, boolean isSticky)
	{
		super(isSticky);
		this.iconOn = iconOn;
		this.iconOff = iconOff;
	}

	public ToggleButtonSlot(String buttonLabel, boolean isSticky)
	{
		super(isSticky);
		this.iconOn = createToggleItem(true, buttonLabel);
		this.iconOff = createToggleItem(false, buttonLabel);
	}

	private ItemStack createToggleItem(boolean flag, String label)
	{
		Material mat = flag ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
		ChatColor color = flag ? ChatColor.GREEN : ChatColor.RED;
		return ItemStackBuilder
			.create(mat)
			.setName(label)
			.addLore(JSONMessage.create("Current: ").color(ChatColor.GRAY).then("" + flag).color(color))
			.setCustomModelData(1)
			.buildItemStack();
	}

	public ToggleButtonSlot setClick(BiFunction<Click, Boolean, Response> click)
	{
		this.click = click;
		return this;
	}

	public ToggleButtonSlot setToggled(boolean isToggled)
	{
		this.isToggled = isToggled;
		updateSlot();
		return this;
	}

	@Override
	public DisableComponent getDisableComponent()
	{
		return disabled;
	}

	@Override
	public ToggleButtonSlot getThis()
	{
		return this;
	}

	@Override
	public ItemStack getIcon()
	{
		if (disabled.isDisabled())
			return disabled.getDisabledIcon();

		return isToggled ? iconOn : iconOff;
	}

	@Override
	public boolean canBeInteractedWith(Click click)
	{
		if (disabled.canInteractWhileDisabled())
			return click.type() != ClickType.DOUBLE_CLICK && !click.type().isKeyboardClick() && click.type() != ClickType.SWAP_OFFHAND;
		else
			return !disabled.isDisabled() && click.type() != ClickType.DOUBLE_CLICK && !click.type().isKeyboardClick() && click.type() != ClickType.SWAP_OFFHAND;
	}

	public boolean isToggled()
	{
		return isToggled;
	}

	@Override
	public Response onClick(Click click)
	{
		if (disabled.isDisabled())
			return disabled.getDisabledResponse();

		isToggled = !isToggled;
		updateSlot(getIcon());

		if (this.click == null)
			return Response.cancel();
		else
			return this.click.apply(click, isToggled);
	}
}
