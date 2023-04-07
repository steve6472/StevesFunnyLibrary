package steve6472.standalone.interactable.script;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.scriptit.*;
import steve6472.scriptit.attributes.AttributeGet;
import steve6472.scriptit.attributes.AttributeJavaParamInjector;
import steve6472.scriptit.attributes.AttributeSet;
import steve6472.scriptit.transformer.SchemeParser;
import steve6472.scriptit.value.Value;

import java.io.File;
import java.util.*;

/**
 * Created by steve6472
 * Date: 8/25/2022
 * Project: AkmaShorts
 */
public class ScriptRepository implements Listener
{
	private final Workspace workspace;
	private final File scritpsFolder;

	/**
	 * String - file name (without extension)
	 * Script - script
	 */
	public final Map<String, Script> scripts;

	public final Set<Script> tickScripts;

	public ScriptRepository()
	{
//		ScriptItSettings.ALLOW_UNSAFE = true;
//		ScriptItSettings.CLASS_TRANSFORMER_DEBUG = true;
//		ScriptItSettings.CLASS_TRANSFORMER_IGNORED_DEBUG = true;
		ScriptItSettings.ALLOW_CLASS_TYPE_CONVERSION = true;

		Log.scriptDebug = Bukkit::broadcastMessage;
		Log.DEBUG_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.BLACK + "DEBUG" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
		Log.DEBUG_COLOR = "" + ChatColor.AQUA;
		Log.RESET_MESSAGES = "" + ChatColor.RESET;
		Log.scriptDebug(true, "Hello debug!");

		scritpsFolder = new File(FunnyLib.getPlugin().getDataFolder(), "scripts");
		scripts = new HashMap<>();
		tickScripts = new HashSet<>();

		Highlighter.basicHighlight();

		workspace = new Workspace();
//		workspace.addAttribute(new AttributeJavaParamInjector());
		workspace.addAttribute(new AttributeGet());
		workspace.addAttribute(new AttributeSet());
		workspace.addLibrary(new MinecraftLibrary());
		workspace.addTransformer("minecraft", new SchemeParser().createConfigs(new File("plugins/" + FunnyLib.getPlugin().getName() + "/transformers/minecraft.txt")));

		reload(null);
	}

	private boolean tryCreateFolder(Player player)
	{
		if (!scritpsFolder.exists())
		{
			if (!scritpsFolder.mkdir())
			{
				messageOrBroadcast(player, ChatColor.RED + "Failed to create scripts folder!");
				return false;
			}
		}

		return true;
	}

	public void reload(Player player)
	{
		workspace.addTransformer("minecraft", new SchemeParser().createConfigs(new File("plugins/" + FunnyLib.getPlugin().getName() + "/transformers/minecraft.txt")));

		if (!tryCreateFolder(player))
		{
			return;
		}

		File[] files = scritpsFolder.listFiles();
		if (files == null)
		{
			messageOrBroadcast(player, ChatColor.RED + "Can not load scripts, listed files are null!");
			return;
		}

		scripts.clear();
		tickScripts.clear();

		for (File file : files)
		{
			String name = file.getName();

			try
			{
				Script script = Script.create(workspace, file);
				String nameWithoutExtension = name.substring(0, name.lastIndexOf('.'));
				if (nameWithoutExtension.isEmpty())
					continue;

				scripts.put(nameWithoutExtension, script);

				messageOrBroadcast(player, "Loaded script '" + ChatColor.BLUE + nameWithoutExtension + ChatColor.RESET + "' (" + ChatColor.AQUA + name + ChatColor.RESET + ")");
			} catch (Exception ex)
			{
				Bukkit.broadcastMessage(ChatColor.RED + "Failed to load script '" + ChatColor.AQUA + name + ChatColor.AQUA + "'");
				Bukkit.broadcastMessage(ChatColor.RED + ex.getMessage());
			}
		}
	}

	private void messageOrBroadcast(Player player, String message)
	{
		if (player == null)
		{
			Bukkit.broadcastMessage(message);
		} else
		{
			player.sendMessage(message);
		}
	}

	@EventHandler
	public void tick(ServerTickEvent e)
	{
		for (Iterator<Script> iterator = tickScripts.iterator(); iterator.hasNext(); )
		{
			try
			{
				Script tickScript = iterator.next();
				runTillDelay(tickScript);

			} catch (Exception ex)
			{
				iterator.remove();
				Bukkit.broadcastMessage(ChatColor.RED + "Tick error " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	public static Value runTillDelay(Script script)
	{
		Result ret;

		do
		{
			ret = script.getMainExecutor().executeSingle(script);
		} while (script.getMainExecutor().canExecuteMore() && !ret.isReturnValue() && !ret.isReturn() && !script.getMainExecutor().isWasLastDelay());

		if (!script.getMainExecutor().isWasLastDelay() && !script.getMainExecutor().canExecuteMore())
		{
			script.getMainExecutor().reset();
		}

		if (ret.isReturnValue())
			return ret.getValue();
		else
			return Value.NULL;
	}
}
