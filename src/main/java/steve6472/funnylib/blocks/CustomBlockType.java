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
public class CustomBlockType implements PersistentDataType<String, CustomBlock>
{
	public static final PersistentDataType<String, CustomBlock> BLOCK = new CustomBlockType();

	@NotNull
	@Override
	public Class<String> getPrimitiveType()
	{
		return String.class;
	}

	@NotNull
	@Override
	public Class<CustomBlock> getComplexType()
	{
		return CustomBlock.class;
	}

	@NotNull
	@Override
	public String toPrimitive(@NotNull CustomBlock complex, @NotNull PersistentDataAdapterContext context)
	{
		return complex.id();
	}

	@NotNull
	@Override
	public CustomBlock fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context)
	{
		return Blocks.getCustomBlockById(primitive);
	}
}
