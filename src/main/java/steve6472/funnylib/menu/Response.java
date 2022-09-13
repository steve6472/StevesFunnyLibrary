package steve6472.funnylib.menu;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Response
{
	private static final Response CANCEL = new Response();
	private static final Response ALLOW = new Response();
	private static final Response EXIT = new Response();

	private MenuBuilder redirect;

	public static Response cancel()
	{
		return CANCEL;
	}

	public static Response allow()
	{
		return ALLOW;
	}

	public static Response exit()
	{
		return EXIT;
	}

	public static Response redirect(MenuBuilder menu)
	{
		Response response = new Response();
		response.redirect = menu;
		return response;
	}

	/*
	 * Getters
	 */

	public MenuBuilder getRedirect()
	{
		return redirect;
	}
}
