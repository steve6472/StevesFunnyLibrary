package steve6472.funnylib.minigame;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.minigame.config.GameConfigMenu;
import steve6472.funnylib.minigame.config.GameConfiguration;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.FunnyLibStandalone;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 9/2/2023
 * Project: StevesFunnyLibrary <br>
 */
public class MinigameCommand extends BrigitCommand
{
	private static final DynamicCommandExceptionType ERROR_NO_GAME = new DynamicCommandExceptionType((var0) -> new LiteralMessage("No game in progress"));
	private static final DynamicCommandExceptionType ERROR_NO_NEXT_PHASE = new DynamicCommandExceptionType((var0) -> new LiteralMessage("No more phases to start exist for this game"));
	private static final DynamicCommandExceptionType ERROR_NO_PHASE = new DynamicCommandExceptionType((var0) -> new LiteralMessage("No phase found for this game"));

	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(literal(getName())
			.requires(c -> {
				try
				{
					return getPlayer(c).isOp();
				} catch (CommandSyntaxException e)
				{
					throw new RuntimeException(e);
				}
			})
			.then(
				literal("validate")
					.then(
						argument("minigame", string())
							.suggests((c, b) -> suggest(listMinigames(), b))
							.executes(c ->
							{
								String minigameId = getString(c, "minigame");

								Optional<GameConfiguration> minigame = FunnyLibStandalone.minigames.games
									.stream()
									.filter(gc -> gc.minigameId.equals(minigameId))
									.findFirst();

								// TODO: make this an exception so I don't have to repeat this shit
								if (minigame.isEmpty())
								{
									JSONMessage.create("Minigame with id ").color(ChatColor.RED)
										.then("'").then(minigameId).color(ChatColor.WHITE).then("' ").color(ChatColor.RED)
										.then("not found!").color(ChatColor.RED)
										.send(getPlayer(c));
									return 0;
								} else
								{
									GameConfiguration gameConfiguration = minigame.get();
									gameConfiguration.load();
									try
									{
										gameConfiguration.validate();
										JSONMessage.create("Validation passed").color(ChatColor.GREEN).send(getPlayer(c));
									} catch (Exception exception)
									{
										JSONMessage.create(exception.getMessage()).color(ChatColor.RED).send(getPlayer(c));
									}
								}

								return 1;
							})
					)
			)
			.then(
				literal("start")
					.then(
						argument("minigame", string())
							.suggests((c, b) -> suggest(listMinigames(), b))
							.executes(c ->
							{
								String minigameId = getString(c, "minigame");

								Optional<GameConfiguration> minigame = FunnyLibStandalone.minigames.games
									.stream()
									.filter(gc -> gc.minigameId.equals(minigameId))
									.findFirst();

								// TODO: make this an exception so I don't have to repeat this shit
								if (minigame.isEmpty())
								{
									JSONMessage.create("Minigame with id ").color(ChatColor.RED)
										.then("'").then(minigameId).color(ChatColor.WHITE).then("' ").color(ChatColor.RED)
										.then("not found!").color(ChatColor.RED)
										.send(getPlayer(c));
									return 0;
								} else
								{
									GameConfiguration gameConfiguration = minigame.get();
									gameConfiguration.validate();
								}

								return 1;
							})
					)
			).then(
				literal("end")
					.executes(c ->
					{
						try
						{
							Game game = getCurrentGame();
							game.dispose();
							FunnyLib.currentGame = null;

							getPlayer(c).sendMessage(ChatColor.GREEN + "Game ended!");
						} catch (Exception ex)
						{
							ex.printStackTrace();
						}
						return 1;
					})
			).then(
				literal("phase")
					.then(
						literal("next")
							.executes(c ->
							{
								Game game = getCurrentGame();
								AbstractGamePhase currentPhase = getCurrentPhase();

								if (!game.phases.hasMorePhases())
									throw ERROR_NO_NEXT_PHASE.create(c);

								currentPhase.endPhase();
								getPlayer(c).sendMessage(ChatColor.GREEN + "Force ended current stage '" + currentPhase.getClass().getSimpleName() + "'");

								return 1;
							})
					).then(
						literal("debug")
							.then(
								literal("tasks")
									.executes(c ->
									{
										AbstractGamePhase currentPhase = getCurrentPhase();
										currentPhase.debugTasks(getEntity(c.getSource()));
										return 1;
									})
							).then(
								literal("events")
									.executes(c ->
									{
										AbstractGamePhase currentPhase = getCurrentPhase();
										currentPhase.debugEvents(getEntity(c.getSource()));
										return 1;
									})
							)
					)
			).then(
				literal("config")
					.then(
						argument("minigame", string())
							.suggests((c, b) -> suggest(listMinigames(), b))
							.executes(c ->
							{
								String minigameId = getString(c, "minigame");

								Optional<GameConfiguration> minigame = FunnyLibStandalone.minigames.games
									.stream()
									.filter(gc -> gc.minigameId.equals(minigameId))
									.findFirst();

								if (minigame.isEmpty())
								{
									JSONMessage.create("Minigame with id ").color(ChatColor.RED)
										.then("'").then(minigameId).color(ChatColor.WHITE).then("' ").color(ChatColor.RED)
										.then("not found!").color(ChatColor.RED)
										.send(getPlayer(c));
									return 0;
								} else
								{
									final Player player = getPlayer(c);
									Bukkit.getScheduler().runTaskLater(FunnyLib.getPlugin(), () ->
									{
										GameConfiguration gameConfig = minigame.get();

										Game currentGame = FunnyLib.currentGame;
										if (currentGame != null && currentGame.getConfig() == gameConfig)
										{
											JSONMessage.create("You can not edit a running game!").color(ChatColor.RED).send(player);
											return;
										}

										gameConfig.load();

										JSONMessage.create("Opening settings for ").color(ChatColor.GREEN).then(gameConfig.name).send(player);
										GameConfigMenu menu = new GameConfigMenu(FunnyLibStandalone.minigames.configTypeRegistry, gameConfig);
										menu.showToPlayer(player);
									}, 0);
								}

								return 1;
							})
					)
			)
		);
	}

	private Collection<String> listMinigames()
	{
		return FunnyLibStandalone.minigames.games.stream().map(gc -> gc.minigameId).toList();
	}

	private Game getCurrentGame() throws CommandSyntaxException
	{
		if (FunnyLib.currentGame == null)
			throw ERROR_NO_GAME.create(null);
		return FunnyLib.currentGame;
	}

	private AbstractGamePhase getCurrentPhase() throws CommandSyntaxException
	{
		Game game = getCurrentGame();
		AbstractGamePhase phase = game.phases.getCurrentPhase();
		if (phase == null)
			throw ERROR_NO_PHASE.create(null);
		return phase;
	}

	@Override
	public String getName()
	{
		return "minigame";
	}

	@Override
	public int getPermissionLevel()
	{
		return 4;
	}
}
