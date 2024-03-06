package steve6472.funnylib.minigame.config;

import org.apache.logging.log4j.util.TriConsumer;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 1/24/2024
 * Project: StevesFunnyLibrary <br>
 */
public class RedirectGameConfiguration<T> extends GameConfiguration
{
	private final GameConfiguration parent;
	private final TriConsumer<Value<T>, T, GameConfiguration> redirect;
	public Function<Value<?>, T> getValueFunc;

	public RedirectGameConfiguration(GameConfiguration parent, ConfigTypeRegistry configTypeRegistry, TriConsumer<Value<T>, T, GameConfiguration> redirect)
	{
		super(configTypeRegistry, parent.minigameId, parent.init);
		this.parent = parent;
		this.redirect = redirect;
	}

	@Override
	public void setValue(Value<?> value, Object object)
	{
		redirect.accept((Value<T>) value, (T) object, parent);
	}

	@Override
	public GameConfiguration registerValue(Value<?> value)
	{
		throw new RuntimeException("Unsupported");
	}

	@Override
	public <T> T getValue(Value<T> value)
	{
		if (getValueFunc == null)
			throw new RuntimeException("Unsupported");
		else
			return (T) getValueFunc.apply(value);
	}

	@Override
	public Map<Value<?>, Object> getValues()
	{
		throw new RuntimeException("Unsupported");
	}

	@Override
	public void save()
	{
		throw new RuntimeException("Unsupported");
	}

	@Override
	public void load()
	{
		throw new RuntimeException("Unsupported");
	}
}
