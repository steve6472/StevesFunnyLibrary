package steve6472.standalone.interactable.ex;

import steve6472.funnylib.json.INBT;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;
import steve6472.standalone.interactable.ex.elements.IElementType;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class Expression implements INBT
{
	protected Expression parent;
	protected int width;
	protected int height;
	protected Type type;

	public Expression(Type type, int width, int height)
	{
		this.type = type;
		this.width = width;
		this.height = height;
	}

	public Expression(Type type)
	{
		this(type, 0, 0);
	}

	public abstract ExpResult execute(ExpContext context);

	public abstract void build(ExpBuilder builder, int x, int y);

	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		return Response.cancel();
	}

	public void createPopup(MenuBuilder builder)
	{

	}

	public Type getType()
	{
		return type;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public abstract IElementType[] getTypes();

	public abstract String stringify(boolean flag);

	public enum Type
	{
		CONTROL,
		HIDDEN,
		CUSTOM,

		BOOL,
		INT,
		STRING,

//		DOUBLE
	}
}
