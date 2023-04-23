package steve6472.funnylib.json;

import steve6472.funnylib.util.NBT;

/**
 * Created by steve6472
 * Date: 4/22/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface INBT
{
	void toNBT(NBT compound);
	void fromNBT(NBT compound);
}
