package steve6472.funnylib.item.events;

import steve6472.funnylib.context.PlayerItemContext;

/**********************
 * Created by steve6472
 * On date: 4/12/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ConsumeEvent
{
	default void consumed(PlayerItemContext context) {}
}
