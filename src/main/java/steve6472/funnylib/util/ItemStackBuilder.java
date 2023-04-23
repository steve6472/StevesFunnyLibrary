package steve6472.funnylib.util;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openjdk.jol.vm.VM;
import steve6472.funnylib.FunnyLib;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * On date: 1/3/2021
 * Project: RBS
 *
 */
public class ItemStackBuilder
{
	private ItemStack item;
	private ItemMeta meta;
	private NBT customData;

	private ItemStackBuilder(Material material)
	{
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
		if (meta == null)
			throw new RuntimeException("Item meta is null! Pls fix thx");
		customData = NBT.create(item, meta);
	}

	private ItemStackBuilder(ItemStack itemStack)
	{
		this.item = itemStack;
		this.meta = itemStack.getItemMeta();
		if (meta == null)
			throw new RuntimeException("Item meta is null! Pls fix thx");
		customData = NBT.create(item, meta);
	}

	public ItemStackBuilder(NBT data)
	{
		this.item = data.getItemStack();
		this.meta = data.getMeta();
		if (meta == null)
			throw new RuntimeException("Item meta is null! Pls fix thx");
		customData = data;
	}

	/*
	 * Creators
	 */

	public static ItemStack quick(Material material, String name)
	{
		return ItemStackBuilder.create(material).setName(JSONMessage.create(name)).buildItemStack();
	}

	public static ItemStack quick(Material material, String name, ChatColor color)
	{
		return ItemStackBuilder.create(material).setName(JSONMessage.create(name).color(color)).buildItemStack();
	}

	public static ItemStack quick(Material material, String name, ChatColor color, int count)
	{
		return ItemStackBuilder.create(material).setName(JSONMessage.create(name).color(color)).count(count).buildItemStack();
	}

	public static ItemStack quick(Material material, String name, String color)
	{
		return ItemStackBuilder.create(material).setName(JSONMessage.create(name).color(color)).buildItemStack();
	}

	public static ItemStack quick(Material material, String name, Color color)
	{
		return ItemStackBuilder.create(material).setName(JSONMessage.create(name).color("#" + Integer.toHexString(color.asRGB()))).buildItemStack();
	}

	public static ItemStackBuilder create(Material material)
	{
		return new ItemStackBuilder(material);
	}

	public static ItemStackBuilder editNonStatic(ItemStack bukkitItemStack)
	{
		return new ItemStackBuilder(bukkitItemStack);
	}

	public static ItemStackBuilder editNonStatic(NBT data)
	{
		return new ItemStackBuilder(data);
	}

	/*
	 * Static builder
	 */

	private static final ItemStackBuilder STATIC_STACK_BUILDER = new ItemStackBuilder(Material.STONE);

	public static ItemStackBuilder edit(ItemStack itemStack)
	{
		STATIC_STACK_BUILDER.staticEdit(itemStack, null);
		return STATIC_STACK_BUILDER;
	}

	public static ItemStackBuilder edit(NBT customData)
	{
		STATIC_STACK_BUILDER.staticEdit(customData.getItemStack(), customData);
		return STATIC_STACK_BUILDER;
	}

	private void staticEdit(ItemStack itemStack, NBT customData)
	{
		if (customData == null)
		{
			this.item = itemStack;
			this.meta = item.getItemMeta();
			if (meta == null)
				throw new RuntimeException("Item meta is null! Pls fix thx");
			this.customData = NBT.create(item, meta);
		} else
		{
			Preconditions.checkTrue(itemStack.equals(customData.getItemStack()), "Item mismatch between passed itemStack and customData");
			this.item = itemStack;
			this.meta = customData.getMeta();
			this.customData = customData;
		}
	}

	/*
	 *
	 */

	public ItemMeta meta()
	{
		return meta;
	}

	public NBT nbt()
	{
		return customData;
	}

	public ItemStackBuilder setType(Material material)
	{
		item.setType(material);
		return this;
	}

	public ItemStackBuilder count(int count)
	{
		item.setAmount(count);
		return this;
	}

	public ItemStackBuilder glow()
	{
		addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		setHideFlags(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	public ItemStackBuilder removeGlow()
	{
		removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
		removeHideFlags(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	public ItemStackBuilder unbreakableHidden()
	{
		setUnbreakable(true);
		setHideFlags(ItemFlag.HIDE_UNBREAKABLE);
		return this;
	}

	@Deprecated
	public ItemStackBuilder playerHead(String player)
	{
		if (meta instanceof SkullMeta skull)
		{
			skull.setOwner(player);
		}

		return this;
	}

	public ItemStackBuilder playerHead(UUID player)
	{
		if (meta instanceof SkullMeta skull)
		{
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
			skull.setOwningPlayer(offlinePlayer);
		}

		return this;
	}

	/*
	 * Lore
	 */

	/**
	 * Transforms legacy chat colors string into JSON format
	 * @param string legacy string
	 * @return builder
	 */
	public ItemStackBuilder addLoreRaw(String string)
	{
		addLoreRaw(MiscUtil.legacyToJsonMessage(string));

		return this;
	}

	public ItemStackBuilder addLoreRaw(JSONMessage message)
	{
		item.setItemMeta(meta);
		SafeNMS.nmsFunction(() ->
		{
			NMS.addLore(item, message);
			meta = item.getItemMeta();
			customData.change(item, meta);
		}, () ->
		{
			meta = item.getItemMeta();
			customData.change(item, meta);

			if (meta == null)
				throw new RuntimeException("Item meta is null! Adding lore failed! Pls fix thx");

			List<String> lore = meta.getLore();
			if (lore == null)
			{
				lore = new ArrayList<>(1);
			}
			lore.add(message.toLegacy());
			meta.setLore(lore);
		});

		return this;
	}

	/**
	 *
	 * @param text lines of lore separated with \n for new line
	 * @return builder
	 * @deprecated use {@link #addLore(JSONMessage)} instead
	 */
	@Deprecated
	public ItemStackBuilder addLoreWithLines(String text)
	{
		String[] split = text.split("\n");
		for (String s : split)
		{
			addLore(s);
		}
		return this;
	}

	/**
	 * Adds lore lines
	 * @param json JSON formatted lore
	 * @return builder
	 */
	public ItemStackBuilder addLore(JSONMessage[] json)
	{
		for (JSONMessage m : json)
			addLore(m);
		return this;
	}

	private List<String> getLore()
	{
		return meta.getLore();
	}

	/**
	 * Adds lore line <br>
	 * If text does not start with a color, gray is selected as default
	 *
	 * @param text lore text
	 * @return builder
	 * @deprecated use {@link #addLore(JSONMessage)} instead
	 */
	@Deprecated
	public ItemStackBuilder addLore(String text)
	{
		Preconditions.checkNotEmpty(text);

		/*
		 * Check for color at start of string
		 * so we don't add useless color information
		 */
		if (text.charAt(0) == ChatColor.COLOR_CHAR)
		{
			return addLoreRaw(text);
		} else
		{
			return addLore(text, ChatColor.GRAY);
		}
	}

	/**
	 * Adds lore line with specified color
	 *
	 * @param text lore text
	 * @param color line color
	 * @return builder
	 * @deprecated use {@link #addLore(JSONMessage)} instead
	 */
	@Deprecated
	public ItemStackBuilder addLore(String text, ChatColor color)
	{
		return addLoreRaw(color + text);
	}

	/**
	 * Adds lore line
	 * @param json JSON formatted lore line
	 * @return
	 */
	public ItemStackBuilder addLore(JSONMessage json)
	{
		return addLoreRaw(json);
	}

	public ItemStackBuilder removeLoreLine(int line)
	{
		List<String> lore = meta.getLore();
		if (lore == null)
			return this;
		lore.remove(line);
		return this;
	}

	public ItemStackBuilder removeLastLoreLine()
	{
		List<String> lore = meta.getLore();
		if (lore == null)
			return this;
		lore.remove(lore.size() - 1);
		return this;
	}

	public ItemStackBuilder removeLore()
	{
		meta.setLore(new ArrayList<>());
		return this;
	}

	/*
	 * Name
	 */

	public ItemStackBuilder setName(String text, ChatColor color)
	{
		meta.setDisplayName(color + text);
		return this;
	}

	public ItemStackBuilder setName(String text)
	{
		return setName(text, ChatColor.WHITE);
	}

	public ItemStackBuilder setName(JSONMessage json)
	{
		meta.setDisplayName(json.toLegacy());
		return this;
	}

	public String getNameLegacy()
	{
		return meta.getDisplayName();
	}

	/*
	 *
	 */

	public ItemStackBuilder setArmorColor(int color)
	{
		if (meta instanceof LeatherArmorMeta lam)
		{
			lam.setColor(Color.fromRGB(color));
		}
		return this;
	}

	public ItemStackBuilder setDamage(int damage)
	{
		if (meta instanceof Damageable d)
		{
			d.setDamage(damage);
		}
		return this;
	}

	public ItemStackBuilder setCustomPotionColor(int color)
	{
		if (meta instanceof PotionMeta potionMeta)
		{
			potionMeta.setColor(Color.fromRGB(color));
		}
		return this;
	}

	public ItemStackBuilder setCompassLocation(Location location)
	{
		if (meta instanceof CompassMeta compassMeta)
		{
			compassMeta.setLodestone(location);
			compassMeta.setLodestoneTracked(false);
		}
		return this;
	}

	/**
	 * @return -1 if item is not Damageable
	 */
	public int getDamage()
	{
		if (meta instanceof Damageable d)
		{
			return d.getDamage();
		}
		return -1;
	}

	public ItemStackBuilder dealDamage(int damage)
	{
		return setDamage(getDamage() + damage);
	}

	public int getMaxDurability()
	{
		return item.getType().getMaxDurability();
	}

	/*
	 * Custom tags
	 */

	public static final String CUSTOM_ID = "custom_id";

	public ItemStackBuilder setCustomId(String id)
	{
		setString(CUSTOM_ID, id);
		return this;
	}

	public String getCustomId()
	{
		return getString(CUSTOM_ID);
	}

	public ItemStackBuilder customTagJson(String key, JSONObject value)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		dataContainer.set(new NamespacedKey(FunnyLib.getPlugin(), key), JsonDataType.JSON, value);
		return this;
	}

	public ItemStackBuilder customTagJsonArray(String key, JSONArray value)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		dataContainer.set(new NamespacedKey(FunnyLib.getPlugin(), key), JsonArrayDataType.JSON_ARRAY, value);
		return this;
	}

	public JSONObject getCustomJson(String key)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		return dataContainer.get(new NamespacedKey(FunnyLib.getPlugin(), key), JsonDataType.JSON);
	}

	public JSONArray getCustomJsonArray(String key)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		return dataContainer.get(new NamespacedKey(FunnyLib.getPlugin(), key), JsonArrayDataType.JSON_ARRAY);
	}

	// region NBT

	public int getInt(String key)
	{
		return customData.getInt(key);
	}

	public ItemStackBuilder setInt(String key, int value)
	{
		customData.setInt(key, value);
		return this;
	}

	public boolean hasInt(String key)
	{
		return customData.hasInt(key);
	}

	public byte getByte(String key)
	{
		return customData.getByte(key);
	}

	public ItemStackBuilder setByte(String key, byte value)
	{
		customData.setByte(key, value);
		return this;
	}

	public boolean hasByte(String key)
	{
		return customData.hasByte(key);
	}

	public String getString(String key)
	{
		return customData.getString(key);
	}

	public ItemStackBuilder setString(String key, String value)
	{
		customData.setString(key, value);
		return this;
	}

	public boolean hasString(String key)
	{
		return customData.hasString(key);
	}

	public NBT getCompound(String key)
	{
		return customData.getCompound(key);
	}

	public boolean hasCompound(String key)
	{
		return customData.hasCompound(key);
	}

	// endregion NBT

	/*
	 * Enchantments
	 */

	public ItemStackBuilder addEnchantment(Enchantment enchantment, int level)
	{
		meta.addEnchant(enchantment, level, true);
		return this;
	}

	public ItemStackBuilder removeEnchantment(Enchantment enchantment)
	{
		meta.removeEnchant(enchantment);
		return this;
	}

	/*
	 * Attributes
	 */

	public ItemStackBuilder addAttribute(Attribute attribute, double amount, AttributeModifier.Operation operation, EquipmentSlot slot)
	{
		meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), "", amount, operation, slot));
		return this;
	}

	/*
	 * Potion
	 */

	public ItemStackBuilder addArrowPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, boolean icon)
	{
		if (meta instanceof Arrow arrowMeta)
		{
			arrowMeta.addCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon), true);
		}
		return this;
	}

	public ItemStackBuilder addArrowPotionEffect(PotionEffectType type, int duration, int amplifier)
	{
		return addArrowPotionEffect(type, duration, amplifier, false, true, true);
	}

	public ItemStackBuilder addPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, boolean icon)
	{
		if (meta instanceof PotionMeta potionMeta)
		{
			potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon), true);
		}
		return this;
	}

	public ItemStackBuilder addPotionEffect(PotionEffectType type, int duration, int amplifier)
	{
		return addPotionEffect(type, duration, amplifier, false, true, true);
	}

	/*
	 * Flags
	 */

	public ItemStackBuilder setHideFlags(ItemFlag... flags)
	{
		meta.addItemFlags(flags);
		return this;
	}

	public ItemStackBuilder removeHideFlags(ItemFlag... flags)
	{
		meta.removeItemFlags(flags);
		return this;
	}

	public ItemStackBuilder setUnbreakable(boolean unbreakable)
	{
		meta.setUnbreakable(unbreakable);
		return this;
	}

	public ItemStackBuilder setCustomModelData(int customModelData)
	{
		meta.setCustomModelData(customModelData);
		return this;
	}

	public ItemStack buildItemStack()
	{
		item.setItemMeta(meta);
		return item;
	}
}
