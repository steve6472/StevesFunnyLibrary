package steve6472.funnylib.blocks.builtin;

import org.bukkit.entity.Player;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.context.PlayerBlockContext;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface AdminInterface<T extends CustomBlockData>
{
	void showInterface(T data, PlayerBlockContext context);
}
