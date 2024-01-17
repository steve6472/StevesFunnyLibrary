package steve6472.funnylib.util;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Created by steve6472
 * Date: 12/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class TransformationBuilder
{
	private final Transformation transformation;

	private TransformationBuilder()
	{
		transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf());
	}

	public static TransformationBuilder create()
	{
		return new TransformationBuilder();
	}

	public Transformation build()
	{
		return transformation;
	}

	/*
	 * Main setters
	 */

	public TransformationBuilder setTranslation(Vector3f vec3f)
	{
		transformation.getTranslation().set(vec3f);
		return this;
	}

	public TransformationBuilder setScale(Vector3f scale)
	{
		transformation.getScale().set(scale);
		return this;
	}

	public TransformationBuilder setLeftRotation(Quaternionf quat)
	{
		transformation.getLeftRotation().set(quat);
		return this;
	}

	public TransformationBuilder setRightRotation(Quaternionf quat)
	{
		transformation.getRightRotation().set(quat);
		return this;
	}

	/*
	 * float setters
	 */

	public TransformationBuilder setTranslation(float x, float y, float z)
	{
		transformation.getTranslation().set(x, y, z);
		return this;
	}

	public TransformationBuilder setScale(float x, float y, float z)
	{
		transformation.getScale().set(x, y, z);
		return this;
	}

	public TransformationBuilder setLeftRotation(float x, float y, float z, float w)
	{
		transformation.getLeftRotation().set(x, y, z, w);
		return this;
	}

	public TransformationBuilder setRightRotation(float x, float y, float z, float w)
	{
		transformation.getRightRotation().set(x, y, z, w);
		return this;
	}

	/*
	 * modifiers
	 */

	public TransformationBuilder translate(float x, float y, float z)
	{
		transformation.getTranslation().add(x, y, z);
		return this;
	}

	public TransformationBuilder scale(float scalar)
	{
		transformation.getScale().mul(scalar);
		return this;
	}
}
