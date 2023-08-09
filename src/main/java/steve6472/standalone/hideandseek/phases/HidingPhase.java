package steve6472.standalone.hideandseek.phases;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;

import java.util.Iterator;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class HidingPhase extends AbstractGamePhase
{
	private final NamespacedKey hidingTimer = new NamespacedKey(game.getPlugin(), "hiding_timer");

	private static final JSONMessage GAME_INFO = JSONMessage
		.create("No milk").newline()
		.then("You are invincible").newline()
		.then("You leave, you can't go back").newline()
		.then("more stuff later");

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
			} else
			{
				game.getStateTracker().addState(p, "hider_hiding");

				p.sendTitle("Hide", "You have 15 minutes", 0, 60, 0);

				GAME_INFO.send(p);
			}

			p.teleport(spawn.toLocation(world));
			game.getStateTracker().addState(p, "invincible");
			game.getStateTracker().addState(p, "border_locked");
			game.getStateTracker().removeState(p, "lobby");
			timer.addPlayer(p);
		});

		registerEvents(PlayerItemConsumeEvent.class, event ->
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
		int seconds = (hidingTime / 20) % 60;

		timer.setTitle("Time left: " + minutes + ":" + seconds);
		timer.setProgress(Math.max(hidingTime / (double) maxHidingTime, 0));
	}
}
