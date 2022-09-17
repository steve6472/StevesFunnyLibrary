package steve6472.funnylib.blocks.events;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.stateengine.State;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BreakBlockEvent
{
	void breakBlock(ItemStack item, State state, CustomBlockData data, org.bukkit.event.block.BlockBreakEvent e);
}
