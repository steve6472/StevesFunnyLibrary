package steve6472.funnylib.blocks;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;

import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CustomBlockStateType implements PersistentDataType<String, State>
{
	public static final PersistentDataType<String, State> BLOCK_STATE = new CustomBlockStateType();

	@NotNull
	@Override
	public Class<String> getPrimitiveType()
	{
		return String.class;
	}

	@NotNull
	@Override
	public Class<State> getComplexType()
	{
		return State.class;
	}

	@NotNull
	@Override
	public String toPrimitive(@NotNull State complex, @NotNull PersistentDataAdapterContext context)
	{
		JSONObject json = new JSONObject();
		CustomBlock block = ((CustomBlock) complex.getObject());
		json.put("id", block.id());
		if (complex.getProperties() != null)
		{
			complex.getProperties().forEach((k, v) -> json.put(k.getName(), k.toString(v)));
		}
		return json.toString();
	}

	@NotNull
	@Override
	public State fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context)
	{
		JSONObject json = new JSONObject(primitive);
		State defaultState = Blocks.getCustomBlockById(json.getString("id")).getDefaultState();
		State state = defaultState;
		if (state.getProperties() != null)
		{
			for (IProperty iProperty : defaultState.getProperties().keySet())
			{
				String string = json.getString(iProperty.getName());
				state = state.with(iProperty, iProperty.fromString(string));
			}
		}
		return state;
	}
}
