package steve6472.funnylib.command;

import java.lang.annotation.*;

/**
 * Created by steve6472
 * On date: 6/9/2022
 * Project: AkmaEventPlugin
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Repeatable(Usages.class)
public @interface Usage
{
	String value();
}
