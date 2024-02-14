package steve6472.funnylib.packgen.gens;

import org.apache.commons.io.FileUtils;
import steve6472.funnylib.packgen.Model;
import steve6472.funnylib.packgen.PackFiles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by steve6472
 * Date: 2/14/2024
 * Project: StevesFunnyLibrary <br>
 */
public class GenericItemModel implements ModelGen
{
	private final String texturePath;

	public GenericItemModel(String texturePath)
	{
		this.texturePath = texturePath;
	}

	@Override
	public void generate(PackFiles files, Model model) throws IOException
	{
		File file = new File(files.genCustomModels, generatedFilePath(files, model) + ".json");

		String json = """
			{
			    "parent": "item/generated",
			    "textures": {"layer0": "%s"}
			}
			""";

		FileUtils.writeStringToFile(file, json.formatted("custom/" + texturePath), Charset.defaultCharset());
	}

	@Override
	public String generatedFilePath(PackFiles files, Model model)
	{
		return model.materialOverride.name().toLowerCase() + "_" + model.customModelData;
	}
}
