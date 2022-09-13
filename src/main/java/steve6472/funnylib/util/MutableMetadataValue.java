package steve6472.funnylib.util;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steve6472.funnylib.FunnyLib;

/**********************
 * Created by steve6472
 * On date: 6/5/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class MutableMetadataValue<T> implements MetadataValue
{
	private T value;

	public MutableMetadataValue(T value)
	{
		this.value = value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	@Nullable
	@Override
	public T value()
	{
		return value;
	}

	@Override
	public int asInt()
	{
		return 0;
	}

	@Override
	public float asFloat()
	{
		return 0;
	}

	@Override
	public double asDouble()
	{
		return 0;
	}

	@Override
	public long asLong()
	{
		return 0;
	}

	@Override
	public short asShort()
	{
		return 0;
	}

	@Override
	public byte asByte()
	{
		return 0;
	}

	@Override
	public boolean asBoolean()
	{
		return false;
	}

	@NotNull
	@Override
	public String asString()
	{
		return value.toString();
	}

	@Nullable
	@Override
	public Plugin getOwningPlugin()
	{
		return FunnyLib.getPlugin();
	}

	@Override
	public void invalidate()
	{

	}
}
