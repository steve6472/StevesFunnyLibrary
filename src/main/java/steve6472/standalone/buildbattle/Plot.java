package steve6472.standalone.buildbattle;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.joml.Vector3i;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.minigame.Game;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 1/25/2024
 * Project: StevesFunnyLibrary <br>
 */
public class Plot
{
	UUID owner;
	Game game;
	Vector3i plotCoords;

	/*
	 * Plot settings
	 */
	private WeatherType weather = WeatherType.CLEAR;
	private int time;

	public Plot(Game game, UUID owner, Vector3i plotCoords)
	{
		this.game = game;
		this.owner = owner;
		this.plotCoords = plotCoords;
	}

	public void place(World world)
	{
		GameStructure plotStructure = game.getConfig().getValue(BuildBattleGame.PLOT);
		plotStructure.place(world, plotCoords.x, plotCoords.y, plotCoords.z, 0, 0, 0, false);
	}

	public Vector3i getCenter()
	{
		Vector3i center = new Vector3i(plotCoords);
		center.add(game.getConfig().getValue(BuildBattleGame.PLOT_BUILD_OFFSET));
		Vector3i buildSize = new Vector3i(game.getConfig().getValue(BuildBattleGame.PLOT_BUILD_SIZE));
		center.add(new Vector3i(buildSize.x / 2, buildSize.y / 2, buildSize.z / 2));
		return center;
	}

	public void teleportToPlot(Player player)
	{
		Vector3i center = getCenter();
		Location location = new Location(((BuildBattleGame) game).world, center.x, center.y, center.z);
		Block highestBlockAt = ((BuildBattleGame) game).world.getHighestBlockAt(location);
		player.teleport(highestBlockAt.getLocation().add(0, 3, 0));
	}

	public void applyPlotSettings(Player player, boolean applyBorder)
	{
		player.setPlayerTime(time, false);
		player.setPlayerWeather(weather);

		if (!applyBorder)
			return;

		WorldBorder border = Bukkit.createWorldBorder();
		Vector3i center = getCenter();
		border.setCenter(center.x, center.z);
		border.setSize(new Vector3i(game.getConfig().getValue(BuildBattleGame.PLOT_BUILD_SIZE)).x);
		player.setWorldBorder(border);
	}

	public boolean locationInPlot(Location location)
	{
		return locationInPlot(location.toVector().toVector3i());
	}

	public boolean locationInPlot(Vector3i location)
	{
		Vector3i size = game.getConfig().getValue(BuildBattleGame.PLOT_BUILD_SIZE);
		Vector3i offset = game.getConfig().getValue(BuildBattleGame.PLOT_BUILD_OFFSET);
		return location.x() >= plotCoords.x + offset.x && location.x() < plotCoords.x + size.x + offset.x &&
			   location.y() >= plotCoords.y + offset.y && location.y() < plotCoords.y + size.y + offset.y &&
			   location.z() >= plotCoords.z + offset.z && location.z() < plotCoords.z + size.z + offset.z;
	}

	public void updatePlotSettings()
	{
		game.getPlayers().forEach(player ->
		{
			if (locationInPlot(player.getLocation()))
			{
				applyPlotSettings(player, false);
			}
		});
	}

	public void setWeather(WeatherType weather)
	{
		this.weather = weather;
		updatePlotSettings();
	}

	public void setTime(int time)
	{
		this.time = time;
		updatePlotSettings();
	}

	public boolean isPlayerOwner(Player player)
	{
		return player.getUniqueId().equals(owner);
	}
}
