package steve6472.funnylib.packgen;

import org.json.JSONObject;
import steve6472.funnylib.util.JSONMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by steve6472
 * Date: 9/16/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ResourcepackGenerator
{
	private static final int format = 8;

	private final String name;
	private final JSONMessage description;

	public ResourcepackGenerator(String name, JSONMessage description)
	{
		this.name = name;
		this.description = description;
	}

	public boolean generate(boolean zip)
	{
		File root = new File(name);
		if (root.renameTo(new File(root, "_backup")))
		{
			System.out.println("Created backup!");
		}
		if (root.delete())
		{
			System.out.println("Deleted existing file!");
		}
		if (root.mkdir())
		{
			System.out.println("Created new pack folder");
		}

		JSONObject pack = new JSONObject();
		pack.put("pack_format", format);
		pack.put("description", description.toLegacy());
		saveString(new File(root, "pack.mcmeta"), new JSONObject().put("pack", pack).toString());


		return true;
	}

	private void saveString(File file, String text)
	{
		try
		{
			boolean newFile = file.createNewFile();
			if (!newFile)
				throw new IOException("Probably already exists or whatever");

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(text);
			writer.close();

		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
