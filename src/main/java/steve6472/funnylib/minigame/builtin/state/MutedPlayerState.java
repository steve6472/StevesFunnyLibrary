package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class MutedPlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "muted";
	}

	@Override
	public void apply(Player player)
	{
		registerEvents(AsyncPlayerChatEvent.class, event ->
		{
			if (event.getPlayer() != player)
				return;

			event.setCancelled(true);
		});
	}

	@Override
	public void revert(Player player)
	{
	}
}
