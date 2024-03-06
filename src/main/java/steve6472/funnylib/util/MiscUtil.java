package steve6472.funnylib.util;

import net.minecraft.nbt.Tag;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_20_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.joml.Quaternionf;
import org.joml.Vector3i;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**********************
 * Created by steve6472
 * On date: 3/18/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class MiscUtil
{
	public static final Material[] DYES = {Material.WHITE_DYE, Material.ORANGE_DYE, Material.MAGENTA_DYE, Material.LIGHT_BLUE_DYE, Material.YELLOW_DYE, Material.LIME_DYE, Material.PINK_DYE, Material.GRAY_DYE, Material.LIGHT_GRAY_DYE, Material.CYAN_DYE, Material.PURPLE_DYE, Material.BLUE_DYE, Material.BROWN_DYE, Material.GREEN_DYE, Material.RED_DYE, Material.BLACK_DYE};
	public static final Color[] COLORS = {Color.fromRGB(0xf9fffe), Color.fromRGB(0xf9801d), Color.fromRGB(0xc74ebd), Color.fromRGB(0x3ab3da), Color.fromRGB(0xfed83d), Color.fromRGB(0x80c71f), Color.fromRGB(0xf38baa), Color.fromRGB(0x474f52), Color.fromRGB(0x9d9d97), Color.fromRGB(0x169c9c), Color.fromRGB(0x8932b8), Color.fromRGB(0x3c44aa), Color.fromRGB(0x835432), Color.fromRGB(0x5e7c16), Color.fromRGB(0xb02e26), Color.fromRGB(0x1d1d21)};
	public static final String[] DYE_NAMES = {"White", "Orange", "Magenta", "Light blue", "Yellow", "Lime", "Pink", "Gray", "Light gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};

	public static final BlockFace[] DIRECTIONS = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
	public static final BlockFace[] HORIZONTAL_DIRECTIONS = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

	public static final ItemStack AIR = new ItemStack(Material.AIR);

	public static final Pattern ID_MATCH = Pattern.compile("[a-z0-9_]*");
	public static final Pattern HEX_MATCH = Pattern.compile("[a-fA-F0-9]{6}");
	public static final Pattern HEX_REPLACER = Pattern.compile("&#[0-9a-f]{6}");
	public static final Pattern IS_INTEGER = Pattern.compile("([+-]?\\d)+");
	public static final Pattern IS_DECIMAL = Pattern.compile("([+-]?\\d*(\\.\\d+)?)+");

	public static String prettyPrintLocation(Location location)
	{
		return ChatColor.RED + "X: " + location.getBlockX() + ChatColor.GREEN + " Y: " + location.getBlockY() + ChatColor.BLUE + " Z: " + location
			.getBlockZ() + ChatColor.RESET;
	}

	public static String prettyPrintLocation(Vector3i location)
	{
		return ChatColor.RED + "X: " + location.x() + ChatColor.GREEN + " Y: " + location.y() + ChatColor.BLUE + " Z: " + location
			.z() + ChatColor.RESET;
	}

	public static void printStackTrace()
	{
		for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace())
		{
			System.out.println(stackTraceElement);
		}
	}

	public static String dataToString(PersistentDataContainer container)
	{
		CraftPersistentDataContainer container_ = (CraftPersistentDataContainer) container;
		Map<String, Tag> stringTagMap = container_.getRaw();
		final String[] out = {""};
		stringTagMap.forEach((k, v) ->
		{
			String msg = k + " " + v.toString();
			out[0] += msg + "\n";
		});
		return out[0];
	}

	public static Quaternionf createRandomRotation()
	{
		// Create a Quaternionf with random rotations on all axes
		float randomAngleX = (float) Math.toRadians(Math.random() * 360.0);
		float randomAngleY = (float) Math.toRadians(Math.random() * 360.0);
		float randomAngleZ = (float) Math.toRadians(Math.random() * 360.0);

		// Create individual quaternions for each axis
		Quaternionf quaternionX = new Quaternionf().rotationX(randomAngleX);
		Quaternionf quaternionY = new Quaternionf().rotationY(randomAngleY);
		Quaternionf quaternionZ = new Quaternionf().rotationZ(randomAngleZ);

		// Combine the individual quaternions to get the final random rotation
		return new Quaternionf().identity().mul(quaternionX).mul(quaternionY).mul(quaternionZ);
	}

	public static Quaternionf applyRandomOffset(Quaternionf originalQuaternion, float maxOffsetAngle)
	{
		// Create a random number generator
		Random random = new Random();

		// Generate random offsets for each axis
		float offsetX = random.nextFloat() * maxOffsetAngle;
		float offsetY = random.nextFloat() * maxOffsetAngle;
		float offsetZ = random.nextFloat() * maxOffsetAngle;

		// Create a quaternion representing the random offset
		Quaternionf offsetQuaternion = new Quaternionf()
			.rotateAxis(offsetX, 1, 0, 0)
			.rotateAxis(offsetY, 0, 1, 0)
			.rotateAxis(offsetZ, 0, 0, 1);

		// Apply the random offset to the original quaternion
		return new Quaternionf(originalQuaternion).mul(offsetQuaternion);
	}

	private static final String MAP_FROM = "0123456789abcdef";
	private static final String MAP_TO = "fedcba9876543210";

	public static String invertColor(String color)
	{
		char[] charArray = color.toCharArray();

		for (int i = 0; i < charArray.length; i++)
		{
			char c = charArray[i];
			int index = MAP_FROM.indexOf(c);
			if (index != -1)
			{
				charArray[i] = MAP_TO.charAt(index);
			}
		}

		return new String(charArray);
	}

	private static String convertHexToColor(String hex)
	{
		return net.md_5.bungee.api.ChatColor.of(hex.substring(1).toUpperCase()).toString();
	}

	public static String hexColor(String original)
	{
		StringBuilder output = new StringBuilder();

		Matcher matcher = HEX_REPLACER.matcher(original);
		if (matcher.find())
		{
			matcher.reset();
			int lastIndex = 0;
			while (matcher.find())
			{
				String group = matcher.group();
				output.append(original, lastIndex, matcher.start()).append(convertHexToColor(group));

				lastIndex = matcher.end();
			}

			if (lastIndex < original.length())
			{
				output.append(original, lastIndex, original.length());
			}
		} else
		{
			output.append(original);
		}

		return output.toString();
	}

	public static JSONMessage legacyToJsonMessage(String string)
	{
		StringBuilder builder = new StringBuilder(string.length());
		JSONMessage message = null;
		ChatColor lastColor = null;

		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			if (c == ChatColor.COLOR_CHAR)
			{
				// String is out of bounds
				if (i + 1 >= string.length())
					break;

				char code = string.charAt(i + 1);

				// Hex
				if (code != 'x')
				{
					if (lastColor != null)
					{
						if (message == null)
						{
							message = JSONMessage.create(builder.toString());
						} else
						{
							message.then(builder.toString());
						}
						if (lastColor.isColor())
							message.color(lastColor);
						else
							message.style(lastColor);
						builder.setLength(0);
					}

					lastColor = ChatColor.getByChar(string.charAt(i + 1));
					Preconditions.checkNotNull(lastColor, "Unknown chat color code ? '" + string.charAt(i + 1) + "'");

					i++;
				} else {
					throw new RuntimeException("Hex is not supported!");
				}
			} else
			{
				builder.append(c);
			}
		}

		if (message == null)
		{
			message = JSONMessage.create(builder.toString());
		} else
		{
			message.then(builder.toString());
		}

		if (lastColor != null)
		{
			if (lastColor.isColor())
				message.color(lastColor);
			else
				message.style(lastColor);
		}

		return message;
	}

	public static boolean isJSONValid(String test)
	{
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(test);
			} catch (JSONException ex1)
			{
				return false;
			}
		}
		return true;
	}

	public static JSONObject getValidJsonObject(String test)
	{
		try
		{
			return new JSONObject(test);
		} catch (JSONException ex)
		{
			return null;
		}
	}

	public static JSONArray getValidJsonArray(String test)
	{
		try
		{
			return new JSONArray(test);
		} catch (JSONException ex)
		{
			return null;
		}
	}

	public static void recursiveSerialization(JSONObject json, String key, Object value)
	{
		if (value instanceof ConfigurationSerializable cs)
		{
			JSONObject child = new JSONObject();
			Map<String, Object> serialize = cs.serialize();
			serialize.forEach((k, v) -> recursiveSerialization(child, k, v));
			json.put(key, child);
		}
		else if (key.equals("PublicBukkitValues") && value instanceof HashMap<?, ?> map)
		{
			JSONObject publicBukkitValues = new JSONObject();
			map.forEach((k, v) -> recursiveSerialization(publicBukkitValues, k.toString(), v));
			json.put(key, publicBukkitValues);
		} else
		{
			json.put(key, value);
		}

	}

	public static JSONObject serializeItemStack(JSONObject json, ItemStack itemStack)
	{
		Map<String, Object> serialize = itemStack.serialize();

		serialize.forEach((k, v) -> recursiveSerialization(json, k, v));

		return json;
	}

	public static JSONObject serializeItemStack(ItemStack itemStack)
	{
		JSONObject json = new JSONObject();
		Map<String, Object> serialize = itemStack.serialize();

		serialize.forEach((k, v) -> recursiveSerialization(json, k, v));

		return json;
	}

	public static ItemStack deserializeItemStack(JSONObject json)
	{
		if (json == null || json.isEmpty())
			return MiscUtil.AIR;

		Map<String, Object> map = new LinkedHashMap<>();
		for (String key : json.keySet())
		{
			Object o = json.get(key);

			if (key.equals("meta") && o instanceof JSONObject metaJson)
			{
				Map<String, Object> metaMap = new LinkedHashMap<>();
				for (String metaKey : metaJson.keySet())
				{
					Object value = metaJson.get(metaKey);

					if (metaKey.equals("PublicBukkitValues") && value instanceof JSONObject valuesJson)
					{
						Map<String, Object> publicBukkitValues = new HashMap<>();
						for (String valuesKey : valuesJson.keySet())
						{
							publicBukkitValues.put(valuesKey, valuesJson.get(valuesKey));
						}
						value = publicBukkitValues;
					}

					metaMap.put(metaKey, value);
				}
				metaMap.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, "ItemMeta");

				o = ConfigurationSerialization.deserializeObject(metaMap);
			}

			map.put(key, o);
		}

		return (ItemStack) ConfigurationSerialization.deserializeObject(map, ItemStack.class);
	}

	public static JSONMessage nameToComponent(ItemStack item, ChatColor translateColor)
	{
		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta == null || !itemMeta.hasDisplayName())
		{
			return JSONMessage.translate((item.getType().isBlock() ? "block" : "item") + ".minecraft." + item.getType().name().toLowerCase()).color(translateColor);
		} else
		{
			return JSONMessage.create(itemMeta.getDisplayName());
		}
	}

	/*
	 * https://gist.github.com/aadnk/8138186
	 */

	public static String itemToBase64(ItemStack itemStack)
	{
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeObject(Objects.requireNonNullElse(itemStack, AIR));

			dataOutput.close();

			return Base64Coder.encodeLines(outputStream.toByteArray());

		} catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static ItemStack itemFromBase64(String data)
	{
		try
		{
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

			Object o = dataInput.readObject();
			if (o instanceof ItemStack i)
			{
				return i;
			} else
			{
				throw new RuntimeException("Read data is NOT an ItemStack");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return new ItemStack(Material.AIR);
		}
	}

	private final static int CENTER_PX = 154;

	/**
	 * @param player player to recieve the message
	 * @param message the message to sent to the player
	 * @author <a href="https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/">SirSpoodles</a>
	 */
	public static void sendCenteredMessage(Player player, String message)
	{
		if (message == null || message.equals(""))
		{
			player.sendMessage("");
			return;
		}
		message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : message.toCharArray())
		{
			if (c == '§')
			{
				previousCode = true;
			} else if (previousCode)
			{
				previousCode = false;
				if (c == 'l' || c == 'L')
				{
					isBold = true;
					continue;
				} else
					isBold = false;
			} else
			{
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate)
		{
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb + message);
	}

	public enum DefaultFontInfo
	{
		A('A', 5),
		a('a', 5),
		B('B', 5),
		b('b', 5),
		C('C', 5),
		c('c', 5),
		D('D', 5),
		d('d', 5),
		E('E', 5),
		e('e', 5),
		F('F', 5),
		f('f', 4),
		G('G', 5),
		g('g', 5),
		H('H', 5),
		h('h', 5),
		I('I', 3),
		i('i', 1),
		J('J', 5),
		j('j', 5),
		K('K', 5),
		k('k', 4),
		L('L', 5),
		l('l', 1),
		M('M', 5),
		m('m', 5),
		N('N', 5),
		n('n', 5),
		O('O', 5),
		o('o', 5),
		P('P', 5),
		p('p', 5),
		Q('Q', 5),
		q('q', 5),
		R('R', 5),
		r('r', 5),
		S('S', 5),
		s('s', 5),
		T('T', 5),
		t('t', 4),
		U('U', 5),
		u('u', 5),
		V('V', 5),
		v('v', 5),
		W('W', 5),
		w('w', 5),
		X('X', 5),
		x('x', 5),
		Y('Y', 5),
		y('y', 5),
		Z('Z', 5),
		z('z', 5),
		NUM_1('1', 5),
		NUM_2('2', 5),
		NUM_3('3', 5),
		NUM_4('4', 5),
		NUM_5('5', 5),
		NUM_6('6', 5),
		NUM_7('7', 5),
		NUM_8('8', 5),
		NUM_9('9', 5),
		NUM_0('0', 5),
		EXCLAMATION_POINT('!', 1),
		AT_SYMBOL('@', 6),
		NUM_SIGN('#', 5),
		DOLLAR_SIGN('$', 5),
		PERCENT('%', 5),
		UP_ARROW('^', 5),
		AMPERSAND('&', 5),
		ASTERISK('*', 5),
		LEFT_PARENTHESIS('(', 4),
		RIGHT_PERENTHESIS(')', 4),
		MINUS('-', 5),
		UNDERSCORE('_', 5),
		PLUS_SIGN('+', 5),
		EQUALS_SIGN('=', 5),
		LEFT_CURL_BRACE('{', 4),
		RIGHT_CURL_BRACE('}', 4),
		LEFT_BRACKET('[', 3),
		RIGHT_BRACKET(']', 3),
		COLON(':', 1),
		SEMI_COLON(';', 1),
		DOUBLE_QUOTE('"', 3),
		SINGLE_QUOTE('\'', 1),
		LEFT_ARROW('<', 4),
		RIGHT_ARROW('>', 4),
		QUESTION_MARK('?', 5),
		SLASH('/', 5),
		BACK_SLASH('\\', 5),
		LINE('|', 1),
		TILDE('~', 5),
		TICK('`', 2),
		PERIOD('.', 1),
		COMMA(',', 1),
		SPACE(' ', 3),
		DEFAULT('a', 4);

		private final char character;
		private final int length;

		DefaultFontInfo(char character, int length)
		{
			this.character = character;
			this.length = length;
		}

		public char getCharacter()
		{
			return this.character;
		}

		public int getLength()
		{
			return this.length;
		}

		public int getBoldLength()
		{
			if(this == DefaultFontInfo.SPACE) return this.getLength();
			return this.length + 1;
		}

		public static DefaultFontInfo getDefaultFontInfo(char c)
		{
			for(DefaultFontInfo dFI : DefaultFontInfo.values())
			{
				if(dFI.getCharacter() == c) return dFI;
			}
			return DefaultFontInfo.DEFAULT;
		}
	}
}
