package steve6472.funnylib.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class JsonDataType implements PersistentDataType<String, JSONObject>
{
	public static final PersistentDataType<String, JSONObject> JSON = new JsonDataType();
	
	@Override
	public @NotNull Class<String> getPrimitiveType()
	{
		return String.class;
	}

	@Override
	public @NotNull Class<JSONObject> getComplexType()
	{
		return JSONObject.class;
	}

	@Override
	public @NotNull String toPrimitive(JSONObject complex, @NotNull PersistentDataAdapterContext context)
	{
		return complex.toString();
	}

	@Override
	public @NotNull JSONObject fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context)
	{
		return new JSONObject(primitive);
	}
}