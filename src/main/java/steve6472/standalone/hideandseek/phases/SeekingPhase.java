package steve6472.standalone.hideandseek.phases;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.standalone.hideandseek.HideAndSeekGame;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class SeekingPhase extends AbstractGamePhase
{
//	private static final Pattern DM = Pattern.compile("/(?:minecraft\\:)?(?:(?:msg )|(?:w )|(?:tell )|(?:whisper )).*");

	public SeekingPhase(Game game)
	{
		super(game);
	}

	@Override
	public void start()
	{
		Player seeker = getSeeker();

		/*
		 * Edit seeker
		 */
		game.getStateTracker().removeState(seeker, "seeker_waiting");
		game.getStateTracker().removeState(seeker, "immovable");
		game.getStateTracker().addState(seeker, "seeker");

		/*
		 * Change hider state
		 */
		game.getPlayers().stream().filter(p -> p != seeker).forEach(p ->
		{
			game.getStateTracker().removeState(p, "hider_hiding");
			game.getStateTracker().addState(p, "hider");
		});

		/*
		 * Disable drinking milk
		 */
		registerEvent(PlayerItemConsumeEvent.class, event ->
		{
			if (event.getItem().getType() == Material.MILK_BUCKET)
			{
				event.setCancelled(true);
			}
		});

		/*
		 * Seeker hitting hiders
		 */
		registerEvent(EntityDamageByEntityEvent.class, event ->
		{
			if (event.getDamager() != getSeeker())
				return;

			if (!(event.getEntity() instanceof Player hider))
				return;

			if (game.getStateTracker().hasState(hider, "hider"))
			{
				game.getStateTracker().removeState(hider, "hider");
				game.getStateTracker().removeState(hider, "invincible");
				game.getStateTracker().addState(hider, "spectator");
			}
		});

		/*
		 * Spectator chat
		 */
//		registerEvents(AsyncPlayerChatEvent.class, event ->
//		{
//			Player player = event.getPlayer();
//			if (!game.getStateTracker().hasState(player, "spectator"))
//				return;
//
//			event.setCancelled(true);
//
//			game.getPlayers()
//				.stream()
//				.filter(p -> game.getStateTracker().hasState(p, "spectator"))
//				.forEach(p -> p.sendMessage(event
//					.getFormat()
//					.formatted(event.getPlayer().getName(), event.getMessage())));
//		});

		/*
		 * Disable DMs
		 */
//		registerEvents(PlayerCommandPreprocessEvent.class, event ->
//		{
//			if (DM.matcher(event.getMessage()).matches())
//			{
//				event.getPlayer().sendMessage(ChatColor.RED + "Direct Messages are disabled!");
//				event.setCancelled(true);
//			}
//		});

		/*
		 * Despawn npc if player was already in game
		 * Set spectator if player was
		 * Kick player if they were not present at the start
		 */
		((HideAndSeekGame) game).joinEventNPC(this, "hider");
		((HideAndSeekGame) game).leaveEventNPC(this, "hider");


		/*
		 * Spawn NPC when player leaves
		 */

		CitizensAPI.registerEvents(new Listener()
		{
			@EventHandler
			public void click(NPCRightClickEvent event)
			{
				if (event.getClicker() != getSeeker())
					return;

				HideAndSeekGame hns = (HideAndSeekGame) game;

				UUID npcUUID = event.getNPC().getUniqueId();
				hns.getNpcRegistry().deregister(event.getNPC());
				UUID playerUUID = hns.npcPlayerMap.get(npcUUID);
				hns.leftSpectators.add(playerUUID);
				hns.npcPlayerMap.remove(npcUUID);
				hns.playerNpcMap.remove(playerUUID);
			}
		});
	}

	private Player getSeeker()
	{
		return game
			.getPlayers()
			.stream()
			.filter(p -> p.getName().equals("akmatras"))
			.findFirst()
			.orElse(null);
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

	@Override
	public void dispose()
	{
		super.dispose();
	}
}
