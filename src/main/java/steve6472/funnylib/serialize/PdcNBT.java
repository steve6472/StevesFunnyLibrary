package steve6472.funnylib.serialize;

import org.bukkit.persistence.PersistentDataContainer;

/**
 * Created by steve6472
 * Date: 5/21/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PdcNBT extends NBT
{
	public static PdcNBT fromPDC(PersistentDataContainer pdc)
	{
		PdcNBT nbt = new PdcNBT();
		nbt.container = pdc;
		return nbt;
	}
}
