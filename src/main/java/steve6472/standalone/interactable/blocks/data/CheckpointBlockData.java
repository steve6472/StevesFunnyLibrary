package steve6472.standalone.interactable.blocks.data;

import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 5/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CheckpointBlockData extends CustomBlockData
{
	public String parkourId = "null";
	public int order;

	@Override
	public void toNBT(NBT compound)
	{
		compound.setString("parkour_id", parkourId);
		compound.setInt("order", order);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		parkourId = compound.getString("parkour_id");
		order = compound.getInt("order");
	}
}
