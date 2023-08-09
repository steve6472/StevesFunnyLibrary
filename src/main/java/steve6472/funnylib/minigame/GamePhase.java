package steve6472.funnylib.minigame;

/**
 * Created by IllusionTheDev
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface GamePhase
{
	// Start method
	void startPhase();

	// Override if you want, this runs every tick
	default void tick()
	{
	}

	// Called when the phase ends
	void endPhase();

	// Called when the phase is cancelled
	default void cancel()
	{
		endPhase();
	}
}