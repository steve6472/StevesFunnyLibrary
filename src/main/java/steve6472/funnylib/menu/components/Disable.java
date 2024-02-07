package steve6472.funnylib.menu.components;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.menu.Response;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 2/7/2024
 * Project: StevesFunnyLibrary <br>
 */
public interface Disable<T>
{
	DisableComponent getDisableComponent();

	T getThis();



	default T editDisabled(Consumer<DisableComponent> edit)
	{
		edit.accept(getDisableComponent());
		return getThis();
	}


	default boolean isDisabled()
	{
		return getDisableComponent().isDisabled();
	}

	default T setDisabled(boolean disabled)
	{
		getDisableComponent().setDisabled(disabled);
		return getThis();
	}

	@NotNull
	default ItemStack getDisabledIcon()
	{
		return getDisableComponent().getDisabledIcon();
	}

	default T setDisabledIcon(@NotNull ItemStack disabledIcon)
	{
		getDisableComponent().setDisabledIcon(disabledIcon);
		return getThis();
	}

	@NotNull
	default Response getDisabledResponse()
	{
		return getDisableComponent().getDisabledResponse();
	}

	default T setDisabledResponse(@NotNull Supplier<Response> disabledResponse)
	{
		getDisableComponent().setDisabledResponse(disabledResponse);
		return getThis();
	}

	default boolean canInteractWhileDisabled()
	{
		return getDisableComponent().canInteractWhileDisabled();
	}

	default T setCanInteractWhileDisabled(boolean canInteractWhileDisabled)
	{
		getDisableComponent().setCanInteractWhileDisabled(canInteractWhileDisabled);
		return getThis();
	}
}
