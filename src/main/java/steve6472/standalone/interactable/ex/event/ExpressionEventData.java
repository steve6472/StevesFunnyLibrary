package steve6472.standalone.interactable.ex.event;

import steve6472.funnylib.json.INBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.elements.EventReferenceType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ExpressionEventData implements INBT
{
	private final Map<InputType<?>, Map<String, Object>> inputs = new HashMap<>();

	public <T> ExpressionEventData with(InputType<T> type, String id, T obj)
	{
		inputs.computeIfAbsent(type, (inputType) -> new HashMap<>()).put(id, obj);
		return this;
	}

	public <T> T get(EventReferenceType<T> reference)
	{
		return (T) inputs.get(reference.getInputType()).get(reference.getInputId());
	}

	@Override
	public void toNBT(NBT compound)
	{

	}

	@Override
	public void fromNBT(NBT compound)
	{

	}

	@Override
	public String toString()
	{
		return "ExpressionEventData{" + "inputs=" + inputs + '}';
	}
}
