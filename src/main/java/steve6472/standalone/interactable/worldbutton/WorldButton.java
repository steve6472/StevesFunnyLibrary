package steve6472.standalone.interactable.worldbutton;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.util.GlowingUtil;
import steve6472.funnylib.util.MetaUtil;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 9/25/2022
 * Project: StevesFunnyLibrary <br>
 */
public class WorldButton
{
	public static final String META_KEY = "world_button";

	private final Consumer<PlayerContext> clickAction;

	final ArmorStand icon, label;
	final Slime hitbox;

	public boolean followClosestPlayer;
	public boolean isRemote;
	public boolean isActivated;

	private ChatColor activeColor, disabledColor;
	private boolean glowAlways;

	public String labelText;
	public boolean labelAboveButton;
	public boolean labelSubtitle;
	public boolean labelActionBar;

	WorldButton(Location location, int size, @NotNull ItemStack item, @Nullable String labelText, @NotNull Consumer<PlayerContext> clickAction)
	{
		this.clickAction = clickAction;
		this.labelText = labelText;

		World world = location.getWorld();
		if (world == null)
			throw new NullPointerException("World is null!");

		icon = world.spawn(location.clone().add(0, -1.7 + ((size - 1) * 0.255), 0), ArmorStand.class, e -> {
			e.setGravity(false);
			e.setMarker(true);
			e.setInvisible(true);
			e.setBasePlate(false);
			e.setGlowing(false);
			e.setHeadPose(new EulerAngle(0, Math.PI, 0));
			e.setPersistent(false);
		});
		Objects.requireNonNull(icon.getEquipment()).setHelmet(item);

		if (labelText == null)
		{
			label = null;
		} else
		{
			label = world.spawn(location.clone().add(0, 0.05 + ((size - 1) * 0.255), 0), ArmorStand.class, e ->
			{
				e.setGravity(false);
				e.setMarker(true);
				e.setInvisible(true);
				e.setBasePlate(false);
				e.setSmall(true);
				e.setPersistent(false);
			});
		}

		hitbox = world.spawn(location.clone().add(0, -size * 0.255, 0), Slime.class, e -> {
			e.setSize(size);
			e.setInvisible(true);
			e.setGravity(false);
			e.setAI(false);
			e.setPersistent(false);
			e.setCollidable(false);
		});
		Objects.requireNonNull(hitbox.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(2048.0);
		hitbox.setHealth(2048.0);

		MetaUtil.setMeta(hitbox, META_KEY, this);

		WorldButtonListener.addWorldButton(this);
	}

	public void glow(ChatColor activeColor, ChatColor disbledColor, boolean glowAlways)
	{
		this.glowAlways = glowAlways;

		if (glowAlways)
		{
			icon.setGlowing(true);
		}

		this.activeColor = activeColor;
		this.disabledColor = disbledColor;

		if (isActivated)
		{
			GlowingUtil.setGlowColor(icon, activeColor);
		} else
		{
			GlowingUtil.setGlowColor(icon, disbledColor);
		}
	}

	public void activate()
	{
		isActivated = true;

		if (label != null)
		{
			label.setCustomName(labelText);
			label.setCustomNameVisible(true);
		}

		GlowingUtil.setGlowColor(icon, activeColor);
		icon.setGlowing(true);
	}

	public void deactivate()
	{
		isActivated = false;

		if (label != null)
		{
			label.setCustomNameVisible(false);
		}

		if (!glowAlways)
		{
			icon.setGlowing(false);
		} else
		{
			GlowingUtil.setGlowColor(icon, disabledColor);
		}
	}

	public void displayLabel(Player player)
	{
		if (labelSubtitle)
			player.sendTitle("", labelText, 0, 5, 0);
		if (labelActionBar)
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(labelText));
	}

	public void remove()
	{
		if (label != null)
		{
			label.remove();
		}
		icon.remove();
		hitbox.remove();
		WorldButtonListener.removeWorldButton(this);
	}

	public void teleport(double x, double y, double z)
	{
		teleport(new Location(icon.getWorld(), x, y, z));
	}

	public void teleport(Location location)
	{
		if (label != null)
		{
			label.teleport(location.clone().add(0, 0.05 + ((hitbox.getSize() - 1) * 0.255), 0));
		}
		icon.teleport(location.clone().add(0, -1.7 + ((hitbox.getSize() - 1) * 0.255), 0));
		hitbox.teleport(location.clone().add(0, -hitbox.getSize() * 0.255, 0));
	}

	public void click(Player player)
	{
		clickAction.accept(new PlayerContext(player));
	}

	public static WorldButtonBuilder builder()
	{
		return WorldButtonBuilder.builder();
	}
}
