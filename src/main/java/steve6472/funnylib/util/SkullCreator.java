package steve6472.funnylib.util;

// Copyright (c) 2017 deanveloper

/*
 * MIT License
 * Copyright (c) 2017 deanveloper
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;

/**
 * A library for the Bukkit API to create player skulls
 * from names, base64 strings, and texture URLs.
 * <p>
 * Does not use any NMS code, and should work across all versions.
 *
 * @author deanveloper on 12/28/2016.
 */
public class SkullCreator
{

	private SkullCreator()
	{
	}

	// some reflection stuff to be used when setting a skull's profile
	private static Field blockProfileField;
	private static Method metaSetProfileMethod;
	private static Field metaProfileField;

	/**
	 * Creates a player skull, should work in both legacy and new Bukkit APIs.
	 */
	public static ItemStack createSkull()
	{
		return new ItemStack(Material.PLAYER_HEAD);
	}

	/**
	 * Creates a player skull item with the skin based on a player's name.
	 *
	 * @param name The Player's name.
	 * @return The head of the Player.
	 * @deprecated names don't make for good identifiers.
	 */
	public static ItemStack itemFromName(String name)
	{
		return itemWithName(createSkull(), name);
	}

	/**
	 * Creates a player skull item with the skin based on a player's UUID.
	 *
	 * @param id The Player's UUID.
	 * @return The head of the Player.
	 */
	public static ItemStack itemFromUuid(UUID id)
	{
		return itemWithUuid(createSkull(), id);
	}

	/**
	 * Creates a player skull item with the skin at a Mojang URL.
	 *
	 * @param url The Mojang URL.
	 * @return The head of the Player.
	 */
	public static ItemStack itemFromUrl(String url)
	{
		return itemWithUrl(createSkull(), url);
	}

	/**
	 * Creates a player skull item with the skin based on a base64 string.
	 *
	 * @param base64 The Base64 string.
	 * @return The head of the Player.
	 */
	public static ItemStack itemFromBase64(String base64)
	{
		return itemWithBase64(createSkull(), base64);
	}

	/**
	 * Modifies a skull to use the skin of the player with a given name.
	 *
	 * @param item The item to apply the name to. Must be a player skull.
	 * @param name The Player's name.
	 * @return The head of the Player.
	 * @deprecated names don't make for good identifiers.
	 */
	@Deprecated
	public static ItemStack itemWithName(ItemStack item, String name)
	{
		notNull(item, "item");
		notNull(name, "name");

		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(name);
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Modifies a skull to use the skin of the player with a given UUID.
	 *
	 * @param item The item to apply the name to. Must be a player skull.
	 * @param id   The Player's UUID.
	 * @return The head of the Player.
	 */
	public static ItemStack itemWithUuid(ItemStack item, UUID id)
	{
		notNull(item, "item");
		notNull(id, "id");

		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Modifies a skull to use the skin at the given Mojang URL.
	 *
	 * @param item The item to apply the skin to. Must be a player skull.
	 * @param url  The URL of the Mojang skin.
	 * @return The head associated with the URL.
	 */
	public static ItemStack itemWithUrl(ItemStack item, String url)
	{
		notNull(item, "item");
		notNull(url, "url");

		return itemWithBase64(item, urlToBase64(url));
	}

	/**
	 * Modifies a skull to use the skin based on the given base64 string.
	 *
	 * @param item   The ItemStack to put the base64 onto. Must be a player skull.
	 * @param base64 The base64 string containing the texture.
	 * @return The head with a custom texture.
	 */
	public static ItemStack itemWithBase64(ItemStack item, String base64)
	{
		notNull(item, "item");
		notNull(base64, "base64");

		if (!(item.getItemMeta() instanceof SkullMeta))
		{
			return null;
		}
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		mutateItemMeta(meta, base64);
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Sets the block to a skull with the given name.
	 *
	 * @param block The block to set.
	 * @param name  The player to set it to.
	 * @deprecated names don't make for good identifiers.
	 */
	@Deprecated
	public static void blockWithName(Block block, String name)
	{
		notNull(block, "block");
		notNull(name, "name");

		Skull state = (Skull) block.getState();
		state.setOwningPlayer(Bukkit.getOfflinePlayer(name));
		state.update(false, false);
	}

	/**
	 * Sets the block to a skull with the given UUID.
	 *
	 * @param block The block to set.
	 * @param id    The player to set it to.
	 */
	public static void blockWithUuid(Block block, UUID id)
	{
		notNull(block, "block");
		notNull(id, "id");

		setToSkull(block);
		Skull state = (Skull) block.getState();
		state.setOwningPlayer(Bukkit.getOfflinePlayer(id));
		state.update(false, false);
	}

	/**
	 * Sets the block to a skull with the skin found at the provided mojang URL.
	 *
	 * @param block The block to set.
	 * @param url   The mojang URL to set it to use.
	 */
	public static void blockWithUrl(Block block, String url)
	{
		notNull(block, "block");
		notNull(url, "url");

		blockWithBase64(block, urlToBase64(url));
	}

	/**
	 * Sets the block to a skull with the skin for the base64 string.
	 *
	 * @param block  The block to set.
	 * @param base64 The base64 to set it to use.
	 */
	public static void blockWithBase64(Block block, String base64)
	{
		notNull(block, "block");
		notNull(base64, "base64");

		setToSkull(block);
		Skull state = (Skull) block.getState();
		mutateBlockState(state, base64);
		state.update(false, false);
	}

	private static void setToSkull(Block block)
	{
		block.setType(Material.PLAYER_HEAD, false);
	}

	private static void notNull(Object o, String name)
	{
		if (o == null)
		{
			throw new NullPointerException(name + " should not be null!");
		}
	}

	private static String urlToBase64(String url)
	{

		URI actualUrl;
		try
		{
			actualUrl = new URI(url);
		} catch (URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
		String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl + "\"}}}";
		return Base64.getEncoder().encodeToString(toEncode.getBytes());
	}

	private static GameProfile makeProfile(String b64)
	{
		// random uuid based on the b64 string
		UUID id = new UUID(b64.substring(b64.length() - 20).hashCode(), b64.substring(b64.length() - 10).hashCode());
		GameProfile profile = new GameProfile(id, "Player");
		profile.getProperties().put("textures", new Property("textures", b64));
		return profile;
	}

	private static void mutateBlockState(Skull block, String b64)
	{
		try
		{
			if (blockProfileField == null)
			{
				blockProfileField = block.getClass().getDeclaredField("profile");
				blockProfileField.setAccessible(true);
			}
			blockProfileField.set(block, makeProfile(b64));
		} catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private static void mutateItemMeta(SkullMeta meta, String b64)
	{
		try
		{
			if (metaSetProfileMethod == null)
			{
				metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
				metaSetProfileMethod.setAccessible(true);
			}
			metaSetProfileMethod.invoke(meta, makeProfile(b64));
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			// if in an older API where there is no setProfile method,
			// we set the profile field directly.
			try
			{
				if (metaProfileField == null)
				{
					metaProfileField = meta.getClass().getDeclaredField("profile");
					metaProfileField.setAccessible(true);
				}
				metaProfileField.set(meta, makeProfile(b64));

			} catch (NoSuchFieldException | IllegalAccessException ex2)
			{
				ex2.printStackTrace();
			}
		}
	}
}
