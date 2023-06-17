package steve6472.standalone.interactable.blocks.data;

import org.bukkit.Material;
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
	public float yaw;
	public float pitch;
	public Material material = Material.LIGHT_WEIGHTED_PRESSURE_PLATE;
	public boolean end;
	public boolean usePlayerFacing;

	@Override
	public void toNBT(NBT compound)
	{
		compound.setString("parkour_id", parkourId);
		compound.setInt("order", order);
		compound.setFloat("yaw", yaw);
		compound.setFloat("pitch", pitch);
		compound.setEnum("material", material);
		compound.setBoolean("end", end);
		compound.setBoolean("use_player_facing", usePlayerFacing);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		parkourId = compound.getString("parkour_id", "null");
		order = compound.getInt("order", 0);
		yaw = compound.getFloat("yaw", 0f);
		pitch = compound.getFloat("pitch", 0f);
		material = compound.getEnum(Material.class, "material", Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		end = compound.getBoolean("end", false);
		usePlayerFacing = compound.getBoolean("use_player_facing", false);
	}
}
