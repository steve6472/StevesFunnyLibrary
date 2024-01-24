package steve6472.funnylib.menu;

import org.bukkit.inventory.ItemStack;

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
	private static final Response CLEAR_ITEM_FROM_CURSOR = new Response();
	private static final Response BACK = new Response();
	private static final Response BACK_RELOAD = new Response();

	private ItemStack setItemToCursor;
	private Menu redirect;

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

	public static Response clearItemFromCursor()
	{
		return CLEAR_ITEM_FROM_CURSOR;
	}

	public static Response back()
	{
		return BACK;
	}

	public static Response backReload()
	{
		return BACK_RELOAD;
	}

	public static Response redirect(Menu menu)
	{
		Response response = new Response();
		response.redirect = menu;
		return response;
	}

	public static Response setItemToCursor(ItemStack itemStack)
	{
		Response response = new Response();
		response.setItemToCursor = itemStack;
		return response;
	}

	/*
	 * Getters
	 */

	public Menu getRedirect()
	{
		return redirect;
	}

	public ItemStack getSetItemToCursor()
	{
		return setItemToCursor;
	}
}
