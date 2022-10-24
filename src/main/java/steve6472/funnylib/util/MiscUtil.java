package steve6472.funnylib.util;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import steve6472.funnylib.FunnyLib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.function.Function;
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

	public static void printStackTrace()
	{
		for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace())
		{
			System.out.println(stackTraceElement);
		}
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

	/*
	 * https://gist.github.com/aadnk/8138186
	 */

	public static String itemToBase64(ItemStack itemStack)
	{
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeObject(Objects.requireNonNullElseGet(itemStack, () -> AIR));

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
