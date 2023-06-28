package steve6472.standalone.interactable.blocks;

import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.CodeExecutor;
import steve6472.standalone.interactable.ex.event.ExpressionEvent;
import steve6472.standalone.interactable.ex.event.ExpressionEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/22/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeBlockData extends CustomBlockData
{
	CodeExecutor executor;
	public boolean repeating;
	transient List<CodeExecutor> executingEvents = new ArrayList<>();
	public List<ExpressionEvent> events = new ArrayList<>();

	@Override
	public void toNBT(NBT compound)
	{
		if (executor != null)
		{
			NBT executorCompound = compound.createCompound();
			executor.toNBT(executorCompound);
			compound.setCompound("executor", executorCompound);
		}
		NBT[] compoundArray = compound.createCompoundArray(events.size());
		for (int i = 0; i < events.size(); i++)
		{
			ExpressionEvent event = events.get(i);
			ExpressionEvents.saveEvent(compoundArray[i], event);
		}
		compound.setCompoundArray("events", compoundArray);
		compound.setBoolean("repeating", repeating);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		if (compound.hasCompound("executor"))
		{
			executor = new CodeExecutor();
			executor.fromNBT(compound.getCompound("executor"));
		}

		if (compound.hasCompoundArray("events"))
		{
			NBT[] eventsArray = compound.getCompoundArray("events");
			for (NBT nbt : eventsArray)
			{
				events.add(ExpressionEvents.loadEvent(nbt));
			}
		}

		repeating = compound.getBoolean("repeating", false);
	}
}
