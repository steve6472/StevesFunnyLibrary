package steve6472.funnylib.minigame.config;

import net.wesjd.anvilgui.AnvilGUI;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.StructureItem;
import steve6472.funnylib.json.JsonNBT;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.*;
import java.util.regex.Pattern;

/**
 * Created by steve6472
 * Date: 12/25/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ConfigTypeRegistry
{
	private final Map<ConfigType<?>, Config<?>> configMap;

	private record Config<T>(
		BiFunction<Value<T>, JSONObject, T> load,
		TriConsumer<Value<T>, T, JSONObject> save,
		BiFunction<Value<T>, T, ItemStack> icon,
		TriFunction<Click, Value<T>, GameConfiguration, Response> click
	)
	{}

	public ConfigTypeRegistry()
	{
		configMap = new HashMap<>();
		registerBuiltin();
	}

	/*
	 * Helper functions
	 */

	@Contract("null -> fail")
	private <T> Config<T> getConfig(Value<T> value)
	{
		//noinspection unchecked
		Config<T> config = (Config<T>) configMap.get(value.getValueType());
		if (config == null)
		{
			throw new RuntimeException("Config not found for " + value.getValueType());
		} else
		{
			return config;
		}
	}

	public static Predicate<Class<?>> predicate(@NotNull Class<?>... classes)
	{
		return clazz ->
		{
			for (Class<?> aClass : classes)
			{
				if (clazz.isAssignableFrom(aClass))
					return true;
			}
			return false;
		};
	}

	public static ItemStack toStringIcon(@NotNull String title, @NotNull Material material, @Nullable Object object, boolean rightClickClear)
	{
		JSONMessage valueLore = JSONMessage.create().then(object == null ? "null" : object.toString());
		valueLore = object == null ? valueLore.color("#CA7631") : valueLore.color(ChatColor.YELLOW);

		ItemStackBuilder builder = ItemStackBuilder
			.create(material)
			.setName(JSONMessage.create(title).setItalic(JSONMessage.ItalicType.FALSE))
			.addLore(JSONMessage
				.create("Value: ")
				.color(ChatColor.GRAY)
				.then(valueLore)
				.setItalic(JSONMessage.ItalicType.FALSE));

		if (rightClickClear)
		{
			builder = builder.addLore(JSONMessage.create("Right click to clear").color(ChatColor.RED));
		}

		return builder.buildItemStack();
	}

	public static Predicate<String> patternValidator(Pattern pattern)
	{
		return s -> pattern.matcher(s).matches();
	}

	public <T> Response anvilInput(Click click, Value<T> value, GameConfiguration gameConfig, Predicate<String> inputValidator, Function<String, T> toObject, boolean rightClickClear)
	{
		if (rightClickClear && click.type().isRightClick())
		{
			gameConfig.setValue(value, null);
			click.slot().updateSlot(createIcon(value, null));
			return Response.cancel();
		}

		click.menu().anvilRedirect();

		final boolean[] a = {false};

		new AnvilGUI
			.Builder()
			.plugin(FunnyLib.getPlugin())
			.title("Provide input: ")
			.itemLeft(ItemStackBuilder.create(Material.PAPER).setName(JSONMessage.create("")).buildItemStack())
			.onClick((i, snapshot) ->
			{
				String text = snapshot.getText();
				if (!inputValidator.test(text))
				{
					return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Input invalid!"));
				}

				a[0] = true;

				T object = toObject.apply(text);
				gameConfig.setValue(value, object);
				click.slot().updateSlot(createIcon(value, object));

				JSONMessage.create("Value updated!").color(ChatColor.GREEN).send(click.player());

				return Collections.singletonList(AnvilGUI.ResponseAction.close());
			})
			.onClose(snapshot ->
			{
				if (!a[0])
				{
					JSONMessage.create("Value discarded!").color(ChatColor.RED).send(click.player());
				}
			})
			.open(click.player());

		return Response.cancel();
	}



	/*
	 * Registration
	 */

	public <T> void registerType(ConfigType<T> configType,
	                         BiFunction<Value<T>, JSONObject, T> load,
	                         TriConsumer<Value<T>, T, JSONObject> save,
	                         BiFunction<Value<T>, T, ItemStack> icon,
	                         TriFunction<Click, Value<T>, GameConfiguration, Response> click
	)
	{
		Config<T> config = new Config<T>(load, save, icon, click);

		configMap.put(configType, config);
	}

	private void registerBuiltin()
	{
		registerType(
			BuiltInConfigType.STRING,
			(value, json) -> json.optString(value.getId(), null),
			(value, object, json) -> json.put(value.getId(), object),
			(value, object) -> toStringIcon(value.getName(), Material.STRING, object, true),
			(click, value, gameConfig) -> anvilInput(click, value, gameConfig, input -> true, t -> t, true)
		);

		registerType(
			BuiltInConfigType.ID_MATCH,
			(value, json) -> json.optString(value.getId(), null),
			(value, object, json) -> json.put(value.getId(), object),
			(value, object) -> toStringIcon(value.getName(), Material.OAK_SIGN, object, true),
			(click, value, gameConfig) -> anvilInput(click, value, gameConfig, patternValidator(MiscUtil.ID_MATCH), t -> t, true)
		);

		registerType(
			BuiltInConfigType.INT,
			(value, json) -> json.optInt(value.getId(), 0),
			(value, object, json) -> json.put(value.getId(), object),
			(value, object) -> toStringIcon(value.getName(), Material.IRON_NUGGET, object, false),
			(click, value, gameConfig) -> anvilInput(click, value, gameConfig, patternValidator(MiscUtil.IS_INTEGER), Integer::parseInt, false)
		);

		registerType(
			BuiltInConfigType.DOUBLE,
			(value, json) -> json.optDouble(value.getId(), 0),
			(value, object, json) -> json.put(value.getId(), object),
			(value, object) -> toStringIcon(value.getName(), Material.GOLD_NUGGET, object, false),
			(click, value, gameConfig) -> anvilInput(click, value, gameConfig, patternValidator(MiscUtil.IS_DECIMAL), Double::parseDouble, false)
		);

		registerType(
			BuiltInConfigType.STRUCTURE,
			(value, json) ->
			{
				if (json.has(value.getId()))
					return GameStructure.structureFromNBT(PdcNBT.fromPDC(JsonNBT.JSONtoNBT(json.getJSONObject(value.getId()))));
				else
					return null;
			},
			(value, object, json) ->
			{
				if (object == null)
				{
					json.put(value.getId(), (Object) null);
					return;
				}
				PdcNBT nbt = PdcNBT.fromPDC(NMS.newCraftContainer());
				object.toNBT(nbt);
				JSONObject jsonObject = JsonNBT.containerToJSON(nbt.getContainer());
				json.put(value.getId(), jsonObject);
			},
			(value, object) ->
			{
				if (object == null)
				{
					return ItemStackBuilder.create(Material.WRITABLE_BOOK).setName(JSONMessage.create(value.getName())).buildItemStack();
				}

				return ItemStackBuilder
					.create(object.icon())
					.setName(object.name() == null ? "Structure" : object.name(), ChatColor.DARK_AQUA)
					.addLore(Messages.createLocationMessage("Size: ", object.getSize().x, object.getSize().y, object.getSize().z))
					.buildItemStack();
			},
			(click, value, gameConfig) ->
			{
				if (click.itemOnCursor() == null || click.itemOnCursor().getType().isAir())
				{
					if (gameConfig.getValue(value) == null)
						return Response.cancel();

					return Response.setItemToCursor((gameConfig.getValue(value).toItem()));
				}

				final Predicate<ItemStack> check = (itemStack) ->
				{
					if (Items.getCustomItem(itemStack) != FunnyLib.STRUCTURE) return false;
					ItemNBT nbt = ItemNBT.create(itemStack);
					if (!nbt.hasCompound(StructureItem.KEY)) return false;
					return nbt.has3i("start") && nbt.has3i("end");
				};

				if (check.test(click.itemOnCursor()))
				{
					GameStructure obj = GameStructure.fromItem(click.itemOnCursor());
					if (obj == null)
						return Response.cancel();

					gameConfig.setValue(value, obj);
					click.slot().updateSlot(ItemStackBuilder
						.create(obj.icon())
						.setName(obj.name() == null ? "Structure" : obj.name(), ChatColor.DARK_AQUA)
						.addLore(Messages.createLocationMessage("Size: ", obj.getSize().x, obj.getSize().y, obj.getSize().z))
						.buildItemStack());
					return Response.cancel();
				}

				return Response.cancel();
			}
		);
	}

	/*
	 *
	 */

	public <T> T load(Value<T> value, JSONObject json)
	{
		Config<T> config = getConfig(value);
		return config.load().apply(value, json);
	}

	public <T> void save(Value<T> value, T obj, JSONObject json)
	{
		Config<T> config = getConfig(value);
		config.save().accept(value, obj, json);
	}

	public <T> ItemStack createIcon(Value<T> value, T object)
	{
		Config<T> config = getConfig(value);
		return config.icon().apply(value, object);
	}

	public <T> Response onClick(Value<T> value, Click click, GameConfiguration gameConfig)
	{
		Config<T> config = getConfig(value);
		return config.click.apply(click, value, gameConfig);
	}

	public boolean isValidType(Value<?> value)
	{
		return configMap.containsKey(value.getValueType());
	}
}
