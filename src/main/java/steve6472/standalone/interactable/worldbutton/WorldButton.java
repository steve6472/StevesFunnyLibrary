package steve6472.standalone.interactable.worldbutton;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.item.Items;
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

	private final Consumer<PlayerItemContext> clickAction;

	final ItemDisplay icon;
	final TextDisplay label;
	final Interaction hitbox;

	public boolean followClosestPlayer;
	public boolean isRemote;
	public boolean isActivated;

	private Color activeColor, disabledColor;
	private boolean glowAlways;

	public String labelText;
	public boolean labelAboveButton;
	public boolean labelSubtitle;
	public boolean labelActionBar;

	WorldButton(Location location, float size, @NotNull ItemStack item, @Nullable String labelText, @NotNull Consumer<PlayerItemContext> clickAction)
	{
		this.clickAction = clickAction;
		this.labelText = labelText;

		World world = location.getWorld();
		if (world == null)
			throw new NullPointerException("World is null!");

		icon = world.spawn(location.clone().add(0, 0, 0), ItemDisplay.class, e ->
		{
			e.setGravity(false);
			e.setGlowing(false);
			e.setPersistent(false);
		});
		icon.setItemStack(item);

		if (labelText == null)
		{
			label = null;
		} else
		{
			label = world.spawn(location.clone().add(0, size / 2f, 0), TextDisplay.class, e ->
			{
				e.setGravity(false);
				e.setPersistent(false);
				e.setBillboard(Display.Billboard.FIXED);
			});
		}

		hitbox = world.spawn(location.clone().add(0, -size / 2f, 0), Interaction.class, e ->
		{
			e.setInteractionHeight(size);
			e.setInteractionWidth(size);
			e.setPersistent(false);
			e.setResponsive(true);
		});

		MetaUtil.setMeta(hitbox, META_KEY, this);

		WorldButtonListener.addWorldButton(this);
	}

	public void glow(Color activeColor, Color disbledColor, boolean glowAlways)
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
			icon.setGlowColorOverride(activeColor);
		} else
		{
			icon.setGlowColorOverride(disabledColor);
		}
	}

	public void setIcon(ItemStack item)
	{
		icon.setItemStack(item);
	}

	public void activate()
	{
		isActivated = true;

		if (label != null)
		{
			label.setText(labelText);
			label.setTextOpacity((byte) -1);
			label.setDefaultBackground(true);
		}

		icon.setGlowColorOverride(activeColor);
		icon.setGlowing(true);
	}

	public void deactivate()
	{
		isActivated = false;

		if (label != null)
		{
			label.setTextOpacity((byte) 4);
			label.setDefaultBackground(false);
			label.setText("");
		}

		if (!glowAlways)
		{
			icon.setGlowing(false);
		} else
		{
			icon.setGlowColorOverride(disabledColor);
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
			label.teleport(location.clone().add(0, hitbox.getInteractionHeight() / 2f, 0));
		}
		icon.teleport(location);
		hitbox.teleport(location.clone().add(0, -hitbox.getInteractionHeight() / 2f, 0));
	}

	public void click(Player player)
	{
		Items.callWithItemContext(player, EquipmentSlot.HAND, clickAction);
	}

	public static WorldButtonBuilder builder()
	{
		return WorldButtonBuilder.builder();
	}
}
