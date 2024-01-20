package steve6472.funnylib.workdistro;

/**
 * Created by steve6472
 * Date: 1/20/2024
 * Project: StevesFunnyLibrary <br>
 */
public interface Workload
{
	void compute();

	default boolean shouldBeRescheduled()
	{
		return false;
	}
}
