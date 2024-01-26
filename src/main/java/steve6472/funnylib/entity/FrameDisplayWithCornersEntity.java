package steve6472.funnylib.entity;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class FrameDisplayWithCornersEntity extends FrameDisplayEntity
{
	private float minCornerScale, maxCornerScale;

	public FrameDisplayWithCornersEntity(@NotNull Player owner, Location location, FrameType frameType, float radius)
	{
		super(owner, location, frameType, radius);
	}

	@Override
	protected void create()
	{
		super.create();
	}

	public void addCorner(FrameType cornerType, Consumer<Transformation> partBehavior)
	{
		addDisplay(ItemDisplay.class, entity ->
		{
			entity.setItemStack(cornerType.createHead());
			partBehavior.accept(entity.getTransformation());
			entity.setBrightness(new Display.Brightness(15, 15));
			this.partBehaviour.put(entity.getUniqueId(), partBehavior);
		});
	}
}
