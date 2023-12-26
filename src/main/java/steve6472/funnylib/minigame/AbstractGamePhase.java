package steve6472.funnylib.minigame;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IllusionTheDev
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class AbstractGamePhase extends EventHolder
{
	protected final Game game;
	private final Set<AbstractGamePhase> components = new HashSet<>();
	// TODO: entities (store by uuid so spigot server is not angery)

	private Runnable onStart, onEnd, onCancel;

	public AbstractGamePhase(Game game)
	{
		super(game.plugin);
		this.game = game;
	}

	public AbstractGamePhase addComponent(AbstractGamePhase component)
	{
		this.components.add(component);
		return this;
	}

	// Event state notification methods

	public void onStart(Runnable task)
	{
		this.onStart = task;
	}

	public void onCancel(Runnable task)
	{
		// This might be called by external factors, make sure to not just set a new task, but to "add" to the old one
		this.onCancel = task;
	}

	public void onEnd(Runnable task)
	{
		this.onEnd = task;
	}

	// Event state methods

	public abstract void start();
	public abstract void end();

	public void tick()
	{

	}

	public final void startPhase()
	{
		start();
		components.forEach(AbstractGamePhase::startPhase);
	}

	public final void endPhase()
	{
		// Call the end task
		end();
		dispose();
		components.forEach(AbstractGamePhase::endPhase);

		if (onEnd != null)
			onEnd.run();
	}

	public final void cancel()
	{
		// Call the cancel task
		dispose();
		components.forEach(AbstractGamePhase::cancel);
		if (onCancel != null)
			onCancel.run();
	}
}