package steve6472.funnylib.context;

/**
 * Created by steve6472
 * Date: 11/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public enum UseType
{
	LEFT, RIGHT;

	public boolean isLeft()
	{
		return this == LEFT;
	}

	public boolean isRight()
	{
		return this == RIGHT;
	}
}
