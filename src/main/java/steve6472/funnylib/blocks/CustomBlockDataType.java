package steve6472.funnylib.blocks;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import steve6472.funnylib.json.codec.Codec;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CustomBlockDataType implements PersistentDataType<String, BlockData>
{
	public static final PersistentDataType<String, BlockData> BLOCK_DATA = new CustomBlockDataType();

	@NotNull
	@Override
	public Class<String> getPrimitiveType()
	{
		return String.class;
	}

	@NotNull
	@Override
	public Class<BlockData> getComplexType()
	{
		return BlockData.class;
	}

	@NotNull
	@Override
	public String toPrimitive(@NotNull BlockData complex, @NotNull PersistentDataAdapterContext context)
	{
		JSONObject blockData = Codec.save(complex);
		JSONObject json = new JSONObject();
		json.put("blockData", blockData);
		json.put("dataClass", complex.getClass().getName());
		json.put("blockId", complex.getBlock().id());
		return json.toString();
	}

	@NotNull
	@Override
	public BlockData fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context)
	{
		JSONObject json = new JSONObject(primitive);
		String classPath = json.getString("dataClass");
		try
		{
			Class<?> clazz = Class.forName(classPath);
			Object o = clazz.getConstructor().newInstance();
			BlockData blockData = (BlockData) Codec.load(o, json.getJSONObject("blockData"));

			CustomBlock block = Blocks.getCustomBlockById(json.getString("blockId"));
			blockData.setLogic(block);

			return blockData;
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
		         InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
