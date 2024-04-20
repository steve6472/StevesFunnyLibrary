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
	/*
	 * [x] Ability to change day/night and weather
	 * [x] Custom heads
	 * [x] Plot biome
	 * [ ] Line tool - beziér curve,  varrying width per point
	 * [ ] A layer tool - place 1 layer of blocks on top of existing blocks (with filter maybe)
	 * [ ] Random place tool, a single block
	 * [ ] Smooth tool, 3D
	 * [ ] Double clicking an item takes items out of custom menu
	 *
	 *
	 * [?] increase entity render distance
	 * [?] fix time (it somehow goes faster)
	 *
	 * [x] Disable TNT
	 * [x] Sphere tool does not dissapear if player is not looking at blocks
	 * [x] /top
	 * [x] Apply on sphere brush is confusing, should not place blocks
	 * [x] help command
	 * [x] give light/barrier command
	 * [x] liquids in fill tools
	 * [ ] one undo
	 * [?] lavacasts should not be possible (place barriers)
	 * [x] prohibit all entities from leaving the plot, not just players
	 * [x] fix left click with rectangle tool not working
	 * [x] click preview for rectangle tool
	 */

	public static final Value<GameStructure> PLOT = Value.create(BuiltInConfigType.STRUCTURE, "Plot", "plot");
	public static final Value<Integer> BUILD_TIME = Value.create(BuiltInConfigType.INT, "Build Time (seconds)", "build_time");
	public static final Value<Marker> CENTER = Value.create(BuiltInConfigType.MARKER, "Center", "center");
	public static final Value<Vector3i> PLOT_BUILD_SIZE = Value.create(BuiltInConfigType.VEC_3I, "Plot Build Size", "plot_build_size");
	public static final Value<Vector3i> PLOT_BUILD_OFFSET = Value.create(BuiltInConfigType.VEC_3I, "Plot Build Offset", "plot_build_offset");
	public static final Value<Vector3i> PLOT_PLACE_OFFSET = Value.create(BuiltInConfigType.VEC_3I, "Plot Place Offset", "plot_place_offset");
	public static final Value<Vector3i> BARRIER_OFFSET = Value.create(BuiltInConfigType.VEC_3I, "Barrier Offset", "barrier_offset");
	public static final Value<Vector3i> BARRIER_SIZE = Value.create(BuiltInConfigType.VEC_3I, "Barrier Size", "barrier_size");
	public static final Value<Boolean> BARRIER_CAP_TOP = Value.create(BuiltInConfigType.BOOLEAN, "Barrier Cap Top", "barrier_top");
	public static final Value<List<String>> THEMES = Value.create(BuiltInConfigType.STRING_LIST, "Themes", "themes", true);

	public World world;
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
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
		world.setFullTime(6000);
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
