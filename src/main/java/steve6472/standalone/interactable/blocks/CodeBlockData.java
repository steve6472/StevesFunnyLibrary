package steve6472.standalone.interactable.blocks;

import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.standalone.interactable.ex.CodeExecutor;

/**
 * Created by steve6472
 * Date: 10/22/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeBlockData extends CustomBlockData
{
	@Save CodeExecutor executor;
}
