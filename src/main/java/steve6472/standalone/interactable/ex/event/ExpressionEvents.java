package steve6472.standalone.interactable.ex.event;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.ExpItems;
import steve6472.standalone.interactable.ex.impl.events.PlayerAreaExpEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ExpressionEvents
{
	public record EventEntry(ItemStack icon, String id, Supplier<ExpressionEvent> constructor) {}

	public static final Map<Class<? extends ExpressionEvent>, EventEntry> ENTRY_BY_CLASS = new HashMap<>();
	public static final Map<String, Class<? extends ExpressionEvent>> CLASS_BY_ID = new HashMap<>();

	static
	{
		register(ExpItems.PLAYER_IN_AREA.newItemStack(), "player_enter_area", PlayerAreaExpEvent.class, PlayerAreaExpEvent::new);
	}

	public static void register(ItemStack icon, String id, Class<? extends ExpressionEvent> clazz, Supplier<ExpressionEvent> constructor)
	{
		EventEntry entry = new EventEntry(icon, id, constructor);
		ENTRY_BY_CLASS.put(clazz, entry);
		CLASS_BY_ID.put(id, clazz);
	}

	public static NBT saveEvent(NBT nbt, ExpressionEvent event)
	{
		if (event == null)
			return nbt;

		nbt.setString("event_id", ENTRY_BY_CLASS.get(event.getClass()).id());
		event.toNBT(nbt);
		return nbt;
	}

	public static ExpressionEvent loadEvent(NBT nbt)
	{
		if (nbt == null || nbt.isEmpty())
			return null;

		if (!nbt.hasString("event_id"))
			return null;

		String eventId = nbt.getString("event_id");
		Class<? extends ExpressionEvent> key = CLASS_BY_ID.get(eventId);
		EventEntry expressionEntry = ENTRY_BY_CLASS.get(key);
		ExpressionEvent event = expressionEntry.constructor.get();
		event.fromNBT(nbt);
		return event;
	}
}
