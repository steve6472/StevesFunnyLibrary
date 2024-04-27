package steve6472.standalone.buildbattle.phases;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.RandomUtil;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;

import java.util.*;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BuildPhase extends AbstractGamePhase
{
	public final List<String> themes;
	public final Map<UUID, Plot> plots;
	public final Set<Material> disallowedBlocks = Set.of(Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.RESPAWN_ANCHOR);
	public final Set<EntityType> disallowedEntities = Set.of(EntityType.MINECART_COMMAND, EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.MINECART_TNT, EntityType.PRIMED_TNT, EntityType.ENDER_CRYSTAL);
	public final Map<UUID, Integer> clearQueueTasks = new HashMap<>();
	public int timeElapsed;

	public static final JSONMessage joinMessage = JSONMessage
		.create("Welcome to testing version of the Build Battle Minigame.").newline()
		.then("Time limit is 8 hours").newline()
		.then("You have been given a theme, but you do not need to follow it").newline()
		.then("Each plot can contain limited amount of entities!").newline()
		.then("Do ").then("/plot help", ChatColor.GOLD).then(" to show all commands.").newline();

	public static final JSONMessage commands = JSONMessage
		.create("/giveskull", ChatColor.GREEN).newline()
		.then("  <player>", ChatColor.GOLD).tooltip(JSONMessage.create("Gives you a skull of said player").newline().then("/giveskull <player>", ChatColor.GRAY)).newline()
		.then("/top", ChatColor.GOLD).tooltip("Teleports you to the highest block").newline()
		.then("/plot", ChatColor.GREEN).newline()
		.then("  help", ChatColor.GOLD).style(ChatColor.BOLD).tooltip(JSONMessage.create("Shows this help menu").newline().then("/plot help", ChatColor.GRAY)).newline()
		.then("  home", ChatColor.GOLD).style(ChatColor.BOLD).tooltip(JSONMessage.create("Teleports you back to your plot").newline().then("/plot home", ChatColor.GRAY)).newline()
		.then("  tool", ChatColor.GRAY).newline()
		.then("    sphere", ChatColor.GOLD).tooltip(JSONMessage.create("Gives you the Sphere Filler Tool").newline().then("/plot tool sphere", ChatColor.GRAY)).newline()
		.then("    rectangle", ChatColor.GOLD).tooltip(JSONMessage.create("Gives you the Rectangle Filler Tool").newline().then("/plot tool rectangle", ChatColor.GRAY)).newline()
		.then("    barrier", ChatColor.GOLD).tooltip(JSONMessage.create("Gives you the Barrier block").newline().then("/plot tool barrier", ChatColor.GRAY)).newline()
		.then("    light", ChatColor.GOLD).tooltip(JSONMessage.create("Gives you the Light block").newline().then("/plot tool light", ChatColor.GRAY)).newline()
		.then("  weather", ChatColor.GRAY).newline()
		.then("    [clear, downfall]", ChatColor.GOLD).tooltip(JSONMessage.create("Sets the plot weather").newline().then("/plot weather [clear, downfall]", ChatColor.GRAY)).newline()
		.then("  time", ChatColor.GRAY).newline()
		.then("    <time>", ChatColor.GOLD).tooltip(JSONMessage.create("Sets plot time").newline().then("/plot time <time>", ChatColor.GRAY)).newline()
		.then("  biome", ChatColor.GRAY).newline()
		.then("    <biome>", ChatColor.GOLD).tooltip(JSONMessage.create("Sets plot biome").newline().then("/plot biome <biome>", ChatColor.GRAY));

	public BuildPhase(Game game)
	{
		super(game);
		this.themes = game.getConfig().getValue(BuildBattleGame.THEMES);
		this.plots = ((BuildBattleGame) game).plots;
	}

	private String pickRandomTheme()
	{
		if (themes.isEmpty())
			themes.addAll(game.getConfig().getValue(BuildBattleGame.THEMES));
		return themes.remove(RandomUtil.randomInt(0, themes.size() - 1));
	}

	private void addPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		Plot plot = plots.computeIfAbsent(uuid, this::createNewPlot);
		Bukkit.getScheduler().runTaskLater(FunnyLib.getPlugin(), () -> {
			Player pl = Bukkit.getPlayer(uuid);
			if (pl != null)
				plot.teleportToPlot(pl);
		}, 1);
		player.setGameMode(GameMode.CREATIVE);
	}

	private World getWorld()
	{
		return ((BuildBattleGame) game).world;
	}

	public Plot createNewPlot(UUID uuid)
	{
		int currentPlotCount = plots.size();
		Vector2i newPlotPos = SpiralGenerator.getPos(currentPlotCount);
		Vector3i plotSize = game.getConfig().getValue(BuildBattleGame.PLOT).getSize();
		Vector3i plotOffset = game.getConfig().getValue(BuildBattleGame.PLOT_PLACE_OFFSET);
		Plot plot = new Plot(
			game,
			uuid,
			new Vector3i(
				newPlotPos.x * (plotSize.x + 1 + plotOffset.x),
				game.getConfig().getValue(BuildBattleGame.CENTER).y(),
				newPlotPos.y * (plotSize.z + 1 + plotOffset.z)
			).add(game.getConfig().getValue(BuildBattleGame.CENTER).toVec3i()),
			pickRandomTheme());
		plot.place(getWorld());
		return plot;
	}

	@Override
	public void start()
	{
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
		{
			game.addPlayer(onlinePlayer);
			addPlayer(onlinePlayer);
			joinMessage.send(onlinePlayer);
		}

		registerEvent(PlayerJoinEvent.class, event ->
		{
			Player player = event.getPlayer();
			addPlayer(player);
			joinMessage.send(player);

			// Cancel the undo quee clear task if player joined within 5 minutes
			Integer rmovedTask = clearQueueTasks.remove(player.getUniqueId());
			if (rmovedTask != null)
				Bukkit.getScheduler().cancelTask(rmovedTask);
		});

		// Clear the undo queue after 5 minutes
		registerEvent(PlayerQuitEvent.class, event ->
		{
			UUID playerUUID = event.getPlayer().getUniqueId();
			var clearQueueTask = scheduleSyncTask(task ->
			{
				FunnyLib.getWorkloadRunnable().undoManager().clearQueue(playerUUID);
				clearQueueTasks.remove(playerUUID);
			}, 20 * 5 * 60);
			clearQueueTasks.put(playerUUID, clearQueueTask.getTaskId());
		});

		registerEvent(PlayerMoveEvent.class, event ->
		{
			Location to = event.getTo();
			Location from = event.getFrom();
			if (to == null)
				return;

			Player player = event.getPlayer();

			Optional<Plot> lastPlot = plots.values().stream().filter(p -> p.isLocationInPlot(from)).findFirst();
			Optional<Plot> currentPlot = plots.values().stream().filter(p -> p.isLocationInPlot(to)).findFirst();

			// Prevent player from leaving plot
			lastPlot.ifPresent(plot ->
			{
				if (currentPlot.orElse(null) == plot)
					return;

				if (player.getGameMode() == GameMode.SPECTATOR)
					return;

				event.setCancelled(true);

				Vector3d vector = from.toVector().toVector3d();
				Vector3i centerI = plot.getCenter();
				Vector3d center = new Vector3d(centerI.x, centerI.y, centerI.z);
				vector.sub(center).normalize().mul(-0.2);

				player.teleport(from.clone().add(vector.x, vector.y, vector.z));

				vector.normalize().mul(0.5d);
				player.setVelocity(new Vector(vector.x, vector.y, vector.z));
			});

			// Apply current plot settings
			currentPlot.ifPresentOrElse(plot -> plot.applyPlotSettings(player, true), () -> resetPlotSettings(player, true));
		});

		scheduleRepeatingTask(task -> plots.forEach((owner, plot) ->
		{
			for (Entity trackedEntity : plot.getTrackedEntities())
			{
				// Entity is in plot, do nothing
				if (plot.isLocationInPlot(trackedEntity.getLocation()))
				 continue;

				Vector3d vector = trackedEntity.getLocation().toVector().toVector3d();
				Vector3i centerI = plot.getCenter();
				Vector3d center = new Vector3d(centerI.x, centerI.y, centerI.z);
				vector.sub(center).normalize().mul(-0.2);

				trackedEntity.teleport(trackedEntity.getLocation().clone().add(vector.x, vector.y, vector.z));

				vector.normalize().mul(0.5d);
				trackedEntity.setVelocity(new Vector(vector.x, vector.y, vector.z));
			}
		}), 0, 0);

		registerEvent(PlayerTeleportEvent.class, event ->
		{
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) return;
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) return;
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) return;

			event.setCancelled(true);
		});

		registerEvent(PlayerInteractEvent.class, event ->
		{
			Block clickedBlock = event.getClickedBlock();
			if (clickedBlock == null) return;

			Location loc = clickedBlock.getLocation();

			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (!clickedBlock.getType().isInteractable() || (event.getPlayer().isSneaking() && !Objects
				.requireNonNull(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND))
				.getType().isAir())))
			{
				loc.add(event.getBlockFace().getDirection());
			}

			Plot plot = ((BuildBattleGame) game).getPlayersPlot(event.getPlayer());
			if (!plot.isLocationInPlot(loc))
			{
				event.setCancelled(true);
			}
		});

		scheduleRepeatingTask(task -> {
			timeElapsed++;
			plots.forEach((k, plot) -> {
				plot.getPlotBossBar().setProgress(Math.min(1.0, Math.max(0.0, 1.0 - timeElapsed / (double) game.getConfig().getValue(BuildBattleGame.BUILD_TIME))));
				plot.setBarTime(game.getConfig().getValue(BuildBattleGame.BUILD_TIME) - timeElapsed);
			});
			if (timeElapsed >= game.getConfig().getValue(BuildBattleGame.BUILD_TIME))
			{
				endPhase();
			}
		}, 10 * 20, 20);

		// start tracking entity so it never leaves plot
		registerEvent(EntitySpawnEvent.class, event ->
		{
			// Ignore display entities
			if (event.getEntity() instanceof Display)
				return;

			getPlot(event.getEntity().getLocation()).ifPresent(plot ->
			{
				if (plot.getTrackedEntities().size() > game.getConfig().getValue(BuildBattleGame.ENTITY_PLOT_LIMIT))
				{
					event.setCancelled(true);
					return;
				}
				plot.trackEntity(event.getEntity());
			});
		});

		// Disable falling block item drops
		registerEvent(EntityChangeBlockEvent.class, event ->
		{
			Entity entity = event.getEntity();

			if (!(entity instanceof FallingBlock fallingBlock))
				return;

			fallingBlock.setDropItem(false);
		});

		/*
		 * Noes
		 */

		// disallow placing of command blocks
		registerEvent(BlockPlaceEvent.class, event ->
		{
			if (event.getPlayer().isOp())
				return;

			if (disallowedBlocks.contains(event.getBlock().getType()))
				event.setCancelled(true);
		});

		// disallow spawning entities
		registerEvent(EntitySpawnEvent.class, event ->
		{
			if (disallowedEntities.contains(event.getEntity().getType()))
				event.setCancelled(true);
		});

		// disallow tnt
		registerEvent(EntityChangeBlockEvent.class, event ->
		{
			if (event.getEntityType() == EntityType.PRIMED_TNT)
				event.setCancelled(true);
		});

		// disallow other ppl damaging entities
		registerEvent(EntityDamageByEntityEvent.class, event ->
		{
			if (event.getDamager() instanceof Player player)
			{
				if (player.isOp())
					return;

				Location location = event.getEntity().getLocation();
				Optional<Plot> plot = getPlot(location);
				plot.ifPresent(p ->
				{
					Plot playersPlot = ((BuildBattleGame) game).getPlayersPlot(player);
					if (p != playersPlot)
					{
						event.setCancelled(true);
					}
				});
			}
		});

		// Despawn arrows after they hit ground
		registerEvent(ProjectileHitEvent.class, event ->
		{
			if (event.getEntity() instanceof Arrow arrow)
			{
				arrow.remove();
			}
		});

		// No creeper exploding
		registerEvent(PlayerInteractEntityEvent.class, event ->
		{
			if (!event.getPlayer().isOp() && event.getRightClicked() instanceof Creeper)
				event.setCancelled(true);
		});
	}

	public Optional<Plot> getPlayersCurrentPlot(Player player)
	{
		return getPlot(player.getLocation());
	}

	public Optional<Plot> getPlot(Location location)
	{
		for (Map.Entry<UUID, Plot> entry : plots.entrySet())
		{
			Plot v = entry.getValue();
			if (v.isLocationInPlot(location))
			{
				return Optional.of(v);
			}
		}

		return Optional.empty();
	}

	public static void resetPlotSettings(Player player, boolean clearBorder)
	{
		player.resetPlayerWeather();
		player.resetPlayerTime();
		if (clearBorder)
			player.setWorldBorder(player.getWorld().getWorldBorder());
		clearBossBars(player);
	}

	public static void clearBossBars(Player player)
	{
		Set<BossBar> bars = new HashSet<>();
		Bukkit.getBossBars().forEachRemaining(bar -> {
			if (bar.getKey().getKey().startsWith("plot_"))
			{
				if (bar.getPlayers().contains(player))
				{
					bars.add(bar);
				}
			}
		});
		bars.forEach(bar -> bar.removePlayer(player));
	}

	@Override
	public void end()
	{
		plots.forEach((k, plot) ->
        {
            plot.setBarTime(-1);
	        FunnyLib.getWorkloadRunnable().undoManager().clearQueue(k);
        });
		game.getPlayers().forEach(player -> resetPlotSettings(player, true));
	}
}
