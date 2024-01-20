package steve6472.funnylib.workdistro;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 1/20/2024
 * Project: StevesFunnyLibrary <br>
 */
public class OnEntityWorkload<T extends Entity> implements Workload
{
	private final UUID entity;
	private final Consumer<T> action;
	private final Class<?> clazz;

	public OnEntityWorkload(T entity, Consumer<T> action)
	{
		this.entity = entity.getUniqueId();
		this.action = action;
		this.clazz = entity.getClass();
	}

	@Override
	public void compute()
	{
		Entity entity;
		if ((entity = Bukkit.getEntity(this.entity)) == null) return;
		if (entity.getClass() != clazz) return;
		//noinspection unchecked - Checked above
		action.accept((T) entity);
	}
}
