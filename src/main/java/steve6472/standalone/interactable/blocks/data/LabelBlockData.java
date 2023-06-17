package steve6472.standalone.interactable.blocks.data;

import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockEntity;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 6/17/2023
 * Project: StevesFunnyLibrary <br>
 */
public class LabelBlockData extends CustomBlockData implements IBlockEntity
{
//	public AreaSelection area;
	public String label = "Label";
	public float distance = 16;

	// Other label data

	private TextDisplay textDisplay;

	@Override
	public void toNBT(NBT compound)
	{
		compound.setFloat("distance", distance);
		compound.setString("label", label);

//		if (area != null)
//		{
//			NBT areaCompound = compound.createCompound();
//			area.toNBT(areaCompound);
//			compound.setCompound("area", areaCompound);
//		}
	}

	@Override
	public void fromNBT(NBT compound)
	{
		distance = compound.getFloat("distance", 16f);
		label = compound.getString("label", "");
//		if (compound.hasCompound("area"))
//		{
//			area = new AreaSelection();
//			area.fromNBT(compound.getCompound("area"));
//		}
	}

	@Override
	public void spawnEntities(BlockContext context)
	{
		textDisplay = context.getWorld().spawn(context.getLocation().clone().add(0.5, 0.5, 0.5), TextDisplay.class, entity ->
		{
			entity.setText(label);
			entity.setBillboard(Display.Billboard.CENTER);
			entity.setViewRange((1f / 64f) * distance);
		});
	}

	@Override
	public Entity[] getEntities()
	{
		return new Entity[] {textDisplay};
	}
}
