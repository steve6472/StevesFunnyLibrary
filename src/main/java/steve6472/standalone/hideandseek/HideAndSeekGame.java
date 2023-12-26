package steve6472.standalone.hideandseek;

import com.google.common.collect.Streams;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.minigame.builtin.phase.*;
import steve6472.funnylib.minigame.builtin.phase.composite.AddStatesOnJoinCPhase;
import steve6472.funnylib.minigame.builtin.phase.composite.AddStatesOnStartCPhase;
import steve6472.funnylib.minigame.builtin.phase.composite.RemoveStatesOnEndCPhase;
import steve6472.funnylib.minigame.builtin.state.*;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Preconditions;
import steve6472.funnylib.util.RandomUtil;
import steve6472.standalone.FunnyLibStandalone;
import steve6472.standalone.hideandseek.phases.HidingPhase;
import steve6472.standalone.hideandseek.phases.SeekingPhase;
import steve6472.standalone.hideandseek.playerstate.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class HideAndSeekGame extends Game
{
	public GameStructure boxStructure;
	public Marker boxSpawn, boxPlayerSpawn, spawn;
	public UUID currentBoxWorld, spawnWorld;
	public Set<UUID> seenPlayersThisGame = new HashSet<>();
	public int borderSize;

	public final Map<UUID, UUID> playerNpcMap = new HashMap<>();
	public final Map<UUID, UUID> npcPlayerMap = new HashMap<>();
	public final Set<UUID> leftSpectators = new HashSet<>();

	private final Team hideNametag;

	public World getBoxWorld()
	{
		return Bukkit.getWorld(currentBoxWorld);
	}

	public World getSpawnWorld()
	{
		return Bukkit.getWorld(spawnWorld);
	}

	public boolean isBoxSpawned()
	{
		return currentBoxWorld != null;
	}

	public HideAndSeekGame(Plugin plugin, World world, int worldBorderSize)
	{
		super(plugin, null, null);

		hideNametag = getScoreboard().registerNewTeam("hidden_nametag");
		hideNametag.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		hideNametag.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

		borderSize = worldBorderSize;

		spawnWorld = world.getUID();

		Preconditions.checkNotNull(world, "World is null!");

		GameStructure lobbyStructure = (GameStructure) FunnyLibStandalone.structureStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyStructure, "Lobby structure not found, make sure the \"lobby\" game structure exists");

		boxStructure = (GameStructure) FunnyLibStandalone.structureStorage.getItem("box_structure");
		Preconditions.checkNotNull(boxStructure, "Box structure not found, make sure the \"box\" game structure exists");

		Marker lobbyLocation = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyLocation, "Lobby location not found, make sure the \"lobby\" marker exists");

		Marker lobbySpawn = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby_spawn");
		Preconditions.checkNotNull(lobbySpawn, "Lobby location not found, make sure the \"lobby_spawn\" marker exists");

		spawn = (Marker) FunnyLibStandalone.markerStorage.getItem("hider_spawn");
		Preconditions.checkNotNull(spawn, "Hider spawn location not found, make sure the \"hider_spawn\" marker exists");

		boxSpawn = (Marker) FunnyLibStandalone.markerStorage.getItem("box");
		Preconditions.checkNotNull(boxSpawn, "Box spawn location not found, make sure the \"box\" marker exists");

		boxPlayerSpawn = (Marker) FunnyLibStandalone.markerStorage.getItem("box_spawn");
		Preconditions.checkNotNull(boxPlayerSpawn, "Box spawn location not found, make sure the \"box_spawn\" marker exists");

		/*
		 * Teams
		 */

		Scoreboard scoreboard = getScoreboard();

		Team hiderTeam = scoreboard.registerNewTeam("hider");
		hiderTeam.setPrefix(JSONMessage.create("[Hider] ").color(ChatColor.GRAY).toLegacy());
		hiderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

		Team spectatorTeam = scoreboard.registerNewTeam("spectator");
		spectatorTeam.setPrefix(JSONMessage.create("[Spectator] ").color(ChatColor.GRAY).toLegacy());
		spectatorTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

		Team deadTeam = scoreboard.registerNewTeam("dead");
		deadTeam.setPrefix(JSONMessage.create("[Dead] ").color(ChatColor.GRAY).toLegacy());
		deadTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		deadTeam.setAllowFriendlyFire(false);

		/*
		 * States
		 */

		registerState("immovable", ImmovablePlayerState::new);
		registerState("invincible", InvinciblePlayerState::new);
		registerState("border_locked", BorderLockedPlayerState::new);
		registerState("adventure", AdventurePlayerState::new);
		registerState("glowing", GlowingPlayerState::new);

		registerState("spectator", () -> new SpectatorTeamPlayerState(spectatorTeam));
		registerState("hider", () -> new HiderPlayerState(hiderTeam));
		registerState("hider_hiding", () -> new HiderHidingPlayerState(hiderTeam));
		registerState("seeker", () -> new SeekerPlayerState(this));
		registerState("seeker_waiting", SeekerWaitingPlayerState::new);
		registerState("dead", () -> new DeadPlayerState(deadTeam));
		registerState("untrackable", () -> new UntrackablePlayerState(this));

		/*
		 * Phases
		 */

		addPhase(new PlaceStructure(this, world, lobbyStructure, lobbyLocation.x(), lobbyLocation.y(), lobbyLocation.z()));

		addPhase(new WaitingForPlayersPhase(this, 50, true, lobbySpawn.toLocation(world))
			.addComponent(new AddStatesOnJoinCPhase(this, "invincible", "border_locked", "adventure"))
			.addComponent(new AddStatesOnStartCPhase(this, "invincible", "border_locked", "adventure")));

		addPhase(new CountdownPhase(this, 10)
			.addComponent(new RemoveStatesOnEndCPhase(this, "adventure")));

		addPhase(new DeleteStructure(this, world, lobbyStructure, lobbyLocation.x(), lobbyLocation.y(), lobbyLocation.z()));

		addPhase(new HidingPhase(this, world, spawn, 15 * 60 * 20));

		addPhase(new SeekingPhase(this));

		addPhase(new VictoryPhase(this, p -> !getStateTracker().hasState(p, "dead") && !getStateTracker().hasState(p, "spectator") && !p.getName().equals("akmatras")));

		setupWorld(world, worldBorderSize, lobbySpawn.toLocation(world));
		setupWorld(Bukkit.getWorld(world.getName() + "_nether"), worldBorderSize, lobbySpawn.toLocation(world));
		setupWorld(Bukkit.getWorld(world.getName() + "_the_end"), worldBorderSize, lobbySpawn.toLocation(world));

		start();
	}

	private void setupWorld(World world, int worldBorderSize, Location worldSpawn)
	{
		Preconditions.checkNotNull(world, "World not found");

		world.setPVP(true);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
		world.setGameRule(GameRule.DO_MOB_LOOT, true);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.DO_TILE_DROPS, true);
		world.setGameRule(GameRule.DO_FIRE_TICK, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
		world.setFullTime(0);
		world.getWorldBorder().setCenter(worldSpawn.getX(), worldSpawn.getZ());
		world.getWorldBorder().setSize(worldBorderSize);
		world.setSpawnLocation(worldSpawn);
	}

	public Collection<Location> getNPCLocations()
	{
		if (getCurrentPhase() instanceof SeekingPhase)
		{
			return Streams.stream(getNpcRegistry()).map(NPC::getStoredLocation).collect(Collectors.toSet());
		}

		return Collections.EMPTY_SET;
	}

	public static void spreadPlayers(World world, int cx, int cz, int spread, Collection<Player> players)
	{
		players.forEach(p ->
		{
			while (true)
			{
				int rx = RandomUtil.randomInt(cx - spread / 2, cx + spread / 2);
				int rz = RandomUtil.randomInt(cz - spread / 2, cz + spread / 2);
				Block highestBlockAt = world.getHighestBlockAt(rx, rz);
				if (highestBlockAt.isLiquid() || highestBlockAt.isPassable() || highestBlockAt.getLocation().getY() <= 60)
					continue;
				p.teleport(highestBlockAt.getLocation().clone().add(0.5, 1.05, 0.5));
				break;
			}
		});
	}

	public void joinEventNPC(AbstractGamePhase phase, String currentState)
	{
		phase.registerEvent(PlayerJoinEvent.class, event ->
		{
			HideAndSeekGame hns = this;
			Player player = event.getPlayer();

			// Players that left while they were spectators
			if (leftSpectators.contains(player.getUniqueId()))
			{
				if (hns.isBoxSpawned())
				{
					getStateTracker().addState(player, "dead");
					getStateTracker().addState(player, "border_locked");
					player.teleport(hns.boxPlayerSpawn.toLocation(hns.getBoxWorld()).add(0.5, 0.05, 0.5));
				} else
				{
					getStateTracker().addState(player, "spectator");
					getStateTracker().addState(player, "border_locked");
				}
				return;
			}

			// Player that did not leave during event joined
			if (!playerNpcMap.containsKey(player.getUniqueId()))
			{
				if (hns.isBoxSpawned())
				{
					getStateTracker().addState(player, "dead");
					getStateTracker().addState(player, "border_locked");
					player.teleport(hns.boxPlayerSpawn.toLocation(hns.getBoxWorld()).add(0.5, 0.05, 0.5));
				} else
				{
					getStateTracker().addState(player, "spectator");
					getStateTracker().addState(player, "border_locked");
				}
				getStateTracker().addState(player, "border_locked");
				return;
			}

			UUID npcUUID = playerNpcMap.get(player.getUniqueId());
			NPC npc = npcUUID == null ? null : getNpcRegistry().getByUniqueId(npcUUID);

			// Player was found when they were AFK
			if (npc == null)
			{
				if (hns.isBoxSpawned())
				{
					getStateTracker().addState(player, "dead");
					getStateTracker().addState(player, "border_locked");
					player.teleport(hns.boxPlayerSpawn.toLocation(hns.getBoxWorld()).add(0.5, 0.05, 0.5));
				} else
				{
					getStateTracker().addState(player, "spectator");
					getStateTracker().addState(player, "border_locked");
				}
				return;
			}

			getStateTracker().addState(player, currentState);
			getStateTracker().addState(player, "invincible");
			getStateTracker().addState(player, "border_locked");
			// Player normally joined
			getNpcRegistry().deregister(npc);
		});
	}

	public void leaveEventNPC(AbstractGamePhase phase, String currentState)
	{
		phase.registerEvent(PlayerQuitEvent.class, event ->
		{
			Player player = event.getPlayer();
			if (getStateTracker().hasState(player, currentState))
			{
				EntityEquipment playerEquipment = player.getEquipment();

				NPC npc = getNpcRegistry().createNPC(EntityType.PLAYER, player.getName(), player.getLocation());
				npc.setAlwaysUseNameHologram(false);
				npc.setFlyable(true);
				hideNametag.addEntry(npc.getName());
				Equipment npcEquipment = new Equipment();
				if (playerEquipment != null)
				{
					npc.addTrait(npcEquipment);
					npcEquipment.set(Equipment.EquipmentSlot.HAND, playerEquipment.getItem(EquipmentSlot.HAND));
					npcEquipment.set(Equipment.EquipmentSlot.OFF_HAND, playerEquipment.getItem(EquipmentSlot.OFF_HAND));
					npcEquipment.set(Equipment.EquipmentSlot.HELMET, playerEquipment.getItem(EquipmentSlot.HEAD));
					npcEquipment.set(Equipment.EquipmentSlot.CHESTPLATE, playerEquipment.getItem(EquipmentSlot.CHEST));
					npcEquipment.set(Equipment.EquipmentSlot.LEGGINGS, playerEquipment.getItem(EquipmentSlot.LEGS));
					npcEquipment.set(Equipment.EquipmentSlot.BOOTS, playerEquipment.getItem(EquipmentSlot.FEET));
				}
				playerNpcMap.put(player.getUniqueId(), npc.getUniqueId());
				npcPlayerMap.put(npc.getUniqueId(), player.getUniqueId());
			} else if (getStateTracker().hasState(player, "dead") || getStateTracker().hasState(player, "spectator"))
			{
				leftSpectators.add(player.getUniqueId());
			}
		});
	}
}
