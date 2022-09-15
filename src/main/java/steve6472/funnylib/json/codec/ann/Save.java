package steve6472.funnylib.json.codec.ann;

import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.json.codec.codecs.ObjectCodec;

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
public @interface Save
{
	Class<? extends Codec<?>> type() default ObjectCodec.class;
}
