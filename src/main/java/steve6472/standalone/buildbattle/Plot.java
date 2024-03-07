package steve6472.standalone.buildbattle;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.NMS;
import steve6472.funnylib.workdistro.impl.ChangeBiomeWorkload;
import steve6472.funnylib.workdistro.impl.ModifyWorldWorkload;
import steve6472.funnylib.workdistro.impl.PlaceBlockWorkload;
import steve6472.standalone.buildbattle.phases.BuildPhase;

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
	private Biome biome;

	private String plotTheme;
	private BossBar plotBossBar;

	public Plot(Game game, UUID owner, Vector3i plotCoords, String plotTheme)
	{
		this.game = game;
		this.owner = owner;
		this.plotCoords = plotCoords;
		this.plotTheme = plotTheme;
		this.plotBossBar = game.createBossBar("plot_" + plotCoords.x + "_" + plotCoords.z, "Theme: " + plotTheme, BarColor.WHITE, BarStyle.SEGMENTED_12);
		plotBossBar.setProgress(1.0);
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
		BuildPhase.clearBossBars(player);
		plotBossBar.addPlayer(player);

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

	public String getPlotTheme()
	{
		return plotTheme;
	}

	public void setPlotTheme(String plotTheme)
	{
		this.plotTheme = plotTheme;
		this.plotBossBar.setTitle("Theme: " + plotTheme);
	}

	public void setBarTime(int time)
	{
		String timeString;
		if (time <= 60)
			timeString = ChatColor.RED + "" + time + "second" + (time > 1 ? "s!" : "!");
		else if (time >= 60 * 60) // more than hour
			timeString = ((time / (60 * 60)) % 24) + " hour" + (((time / (60 * 60)) % 24) > 1 ? "s " : " ") + ((time / 60) % 60) + " minute" + (((time / 60) % 60) > 1 ? "s" : "");
		else
			timeString = (time / 60) + " minute" + ((time / 60) > 1 ? "s" : "");
		this.plotBossBar.setTitle("Theme: " + plotTheme + (time > 0 ? "   Time: " + timeString : ""));
	}

	public BossBar getPlotBossBar()
	{
		return plotBossBar;
	}

	public boolean isPlayerOwner(Player player)
	{
		return player.getUniqueId().equals(owner);
	}

	public void setBiome(Biome biome)
	{
		Vector3i size = game.getConfig().getValue(BuildBattleGame.PLOT_BUILD_SIZE);
		Vector3i offset = game.getConfig().getValue(BuildBattleGame.PLOT_BUILD_OFFSET);

		this.biome = biome;
		int startX = plotCoords.x + offset.x - 4;
		int endX = plotCoords.x + size.x + offset.x + 4;
		int startY = plotCoords.y + offset.y - 4;
		int endY = plotCoords.y + size.y + offset.y + 4;
		int startZ = plotCoords.z + offset.z - 4;
		int endZ = plotCoords.z + size.z + offset.z + 4;

		World world = ((BuildBattleGame) game).world;

		for (int i = startX; i < endX; i++)
		{
			for (int j = startY; j < endY; j++)
			{
				for (int k = startZ; k < endZ; k++)
				{
					FunnyLib
						.getWorkloadRunnable()
						.addWorkload(new ChangeBiomeWorkload(world, i, j, k, biome));
				}
			}
		}
		FunnyLib.getWorkloadRunnable().addWorkload(new ModifyWorldWorkload(world, w -> {
			NMS.resentBiomes(w, startX, startZ, endX, endZ);
		}));
	}
}
