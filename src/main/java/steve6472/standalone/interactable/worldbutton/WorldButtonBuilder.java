package steve6472.standalone.interactable.worldbutton;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.funnylib.context.PlayerItemContext;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 9/27/2022
 * Project: StevesFunnyLibrary <br>
 */
public class WorldButtonBuilder
{
	private String label;
	private boolean followClosestPlayer;
	private boolean isRemote;
	private Consumer<PlayerItemContext> clickAction;

	private ItemStack icon;
	private Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf());
	private float size = 0.5f;

	private Color activeColor;
	private Color disabledColor;
	private boolean glowAlways;
	private boolean labelAboveButton = true;
	private boolean labelSubtitle;
	private boolean labelActionBar;

	public static WorldButtonBuilder builder()
	{
		return new WorldButtonBuilder();
	}

	public WorldButtonBuilder label(@Nullable String label)
	{
		this.label = label;
		return this;
	}

	public WorldButtonBuilder followClosestPlayer(boolean followClosestPlayer)
	{
		this.followClosestPlayer = followClosestPlayer;
		return this;
	}

	public WorldButtonBuilder remote(boolean isRemote)
	{
		this.isRemote = isRemote;
		return this;
	}

	public WorldButtonBuilder clickAction(@NotNull Consumer<PlayerItemContext> clickAction)
	{
		this.clickAction = clickAction;
		return this;
	}

	public WorldButtonBuilder icon(@NotNull ItemStack icon)
	{
		this.icon = icon;
		return this;
	}

	public WorldButtonBuilder size(float size)
	{
		this.size = size;
		return this;
	}

	public WorldButtonBuilder activeColor(@Nullable Color activeColor)
	{
		this.activeColor = activeColor;
		return this;
	}

	public WorldButtonBuilder disabledColor(@Nullable Color disabledColor)
	{
		this.disabledColor = disabledColor;
		return this;
	}

	public WorldButtonBuilder glowAlways(boolean glowAlways)
	{
		this.glowAlways = glowAlways;
		return this;
	}

	public WorldButtonBuilder labelAboveButton()
	{
		this.labelAboveButton = true;
		this.labelSubtitle = false;
		this.labelActionBar = false;
		return this;
	}

	public WorldButtonBuilder labelSubtitle()
	{
		this.labelAboveButton = false;
		this.labelSubtitle = true;
		this.labelActionBar = false;
		return this;
	}

	public WorldButtonBuilder labelActionBar()
	{
		this.labelAboveButton = false;
		this.labelSubtitle = false;
		this.labelActionBar = true;
		return this;
	}

	public WorldButtonBuilder setTransformation(Transformation transformation)
	{
		this.transformation = transformation;
		return this;
	}

	public WorldButtonBuilder scaleItem(double scale)
	{
		transformation.getScale().set(scale);
		return this;
	}

	public WorldButton build(Location location)
	{
		WorldButton button = new WorldButton(location, size, icon, labelAboveButton ? label : null, clickAction);
		button.labelText = label;
		button.glow(activeColor, disabledColor, glowAlways);
		button.isRemote = isRemote;
		button.followClosestPlayer = followClosestPlayer;
		button.labelAboveButton = labelAboveButton;
		button.labelSubtitle = labelSubtitle;
		button.labelActionBar = labelActionBar;
		button.icon.setTransformation(transformation);

		return button;
	}
}
