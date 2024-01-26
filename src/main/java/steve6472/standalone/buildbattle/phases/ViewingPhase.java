package steve6472.standalone.buildbattle.phases;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
		registerEvent(PlayerMoveEvent.class, event ->
		{
			Location to = event.getTo();
			if (to == null)
				return;

			Player player = event.getPlayer();

			Optional<Plot> currentPlot = plots.values().stream().filter(p -> p.locationInPlot(to)).findFirst();

			// Apply current plot settings
			currentPlot.ifPresentOrElse(plot -> plot.applyPlotSettings(player, false), () -> BuildPhase.resetPlotSettings(player));
		});
	}

	@Override
	public void end()
	{
		game.getPlayers().forEach(BuildPhase::resetPlotSettings);
	}
}
