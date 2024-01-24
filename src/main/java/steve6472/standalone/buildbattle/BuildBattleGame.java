package steve6472.standalone.buildbattle;

import org.bukkit.GameRule;
import org.bukkit.World;
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

import java.util.Set;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BuildBattleGame extends Game
{
	public static Set<String> POSSIBLE_THEMES = Set.of("Car", "House", "baldmatras");

	/*
	 * Ability to change day/night and weather
	 * Custom heads
	 */

	public static final Value<GameStructure> PLOT = Value.create(BuiltInConfigType.STRUCTURE, "Plot", "plot");
	public static final Value<String> TEST_STRING = Value.create(BuiltInConfigType.STRING, "Test String", "test");
	public static final Value<Integer> BUILD_TIME = Value.create(BuiltInConfigType.INT, "Build Time", "build_time");
	public static final Value<Marker> CENTER = Value.create(BuiltInConfigType.MARKER, "Center", "center");
	public static final Value<Vector3i> PLOT_BUILD_SIZE = Value.create(BuiltInConfigType.VEC_3I, "Plot Build Size", "plot_build_size");

	public BuildBattleGame(Plugin plugin, World world, GameConfiguration gameConfig)
	{
		super(plugin, null, gameConfig);

		Preconditions.checkNotNull(world, "World is null!");

		registerState("immovable", ImmovablePlayerState::new);
		registerState("invincible", InvinciblePlayerState::new);
		registerState("spectator", SpectatorPlayerState::new);
		registerState("border_locked", BorderLockedPlayerState::new);

		addPhase(new BuildPhase(this));

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
}
