package steve6472.standalone.dimensionviewer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoGame;
import steve6472.standalone.bingo.BingoTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 9/2/2023
 * Project: StevesFunnyLibrary <br>
 */
public class VarCommand extends BrigitCommand
{
	private final Plugin plugin;

	public VarCommand(Plugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(
			literal(getName())
				.requires(c -> {
					try
					{
						return getPlayer(c).isOp();
					} catch (CommandSyntaxException e)
					{
						throw new RuntimeException(e);
					}
				}).then(literal("scale").then(argument("scale", doubleArg()).executes(c ->
				{
					DimensionViewer.scale = getDouble(c, "scale");
					return 0;
				}))).then(literal("refresh").then(argument("time", integer(5)).executes(c ->
				{
					DimensionViewer.refreshTime = getInteger(c, "time");
					return 0;
				}))).then(literal("testPortal").executes(c ->
				{
					Player player = getPlayer(c);
					World netherWorld = Bukkit
						.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NETHER).findFirst().orElseThrow();
					World voerWorld = Bukkit
						.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().orElseThrow();

					Location portal = PortalLinkFinder.findPortal(player.getLocation().multiply(player.getWorld().getEnvironment() == World.Environment.NETHER ? 8d : 1d / 8d),
						player.getWorld().getEnvironment() == World.Environment.NETHER ? voerWorld : netherWorld);
					Bukkit.broadcastMessage("" + portal);
					return 0;
				}))

		);
	}

	@Override
	public String getName()
	{
		return "var";
	}

	@Override
	public int getPermissionLevel()
	{
		return 4;
	}
}
