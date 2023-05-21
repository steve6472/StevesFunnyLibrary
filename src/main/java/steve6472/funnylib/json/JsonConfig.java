package steve6472.funnylib.json;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;
import steve6472.funnylib.util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 8/9/2022
 * Project: AkmaShorts
 */
public class JsonConfig
{
	private final Plugin plugin;
	private final File jsonConfigFile;

	private JSONObject jsonConfig;
	private boolean canSave = true;

	public JsonConfig()
	{
		plugin = null;
		jsonConfigFile = null;
		canSave = false;
	}

	public JsonConfig(String configName, Plugin plugin)
	{
		this.plugin = plugin;
		jsonConfigFile = new File(plugin.getDataFolder(), configName + ".json");

		if (!jsonConfigFile.exists())
		{
			plugin.saveResource(jsonConfigFile.getName(), false);
		}
	}

	public void save(Consumer<JSONObject> saveData)
	{
		if (saveData == null)
			return;

		if (!canSave)
		{
			Log.error(ChatColor.RED + "NOT saving config due to a possible loss of data");
			return;
		}

		saveData.accept(jsonConfig);

		saveJsonConfig();

		Log.info(ChatColor.GREEN + "Saved JSON config");
	}

	public JSONObject getJsonConfig()
	{
		return jsonConfig;
	}

	public void saveJsonConfig()
	{
		try
		{
			//			Bukkit.broadcastMessage(ChatColor.GREEN + "Saving JSON");
			writeJSON(jsonConfigFile, jsonConfig);
			//			Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Saved JSON");

		} catch (IOException e)
		{
			e.printStackTrace();
			Log.error(ChatColor.RED + "Could not write config!");
			Log.error(ChatColor.RED + "See logs for more information");
		}
	}

	public void load(Consumer<JSONObject> loadData)
	{
		if (loadData == null)
			return;

		loadJsonConfig();

		try
		{
			loadData.accept(jsonConfig);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			canSave = false;
			Log.error(ChatColor.RED + "JSON loading failed!");
			Log.error(ChatColor.RED + "Plugin will NOT save any changes due to a possible loss of data");
		}
	}

	public void loadJsonConfig()
	{
		try
		{
			//			Bukkit.broadcastMessage(ChatColor.GREEN + "Loading JSON");
			if (!jsonConfigFile.exists())
			{
				//				Bukkit.broadcastMessage(ChatColor.YELLOW + "Config does not exist, creating resource");
				plugin.saveResource(jsonConfigFile.getName(), false);
			}
			jsonConfig = readJSON(jsonConfigFile);
			//			Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Loading JSON");
		} catch (IOException e)
		{
			e.printStackTrace();
			canSave = false;
			Log.error(ChatColor.RED + "Loading of JSON Config Failed!");
			Log.error(ChatColor.RED + "Plugin will NOT save any changes due to a possible loss of data");
		}
	}

	private static void writeJSON(File file, JSONObject json) throws IOException
	{
		if (file.renameTo(new File(file.getAbsolutePath() + ".backup")))
		{
			Log.debug("Created backup");
		} else
		{
			Log.error("Backup could not be created, using Move");
			try
			{
				Files.move(file.toPath(), new File(file.getAbsolutePath() + ".backup").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e)
			{
				Log.error("Failed to create a backup!");
				e.printStackTrace();
			}
		}
		if (file.delete())
		{
			Log.debug("Deleted old config at " + file.getAbsolutePath());
		}
		if (file.createNewFile())
		{
			Log.debug("Successfully created file");
		}

		FileWriter writer = new FileWriter(file);
		writer.write(JsonPrettify.prettify(json));
//		writer.write(json.toString(4));
		writer.flush();
		writer.close();
	}

	private static JSONObject readJSON(File file) throws IOException
	{
		FileReader in = new FileReader(file);
		BufferedReader reader = new BufferedReader(in);
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
		}
		in.close();
		reader.close();

		if (builder.isEmpty())
			return new JSONObject();
		else
			return new JSONObject(builder.toString());
	}
}
