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
import steve6472.funnylib.FunnyLib;

import javax.naming.Name;
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

	private ItemStackBuilder(Material material)
	{
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
		if (meta == null)
			throw new RuntimeException("Item meta is null! Pls fix thx");
	}

	private ItemStackBuilder(ItemStack itemStack)
	{
		this.item = itemStack;
		this.meta = itemStack.getItemMeta();
		if (meta == null)
			throw new RuntimeException("Item meta is null! Pls fix thx");
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

	/*
	 * Static builder
	 */

	private static final ItemStackBuilder STATIC_STACK_BUILDER = new ItemStackBuilder(Material.STONE);

	public static ItemStackBuilder edit(ItemStack itemStack)
	{
		STATIC_STACK_BUILDER.staticEdit(itemStack);
		return STATIC_STACK_BUILDER;
	}

	private void staticEdit(ItemStack itemStack)
	{
		this.item = itemStack;
		this.meta = item.getItemMeta();
		if (meta == null)
			throw new RuntimeException("Item meta is null! Pls fix thx");
	}

	/*
	 *
	 */

	public ItemMeta meta()
	{
		return meta;
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

	public ItemStackBuilder addLore(String text)
	{
		return addLore(text, ChatColor.GRAY);
	}

	public ItemStackBuilder addLore(String text, ChatColor color)
	{
		List<String> lore = meta.getLore();
		if (lore == null)
		{
			lore = new ArrayList<>(1);
		}
		lore.add(color + text);
		meta.setLore(lore);
		return this;
	}

	public ItemStackBuilder addLore(JSONMessage json)
	{
		List<String> lore = meta.getLore();
		if (lore == null)
		{
			lore = new ArrayList<>(1);
		}
		lore.add(json.toLegacy());
		meta.setLore(lore);
		return this;
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
		customTagString(CUSTOM_ID, id);
		return this;
	}

	public String getCustomId()
	{
		return getCustomTagString(CUSTOM_ID);
	}

	public ItemStackBuilder customTagString(String key, String value)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		dataContainer.set(new NamespacedKey(FunnyLib.getPlugin(), key), PersistentDataType.STRING, value);
		return this;
	}

	public ItemStackBuilder customTagInt(String key, int value)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		dataContainer.set(new NamespacedKey(FunnyLib.getPlugin(), key), PersistentDataType.INTEGER, value);

		return this;
	}

	public ItemStackBuilder customTagByte(String key, byte value)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		dataContainer.set(new NamespacedKey(FunnyLib.getPlugin(), key), PersistentDataType.BYTE, value);

		return this;
	}

	public String getCustomTagString(String key)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		return dataContainer.get(new NamespacedKey(FunnyLib.getPlugin(), key), PersistentDataType.STRING);
	}

	public int getCustomTagInt(String key)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		Integer integer = dataContainer.get(new NamespacedKey(FunnyLib.getPlugin(), key), PersistentDataType.INTEGER);
		return integer == null ? 0 : integer;
	}

	public byte getCustomTagByte(String key)
	{
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		Byte abyte = dataContainer.get(new NamespacedKey(FunnyLib.getPlugin(), key), PersistentDataType.BYTE);
		return abyte == null ? 0 : abyte;
	}

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
