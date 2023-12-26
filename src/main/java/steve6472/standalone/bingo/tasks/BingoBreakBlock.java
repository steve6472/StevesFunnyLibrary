package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoBreakBlock extends BingoTask
{
	private final Predicate<Block> predicate;

	public BingoBreakBlock(Bingo bingo, Material icon, String name, String description, String id, Predicate<Block> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(BlockBreakEvent.class, event ->
		{
			if (predicate.test(event.getBlock()))
				finishTask(event.getPlayer());
		});
	}
}
