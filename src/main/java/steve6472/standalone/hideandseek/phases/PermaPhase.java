package steve6472.standalone.hideandseek.phases;

import org.bukkit.event.player.PlayerJoinEvent;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.standalone.hideandseek.HideAndSeekGame;

/**
 * Created by steve6472
 * Date: 8/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PermaPhase extends AbstractGamePhase
{
	public PermaPhase(Game game)
	{
		super(game);
	}

	@Override
	public void start()
	{
		registerEvent(PlayerJoinEvent.class, event ->
		{
			HideAndSeekGame hns = (HideAndSeekGame) game;
			hns.seenPlayersThisGame.add(event.getPlayer().getUniqueId());
		});
	}

	@Override
	public void end()
	{

	}
}
