package steve6472.funnylib.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by steve6472
 * On date: 6/9/2022
 * Project: AkmaEventPlugin
 * <pre>
 * {@code
 * @Command
 * @Description("description")
 * @Usage("usage")
 * public static boolean mute(@NotNull Player player, @NotNull String[] args)
 * {...}
 * }
 * </pre>
 */
public class AnnotationCommand extends BukkitCommand
{
	@NotNull
	Method method;
	boolean requireOp;

	private AnnotationCommand(@NotNull String name, @NotNull Method method, boolean requireOp)
	{
		super(name);
		this.method = method;
		this.requireOp = requireOp;
	}

	public static String[] sendUsage(@Nullable CommandSender sender, String command)
	{
		CommandData commandData = AnnotationCommand.commands.get(command);
		if (commandData == null)
		{
			return new String[]{ChatColor.RED + "Can not generate usage for command " + command
				, ChatColor.RED + "Command not found"};
		}
		String[] usage = commandData.usages();
		List<String> usageMsg = new ArrayList<>();
		if (usage != null)
		{
			StringBuilder msg = new StringBuilder();
			msg.append(ChatColor.GRAY).append("Usage:").append(ChatColor.RESET);
			usageMsg.add(ChatColor.GRAY + "Usage: " + ChatColor.RESET);
			for (String s : usage)
			{
				msg.append("\n  ").append(s);
				usageMsg.add("  " + s);
			}

			if (sender != null)
			{
				sender.sendMessage(msg.toString());
			}
		}

		return usageMsg.toArray(new String[0]);
	}

	public static JSONMessage[] sendDescription(@Nullable CommandSender sender, String command)
	{
		CommandData commandData = AnnotationCommand.commands.get(command);
		if (commandData == null)
		{
			return new JSONMessage[]{JSONMessage
				.create("Can not generate description for command " + command)
				.color(ChatColor.RED)
				.newline()
				.then("Command not found").color(ChatColor.RED)};
		}

		String[] description = commandData.description();
		List<JSONMessage> descriptionMsg = new ArrayList<>();
		if (description != null)
		{
			descriptionMsg.add(JSONMessage.create("Description: ").color(ChatColor.GRAY));
			for (String s : description)
			{
				descriptionMsg.add(JSONMessage.create().then("  ").then(s));
			}

			if (sender != null)
			{
				for (JSONMessage jsonMessage : descriptionMsg)
				{
					jsonMessage.send(sender);
				}
			}
		}

		return descriptionMsg.toArray(new JSONMessage[0]);
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args)
	{
		if (!(sender instanceof Player player))
		{
			sender.sendMessage("You are not player, you can not run this command!");
			return false;
		}

		if (!player.isOp() && requireOp)
		{
			sender.sendMessage("You are not authorized to run this command");
			return false;
		}

//		if (requireAkmaOp && !player.hasPermission(AkmaEventMain.ADMIN_PERMISSION))
//			return false;

		try
		{
			return (boolean) method.invoke(null, player, args);
		} catch (IllegalAccessException | InvocationTargetException e)
		{
			Log.error(ChatColor.RED + "Error invoking command method");
			JSONMessage.create("Error invoking command method").color(ChatColor.RED).tooltip(Objects.requireNonNullElse(e.getMessage(), "no reason")).send(sender);

			sendUsage(sender, getName());

			throw new RuntimeException(e);
		}
	}

	public static HashMap<String, CommandData> commands = new HashMap<>();

	public static void delete()
	{
		commands.clear();
	}

	public static void registerCommands(Class<?> clazz)
	{
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method declaredMethod : declaredMethods)
		{
			if (declaredMethod.isAnnotationPresent(Command.class))
			{
				declaredMethod.setAccessible(true);
				Command annotation = declaredMethod.getAnnotation(Command.class);
				String name;
				if (annotation.overrideName().isEmpty())
				{
					name = declaredMethod.getName();
				} else
				{
					name = annotation.overrideName();
				}
				String key = name.toLowerCase();

				String[] descriptions = null;

				if (declaredMethod.isAnnotationPresent(Description.class))
				{
					Description descriptionAnnotation = declaredMethod.getAnnotation(Description.class);
					descriptions = new String[] {descriptionAnnotation.value()};
				}

				if (declaredMethod.isAnnotationPresent(Descriptions.class))
				{
					Descriptions descriptionsAnnotation = declaredMethod.getAnnotation(Descriptions.class);
					Description[] value = descriptionsAnnotation.value();
					descriptions = new String[value.length];
					for (int i = 0; i < value.length; i++)
					{
						Description usage = value[i];
						descriptions[i] = usage.value();
					}
				}

				String[] usages = null;

				if (declaredMethod.isAnnotationPresent(Usage.class))
				{
					Usage usageAnnotation = declaredMethod.getAnnotation(Usage.class);
					usages = new String[] {usageAnnotation.value()};
				}

				if (declaredMethod.isAnnotationPresent(Usages.class))
				{
					Usages usageAnnotation = declaredMethod.getAnnotation(Usages.class);
					Usage[] value = usageAnnotation.value();
					usages = new String[value.length];
					for (int i = 0; i < value.length; i++)
					{
						Usage usage = value[i];
						usages[i] = usage.value();
					}
				}

				boolean hidden = declaredMethod.isAnnotationPresent(Hidden.class);

				commands.put(key, new CommandData(key, new AnnotationCommand(key, declaredMethod, annotation.requireOp()), descriptions, usages, hidden));
			}
		}
	}

	public record CommandData(String command, BukkitCommand executor, String[] description, String[] usages, boolean hidden) {}
}
