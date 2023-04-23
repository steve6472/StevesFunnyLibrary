package steve6472.funnylib.util;

import org.bukkit.ChatColor;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 8/11/2022
 * Project: AkmaShorts
 */
public class Messages
{
	private static final BiFunction<Integer, JSONMessage, JSONMessage> stopTaskExtra = (i, m) -> m.tooltip("Run command /stoptask " + i).runCommand("/stoptask " + i);

	public static final Function<Integer, JSONMessage> STOP_TASK = i -> stopTaskExtra.apply(i, stopTaskExtra.apply(i, JSONMessage.create("Task Id: ").color(ChatColor.DARK_GRAY)).then("" + i).color(ChatColor.GRAY));

	public static JSONMessage createLocationMessage(String locationName, int x, int y, int z)
	{
		return JSONMessage
			.create(locationName).color(ChatColor.DARK_GRAY)
			.then(Integer.toString(x), ChatColor.RED)
			.then("/", ChatColor.WHITE)
			.then(Integer.toString(y), ChatColor.GREEN)
			.then("/", ChatColor.WHITE)
			.then(Integer.toString(z), ChatColor.BLUE)
			.setItalic(JSONMessage.ItalicType.FALSE);
	}

	public static JSONMessage createLocationMessage(int x, int y, int z)
	{
		return createLocationMessage("Location: ", x, y, z);
	}
}
