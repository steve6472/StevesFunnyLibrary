package steve6472.funnylib.category;

import org.bukkit.Material;
import steve6472.funnylib.json.IJSON;

/**
 * Created by steve6472
 * Date: 2/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface ICategorizable extends IJSON
{
	String name();
	void setName(String name);

	Material icon();
	void setIcon(Material material);
	String id();
}
