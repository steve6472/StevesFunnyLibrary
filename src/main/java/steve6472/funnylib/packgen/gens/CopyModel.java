package steve6472.funnylib.packgen.gens;

import org.apache.commons.io.FileUtils;
import steve6472.funnylib.packgen.Model;
import steve6472.funnylib.packgen.PackFiles;

import java.io.File;
import java.io.IOException;

/**
 * Created by steve6472
 * Date: 2/14/2024
 * Project: StevesFunnyLibrary <br>
 */
public class CopyModel implements ModelGen
{
	private final String modelPath;

	public CopyModel(String modelPath)
	{
		this.modelPath = modelPath;
	}

	@Override
	public void generate(PackFiles files, Model model) throws IOException
	{
		FileUtils.copyFile(new File(files.packModels, modelPath + ".json"), new File(files.genCustomModels, generatedFilePath(files, model) + ".json"));
	}

	@Override
	public String generatedFilePath(PackFiles files, Model model)
	{
		return model.materialOverride.name().toLowerCase() + "_" + model.customModelData + "_" + modelPath;
	}
}
