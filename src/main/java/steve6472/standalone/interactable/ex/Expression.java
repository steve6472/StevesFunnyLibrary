package steve6472.standalone.interactable.ex;

import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class Expression
{
	public abstract ExpResult execute(ExpContext context);

	public abstract void build(ExpBuilder builder, int x, int y);

	public abstract int getHeight();
	public abstract int getWidth();

	public Response action(IElementType type, Click click, Menu menu, Expression expression) { return Response.cancel(); }

	public abstract IElementType[] getTypes();
}
