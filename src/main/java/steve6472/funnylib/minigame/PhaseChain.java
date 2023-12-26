package steve6472.funnylib.minigame;

import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IllusionTheDev
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PhaseChain
{
	private final List<AbstractGamePhase> chain = new LinkedList<>();
	private AbstractGamePhase currentPhase;
	private boolean isDisposed = false;

	public void addPhase(AbstractGamePhase phase)
	{
		chain.add(phase);
		phase.onEnd(() ->
		{
			if (isDisposed) return;
			tryAdvance(phase);
		}); // When the phase ends, we try advancing to the next one
		phase.onCancel(() ->
		{
			if (isDisposed) return;
			tryRetreat(phase);
		}); // When the phase is cancelled, we try going back to the previous one
	}

	public AbstractGamePhase getCurrentPhase()
	{
		return currentPhase;
	}

	public void tryAdvance(AbstractGamePhase previous)
	{
		// Prevents accidental recursion
		if (currentPhase != previous)
		{
			return;
		}

		AbstractGamePhase next = getNext(previous);

		// End of the chain or the chain is broken somehow
		if (next == null)
		{
			dispose();
			throw new RuntimeException("Phase Chain is somehow broken, next phase not found");
		}

		setPhase(next);
	}

	public void tryRetreat(AbstractGamePhase current)
	{
		if (currentPhase != current)
		{
			return;
		}

		AbstractGamePhase previous = getPrevious(current);

		if (previous == null)
		{
			current.dispose();
			current.startPhase(); // We dispose and start again, which reboots the phase
			return;
		}

		setPhase(previous);
	}

	public void start()
	{
		setPhase(chain.get(0));
	}

	public void dispose()
	{
		isDisposed = true;
		currentPhase.endPhase();
		for (AbstractGamePhase phase : chain)
		{
			if (phase != currentPhase)
				phase.dispose();
		}

		chain.clear();
		currentPhase = null;
	}

	// internal methods
	private void setPhase(AbstractGamePhase current)
	{
//		Bukkit.broadcastMessage("New phase: §2" + (current == null ? "null" : current.getClass().getSimpleName()) + "§r (from §7" + (currentPhase == null ? "null" : currentPhase.getClass().getSimpleName()) + "§r)");
		currentPhase = current;
		current.startPhase();
	}

	private AbstractGamePhase getNext(AbstractGamePhase current)
	{
		if (current == null && !chain.isEmpty())
		{
			return chain.get(0);
		}

		int index = chain.indexOf(current);

		if (index == -1 || index == chain.size() - 1)
		{
			return null; // If it's the last phase or if the phase isn't even in the chain anymore
		}

		return chain.get(index + 1);
	}

	private AbstractGamePhase getPrevious(AbstractGamePhase current)
	{
		int index = chain.indexOf(current);

		if (index < 1) // If it's 0 or -1, meaning there's no previous or it's not even in the chain
			return null;

        return chain.get(index -1);
	}

	public boolean hasMorePhases()
	{
		return getNext(currentPhase) != null;
	}
}