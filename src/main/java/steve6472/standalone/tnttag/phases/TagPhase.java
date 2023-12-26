package steve6472.standalone.tnttag.phases;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Preconditions;
import steve6472.funnylib.util.RandomUtil;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class TagPhase extends AbstractGamePhase
{
	public TagPhase(Game game)
	{
		super(game);
	}

	@Override
	public void start()
	{
		/*
		 * Tagging players
		 * spawning firework on tag
		 * Printing tag message
		 */
		registerEvent(EntityDamageByEntityEvent.class, event ->
		{
			if (!(event.getDamager() instanceof Player tagger) || !(event.getEntity() instanceof Player runner))
				return;

			if (!game.getStateTracker().hasState(tagger, "tag"))
				return;

			game.getStateTracker().removeState(runner, "runner");
			game.getStateTracker().removeState(tagger, "tag");

			game.getStateTracker().addState(tagger, "runner");
			game.getStateTracker().addState(runner, "tag");

			World world = runner.getLocation().getWorld();
			Preconditions.checkNotNull(world);

			world.spawn(runner.getLocation().clone().add(0, 1, 0), Firework.class, entity ->
			{
				FireworkMeta fireworkMeta = entity.getFireworkMeta();
				fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(0xcc1111), Color.fromRGB(0xeeeeee)).build());
				entity.setFireworkMeta(fireworkMeta);
			}).detonate();

			game.broadcast(JSONMessage.create(runner.getName()).color(ChatColor.RED).then(" was tagged!").color(ChatColor.YELLOW));
		});

		/*
		 * So the fireworks don't deal damage
		 */
		registerEvent(EntityDamageByEntityEvent.class, event ->
		{
			if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getDamager() instanceof Firework)
			{
				event.setCancelled(true);
			}
		});

//		scheduleRepeatingTask(task ->
//		{
//			Player tag = getCurrentTag();
//
//			if (tag == null)
//			{
//				tagRandomPlayer();
//				checkWin();
//				return;
//			}
//
//			explodePlayer(tag);
//
//			tagRandomPlayer();
//			checkWin();
//
//		}, 5 * 20, 15 * 20);
	}

	private void checkWin()
	{
		if (countRunners() <= 0)
		{
			game.getStateTracker().addState(getCurrentTag(), "winner");
			endPhase();
		}
	}

	private void explodePlayer(Player player)
	{
		game.getStateTracker().removeState(player, "tag");
		game.getStateTracker().addState(player, "spectator");
		player.getWorld().createExplosion(player.getLocation(), 0, false, false);
		game.broadcast(JSONMessage.create(player.getName()).color(ChatColor.RED).then(" exploded!").color(ChatColor.YELLOW));
	}

	private int countRunners()
	{
		return (int) game.getPlayers().stream().filter(p -> game.getStateTracker().hasState(p, "runner")).count();
	}

	private Player getCurrentTag()
	{
		return game
			.getPlayers()
			.stream()
			.filter(p -> game.getStateTracker().hasState(p, "tag"))
			.findFirst()
			.orElse(null);
	}

	private void tagRandomPlayer()
	{
		Set<? extends Player> runners = game.getPlayers().stream().filter(p -> game.getStateTracker().hasState(p, "runner")).collect(Collectors.toSet());
		int random = RandomUtil.randomInt(0, runners.size() - 1);
		runners.stream().skip(random).findAny().ifPresent(p ->
		{
			game.getStateTracker().removeState(p, "runner");
			game.getStateTracker().addState(p, "tag");
		});
	}

	@Override
	public void end()
	{

	}
}
