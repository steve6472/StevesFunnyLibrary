package steve6472.funnylib.entity.ecs;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Scheduler;
import steve6472.funnylib.entity.ecs.components.UUIDEntityComp;
import steve6472.funnylib.entity.ecs.systems.generic.ParticleDebugSystem;
import steve6472.funnylib.entity.ecs.systems.generic.PositionSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 2/11/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ECSMain
{
	private final Dominion ecs;
	private final Scheduler scheduler;
	private final List<System> systemList;

	public ECSMain()
	{
		java.lang.System.setProperty("dominion.show-banner", "false");

		this.ecs = Dominion.create();
		this.scheduler = ecs.createScheduler();
		this.systemList = new ArrayList<>();

		registerSystems();
	}

	public void tick()
	{
//		scheduler.tick();
		systemList.forEach(s -> s.tick(ecs));
	}

	private void registerSystem(System system)
	{
//		scheduler.schedule(() -> system.tick(ecs));
		systemList.add(system);
	}

	private void registerSystems()
	{
		registerSystem(new PositionSystem());
		registerSystem(new ParticleDebugSystem());
	}

	public void addEntity(Object... components)
	{
		ecs.createEntity(components);
	}

	public void unload()
	{
		ecs.findEntitiesWith(UUIDEntityComp.class).forEach(e -> e.comp().removeEntity());
	}
}
