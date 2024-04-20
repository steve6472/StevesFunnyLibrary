package steve6472.standalone.buildbattle.phases;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.joml.Vector2i;
import org.joml.Vector3i;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;

import java.util.*;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ViewingPhase extends AbstractGamePhase
{
	public final Map<UUID, Plot> plots;

	public ViewingPhase(Game game)
	{
		super(game);
		this.plots = ((BuildBattleGame) game).plots;
	}

	@Override
	public void start()
	{
		registerEvent(PlayerJoinEvent.class, event ->
		{
			updateBorder(event.getPlayer());
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
		});

		for (Player player : game.getPlayers())
		{
			updateBorder(player);
			player.setGameMode(GameMode.SPECTATOR);
		}

		registerEvent(PlayerMoveEvent.class, event ->
		{
			Location to = event.getTo();
			if (to == null)
				return;

			Player player = event.getPlayer();

			Optional<Plot> currentPlot = plots.values().stream().filter(p -> p.isLocationInPlot(to)).findFirst();

			// Apply current plot settings
			currentPlot.ifPresentOrElse(plot -> plot.applyPlotSettings(player, false), () -> BuildPhase.resetPlotSettings(player, false));
		});

		registerEvent(PlayerInteractEvent.class, event -> event.setCancelled(true));

		// disallow players damaging entities
		registerEvent(EntityDamageByEntityEvent.class, event ->
		{
			if (event.getDamager() instanceof Player player)
				if (!player.isOp())
					event.setCancelled(true);
		});

		registerEvent(PlayerInteractEntityEvent.class, event ->
		{
			if (!event.getPlayer().isOp())
				event.setCancelled(true);
		});
	}

	private void updateBorder(Player player)
	{
		Vector2i pos = SpiralGenerator.getPos(plots.size());
		int max = Math.max(Math.abs(pos.x), Math.abs(pos.y));

		Marker center = game.getConfig().getValue(BuildBattleGame.CENTER);

		WorldBorder border = Bukkit.createWorldBorder();
		Vector3i plotSize = game.getConfig().getValue(BuildBattleGame.PLOT).getSize();
		border.setCenter(center.x() + plotSize.x / 2d, center.z() + plotSize.z / 2d);
		border.setSize((max + 1) * Math.max(plotSize.x, plotSize.z) * 2);

		player.setWorldBorder(border);
	}

	@Override
	public void end()
	{
		game.getPlayers().forEach(player -> BuildPhase.resetPlotSettings(player, true));
	}
}
