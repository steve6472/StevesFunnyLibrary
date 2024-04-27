package steve6472.standalone.buildbattle.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.minigame.Minigames;
import steve6472.funnylib.util.SkullCreator;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;
import steve6472.standalone.buildbattle.phases.BuildPhase;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 1/25/2024
 * Project: StevesFunnyLibrary <br>
 */
public class TopCommand extends BrigitCommand
{
	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(
			literal(getName())
				.executes(c ->
					{
						checkGame();

						BuildPhase phase = (BuildPhase) FunnyLib.currentGame.getCurrentPhase();

						Player player = getPlayer(c);
						Block highestBlockAt = player.getWorld().getHighestBlockAt(player.getLocation());
						Location tpLoc = highestBlockAt.getLocation().clone().add(0.5, 1.5, 0.5);
						Optional<Plot> playersCurrentPlot = phase.getPlayersCurrentPlot(player);
						playersCurrentPlot.ifPresent(plot -> {

							Vector3i size = FunnyLib.currentGame.getConfig().getValue(BuildBattleGame.PLOT_BUILD_SIZE);
							Vector3i offset = FunnyLib.currentGame.getConfig().getValue(BuildBattleGame.PLOT_BUILD_OFFSET);

							if (tpLoc.getY() + 0.2 >= plot.getPlotCoords().y + offset.y + size.y)
								tpLoc.setY(plot.getPlotCoords().y + offset.y + size.y - 0.2);
						});

						player.teleport(tpLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

						return 0;
					})
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
		return "top";
	}

	@Override
	public int getPermissionLevel()
	{
		return 0;
	}
}
