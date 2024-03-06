package steve6472.funnylib.minigame.config.menu;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.events.ConfigValueChangeEvent;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.Slot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.minigame.config.*;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 1/20/2024
 * Project: StevesFunnyLibrary <br>
 */
public class StringListConfigMenu extends Menu
{
	private final ConfigTypeRegistry configTypeRegistry;
	private final GameConfiguration gameConfig;
	private final Value<List<String>> value;
	private final List<String> originalValue;

	public StringListConfigMenu(ConfigTypeRegistry configTypeRegistry, GameConfiguration gameConfig, Value<List<String>> value)
	{
		super(4, "String List - " + value.getName(), true);
		this.configTypeRegistry = configTypeRegistry;
		this.gameConfig = gameConfig;
		this.value = value;
		this.originalValue = new ArrayList<>();
		this.originalValue.addAll(gameConfig.getValue(value));
	}

	@Override
	protected void setup()
	{
		setSlot(8, 3, new ButtonSlot(JSONMessage.create("Confirm").color(ChatColor.GREEN), Material.EMERALD).setClick(click ->
		{
			if (!ConfigValueChangeEvent.change(gameConfig, value, gameConfig.getValue(value), click.player()))
				return Response.backReload();

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

		setSlot(0, 3, new ButtonSlot(JSONMessage.create("Add String").color(ChatColor.GREEN), Material.PAPER).setClick(click ->
		{
			List<String> stringList = gameConfig.getValue(value);
			stringList.add("");
			int i = stringList.size() - 1;
			int page = i / 27;
			Value<String> val = Value.create(BuiltInConfigType.STRING, "String: " + i, "str_" + i);
			createConfigButton(val, "", (i % 9) + (page * 9), (i / 9) % 3, (value, object, config) -> {
				stringList.set(i, object);
			});
			Slot slot = getSlot((i % 9) + (page * 9), (i / 9) % 3);
			slot.updateSlot();
			Click newClick = new Click(click.player(), slot, click.type(), click.action(), click.itemOnCursor());
			((ButtonSlot) slot).click.apply(newClick);
			return Response.cancel();
		}));

		createStringSlots(0);
	}

	private void createStringSlots(int slotsToClear)
	{
		for (int i = 0; i < slotsToClear; i++)
		{
			int page = i / 27;
			removeSlot((i % 9) + (page * 9), (i / 9) % 3);
		}

		List<String> stringList = gameConfig.getValue(value);
		for (int i = 0; i < stringList.size(); i++)
		{
			String s = stringList.get(i);
			int page = i / 27;
			Value<String> val = Value.create(BuiltInConfigType.STRING, "String: " + i, "str_" + i);
			int finalI = i;
			createConfigButton(val, s, (i % 9) + (page * 9), (i / 9) % 3, (value, object, config) -> {
				stringList.set(finalI, object);
			});
		}

		reload();
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
		}.setClick(click ->
		{
			if (click.type().isRightClick())
			{
				List<String> stringList = gameConfig.getValue(this.value);
				String s = stringList.remove(Integer.parseInt(value.getId().split("_")[1]));
				click.player().sendMessage("Removed '" + s + "'");
				createStringSlots(stringList.size() + 1);
				return Response.cancel();
			}
			RedirectGameConfiguration<T> config = new RedirectGameConfiguration<>(gameConfig, configTypeRegistry, redirect);
			config.getValueFunc = (val) ->
			{
				List<String> stringList = gameConfig.getValue(this.value);
				String s = stringList.get(Integer.parseInt(val.getId().split("_")[1]));
				return (T) s;
			};
			return configTypeRegistry.onClick(value, click, config);
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
		JSONMessage.create("Changed discarded!").color(ChatColor.RED).send(player);
		return Response.backReload();
	}
}
