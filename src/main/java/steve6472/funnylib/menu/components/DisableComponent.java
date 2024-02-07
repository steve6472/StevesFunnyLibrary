package steve6472.funnylib.menu.components;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.Slot;

import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 2/7/2024
 * Project: StevesFunnyLibrary <br>
 */
public class DisableComponent
{
	private final Slot parentSlot;
	private boolean isDisabled;
	private boolean canInteractWhileDisabled = false;
	@NotNull private ItemStack disabledIcon;
	@NotNull private Supplier<Response> disabledResponse = Response::cancel;

	public DisableComponent(Slot parentSlot, boolean isDisabled, @NotNull ItemStack disabledIcon, @NotNull Supplier<Response> disabledResponse)
	{
		this.parentSlot = parentSlot;
		this.isDisabled = isDisabled;
		this.disabledIcon = disabledIcon;
		this.disabledResponse = disabledResponse;
	}

	public DisableComponent(Slot parentSlot, @NotNull ItemStack disabledIcon, @NotNull Supplier<Response> disabledResponse)
	{
		this.parentSlot = parentSlot;
		this.disabledIcon = disabledIcon;
		this.disabledResponse = disabledResponse;
	}

	public DisableComponent(Slot parentSlot, @NotNull ItemStack disabledIcon)
	{
		this.parentSlot = parentSlot;
		this.disabledIcon = disabledIcon;
	}

	public boolean isDisabled()
	{
		return isDisabled;
	}

	public void setDisabled(boolean disabled)
	{
		this.isDisabled = disabled;
		parentSlot.updateSlot();
	}

	public @NotNull ItemStack getDisabledIcon()
	{
		return disabledIcon;
	}

	public void setDisabledIcon(@NotNull ItemStack disabledIcon)
	{
		this.disabledIcon = disabledIcon;
		parentSlot.updateSlot();
	}

	public @NotNull Response getDisabledResponse()
	{
		return disabledResponse.get();
	}

	public void setDisabledResponse(@NotNull Supplier<Response> disabledResponse)
	{
		this.disabledResponse = disabledResponse;
	}

	public boolean canInteractWhileDisabled()
	{
		return canInteractWhileDisabled;
	}

	public void setCanInteractWhileDisabled(boolean canInteractWhileDisabled)
	{
		this.canInteractWhileDisabled = canInteractWhileDisabled;
	}
}
