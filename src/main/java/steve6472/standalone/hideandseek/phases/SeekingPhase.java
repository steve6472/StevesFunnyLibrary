package steve6472.standalone.hideandseek.phases;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class SeekingPhase extends AbstractGamePhase
{
	public SeekingPhase(Game game)
	{
		super(game);
	}

	@Override
	public void start()
	{
		Player seeker = game
			.getPlayers()
			.stream()
			.filter(p -> p.getName().equals("akmatras"))
			.findFirst()
			.orElse(null);

		if (seeker == null)
		{
			endPhase();
			return;
		}

		game.getStateTracker().removeState(seeker, "seeker_waiting");
		game.getStateTracker().removeState(seeker, "immovable");
		game.getStateTracker().addState(seeker, "seeker");

		game.getPlayers().stream().filter(p -> p != seeker).forEach(p ->
		{
			game.getStateTracker().removeState(p, "hider_hiding");
			game.getStateTracker().addState(p, "hider");
		});

		/*
		 * Disable drinking milk
		 */
		registerEvents(PlayerItemConsumeEvent.class, event ->
		{
			if (event.getItem().getType() == Material.MILK_BUCKET)
			{
				event.setCancelled(true);
			}
		});

		/*
		 * Seeker hitting hiders
		 */
		registerEvents(EntityDamageByEntityEvent.class, event ->
		{
			if (event.getDamager() != seeker)
				return;

			if (!(event.getEntity() instanceof Player hider))
				return;

			game.getStateTracker().removeState(hider, "hider");
			game.getStateTracker().addState(hider, "spectator");
		});

		/*
		 * Spectator chat
		 */
		registerEvents(AsyncPlayerChatEvent.class, event ->
		{
			Player player = event.getPlayer();
			if (!game.getStateTracker().hasState(player, "spectator"))
				return;

			event.setCancelled(true);

			game.getPlayers()
				.stream()
				.filter(p -> game.getStateTracker().hasState(p, "spectator"))
				.forEach(p -> p.sendMessage(event
					.getFormat()
					.formatted(event.getPlayer().getName(), event.getMessage())));
		});
	}

	@Override
	public void tick()
	{
		Set<? extends Player> winner = game
			.getPlayers()
			.stream()
			.filter(p -> !game.getStateTracker().hasState(p, "spectator") && !p.getName().equals("akmatras"))
			.collect(Collectors.toSet());
		if (winner.size() <= 1)
		{
			winner.forEach(p -> game.getStateTracker().addState(p, "glowing"));
			endPhase();
		}
	}

	@Override
	public void end()
	{

	}
}
