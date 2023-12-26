package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoHavePotionEffect extends BingoTask
{
	private final Predicate<PotionEffectType> predicate;

	public BingoHavePotionEffect(Bingo bingo, Material icon, String name, String description, String id, Predicate<PotionEffectType> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(ServerTickEvent.class, event ->
		{
			bingo.getGame().getPlayers().forEach(player ->
			{
				if (!getAdvancement().isGranted(player))
				{
					for (PotionEffect activePotionEffect : player.getActivePotionEffects())
					{
						if (predicate.test(activePotionEffect.getType()))
							finishTask(player);
					}
				}
			});
		});
	}
}
