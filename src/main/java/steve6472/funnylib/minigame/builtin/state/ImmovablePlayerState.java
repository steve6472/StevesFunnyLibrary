package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ImmovablePlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "immovable";
	}

	@Override
	public void apply(Player player)
	{
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, -1, 127, false, false, false));
	}

	@Override
	public void revert(Player player)
	{
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.SLOW_FALLING);
		player.removePotionEffect(PotionEffectType.SLOW_DIGGING);

		dispose();
	}
}
