package steve6472.funnylib.coroutine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 11/9/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CoEx
{
	public final List<Coroutine<?, ?>> lines = new ArrayList<>();
	private int index;
	public boolean ended;

	public boolean execute(CoroutineExecutor executor)
	{
		ended = false;
		for (; index < lines.size(); index++)
		{
			Coroutine<?, ?> line = lines.get(index);
			if (line.execute(executor))
			{
				if (line.goToNextOnSleep())
					index++;
				return true;
			}
		}
		index = 0;
		ended = true;
		return false;
	}

	public boolean add(Coroutine<?, ?> coroutine)
	{
		return lines.add(coroutine);
	}

	public void forEach(Consumer<? super Coroutine<?, ?>> action)
	{
		lines.forEach(action);
	}

	@Override
	public String toString()
	{
		return "CoEx{" + "lines=" + lines + ", index=" + index + '}';
	}
}
