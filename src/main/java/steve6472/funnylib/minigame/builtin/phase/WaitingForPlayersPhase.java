package steve6472.funnylib.minigame.builtin.phase;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class WaitingForPlayersPhase extends AbstractGamePhase
{
	private static final String KEY = "phase_waiting_counter";

	private final int minPlayers;
	private final boolean includeCurrentPlayers;
	private final Location spawn;
	private BossBar countBar;

	public WaitingForPlayersPhase(Game game, int minPlayers, boolean includeCurrentPlayers, Location spawn)
	{
		super(game);
		this.minPlayers = minPlayers;
		this.includeCurrentPlayers = includeCurrentPlayers;
		this.spawn = spawn;
	}

	@Override
	public void start()
	{
		countBar = game.createBossBar(KEY, "Players: " + game.getPlayers().size() + "/" + minPlayers, BarColor.WHITE, BarStyle.SOLID);

		if (includeCurrentPlayers)
		{
			for (Player onlinePlayer : Bukkit.getOnlinePlayers())
			{
				game.addPlayer(onlinePlayer);
				countBar.addPlayer(onlinePlayer);
				onlinePlayer.teleport(spawn);
			}
		}

		updateCountBar();

		registerEvent(PlayerJoinEvent.class, event ->
		{
			// It's ok to run this check on every player join event, ideally we'd have a GameAddPlayerEvent
			game.addPlayer(event.getPlayer());
			countBar.addPlayer(event.getPlayer());
			updateCountBar();
			event.getPlayer().teleport(spawn);

			if (game.getPlayers().size() >= minPlayers)
			{
				endPhase();
			}

			event.getPlayer().getInventory().clear();
		});

		registerEvent(PlayerQuitEvent.class, event ->
		{
			game.removePlayer(event.getPlayer());
			updateCountBar();
		});

		registerEvent(PlayerItemConsumeEvent.class, event -> event.setCancelled(true));
		registerEvent(PlayerInteractEvent.class, event -> event.setCancelled(true));
		registerEvent(PlayerInteractAtEntityEvent.class, event -> event.setCancelled(true));

		if (game.getPlayers().size() >= minPlayers)
		{
			endPhase();
		}
	}

	private void updateCountBar()
	{
		countBar.setTitle("Players: " + game.getPlayers().size() + "/" + minPlayers);
		countBar.setProgress(Math.min(game.getPlayers().size() / (double) minPlayers, 1.0));

		countBar.setColor(game.getPlayers().size() >= minPlayers ? BarColor.GREEN : BarColor.WHITE);
	}

	@Override
	public void end()
	{
		game.deleteBossBar(KEY);
	}
}
