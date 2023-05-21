package steve6472.funnylib.json;

import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 5/21/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface INbtConfig
{
	void save(NBT nbt);
	void load(NBT nbt);
}
