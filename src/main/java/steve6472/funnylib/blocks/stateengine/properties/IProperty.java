package steve6472.funnylib.blocks.stateengine.properties;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 02.07.2020
 * Project: StateTest
 *
 ***********************/
public interface IProperty<T extends Comparable<T>>
{
	T[] getPossibleValues();

	String getName();

	String toString(Comparable<?> obj);

	T fromString(String str);
}
