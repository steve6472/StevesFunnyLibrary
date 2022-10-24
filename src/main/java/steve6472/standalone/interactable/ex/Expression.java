package steve6472.standalone.interactable.ex;

import org.json.JSONObject;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class Expression
{
	protected Expression parent;

	public abstract ExpResult execute(ExpContext context);

	public abstract void build(ExpBuilder builder, int x, int y);

	public abstract int getHeight();
	public abstract int getWidth();

	public Response action(IElementType type, Click click, Menu menu, Expression expression) { return Response.cancel(); }

	public void createPopup(MenuBuilder builder)
	{

	}

	public abstract Type getType();

	public abstract IElementType[] getTypes();

	public abstract String stringify(boolean flag);

	public abstract void save(JSONObject json);

	public enum Type
	{
		CONTROL,
		HIDDEN,

		BOOL,
		INT,
		STRING,

//		DOUBLE
	}
}
