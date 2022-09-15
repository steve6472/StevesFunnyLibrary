package steve6472.funnylib.json.codec.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 4/20/2021
 * Project: StevesGameEngine
 *
 ***********************/
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface SaveFloat
{
	float defVal() default 0.0f;
}
