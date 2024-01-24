package steve6472.funnylib.minigame.config.menu;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.Slot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.minigame.config.*;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 1/20/2024
 * Project: StevesFunnyLibrary <br>
 */
public class Vec3iConfigMenu extends Menu
{
	private static final Value<Integer> X = Value.create(BuiltInConfigType.INT, "X", "x");
	private static final Value<Integer> Y = Value.create(BuiltInConfigType.INT, "Y", "y");
	private static final Value<Integer> Z = Value.create(BuiltInConfigType.INT, "Z", "z");

	private final ConfigTypeRegistry configTypeRegistry;
	private final GameConfiguration gameConfig;
	private final Value<Vector3i> value;
	private final Vector3i originalValue;

	public Vec3iConfigMenu(ConfigTypeRegistry configTypeRegistry, GameConfiguration gameConfig, Value<Vector3i> value)
	{
		super(4, "Vec 3i - " + value.getName(), true);
		this.configTypeRegistry = configTypeRegistry;
		this.gameConfig = gameConfig;
		this.value = value;
		this.originalValue = new Vector3i(gameConfig.getValue(value));
	}

	@Override
	protected void setup()
	{
		setSlot(8, 3, new ButtonSlot(JSONMessage.create("Confirm").color(ChatColor.GREEN), Material.EMERALD).setClick(click ->
		{
			JSONMessage.create("Settings confirmed!").color(ChatColor.GREEN).send(click.player());
			if (click.type().isShiftClick())
				return Response.backReload();
			return Response.backReload();
		}));

		setSlot(7, 3, new ButtonSlot(JSONMessage.create("Discard").color(ChatColor.RED), Material.REDSTONE).setClick(click ->
		{
			gameConfig.setValue(value, originalValue);
			return Response.backReload();
		}));

		// TODO: move to createConfigButton(Value<?>, Object) method
		createConfigButton(X, gameConfig.getValue(value).x, 2, 1, (value, object, config) -> config.getValue(this.value).x = object);
		createConfigButton(Y, gameConfig.getValue(value).y, 4, 1, (value, object, config) -> config.getValue(this.value).y = object);
		createConfigButton(Z, gameConfig.getValue(value).z, 6, 1, (value, object, config) -> config.getValue(this.value).z = object);
	}

	private <T> void createConfigButton(Value<T> value, T object, int x, int y, TriConsumer<Value<T>, T, GameConfiguration> redirect)
	{
		ItemStack icon = configTypeRegistry.createIcon(value, object);

		setSlot(x, y, new ButtonSlot(icon, false)
		{
			@Override
			public Slot updateSlot(ItemStack itemStack)
			{
				ItemStackBuilder debugIcon = ItemStackBuilder.editNonStatic(itemStack);
				addDebugInfoToIcon(debugIcon, value, object);
				debugIcon.setName(value.getName());
				ItemStack editedIcon = debugIcon.buildItemStack();
				return super.updateSlot(editedIcon);
			}
		}.setClick(click -> configTypeRegistry.onClick(value, click, new RedirectGameConfiguration<>(gameConfig, configTypeRegistry, redirect))));
	}

	private void addDebugInfoToIcon(ItemStackBuilder icon, Value<?> value, Object object)
	{
		icon.addLore(JSONMessage.create());
		icon.addLore(JSONMessage.create(value.getValueType().getName() + ": " + value.getId()).color(ChatColor.DARK_GRAY).setItalic(JSONMessage.ItalicType.FALSE));
		if (!icon.getNameLegacy().equals(value.getName()))
			icon.addLore(JSONMessage.create("Original name: " + icon.getNameLegacy()).color(ChatColor.DARK_GRAY).setItalic(JSONMessage.ItalicType.FALSE));
	}

	@Override
	public Response onClose(Player player)
	{
		JSONMessage.create("Changed discarded!").color(ChatColor.RED).send(player);
		return Response.backReload();
	}
}
