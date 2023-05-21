package steve6472.funnylib.category;

import org.bukkit.Material;
import steve6472.funnylib.json.IJSON;
import steve6472.funnylib.json.INBT;

/**
 * Created by steve6472
 * Date: 2/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface ICategorizable extends INBT
{
	String name();
	void setName(String name);

	Material icon();
	void setIcon(Material material);
	String id();
}
