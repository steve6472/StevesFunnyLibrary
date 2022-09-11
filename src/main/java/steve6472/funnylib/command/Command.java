package steve6472.funnylib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by steve6472
 * On date: 6/9/2022
 * Project: AkmaEventPlugin
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Command
{
	String overrideName() default "";

	boolean requireOp() default true;
}
