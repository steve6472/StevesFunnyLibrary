package steve6472.funnylib.minigame.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.Slot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
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
			Value<?> value = entry.getKey();
			Object object = entry.getValue();

			ItemStack icon = configTypeRegistry.createIcon(value, object);

			setSlot(i + 1, 1, new ButtonSlot(icon, false)
			{
				@Override
				public Slot updateSlot(ItemStack itemStack)
				{
					ItemStackBuilder debugIcon = ItemStackBuilder.editNonStatic(itemStack);
					addDebugInfoToIcon(debugIcon, value, object);
					ItemStack editedIcon = debugIcon.buildItemStack();
					return super.updateSlot(editedIcon);
				}
			}.setClick(click -> configTypeRegistry.onClick(value, click, gameConfig)));

			i++;
		}

		setSlot(8, 3, new ButtonSlot(JSONMessage.create("Save").color(ChatColor.GREEN), Material.EMERALD).setClick(click ->
		{
			gameConfig.save();
			saved = true;
			JSONMessage.create("Settings saved!").color(ChatColor.GREEN).send(click.player());
			if (click.type().isShiftClick())
				return Response.cancel();
			return Response.exit();
		}));

		setSlot(7, 3, new ButtonSlot(JSONMessage.create("Discard").color(ChatColor.RED), Material.REDSTONE).setClick(click ->
		{
			gameConfig.load();
			return Response.exit();
		}));
	}

	private void addDebugInfoToIcon(ItemStackBuilder icon, Value<?> value, Object object)
	{
		icon.addLore(JSONMessage.create());
		icon.addLore(JSONMessage.create(value.getValueType().getName() + ": " + value.getId()).color(ChatColor.DARK_GRAY));
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
