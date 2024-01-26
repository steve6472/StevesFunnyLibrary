package steve6472.standalone.buildbattle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.minigame.Minigames;
import steve6472.standalone.buildbattle.phases.BuildPhase;

import java.util.List;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PlotCommand extends BrigitCommand
{
	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(
			literal(getName())
				.then(literal("weather")
					.then(argument("weather", string())
						.suggests((c, b) -> suggest(List.of("clear", "downfall"), b))
						.executes(c ->
						{
							checkGame();

							BuildPhase phase = (BuildPhase) FunnyLib.currentGame.getCurrentPhase();
							Player player = getPlayer(c);
							phase.getPlayersCurrentPlot(player).ifPresent(plot ->
							{
								if (!plot.isPlayerOwner(player))
									return;

								plot.setWeather(getString(c, "weather").equalsIgnoreCase("downfall") ? WeatherType.DOWNFALL : WeatherType.CLEAR);
							});
							return 0;
						})
					)
				)
				.then(literal("time")
					.then(argument("time", integer(0, 24000))
						.suggests((c, b) -> suggest(List.of("0", "1000", "6000", "13000", "18000"), b))
						.executes(c ->
						{
							checkGame();

							BuildPhase phase = (BuildPhase) FunnyLib.currentGame.getCurrentPhase();
							Player player = getPlayer(c);
							phase.getPlayersCurrentPlot(player).ifPresent(plot ->
							{
								if (!plot.isPlayerOwner(player))
									return;

								plot.setTime(getInteger(c, "time"));
							});
							return 0;
						})
					)
				)
		);
	}

	private void checkGame() throws CommandSyntaxException
	{
		if (!(FunnyLib.currentGame instanceof BuildBattleGame bbg))
		{
			throw Minigames.ERROR_MINIGAME_SPECIFIC.create(null);
		}

		if (!(bbg.getCurrentPhase() instanceof BuildPhase))
		{
			throw Minigames.ERROR_MINIGAME_SPECIFIC.create(null);
		}
	}

	@Override
	public String getName()
	{
		return "plot";
	}

	@Override
	public int getPermissionLevel()
	{
		return 0;
	}
}
