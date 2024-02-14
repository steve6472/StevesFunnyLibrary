package steve6472.funnylib.entity.ecs;

import dev.dominion.ecs.api.Dominion;

/**
 * Created by steve6472
 * Date: 2/11/2024
 * Project: StevesFunnyLibrary <br>
 */
public interface System
{
	void tick(Dominion ecs);
}
