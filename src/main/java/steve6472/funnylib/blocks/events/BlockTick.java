package steve6472.funnylib.blocks.events;

import org.bukkit.Location;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.stateengine.State;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BlockTick
{
	void tick(State state, Location location, CustomBlockData data);
}
