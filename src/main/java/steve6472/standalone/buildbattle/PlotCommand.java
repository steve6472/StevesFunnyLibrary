package steve6472.standalone.buildbattle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.minigame.Minigames;
import steve6472.standalone.buildbattle.phases.BuildPhase;

import java.util.List;
import java.util.Objects;

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
				).then(literal("tool")
					.then(literal("sphere")
						.executes(c -> {
							getPlayer(c).getInventory().addItem(Minigames.FILL_SPHERE_LIMITED.newItemStack());
							return 0;
						})
					).then(literal("rectangle")
						.executes(c -> {
							getPlayer(c).getInventory().addItem(Minigames.FILL_RECTANGLE_LIMITED.newItemStack());
							return 0;
						})
					)
				)
		);
	}

	protected ResourceArgument<Biome> biome(CommandBuildContext commandBuildContext) {
		return ResourceArgument.resource(commandBuildContext, Registries.BIOME);
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
