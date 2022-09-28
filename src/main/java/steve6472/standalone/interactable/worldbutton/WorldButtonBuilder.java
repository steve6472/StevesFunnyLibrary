package steve6472.standalone.interactable.worldbutton;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.context.PlayerContext;

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
	private Consumer<PlayerContext> clickAction;

	private ItemStack icon;
	private int size = 1;

	private ChatColor activeColor;
	private ChatColor disabledColor;
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

	public WorldButtonBuilder clickAction(@NotNull Consumer<PlayerContext> clickAction)
	{
		this.clickAction = clickAction;
		return this;
	}

	public WorldButtonBuilder icon(@NotNull ItemStack icon)
	{
		this.icon = icon;
		return this;
	}

	public WorldButtonBuilder size(int size)
	{
		this.size = size;
		return this;
	}

	public WorldButtonBuilder activeColor(@Nullable ChatColor activeColor)
	{
		this.activeColor = activeColor;
		return this;
	}

	public WorldButtonBuilder disabledColor(@Nullable ChatColor disabledColor)
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

		return button;
	}
}
