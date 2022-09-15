package steve6472.funnylib.blocks.stateengine;

import steve6472.funnylib.blocks.stateengine.properties.IProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 03.07.2020
 * Project: StateTest
 *
 ***********************/
public class StateBuilder
{
	public static void generateStates(StateObject tile, List<IProperty<?>> properties)
	{
		if (properties.isEmpty())
		{
			tile.setDefaultState(new State(tile, null, null));
			return;
		}

		// Generate all possible state values

		int possibleStatesCount = 1;
		List<Integer> possibleValuesCount = new ArrayList<>(properties.size());
		List<Integer> possibleValuesCount_ = new ArrayList<>(properties.size());

		List<List<Comparable<?>>> possibleValues = new ArrayList<>(possibleStatesCount);

		for (IProperty<?> p : properties)
		{
			possibleValuesCount.add(p.getPossibleValues().length);
			possibleValuesCount_.add(p.getPossibleValues().length);
			possibleStatesCount *= p.getPossibleValues().length;
		}

		for (int i = 0; i < possibleStatesCount; i++)
			possibleValues.add(new ArrayList<>(properties.size()));

		for (int i = 0; i < possibleStatesCount; i++)
		{
			List<Comparable<?>> states = possibleValues.get(i);

			for (int j = 0; j < possibleValuesCount.size(); j++)
			{
				states.add(properties.get(j).getPossibleValues()[possibleValuesCount_.get(j) - 1]);
			}
			sub(possibleValuesCount_, possibleValuesCount, possibleValuesCount.size() - 1);
		}

		// Create all posible states
		List<State> tileStates = new ArrayList<>(possibleStatesCount);
		for (List<Comparable<?>> list : possibleValues)
		{
			HashMap<IProperty<?>, Comparable<?>> map = new HashMap<>();

			for (int i = 0; i < list.size(); i++)
			{
				map.put(properties.get(i), list.get(i));
			}

			State state = new State(tile, map, tileStates);
			if (tile.getDefaultState() == null)
				tile.setDefaultState(state);
			tileStates.add(state);
		}
	}

	private static void sub(List<Integer> list, List<Integer> max, int index)
	{
		if (index == -1)
			return;

		int v = list.get(index);
		if (v > 1)
		{
			v--;
			list.set(index, v);
		} else
		{
			list.set(index, max.get(index));
			sub(list, max, index - 1);
		}
	}
}
