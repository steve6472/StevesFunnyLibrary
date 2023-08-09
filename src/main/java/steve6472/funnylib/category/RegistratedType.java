package steve6472.funnylib.category;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Menu;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

record RegistratedType(
		Supplier<Categorizable> constructor,
		Function<Categorizable, ItemStack> icon,
		Function<Categorizable, ItemStack> itemFromObject,
		Function<ItemStack, Categorizable> objectFromItem,
		Consumer<Menu> populatePopup)
{

}