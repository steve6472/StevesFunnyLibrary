package steve6472.funnylib.item.builtin.worldtools;

import org.bukkit.ChatColor;
import org.joml.Vector3i;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 2/10/2024
 * Project: StevesFunnyLibrary <br>
 */
public interface Boundable
{
	default boolean canBeUsed(PlayerContext context)
	{
		return true;
	}

	default JSONMessage canNotBeUsedMessage(PlayerContext context)
	{
		return JSONMessage.create("This item can not currently be used!", ChatColor.RED);
	}

	default boolean isInBounds(PlayerContext context, Vector3i pos)
	{
		return true;
	}

	default JSONMessage outOfBoundsMessage(PlayerContext context)
	{
		return JSONMessage.create("Out of bounds!", ChatColor.RED);
	}
}
