package steve6472.standalone.bingo;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 9/2/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoCommand extends BrigitCommand
{
	private static final DynamicCommandExceptionType ERROR_UNKNOWN_TASK = new DynamicCommandExceptionType((var0) -> new LiteralMessage("Task with id \"%s\" does not exist".formatted(var0)));

	private final Plugin plugin;

	public BingoCommand(Plugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(
			literal(getName())
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
					literal("clearAll")
						.executes(c ->
						{
							getBingo(c.getSource()).tasks.clearAll();
							return 1;
						})
				).then(
					literal("forceEnd")
						.executes(c ->
						{
							getBingo(c.getSource()).finishBingo(null);
							return 1;
						})
						.then(
							argument("winner", singlePlayer())
								.executes(c ->
								{
									getBingo(c.getSource()).finishBingo(getSinglePlayer(c, "winner"));
									return 1;
								})
						)
				).then(
					literal("start")
						.then(
							argument("seed", longArg())
								.executes(c ->
								{
									try
									{
										if (FunnyLib.currentGame != null)
											FunnyLib.currentGame.dispose();

										FunnyLib.currentGame = new BingoGame(plugin, getWorld(c), getLong(c, "seed"));
									} catch (Exception ex)
									{
										ex.printStackTrace();
										throw ex;
									}
									return 1;
								})
						)
				).then(
					literal("clear")
						.then(
							argument("players", multiplePlayers())
								.executes(c ->
								{
									Collection<Player> players = getPlayers(c, "players");
									Bingo bingo = getBingo(c.getSource());
									players.forEach(bingo.tasks::clear);
									return 1;
								})
						)
				).then(
					literal("grant")
						.then(
							argument("players", multiplePlayers())
								.then(
									argument("task", string())
										.suggests((c, b) -> suggest(listTasks(c.getSource()), b))
										.executes(c ->
										{
											Collection<Player> players = getPlayers(c, "players");
											String taskId = getString(c, "task");
											Bingo bingo = getBingo(c.getSource());
											BingoTask task = bingo.tasks.getTask(taskId);

											if (task == null)
												throw ERROR_UNKNOWN_TASK.create(taskId);

											players.forEach(player -> bingo.tasks.grant(player, task.key));
											return players.size();
										})
								)
								.then(
									literal("*")
										.executes(c ->
										{
											Collection<Player> players = getPlayers(c, "players");
											Bingo bingo = getBingo(c.getSource());

											players.forEach(player -> bingo.tasks.getTasks().forEach(task -> bingo.tasks.grant(player, task.key)));
											return players.size();
										})
								)
						)
				).then(
					literal("grantRandom")
						.then(
							argument("players", multiplePlayers())
								.then(
									argument("taskCount", integer(1, 20))
										.executes(c ->
										{
											Collection<Player> players = getPlayers(c, "players");
											int taskCount = getInteger(c, "taskCount");
											Bingo bingo = getBingo(c.getSource());

											players.forEach(player ->
											{
												List<BingoTask> tasks = new ArrayList<>(bingo.tasks.getTasks());
												Collections.shuffle(tasks);
												for (int i = 0; i < taskCount; i++)
												{
													bingo.tasks.grant(player, tasks.get(i).getKey());
												}
											});
											return players.size();
										})
								)
						)
				)
				.then(
					literal("revoke")
						.then(
							argument("players", multiplePlayers())
								.then(
									argument("task", string())
										.suggests((c, b) -> suggest(listTasks(c.getSource()), b))
										.executes(c ->
										{
											Collection<Player> players = getPlayers(c, "players");
											String taskId = getString(c, "task");
											Bingo bingo = getBingo(c.getSource());
											BingoTask task = bingo.tasks.getTask(taskId);

											if (task == null)
												throw ERROR_UNKNOWN_TASK.create(taskId);

											players.forEach(player -> bingo.tasks.revoke(player, task.key));
											return players.size();
										})
								)
								.then(
									literal("*")
										.executes(c ->
										{
											Collection<Player> players = getPlayers(c, "players");
											Bingo bingo = getBingo(c.getSource());

											players.forEach(bingo.tasks::clear);
											return players.size();
										})
								)
						)
				).then(
					literal("count")
						.then(
							argument("player", singlePlayer())
								.executes(c ->
								{
									Bingo bingo = getBingo(c.getSource());
									Player player = getSinglePlayer(c, "player");

									getPlayer(c).sendMessage(ChatColor.GREEN + "" + player.getName() + " has completed " + bingo.tasks.countCompletedTasks(player) + " tasks");

									return bingo.tasks.countCompletedTasks(player);
								})
						)
				)
		);
	}

	private Bingo getBingo(CommandSourceStack stack) throws CommandSyntaxException
	{
		if (FunnyLib.currentGame instanceof BingoGame bingoGame)
		{
			return bingoGame.bingo;
		}
		throw new DynamicCommandExceptionType(c -> new LiteralMessage("Bingo has not started!")).create(stack);
	}

	private Collection<String> listTasks(CommandSourceStack stack) throws CommandSyntaxException
	{
		return getBingo(stack).tasks.getTasks().stream().map(BingoTask::getKey).collect(Collectors.toList());
	}

	@Override
	public String getName()
	{
		return "bingo";
	}

	@Override
	public int getPermissionLevel()
	{
		return 4;
	}
}
