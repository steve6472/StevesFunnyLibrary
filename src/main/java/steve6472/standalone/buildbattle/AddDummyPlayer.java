package steve6472.standalone.buildbattle;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.standalone.buildbattle.phases.BuildPhase;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 1/28/2024
 * Project: StevesFunnyLibrary <br>
 */
public class AddDummyPlayer extends BrigitCommand
{
	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(literal(getName()).executes(c ->
		{
			if (!(FunnyLib.currentGame instanceof BuildBattleGame game))
				return -1;

			if (!(game.getCurrentPhase() instanceof BuildPhase build))
				return -2;

			UUID uuid = UUID.randomUUID();
			Plot newPlot = build.createNewPlot(uuid);
			game.plots.put(uuid, newPlot);

			return 0;
		}));
	}

	@Override
	public String getName()
	{
		return "adddummyplayer";
	}

	@Override
	public int getPermissionLevel()
	{
		return 0;
	}
}
