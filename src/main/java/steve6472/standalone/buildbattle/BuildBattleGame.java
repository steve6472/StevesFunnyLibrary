package steve6472.standalone.buildbattle;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.joml.Vector3i;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.minigame.builtin.state.*;
import steve6472.funnylib.minigame.config.BuiltInConfigType;
import steve6472.funnylib.minigame.config.GameConfiguration;
import steve6472.funnylib.minigame.config.Value;
import steve6472.funnylib.util.Preconditions;
import steve6472.standalone.buildbattle.phases.BuildPhase;
import steve6472.standalone.buildbattle.phases.ViewingPhase;

import java.util.*;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BuildBattleGame extends Game
{
	public static Set<String> POSSIBLE_THEMES = Set.of("Car", "House", "baldmatras");

	/*
	 * [x] Ability to change day/night and weather
	 * [x] Custom heads
	 * [ ] Plot biome
	 */

	public static final Value<GameStructure> PLOT = Value.create(BuiltInConfigType.STRUCTURE, "Plot", "plot");
	public static final Value<Integer> BUILD_TIME = Value.create(BuiltInConfigType.INT, "Build Time", "build_time");
	public static final Value<Marker> CENTER = Value.create(BuiltInConfigType.MARKER, "Center", "center");
	public static final Value<Vector3i> PLOT_BUILD_SIZE = Value.create(BuiltInConfigType.VEC_3I, "Plot Build Size", "plot_build_size");
	public static final Value<Vector3i> PLOT_BUILD_OFFSET = Value.create(BuiltInConfigType.VEC_3I, "Plot Build Offset", "plot_build_offset");
	public static final Value<Vector3i> PLOT_PLACE_OFFSET = Value.create(BuiltInConfigType.VEC_3I, "Plot Place Offset", "plot_place_offset");

	public World world;
	public final List<String> themes;
	public final Map<UUID, Plot> plots;

	public BuildBattleGame(Plugin plugin, World world, GameConfiguration gameConfig)
	{
		super(plugin, null, gameConfig);

		Preconditions.checkNotNull(world, "World is null!");

		registerState("immovable", ImmovablePlayerState::new);
		registerState("invincible", InvinciblePlayerState::new);
		registerState("spectator", SpectatorPlayerState::new);
		registerState("border_locked", BorderLockedPlayerState::new);

		this.world = world;
		this.themes = new ArrayList<>(BuildBattleGame.POSSIBLE_THEMES);
		this.plots = new HashMap<>();

		addPhase(new BuildPhase(this));
		addPhase(new ViewingPhase(this));

		/*
		 * Setup world
		 */

		world.setPVP(false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_TILE_DROPS, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_MOB_LOOT, false);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setFullTime(6000);

		start();
	}

	public Plot getPlayersPlot(Player player)
	{
		return plots.get(player.getUniqueId());
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
}
