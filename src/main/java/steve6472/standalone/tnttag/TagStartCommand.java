package steve6472.standalone.tnttag;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Usage;
import steve6472.standalone.hideandseek.HideAndSeekGame;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class TagStartCommand
{
	@Command
	@Usage("/startTag")
	public static boolean startTag(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		if (FunnyLib.currentGame != null)
		{
			FunnyLib.currentGame.dispose();
		}

		FunnyLib.currentGame = new TNTTagGame(FunnyLib.getPlugin(), player.getWorld());

		return true;
	}
}
