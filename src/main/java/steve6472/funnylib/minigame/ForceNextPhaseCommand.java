package steve6472.funnylib.minigame;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.FunnyLib;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ForceNextPhaseCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
	{
		if (!commandSender.isOp() || !(commandSender instanceof Player player))
		{
			return false;
		}

		Game currentGame = FunnyLib.currentGame;

		if (currentGame == null)
		{
			commandSender.sendMessage(ChatColor.RED + "No game in progress!");
			return false;
		}

		AbstractGamePhase currentPhase = currentGame.phases.getCurrentPhase();
		currentPhase.endPhase();
		commandSender.sendMessage(ChatColor.GREEN + "Force ended current stage '" + currentPhase.getClass().getSimpleName() + "'");

		return true;
	}
}
