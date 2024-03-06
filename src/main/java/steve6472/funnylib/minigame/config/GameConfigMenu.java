package steve6472.funnylib.minigame.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.menu.slots.buttons.MoveButtonSlot;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

import java.util.Map;

/**
 * Created by steve6472
 * Date: 12/25/2023
 * Project: StevesFunnyLibrary <br>
 */
public class GameConfigMenu extends Menu
{
	private final ConfigTypeRegistry configTypeRegistry;
	private final GameConfiguration gameConfig;

	private boolean saved = false;

	public GameConfigMenu(ConfigTypeRegistry configTypeRegistry, GameConfiguration gameConfig)
	{
		super(4, gameConfig.name.toLegacy(), true);
		this.configTypeRegistry = configTypeRegistry;
		this.gameConfig = gameConfig;

		allowPlayerInventory();
	}

	@Override
	protected void setup()
	{
		// TODO: add start button ?

		int i = 0;
		for (Map.Entry<Value<?>, Object> entry : gameConfig.getValues().entrySet())
		{
			//noinspection unchecked
			Value<Object> value = (Value<Object>) entry.getKey();
			Object object = entry.getValue();

			ItemStack icon = configTypeRegistry.createIcon(value, object);

			setSlot(i + 1, 1, new ButtonSlot(icon, false)
			{
				@Override
				public ItemStack getIcon()
				{
					Object object = gameConfig.getValue(value);

					ItemStackBuilder debugIcon = ItemStackBuilder.editNonStatic(configTypeRegistry.createIcon(value, object));
					addDebugInfoToIcon(debugIcon, value, object);
					JSONMessage name = JSONMessage.create(value.getName());
					Game currentGame = FunnyLib.currentGame;
					if (!value.allowRuntimeEdit() && (currentGame != null && currentGame.getConfig() == gameConfig))
					{
						name.color(ChatColor.DARK_RED).then(" (Uneditable during runtime)");
					}
					debugIcon.setName(name);
					return debugIcon.buildItemStack();
				}
			}.setClick(click ->
			{
				Game currentGame = FunnyLib.currentGame;
				if (!value.allowRuntimeEdit() && (currentGame != null && currentGame.getConfig() == gameConfig))
				{
					return Response.cancel();
				}
				return configTypeRegistry.onClick(value, click, gameConfig);
			}));

			i++;
		}

		setSlot(0, 1, new MoveButtonSlot(JSONMessage.create("Left"), Material.ARROW, -1, 0, true));
		setSlot(8, 1, new MoveButtonSlot(JSONMessage.create("Right"), Material.ARROW, 1, 0, true));

		setSlot(8, 3, new ButtonSlot(JSONMessage.create("Save").color(ChatColor.GREEN), Material.EMERALD, true).setClick(click ->
		{
			gameConfig.save();
			saved = true;
			JSONMessage.create("Settings saved!").color(ChatColor.GREEN).send(click.player());
			if (click.type().isShiftClick())
				return Response.cancel();
			return Response.exit();
		}));

		setSlot(7, 3, new ButtonSlot(JSONMessage.create("Discard").color(ChatColor.RED), Material.REDSTONE, true).setClick(click ->
		{
			gameConfig.load();
			return Response.exit();
		}));
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
		if (!saved)
		{
			gameConfig.load();
			JSONMessage.create("Changed discarded!").color(ChatColor.RED).send(player);
		}
		return Response.allow();
	}
}
