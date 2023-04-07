package steve6472.funnylib.category;

import org.bukkit.Material;

/**
 * Created by steve6472
 * Date: 2/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface ICategorizable
{
	String name();
	void setName(String name);

	Material icon();
	void setIcon(Material material);
}
