package steve6472.funnylib.packgen.gens;

import steve6472.funnylib.packgen.Model;
import steve6472.funnylib.packgen.PackFiles;

import java.io.IOException;

/**
 * Created by steve6472
 * Date: 2/14/2024
 * Project: StevesFunnyLibrary <br>
 */
public interface ModelGen
{
	void generate(PackFiles files, Model model) throws IOException;

	String generatedFilePath(PackFiles files, Model model);
}
