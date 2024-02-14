package steve6472.funnylib.packgen;

import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.packgen.gens.CopyModel;
import steve6472.funnylib.packgen.gens.GenericItemModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by steve6472
 * Date: 2/14/2024
 * Project: StevesFunnyLibrary <br>
 */
public class Models
{
	private final PackEngine engine;
	private final Map<Material, Integer> materials = new HashMap<>();
	private final Map<String, Model> models = new HashMap<>();
	private final Map<Material, JSONObject> baseOverrides = new HashMap<>();

	public final Model CROOK = genericItemTexture("crook", Material.WOODEN_SWORD, "wooden_crook");
	public final Model TUMBLEWEED = fromModel("tumbleweed", Material.COMMAND_BLOCK, "tumbleweed");

	public Models(PackEngine engine)
	{
		this.engine = engine;
		fillBaseOverrides();
		update();
	}

	public Model genericItemTexture(String modelId, Material material, String texturePath)
	{
		Model model = new Model(modelId);
		model.customModelData = getData(material);
		model.materialOverride = material;
		model.modelGenerator = new GenericItemModel(texturePath);
		models.put(modelId, model);
		return model;
	}

	public Model fromModel(String modelId, Material material, String modelPath)
	{
		Model model = new Model(modelId);
		model.customModelData = getData(material);
		model.materialOverride = material;
		model.modelGenerator = new CopyModel(modelPath);
		models.put(modelId, model);
		return model;
	}

	private void fillBaseOverrides()
	{
		// Special for basic items
		baseOverrides.put(Material.AIR, new JSONObject().put("parent", "minecraft:item/handheld"));
		baseOverrides.put(Material.COMMAND_BLOCK, new JSONObject().put("parent", "minecraft:block/command_block"));
		baseOverrides.put(Material.WOODEN_SWORD, new JSONObject().put("parent", "minecraft:item/handheld").put("textures", new JSONObject().put("layer0", "item/wooden_sword")));
		baseOverrides.put(Material.LEATHER_HORSE_ARMOR, new JSONObject().put("parent", "minecraft:item/generated").put("textures", new JSONObject().put("layer0", "minecraft:item/leather_horse_armor")));
	}

	/**
	 * Regenerate custom model data
	 */
	public void update()
	{
		materials.clear();
		for (Model model : models.values())
		{
			model.customModelData = getData(model.materialOverride);
		}
	}

	/**
	 * Generate new
	 */
	public void generate()
	{
		update();

		// Generate custom files
		models.forEach((key, model) -> {
			try
			{
				model.modelGenerator.generate(engine.files, model);
			} catch (IOException e)
			{
				System.err.println("Failed to generate for model " + model);
				throw new RuntimeException(e);
			}
		});

		overrideVanilla();
	}

	private void overrideVanilla()
	{
		final Map<Material, JSONArray> overrides = new HashMap<>();

		for (Model model : models.values())
		{
			JSONArray array = overrides.computeIfAbsent(model.materialOverride, mat -> new JSONArray());
			JSONObject json = new JSONObject();
			json.put("model", "custom/" + model.modelGenerator.generatedFilePath(engine.files, model));
			json.put("predicate", new JSONObject().put("custom_model_data", model.customModelData));
			array.put(json);
		}

		overrides.forEach((mat, array) ->
		{
			JSONObject jsonObject = baseOverrides.get(mat);
			if (jsonObject == null)
				jsonObject = baseOverrides.get(Material.AIR);
			JSONObject baseOverride = new JSONObject(jsonObject.toString());
			baseOverride.put("overrides", array);

			try
			{
				FileUtils.writeStringToFile(new File(engine.files.genItemModels, mat.name().toLowerCase() + ".json"), baseOverride.toString(4), Charset.defaultCharset());
			} catch (IOException e)
			{
				System.err.println("Failed to generate for vanilla override for material " + mat);
				throw new RuntimeException(e);
			}
		});
	}

	private int getData(Material material)
	{
		Integer data = materials.get(material);
		if (data == null)
		{
			materials.put(material, 1);
			data = 1;
		} else
		{
			data++;
		}

		return data;
	}

	public Collection<Model> getModels()
	{
		return models.values();
	}

	public Model getModel(String id)
	{
		return models.get(id);
	}
}
