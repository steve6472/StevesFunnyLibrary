package steve6472.standalone.interactable.ex;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpResult
{
	public static final ExpResult PASS = new ExpResult(null);
	public static final ExpResult DELAY = new ExpResult(null);

	private Object obj;

	public ExpResult(Object obj)
	{
		this.obj = obj;
	}

	public boolean asBoolean()
	{
		return Boolean.TRUE.equals(obj);
	}

	public int asInt()
	{
		return (int) obj;
	}

	public String asString()
	{
		return (String) obj;
	}
}
