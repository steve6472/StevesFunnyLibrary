package steve6472.standalone.buildbattle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.Registries;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.minigame.Minigames;
import steve6472.standalone.buildbattle.phases.BuildPhase;

import java.util.Arrays;
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
				.then(literal("biome")
					.then(argument("biome", string())
						.suggests((a, b) -> suggest(biome(), b))
						.executes(c ->
						{
							checkGame();

							BuildPhase phase = (BuildPhase) FunnyLib.currentGame.getCurrentPhase();
							Player player = getPlayer(c);
							phase.getPlayersCurrentPlot(player).ifPresent(plot ->
							{
								if (!plot.isPlayerOwner(player))
									return;

								try
								{
									Biome b = Biome.valueOf(getString(c, "biome"));
									if (b == Biome.CUSTOM)
									{
										player.sendMessage(ChatColor.RED + "Invalid biome");
										return;
									}
									plot.setBiome(b);
								} catch (Exception ex)
								{
									player.sendMessage(ChatColor.RED + "Biome not found!");
								}

							});
							return 0;
						})
					)
				)
				.then(literal("tool")
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
				).then(literal("theme")
					.then(literal("set")
						.then(argument("theme", StringArgumentType.greedyString()).suggests((a, b) -> suggest(suggestThemes(a), b))
							.executes(c -> {

								checkGame();

								if (!getPlayer(c).isOp())
								{
									getPlayer(c).sendMessage(ChatColor.RED + "You do not have permission to run this command.");
									return 0;
								}

								BuildPhase phase = (BuildPhase) FunnyLib.currentGame.getCurrentPhase();
								Player player = getPlayer(c);
								phase.getPlayersCurrentPlot(player).ifPresent(plot ->
								{
									String theme = getString(c, "theme");
									if (!FunnyLib.currentGame.getConfig().getValue(BuildBattleGame.THEMES).contains(theme))
									{
										player.sendMessage(ChatColor.RED + "You set a theme that is not in the current theme list! (" + theme + ")");
									}
									player.sendMessage(ChatColor.GREEN + "You set new theme for this plot: " + theme);

									plot.setPlotTheme(theme);
								});

								return 0;
							}
						)))
					.then(literal("get")
						.executes(c -> {

							checkGame();
							BuildPhase phase = (BuildPhase) FunnyLib.currentGame.getCurrentPhase();
							Player player = getPlayer(c);

							phase.getPlayersCurrentPlot(player).ifPresent(plot ->
							{
								player.sendMessage(ChatColor.YELLOW + "This plots theme is: " + ChatColor.WHITE + plot.getPlotTheme());
							});
							return 0;
					}))
				)
		);
	}

	private List<String> suggestThemes(CommandContext<CommandSourceStack> a) throws CommandSyntaxException
	{
		Player player = getPlayer(a);
		if (!player.isOp())
			return List.of("");

		return FunnyLib.currentGame.getConfig().getValue(BuildBattleGame.THEMES);
	}

	protected List<String> biome() {
		return Arrays.stream(Biome.values()).map(Enum::name).toList();
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
