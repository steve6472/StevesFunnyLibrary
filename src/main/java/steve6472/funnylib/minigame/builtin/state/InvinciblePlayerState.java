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
public class InvinciblePlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "invincible";
	}

	@Override
	public void apply(Player player)
	{
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 255, false, false, false));
	}

	@Override
	public void revert(Player player)
	{
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.REGENERATION);
		player.removePotionEffect(PotionEffectType.SATURATION);

		dispose();
	}
}
