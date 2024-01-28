package steve6472.standalone.buildbattle.phases;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import steve6472.brigit.BrigitCommand;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class SetBrush extends BrigitCommand
{
	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{

	}

	@Override
	public String getName()
	{
		return "setbrush";
	}

	@Override
	public int getPermissionLevel()
	{
		return 0;
	}
}
