package steve6472.funnylib.workdistro.impl;

import org.bukkit.World;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ModifyWorldWorkload extends WorldWorkload
{
	private final Consumer<World> action;

	public ModifyWorldWorkload(World world, Consumer<World> action)
	{
		super(world);
		this.action = action;
	}

	@Override
	public void compute()
	{
		World world = getWorld();
		if (world == null)
			return;

		action.accept(world);
	}
}
