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

	private ItemStack setItemToCursor;
	private MenuBuilder redirect;
	private ArbitraryData redirectData;

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

	public static Response redirect(MenuBuilder menu)
	{
		Response response = new Response();
		response.redirect = menu;
		return response;
	}

	public static Response redirect(MenuBuilder menu, ArbitraryData redirectData)
	{
		Response response = new Response();
		response.redirect = menu;
		response.redirectData = redirectData;
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

	public MenuBuilder getRedirect()
	{
		return redirect;
	}

	public ArbitraryData getRedirectData()
	{
		return redirectData;
	}

	public ItemStack getSetItemToCursor()
	{
		return setItemToCursor;
	}
}
