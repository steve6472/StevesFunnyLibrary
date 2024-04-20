package steve6472.funnylib.entity.display;

import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.util.Preconditions;
import steve6472.funnylib.util.SkullCreator;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 12/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class FrameDisplayEntity extends AdjustableDisplayEntity
{
	private FrameType frameType;
	private float radius;
	private float scaleX, scaleY, scaleZ;

	public FrameDisplayEntity(@NotNull Player owner, Location location, FrameType frameType, float radius)
	{
		super(Objects.requireNonNull(location.getWorld()).spawn(location, ItemDisplay.class));
		Preconditions.checkNotNull(owner);
		this.frameType = frameType;
		this.radius = radius;
//		if (frameType.isOuterLayer)
//			this.radius -= radius * 0.11765f;
		interpolationDuration = 3;
		create();
	}

	public FrameType getFrameType()
	{
		return frameType;
	}

	public void changeFrameType(FrameType frameType)
	{
		this.frameType = frameType;
		iteratePassengers(ent ->
		{
			if (ent instanceof ItemDisplay display)
				display.setItemStack(frameType.createHead());
		});
	}

	public void setScale(float scaleX, float scaleY, float scaleZ)
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		update();
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
//		if (frameType.isOuterLayer)
//			this.radius -= radius * 0.11765f;
		update();
	}

	public float getScaleX()
	{
		return scaleX;
	}

	public float getScaleY()
	{
		return scaleY;
	}

	public float getScaleZ()
	{
		return scaleZ;
	}

	public float getRadius()
	{
		return radius;
	}

	protected void create()
	{
		// Side North West
		createFramePart(t ->
		{
			t.getTranslation().set(-scaleX, scaleY + radius / 4f, -scaleZ);
			t.getScale().set(radius, scaleY * 4f + radius, radius);
		});

		// Side North East
		createFramePart(t ->
		{
			t.getTranslation().set(scaleX, scaleY + radius / 4f, -scaleZ);
			t.getScale().set(radius, scaleY * 4f + radius, radius);
		});

		// Side South East
		createFramePart(t ->
		{
			t.getTranslation().set(scaleX, scaleY + radius / 4f, scaleZ);
			t.getScale().set(radius, scaleY * 4f + radius, radius);
		});

		// Side South West
		createFramePart(t ->
		{
			t.getTranslation().set(-scaleX, scaleY + radius / 4f, scaleZ);
			t.getScale().set(radius, scaleY * 4f + radius, radius);
		});



		// Top North
		createFramePart(t ->
		{
			t.getTranslation().set(0, scaleY + radius / 4f, -scaleZ);
			t.getScale().set(scaleX * 4f, radius, radius);
		});

		// Top East
		createFramePart(t ->
		{
			t.getTranslation().set(scaleX, scaleY + radius / 4f, 0);
			t.getScale().set(radius, radius, scaleZ * 4f);
		});

		// Top South
		createFramePart(t ->
		{
			t.getTranslation().set(0, scaleY + radius / 4f, scaleZ);
			t.getScale().set(scaleX * 4f, radius, radius);
		});

		// Top West
		createFramePart(t ->
		{
			t.getTranslation().set(-scaleX, scaleY + radius / 4f, 0);
			t.getScale().set(radius, radius, scaleZ * 4f);
		});



		// Bottom North
		createFramePart(t ->
		{
			t.getTranslation().set(0, -scaleY + radius / 4f, -scaleZ);
			t.getScale().set(scaleX * 4f, radius, radius);
		});

		// Bottom East
		createFramePart(t ->
		{
			t.getTranslation().set(scaleX, -scaleY + radius / 4f, 0);
			t.getScale().set(radius, radius, scaleZ * 4f);
		});

		// Bottom South
		createFramePart(t ->
		{
			t.getTranslation().set(0, -scaleY + radius / 4f, scaleZ);
			t.getScale().set(scaleX * 4f, radius, radius);
		});

		// Bottom West
		createFramePart(t ->
		{
			t.getTranslation().set(-scaleX, -scaleY + radius / 4f, 0);
			t.getScale().set(radius, radius, scaleZ * 4f);
		});
	}

	public void createFramePart(Consumer<Transformation> partBehavior)
	{
		createCenteredPart(frameType.createHead(), partBehavior);
	}

	public void createFramePart(FrameType cornerType, Consumer<Transformation> partBehavior)
	{
		createCenteredPart(cornerType.createHead(), partBehavior);
	}

	public enum FrameType
	{
		UGLY_PURPLE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY5N2I0YmY1MWNlMmIyMmQ2ZWI1ZWNjMTU0YTdhYjM5M2Y2YTRiMGIxNDJjODg0NjE1MzE3ZDAxOWQ4OTYzOSJ9fX0=", false),
		DEBUG("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWRlOWZjYzA1YTZmYTM3YTI4MGMzMjMwZTc2OWQyM2EwZDMwMDJjMDQ1MjM0MzU2YmQ1MWY0NzRhMjcwZTQzOSJ9fX0=", false),
		VANILLA_AQUA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllMTY5NzkzMDliNWE5YjY3M2Q2MGQxMzkwYmJhYjBkMDM4NWVhYzcyNTRkODI4YWRhMmEzNmE0NmY3M2E1OSJ9fX0=", true),
		VANILLA_DARK_AQUA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc1YjdhYzlmMGM3MTIzMDNjZDNiNjU0ZTY0NmNlMWM0YmYyNDNhYjM0OGE2YTI1MzcwZjI2MDNlNzlhNjJhMCJ9fX0=", true),
		AQUA_MARINE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTcwNjIyOTdmMjZlNTA5Y2U2YTI0Yzk5MjAxYThmOGI0YWUwMzBkNThmNDFlNzNjZDkzZDdiZDc3ZTY0NyJ9fX0=", false),
		MEDIUMAQUA_MARINE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTIxOGM0OTZkZmJkZWI0NTUzYTZkMzJiMjc0Yjk5NTZiN2UxNmE5ZTRkYWZjOWIzM2ZjZjE4OTdiOCJ9fX0=", false),
		CHART_REUSE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzNjY2I0ZGIyYzE5NjkyODM0M2U2YzFjNzlhMjdkNzM3ZTY1NWQwYzlmOTE2OWI3YTg4ZDE3NDQ1NzE0MTcifX19", false)
		;

		private final String base64;
		private final boolean isOuterLayer;

		FrameType(String base64, boolean isOuterLayer)
		{
			this.base64 = base64;
			this.isOuterLayer = isOuterLayer;
		}

		public ItemStack createHead()
		{
			return SkullCreator.itemFromBase64(base64);
		}
	}
}
