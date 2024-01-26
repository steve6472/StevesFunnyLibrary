package steve6472.standalone.buildbattle.phases;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.RandomUtil;
import steve6472.standalone.bingo.BingoGame;
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

	public BuildPhase(Game game)
	{
		super(game);
		this.themes = ((BuildBattleGame) game).themes;
		this.plots = ((BuildBattleGame) game).plots;
	}

	private String pickRandomTheme()
	{
		if (themes.isEmpty())
			themes.addAll(BuildBattleGame.POSSIBLE_THEMES);
		return themes.remove(RandomUtil.randomInt(0, themes.size() - 1));
	}

	private void addPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		Plot plot = plots.computeIfAbsent(uuid, this::createNewPlot);
		plot.teleportToPlot(player);
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
		Plot plot = new Plot(game, uuid, new Vector3i(newPlotPos.x * plotSize.x, 0, newPlotPos.y * plotSize.z).add(game
			.getConfig()
			.getValue(BuildBattleGame.CENTER)
			.toVec3i()));
		plot.place(getWorld());
		return plot;
	}

	@Override
	public void start()
	{
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
		{
			game.addPlayer(onlinePlayer);
			addPlayer(onlinePlayer);
		}

		registerEvent(PlayerJoinEvent.class, event ->
		{
			addPlayer(event.getPlayer());
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
			currentPlot.ifPresentOrElse(plot -> plot.applyPlotSettings(player, true), () -> resetPlotSettings(player));
		});

		registerEvent(PlayerTeleportEvent.class, event ->
		{
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) return;
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) return;
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) return;

			event.setCancelled(true);
		});
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

	public static void resetPlotSettings(Player player)
	{
		player.resetPlayerWeather();
		player.resetPlayerTime();
		player.setWorldBorder(player.getWorld().getWorldBorder());
	}

	@Override
	public void end()
	{
		game.getPlayers().forEach(BuildPhase::resetPlotSettings);
	}
}
