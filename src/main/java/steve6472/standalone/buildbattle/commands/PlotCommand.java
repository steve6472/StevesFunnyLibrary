package steve6472.standalone.buildbattle.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.builtin.worldtools.RectangleFillerItem;
import steve6472.funnylib.item.builtin.worldtools.SphereFillerItem;
import steve6472.funnylib.minigame.Minigames;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;
import steve6472.standalone.buildbattle.phases.BuildPhase;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
				.then(literal("help")
					.executes(c -> {
						BuildPhase.commands.send(getPlayer(c));
						return 0;
					})
				)
				.then(literal("home")
					.executes(c -> {
						checkGame();

						BuildPhase phase = (BuildPhase) FunnyLib.currentGame.getCurrentPhase();

						Player player = getPlayer(c);
						Plot plot = phase.plots.get(player.getUniqueId());
						if (plot == null)
						{
							player.sendMessage(ChatColor.RED + "Your plot was not found!");
							return -1;
						}

						Block highestBlockAt = player.getWorld().getHighestBlockAt(new Location(player.getWorld(), plot.getCenter().x, plot.getCenter().y, plot.getCenter().z));
						Location tpLoc = highestBlockAt.getLocation().clone().add(0.5, 1.5, 0.5);

						Vector3i size = FunnyLib.currentGame.getConfig().getValue(BuildBattleGame.PLOT_BUILD_SIZE);
						Vector3i offset = FunnyLib.currentGame.getConfig().getValue(BuildBattleGame.PLOT_BUILD_OFFSET);

						if (tpLoc.getY() + 0.2 >= plot.getPlotCoords().y + offset.y + size.y)
							tpLoc.setY(plot.getPlotCoords().y + offset.y + size.y - 0.2);

						player.teleport(tpLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

						return 0;
					})
				)
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
							checkGame();

							ItemStack itemStack = Minigames.FILL_SPHERE_LIMITED.newItemStack();
							getPlayer(c).getInventory().addItem(itemStack);
							JSONMessage.create("Information about item: ").then("[Hover over me]", ChatColor.GOLD).tooltip(itemStack).send(getPlayer(c));

							SphereFillerItem.HELP.send(getPlayer(c));

							return 0;
						})
					).then(literal("rectangle")
						.executes(c -> {
							checkGame();

							ItemStack itemStack = Minigames.FILL_RECTANGLE_LIMITED.newItemStack();
							getPlayer(c).getInventory().addItem(itemStack);
							JSONMessage.create("Information about item: ").then("[Hover over me]", ChatColor.GOLD).tooltip(itemStack).send(getPlayer(c));
							RectangleFillerItem.HELP.send(getPlayer(c));

							return 0;
						})
					)
					.then(literal("barrier")
						.executes(c -> {
							checkGame();

							getPlayer(c).getInventory().addItem(new ItemStack(Material.BARRIER));
							JSONMessage.create("You have been given a Barrier block", ChatColor.YELLOW).send(getPlayer(c));

							return 0;
						})
					)
					.then(literal("light")
						.executes(c -> {
							checkGame();

							getPlayer(c).getInventory().addItem(new ItemStack(Material.LIGHT));
							JSONMessage.create("You have been given a Light block", ChatColor.YELLOW).send(getPlayer(c));

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
