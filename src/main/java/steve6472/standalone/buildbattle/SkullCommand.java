package steve6472.standalone.buildbattle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.inventory.ItemStack;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.minigame.Minigames;
import steve6472.funnylib.util.SkullCreator;
import steve6472.standalone.buildbattle.phases.BuildPhase;

/**
 * Created by steve6472
 * Date: 1/25/2024
 * Project: StevesFunnyLibrary <br>
 */
public class SkullCommand extends BrigitCommand
{
	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(
			literal(getName())
				.then(argument("player", string())
					.executes(c ->
						{
							checkGame();

							ItemStack skull = SkullCreator.itemFromName(getString(c, "player"));
							getPlayer(c).getInventory().addItem(skull);
							return 0;
						})
				)
		);
	}

	private void checkGame() throws CommandSyntaxException
	{
		if (!(FunnyLib.currentGame instanceof BuildBattleGame))
		{
			throw Minigames.ERROR_MINIGAME_SPECIFIC.create(null);
		}
	}

	@Override
	public String getName()
	{
		return "giveskull";
	}

	@Override
	public int getPermissionLevel()
	{
		return 0;
	}
}
