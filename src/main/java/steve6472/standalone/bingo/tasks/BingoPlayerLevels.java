package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoPlayerLevels extends BingoTask
{
	private final int target;

	public BingoPlayerLevels(Bingo bingo, Material icon, String name, String description, String id, int target)
	{
		super(bingo, icon, name, description, id);
		this.target = target;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(PlayerLevelChangeEvent.class, event ->
		{
			if (event.getNewLevel() < target) return;
			finishTask(event.getPlayer());
		});
	}
}
