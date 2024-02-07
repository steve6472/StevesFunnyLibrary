package steve6472.funnylib.menu.slots.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.components.Disable;
import steve6472.funnylib.menu.components.DisableComponent;
import steve6472.funnylib.menu.slots.ItemSlot;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 6/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ButtonSlot extends ItemSlot implements Disable<ButtonSlot>
{
	private final DisableComponent disabled = new DisableComponent(this, ItemStackBuilder.quick(Material.GRAY_DYE, "Disabled Button", ChatColor.DARK_GRAY));
	public Function<Click, Response> click;

	public ButtonSlot(ItemStack icon, boolean isSticky)
	{
		super(icon, isSticky);
	}

	public ButtonSlot(JSONMessage label, Material material, boolean isSticky)
	{
		super(ItemStackBuilder.create(material).setName(label).buildItemStack(), isSticky);
	}

	public ButtonSlot(JSONMessage label, Material material)
	{
		super(ItemStackBuilder.create(material).setName(label).buildItemStack(), false);
	}

	public ButtonSlot setClick(Function<Click, Response> click)
	{
		this.click = click;
		return this;
	}

	@Override
	public ItemStack getIcon()
	{
		if (isDisabled()) return getDisabledIcon();
		return super.getIcon();
	}

	public static boolean interactionCheckWithDisable(Disable<?> disable, Click click)
	{
		if (disable.canInteractWhileDisabled())
			return click.type() != ClickType.DOUBLE_CLICK && !click.type().isKeyboardClick() && click.type() != ClickType.SWAP_OFFHAND;
		else
			return !disable.isDisabled() && click.type() != ClickType.DOUBLE_CLICK && !click.type().isKeyboardClick() && click.type() != ClickType.SWAP_OFFHAND;
	}

	@Override
	public boolean canBeInteractedWith(Click click)
	{
		return interactionCheckWithDisable(this, click);
	}

	@Override
	public Response onClick(Click click)
	{
		if (isDisabled())
			return getDisabledResponse();

		if (this.click == null)
			return Response.cancel();
		else
			return this.click.apply(click);
	}

	@Override
	public DisableComponent getDisableComponent()
	{
		return disabled;
	}

	@Override
	public ButtonSlot getThis()
	{
		return this;
	}
}