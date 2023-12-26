package steve6472.standalone.hideandseek;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.minigame.Game;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
		{
			player.sendMessage(ChatColor.RED + "Usage: /startHideAndSeek <worldBorderSize>");
			return false;
		}

		if (FunnyLib.currentGame != null)
		{
			FunnyLib.currentGame.dispose();
		}

		FunnyLib.currentGame = new HideAndSeekGame(FunnyLib.getPlugin(), player.getWorld(), Integer.parseInt(args[0]));

		return true;
	}

	@Command
	@Usage("/debugStates")
	public static boolean debugStates(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		FunnyLib.debugGame();

		return true;
	}

	@Command
	@Usage("/box")
	public static boolean box(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		Game game = FunnyLib.currentGame;

		if (!(game instanceof HideAndSeekGame hns))
		{
			return false;
		}

		if (hns.isBoxSpawned())
		{
			player.sendMessage(ChatColor.RED + "Box is already spawned in world '" + hns.getBoxWorld().getName() + "'");
			return false;
		}

		// Place the box
		hns.currentBoxWorld = player.getWorld().getUID();
		World boxWorld = hns.getBoxWorld();
		hns.boxStructure.placeCentered(boxWorld, hns.boxSpawn.x(), hns.boxSpawn.y(), hns.boxSpawn.z());

		// Teleport spectators to box and set them to "dead" state
		game.getPlayers().stream().filter(p -> game.getStateTracker().hasState(p, "spectator")).forEach(p ->
		{
			Collection<PotionEffect> activePotionEffects = new HashSet<>(p.getActivePotionEffects());
			for (PotionEffect activePotionEffect : activePotionEffects)
			{
				p.removePotionEffect(activePotionEffect.getType());
			}

			game.getStateTracker().removeState(p, "spectator");
			game.getStateTracker().addState(p, "dead");
			p.getInventory().clear();
			p.teleport(hns.boxPlayerSpawn.toLocation(boxWorld).add(0.5, 0.05, 0.5));
		});

		return true;
	}

	@Command
	@Usage("/unbox")
	public static boolean unbox(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		Game game = FunnyLib.currentGame;

		if (!(game instanceof HideAndSeekGame hns))
		{
			return false;
		}

		// Despawn the box
		if (!hns.isBoxSpawned())
		{
			player.sendMessage(ChatColor.RED + "Box is not spawned!");
			return false;
		}

		World boxWorld = hns.getBoxWorld();

		hns.boxStructure.unplaceCentered(boxWorld, hns.boxSpawn.x(), hns.boxSpawn.y(), hns.boxSpawn.z());
		hns.currentBoxWorld = null;

		// Set non-revived players back to spectator
		game.getPlayers().stream().filter(p -> game.getStateTracker().hasState(p, "dead")).forEach(p ->
		{
			game.getStateTracker().removeState(p, "dead");
			game.getStateTracker().addState(p, "spectator");
		});

		return true;
	}

	@Command
	@Usage("/revive <player>")
	public static boolean revive(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		if (args.length != 1)
			return false;

		Game game = FunnyLib.currentGame;

		if (!(game instanceof HideAndSeekGame hns))
		{
			return false;
		}

		game.getPlayers().stream().filter(p -> p.getName().equalsIgnoreCase(args[0])).findFirst().ifPresentOrElse(p ->
		{
			game.getStateTracker().removeState(p, "dead");
			game.getStateTracker().removeState(p, "spectator");
			game.getStateTracker().addState(p, "hider");
			game.getStateTracker().addState(p, "untrackable");
			game.getStateTracker().addState(p, "invincible");
			p.getInventory().clear();
			player.sendMessage(ChatColor.GREEN + "Player revived!");

			Location center = hns.getSpawnWorld().getWorldBorder().getCenter();
			HideAndSeekGame.spreadPlayers(hns.getSpawnWorld(), center.getBlockX(), center.getBlockZ(), (int) hns.getSpawnWorld().getWorldBorder().getSize(), Set.of(p));
		}, () ->
		{
			player.sendMessage(ChatColor.RED + "Player not found! (wrong name, player not in game)");
		});

		return true;
	}

	@Command
	@Usage("/removePlayer <player>")
	public static boolean removePlayer(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		if (args.length != 1)
			return false;

		Game game = FunnyLib.currentGame;

		game.getPlayers().stream().filter(p -> p.getName().equalsIgnoreCase(args[0])).findFirst().ifPresentOrElse(p ->
		{
			game.removePlayer(p);
			player.sendMessage(ChatColor.GREEN + "Player removed from game!");
		}, () ->
		{
			player.sendMessage(ChatColor.RED + "Player not found! (wrong name, player not in game)");
		});

		return true;
	}
}
