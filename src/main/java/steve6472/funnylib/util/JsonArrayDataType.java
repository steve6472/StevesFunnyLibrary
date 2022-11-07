package steve6472.funnylib.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonArrayDataType implements PersistentDataType<String, JSONArray>
{
	public static final PersistentDataType<String, JSONArray> JSON_ARRAY = new JsonArrayDataType();
	
	@Override
	public @NotNull Class<String> getPrimitiveType()
	{
		return String.class;
	}

	@Override
	public @NotNull Class<JSONArray> getComplexType()
	{
		return JSONArray.class;
	}

	@Override
	public @NotNull String toPrimitive(JSONArray complex, @NotNull PersistentDataAdapterContext context)
	{
		return "\"" + complex.toString() + "\"";
	}

	@Override
	public @NotNull JSONArray fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context)
	{
		if (primitive.startsWith("\"") && primitive.endsWith("\""))
			return new JSONArray(primitive.substring(1, primitive.length() - 1));
		else
			return new JSONArray(primitive);
	}
}