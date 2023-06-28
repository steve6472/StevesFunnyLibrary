package steve6472.standalone.interactable.ex.elements;

import org.bukkit.inventory.ItemStack;

/**
 * Created by steve6472
 * Date: 10/7/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface IElementType
{
	String label();
	int ordinal();
	ItemStack item();
}
