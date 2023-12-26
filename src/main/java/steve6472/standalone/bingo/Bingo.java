package steve6472.standalone.bingo;

import eu.endercentral.crazy_advancements.CrazyAdvancementsAPI;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.*;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;

import java.util.*;

/**********************
 * Created by steve6472
 * On date: 4/9/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class Bingo extends AbstractGamePhase
{
	public final String bingoKey = "bingo";
	private final long seed;
	private boolean endGame = false;

	public final BingoTaskManager tasks;

	final AdvancementManager advancementManager;
	final Advancement root;

	private final UUID world;

	private BossBar bossBar;
	private int countedDown;

	public Bingo(Game game, UUID world, long seed)
	{
		super(game);
		this.world = world;

		reset();

		advancementManager = new AdvancementManager(new NameKey(game.getPlugin().getName().toLowerCase(Locale.ROOT), bingoKey));
		advancementManager.makeAccessible();

		AdvancementDisplay display = new AdvancementDisplay(Material.PAPER, "BINGO!", "Bingo Card", AdvancementDisplay.AdvancementFrame.CHALLENGE, AdvancementVisibility.ALWAYS);
		display.setCoordinates(0, 0);
		display.setBackgroundTexture("textures/block/light_gray_concrete.png");
		root = new Advancement(new NameKey(game.getPlugin().getName().toLowerCase(Locale.ROOT), bingoKey), display, true, AdvancementFlag.SHOW_TOAST);
		advancementManager.addAdvancement(root);

		tasks = new BingoTaskManager(this);
		tasks.setupTasks(seed);
		this.seed = seed;
	}

	public Plugin getPlugin()
	{
		return game.getPlugin();
	}

	public Game getGame()
	{
		return game;
	}

	private void reset()
	{
		AdvancementManager accessibleManager = AdvancementManager.getAccessibleManager(new NameKey(game.getPlugin().getName().toLowerCase(Locale.ROOT), bingoKey));
		while (accessibleManager != null)
		{
			accessibleManager.resetAccessible();
			accessibleManager = AdvancementManager.getAccessibleManager(new NameKey(game.getPlugin().getName().toLowerCase(Locale.ROOT), bingoKey));
		}
	}

	@Override
	public void start()
	{
		System.out.println("Bingo started with seed " + seed);
		game.broadcast(JSONMessage.create("Bingo started with seed " + seed));

		tasks.enable();

		game.getPlayers().forEach(this::setupPlayer);
		registerEvent(PlayerJoinEvent.class, event -> setupPlayer(event.getPlayer()));

		registerEvent(PlayerQuitEvent.class, event -> advancementManager.removePlayer(event.getPlayer()));

		// Pvp
		scheduleSyncTask(task ->
		{
			Objects.requireNonNull(Bukkit.getWorld(world)).setPVP(true);
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(ChatColor.RED + "        PvP has been enabled!");
			Bukkit.broadcastMessage("");
		}, 20 * 90).named("Pvp enable");
	}

	private void setupPlayer(Player player)
	{
		scheduleSyncTask(task ->
		{
			root.saveVisibilityStatus(player, true);
			advancementManager.addPlayer(player);
			advancementManager.updateVisibility(player);

			tasks.populateUnfinishedTasks(player);

			CrazyAdvancementsAPI.setActiveTab(player, root.getName());

			ToastNotification notification = new ToastNotification(Material.PAPER, "Open Advancements to view the Bingo card", AdvancementDisplay.AdvancementFrame.CHALLENGE);
			notification.send(player);
		}, 20).named("Advancement visibility");
	}

	/*
	 * Endgame
	 */

	public void finishBingo(Player winner)
	{
		List<Player> players = new ArrayList<>(game.getPlayers());

		players.sort((a, b) -> Integer.compare(tasks.countCompletedTasks(b), tasks.countCompletedTasks(a)));

		game.broadcast(JSONMessage.create("Game ended!").color(ChatColor.YELLOW).newline().then("Leaderboard:").newline());

		for (Player player : players)
		{
			JSONMessage hover = JSONMessage.create();

			int completed = 0;

			for (BingoTask task : tasks.getTasks())
			{
				boolean compl = tasks.hasCompleted(player, task.getKey());
				if (compl)
					completed++;
				hover.then(task.name).color(compl ? ChatColor.GREEN : ChatColor.GRAY).newline();
			}

			hover.newline().then("Completed: ").then("" + completed).then(" / ").then("" + tasks.getTaskCount());

			JSONMessage message = JSONMessage.create(player.getName()).color(player == winner ? ChatColor.GOLD : ChatColor.WHITE).tooltip(hover);
			game.broadcast(message);

			player.setGameMode(GameMode.SPECTATOR);
			tasks.clear(player);
		}

		endPhase();
	}

	public void startEndGame(Player winner)
	{
		if (endGame)
			return;

		endGame = true;

		scheduleSyncTask(task -> finishBingo(winner), 20 * 60).named("Endgame"); // Minute

		startBossBar();
	}

	private void startBossBar()
	{
		bossBar = game.createBossBar("bingo", "Bingo ends in " + 0 + " seconds", BarColor.PINK, BarStyle.SEGMENTED_6);

		countedDown = 60;

		scheduleRepeatingTask(task ->
		{
			for (Player player : Bukkit.getOnlinePlayers())
			{
				bossBar.addPlayer(player);
			}

			bossBar.setProgress(1.0 - countedDown / 60d);
			bossBar.setTitle("Bingo ends in " + countedDown + " seconds");

			countedDown--;
			if (countedDown <= -1)
			{
				task.cancel();
			}
		}, 0, 20).named("Ending Bossbar");
	}

	@Override
	public void end()
	{
		advancementManager.resetAccessible();
		getGame().getPlayers().forEach(advancementManager::removePlayer);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		tasks.dispose();
		game.deleteBossBar("bingo");
	}
}
