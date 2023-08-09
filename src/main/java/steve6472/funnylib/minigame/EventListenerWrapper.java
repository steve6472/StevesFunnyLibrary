package steve6472.funnylib.minigame;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class EventListenerWrapper<E extends Event> implements Listener
{
	public Consumer<E> event;
}
