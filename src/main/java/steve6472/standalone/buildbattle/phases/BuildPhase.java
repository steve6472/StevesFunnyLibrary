package steve6472.standalone.buildbattle.phases;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.RandomUtil;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;

import java.util.*;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BuildPhase extends AbstractGamePhase
{
	public final List<String> themes;
	public final Map<UUID, Plot> plots;
	public int timeElapsed;

	public BuildPhase(Game game)
	{
		super(game);
		this.themes = game.getConfig().getValue(BuildBattleGame.THEMES);
		this.plots = ((BuildBattleGame) game).plots;
	}

	private String pickRandomTheme()
	{
		if (themes.isEmpty())
			themes.addAll(game.getConfig().getValue(BuildBattleGame.THEMES));
		return themes.remove(RandomUtil.randomInt(0, themes.size() - 1));
	}

	private void addPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		Plot plot = plots.computeIfAbsent(uuid, this::createNewPlot);
		plot.teleportToPlot(player);
		player.setGameMode(GameMode.CREATIVE);
	}

	private World getWorld()
	{
		return ((BuildBattleGame) game).world;
	}

	public Plot createNewPlot(UUID uuid)
	{
		int currentPlotCount = plots.size();
		Vector2i newPlotPos = SpiralGenerator.getPos(currentPlotCount);
		Vector3i plotSize = game.getConfig().getValue(BuildBattleGame.PLOT).getSize();
		Vector3i plotOffset = game.getConfig().getValue(BuildBattleGame.PLOT_PLACE_OFFSET);
		Plot plot = new Plot(
			game,
			uuid,
			new Vector3i(
				newPlotPos.x * (plotSize.x + 1 + plotOffset.x),
				game.getConfig().getValue(BuildBattleGame.CENTER).y(),
				newPlotPos.y * (plotSize.z + 1 + plotOffset.z)
			).add(game.getConfig().getValue(BuildBattleGame.CENTER).toVec3i()),
			pickRandomTheme());
		plot.place(getWorld());
		return plot;
	}

	@Override
	public void start()
	{
		JSONMessage joinMessage = JSONMessage
			.create("Welcome to testing version of the Build Battle Minigame.").newline()
			.then("Time limit is 24 hours").newline()
			.then("You have been given a theme, but you do not need to follow it").newline()
			.then("You can use these commands:").newline()
			.then("(Hover over gold text to see what the command does)").newline()
			.then("/giveskull", ChatColor.GREEN).newline()
			.then("  <player>", ChatColor.GOLD).tooltip("Gives you a skull of said player").newline().newline()
			.then("/plot", ChatColor.GREEN).newline()
			.then("  tool", ChatColor.GRAY).newline()
			.then("    sphere", ChatColor.GOLD).tooltip("Gives you the Sphere Filler Tool").newline()
			.then("    rectangle", ChatColor.GOLD).tooltip("Gives you the Rectangle Filler Tool").newline()
			.then("  weather", ChatColor.GRAY).newline()
			.then("    [clear, downfall]", ChatColor.GOLD).tooltip("Sets the plot weather").newline()
			.then("  time", ChatColor.GRAY).newline()
			.then("    <time>", ChatColor.GOLD).tooltip("Sets plot time");

		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
		{
			game.addPlayer(onlinePlayer);
			addPlayer(onlinePlayer);
			joinMessage.send(onlinePlayer);
		}

		registerEvent(PlayerJoinEvent.class, event ->
		{
			addPlayer(event.getPlayer());
			joinMessage.send(event.getPlayer());
		});

		registerEvent(PlayerMoveEvent.class, event ->
		{
			Location to = event.getTo();
			Location from = event.getFrom();
			if (to == null)
				return;

			Player player = event.getPlayer();

			Optional<Plot> lastPlot = plots.values().stream().filter(p -> p.locationInPlot(from)).findFirst();
			Optional<Plot> currentPlot = plots.values().stream().filter(p -> p.locationInPlot(to)).findFirst();

			// Prevent player from leaving plot
			lastPlot.ifPresent(plot ->
			{
				if (currentPlot.orElse(null) == plot)
					return;

				if (player.getGameMode() == GameMode.SPECTATOR)
					return;

				event.setCancelled(true);

				Vector3d vector = from.toVector().toVector3d();
				Vector3i centerI = plot.getCenter();
				Vector3d center = new Vector3d(centerI.x, centerI.y, centerI.z);
				vector.sub(center).normalize().mul(-0.2);

				player.teleport(from.clone().add(vector.x, vector.y, vector.z));

				vector.normalize().mul(0.5d);
				player.setVelocity(new Vector(vector.x, vector.y, vector.z));
			});

			// Apply current plot settings
			currentPlot.ifPresentOrElse(plot -> plot.applyPlotSettings(player, true), () -> resetPlotSettings(player, true));
		});

		registerEvent(PlayerTeleportEvent.class, event ->
		{
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) return;
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) return;
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) return;

			event.setCancelled(true);
		});

		registerEvent(PlayerInteractEvent.class, event ->
		{
			Block clickedBlock = event.getClickedBlock();
			if (clickedBlock == null) return;

			Location loc = clickedBlock.getLocation();

			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (!clickedBlock.getType().isInteractable() || (event.getPlayer().isSneaking() && !Objects
				.requireNonNull(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND))
				.getType().isAir())))
			{
				loc.add(event.getBlockFace().getDirection());
			}

			Plot plot = ((BuildBattleGame) game).getPlayersPlot(event.getPlayer());
			if (!plot.locationInPlot(loc))
			{
				event.setCancelled(true);
			}
		});

		scheduleRepeatingTask(task -> {
			plots.forEach((k, plot) -> {
				timeElapsed++;
				plot.getPlotBossBar().setProgress(Math.min(1.0, Math.max(0.0, 1.0 - timeElapsed / (double) game.getConfig().getValue(BuildBattleGame.BUILD_TIME))));
				plot.setBarTime(game.getConfig().getValue(BuildBattleGame.BUILD_TIME) - timeElapsed);
				if (timeElapsed >= game.getConfig().getValue(BuildBattleGame.BUILD_TIME))
				{
					endPhase();
				}
			});
		}, 10 * 20, 20);
	}

	public Optional<Plot> getPlayersCurrentPlot(Player player)
	{
		Location location = player.getLocation();

		for (Map.Entry<UUID, Plot> entry : plots.entrySet())
		{
			Plot v = entry.getValue();
			if (v.locationInPlot(location))
			{
				return Optional.of(v);
			}
		}

		return Optional.empty();
	}

	public static void resetPlotSettings(Player player, boolean clearBorder)
	{
		player.resetPlayerWeather();
		player.resetPlayerTime();
		if (clearBorder)
			player.setWorldBorder(player.getWorld().getWorldBorder());
		clearBossBars(player);
	}

	public static void clearBossBars(Player player)
	{
		Set<BossBar> bars = new HashSet<>();
		Bukkit.getBossBars().forEachRemaining(bar -> {
			if (bar.getKey().getKey().startsWith("plot_"))
			{
				if (bar.getPlayers().contains(player))
				{
					bars.add(bar);
				}
			}
		});
		bars.forEach(bar -> bar.removePlayer(player));
	}

	@Override
	public void end()
	{
		plots.forEach((k, plot) -> plot.setBarTime(-1));
		game.getPlayers().forEach(player -> resetPlotSettings(player, true));
	}
}
