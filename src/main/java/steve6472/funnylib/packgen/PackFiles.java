package steve6472.funnylib.packgen;

import org.bukkit.plugin.Plugin;
import steve6472.funnylib.FunnyLib;

import java.io.File;

/**
 * Created by steve6472
 * Date: 2/14/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PackFiles
{
	public final File zippedFile;           // UUID.zip
	public final File finalPackFolder;      // resource-pack

	public final File generatedRoot;        // generated
	public final File genMinecraft;         // generated/minecraft
	public final File genItemModels;        // generated/minecraft/models/item
	public final File genCustomModels;      // generated/minecraft/models/custom
	public final File genCustomTextures;    // generated/minecraft/textures/custom

	public final File pack;                 // pack
	public final File staticRoot;           // pack/static
	public final File packModels;           // pack/models
	public final File packTextures;         // pack/textures

	public PackFiles(Plugin plugin)
	{
		final File data = plugin.getDataFolder();

		zippedFile = new File(FunnyLib.getSettings().packUUID.toString() + ".zip");
		finalPackFolder = new File(data, "/resource-pack");

		generatedRoot = new File(data, "/generated");
		genMinecraft = new File(generatedRoot, "/assets/minecraft");
		genItemModels = new File(genMinecraft, "/models/item");
		genCustomModels = new File(genMinecraft, "/models/custom");
		genCustomTextures = new File(genMinecraft, "/textures/custom");

		pack = new File(data, "/pack");
		staticRoot = new File(pack, "/static");
		packModels = new File(pack, "/models");
		packTextures = new File(pack, "/textures");
	}
}
