package steve6472.funnylib.blocks.stateengine;

import steve6472.funnylib.blocks.stateengine.properties.IProperty;

import java.util.ArrayList;
import java.util.List;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 3/20/2021
 * Project: GameMaker
 *
 ***********************/
public abstract class StateObject
{
	private State defaultState;

	public StateObject()
	{
		generateStates();
	}

	private void generateStates()
	{
		List<IProperty<?>> properties = new ArrayList<>();
		fillStates(properties);
		StateBuilder.generateStates(this, properties);
	}

	public void setDefaultState(State state)
	{
		this.defaultState = state;
	}

	public State getDefaultState()
	{
		return defaultState;
	}

	public void fillStates(List<IProperty<?>> properties)
	{
	}
}
