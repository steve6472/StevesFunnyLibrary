package steve6472.standalone.hideandseek;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Usage;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class HNSStartCommand
{
	@Command
	@Usage("/startHideAndSeek <worldBorderSize>")
	public static boolean startHideAndSeek(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		if (args.length != 1)
			return false;

		if (FunnyLib.currentGame != null)
		{
			FunnyLib.currentGame.dispose();
		}

		FunnyLib.currentGame = new HideAndSeekGame(FunnyLib.getPlugin(), player.getWorld(), Integer.parseInt(args[0]));

		return true;
	}

	// TODO: fix this, shit don't work
	@Command
	@Usage("/endgame")
	public static boolean endgame(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		if (args.length != 1)
			return false;

		if (FunnyLib.currentGame != null)
		{
			FunnyLib.currentGame.dispose();
			FunnyLib.currentGame = null;
		}

		return true;
	}
}
