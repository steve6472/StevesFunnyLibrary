package steve6472.funnylib.blocks.stateengine.properties;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 03.07.2020
 * Project: CaveGame
 *
 ***********************/
public class IntProperty extends Property<Integer>
{
	private final Integer[] possibleValues;

	public static IntProperty create(String name, int min, int max)
	{
		Integer[] vals = new Integer[max - min + 1];
		for (int i = 0; i < vals.length; i++)
		{
			vals[i] = i + min;
		}
		return new IntProperty(name, vals);
	}

	private IntProperty(String name, Integer[] possibleValues)
	{
		super(name);
		this.possibleValues = possibleValues;
	}

	@Override
	public Integer[] getPossibleValues()
	{
		return possibleValues;
	}
}
