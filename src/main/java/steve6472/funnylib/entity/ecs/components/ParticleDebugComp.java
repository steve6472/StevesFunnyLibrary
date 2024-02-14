package steve6472.funnylib.entity.ecs.components;

import org.bukkit.Particle;

/**
 * Created by steve6472
 * Date: 2/12/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ParticleDebugComp
{
	public Particle particle;
	public Object data;

	public ParticleDebugComp(Particle particle)
	{
		this.particle = particle;
	}

	public ParticleDebugComp(Particle particle, Object data)
	{
		this.particle = particle;
		this.data = data;
	}
}
