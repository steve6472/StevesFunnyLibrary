package steve6472.standalone.hideandseek.phases;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.hideandseek.HideAndSeekGame;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class HidingPhase extends AbstractGamePhase
{
	private final NamespacedKey hidingTimer = new NamespacedKey(game.getPlugin(), "hiding_timer");

	private static final JSONMessage GAME_INFO = JSONMessage
		.create("\nYou can't drink milk.").color(ChatColor.YELLOW).newline()
		.then("You can take damage, but you can not die.").color(ChatColor.YELLOW).newline()
		.then("If you leave, an NPC will be spawned in your place. (You can rejoin)").color(ChatColor.YELLOW).newline()
		.then("Your goal is to not be punched by").color(ChatColor.GREEN).then(" akmatras", ChatColor.AQUA).then(".", ChatColor.GREEN).newline()
		.then("");

	Marker spawn;
	World world;
	private BossBar timer;
	int hidingTime, maxHidingTime;

	public HidingPhase(Game game, World world, Marker spawn, int hidingTime)
	{
		super(game);
		this.world = world;
		this.spawn = spawn;
		this.hidingTime = hidingTime;
		this.maxHidingTime = hidingTime;
	}

	@Override
	public void start()
	{
		timer = Bukkit.createBossBar(hidingTimer, "Time left: 15:00", BarColor.WHITE, BarStyle.SOLID);
		timer.setProgress(1.0);

		game.getPlayers().forEach(p ->
		{
			Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();

			while (advancements.hasNext())
			{
				AdvancementProgress progress = p.getAdvancementProgress(advancements.next());
				for (String s : progress.getAwardedCriteria())
					progress.revokeCriteria(s);
			}

			if (p.getName().equals("akmatras"))
			{
				game.getStateTracker().addState(p, "seeker_waiting");
				game.getStateTracker().addState(p, "immovable");

				p.sendTitle("Close your eyes for 15 minutes", "", 0, hidingTime, 0);
				p.teleport(spawn.toLocation(world));
			} else
			{
				game.getStateTracker().addState(p, "hider_hiding");

				p.sendTitle("Hide", "You have 15 minutes", 0, 60, 0);

				GAME_INFO.send(p);

				// Spread player
				HideAndSeekGame hns = (HideAndSeekGame) game;
				Location center = hns.getSpawnWorld().getWorldBorder().getCenter();
				HideAndSeekGame.spreadPlayers(hns.getSpawnWorld(), center.getBlockX(), center.getBlockZ(), (int) hns.getSpawnWorld().getWorldBorder().getSize(), Set.of(p));
			}

			game.getStateTracker().addState(p, "invincible");
			game.getStateTracker().addState(p, "border_locked");
			game.getStateTracker().removeState(p, "lobby");
			timer.addPlayer(p);
		});

		/*
		 * Players (re)joining mid-game
		 */
		((HideAndSeekGame) game).joinEventNPC(this, "hider_hiding");
		((HideAndSeekGame) game).leaveEventNPC(this, "hider_hiding");

		registerEvent(PlayerJoinEvent.class, event ->
		{
			Player player = event.getPlayer();
			timer.addPlayer(player);
		});

		/*
		 * Remove from timer just in case it can somewhy get duplicated for the player
		 */
		registerEvent(PlayerQuitEvent.class, event ->
		{
			timer.removePlayer(event.getPlayer());
		});

		/*
		 * Forbid drinking milk
		 */
		registerEvent(PlayerItemConsumeEvent.class, event ->
		{
			if (event.getItem().getType() == Material.MILK_BUCKET)
			{
				event.setCancelled(true);
			}
		});
	}

	@Override
	public void end()
	{
		timer.removeAll();
		Bukkit.removeBossBar(hidingTimer);
	}

	@Override
	public void tick()
	{
		hidingTime--;

		if (hidingTime < 0)
		{
			endPhase();
			return;
		}

		int minutes = (hidingTime / 20 / 60) % 60;
		String seconds = Integer.toString((hidingTime / 20) % 60);
		if (seconds.length() == 1)
		{
			seconds = "0" + seconds;
		}

		timer.setTitle("Time left: " + minutes + ":" + seconds);
		timer.setProgress(Math.max(hidingTime / (double) maxHidingTime, 0));
	}
}
