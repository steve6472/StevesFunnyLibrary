package steve6472.funnylib.blocks.stateengine.properties;

import java.util.Arrays;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 02.07.2020
 * Project: StateTest
 *
 ***********************/
public abstract class Property<T extends Comparable<T>> implements IProperty<T>
{
	private final String name;

	public Property(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "Property{" + "name='" + name + "', possibleValues=" + Arrays.toString(getPossibleValues()) + '}';
	}


}
