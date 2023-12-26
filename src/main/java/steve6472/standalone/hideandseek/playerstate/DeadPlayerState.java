package steve6472.standalone.hideandseek.playerstate;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import steve6472.funnylib.minigame.AbstractPlayerState;
import steve6472.standalone.hideandseek.HideAndSeekGame;

/**
 * Created by steve6472
 * Date: 8/13/2023
 * Project: StevesFunnyLibrary <br>
 */
public class DeadPlayerState extends AbstractPlayerState
{
	private final Team deadTeam;

	public DeadPlayerState(Team deadTeam)
	{
		this.deadTeam = deadTeam;
	}

	@Override
	public String getName()
	{
		return "dead";
	}

	@Override
	public void apply(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 255, false, false, false));
		deadTeam.addEntry(player.getName());
		registerEvents(EntityDamageEvent.class, e -> damageEvent(player, e));
		registerEvents(EntityDamageByEntityEvent.class, e -> damageEvent(player, e));
		registerEvents(EntityDamageByBlockEvent.class, e -> damageEvent(player, e));
	}

	private void damageEvent(Player player, EntityDamageEvent event)
	{
		if (event.getEntity() != player)
			return;

		if (player.getHealth() - event.getFinalDamage() <= 0.2)
		{
			event.setCancelled(true);

			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			player.teleport(((HideAndSeekGame) this.tracker.game).boxPlayerSpawn.toLocation(event.getEntity().getWorld()).add(0.5, 0.05, 0.5));
		}
	}

	@Override
	public void revert(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
		player.removePotionEffect(PotionEffectType.SATURATION);
		deadTeam.removeEntry(player.getName());
	}
}
