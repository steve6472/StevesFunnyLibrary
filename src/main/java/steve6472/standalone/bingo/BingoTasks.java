package steve6472.standalone.bingo;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.standalone.bingo.tasks.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoTasks
{
	private final List<Function<Bingo, BingoTask>> tasks;

	public BingoTasks()
	{
		tasks = new ArrayList<>();

		addTasks();
	}

	public List<Function<Bingo, BingoTask>> getTasks()
	{
		return tasks;
	}

	private void addTask(Function<Bingo, BingoTask> task)
	{
		tasks.add(task);
	}

	private void addTasks()
	{
		addTask(BingoTradeVillager::new);
//		addTask(bingo -> new BingoKillEntity(bingo, Material.IRON_AXE, "Kill a Pillager", "Somewhere near the border :)", "kill_pillager", entity -> entity instanceof Pillager));
		addTask(bingo -> new BingoCraftItem(bingo, Material.WRITABLE_BOOK, "Craft Book and Quill", "Kill a squid, cow and a chicken", "craft_book_and_quill", item -> item.getType().equals(Material.WRITABLE_BOOK)));
		addTask(bingo -> new BingoGiveItemToPlayer(bingo, Material.POPPY, "Give another Player a Flower", "Harder than it seems", "gift_flower", item -> Tag.FLOWERS.isTagged(item.getType())));
		addTask(bingo -> new BingoConsumeItem(bingo, Material.APPLE, "Eat an Apple", "Apples don't grow on trees", "eat_apple", item -> item.equals(Material.APPLE)));
		addTask(bingo -> new BingoCraftItem(bingo, Material.GOLDEN_CARROT, "Craft a Golden Carrot", "This is the sixth description and I'm already out of 'jokes'", "craft_golden_carrot", item -> item.getType().equals(Material.GOLDEN_CARROT)));
		addTask(bingo -> new BingoObtainItem(bingo, Material.DIAMOND, "Obtain a Diamond", "You need to go deeper", "get_diamond", item -> item.getType().equals(Material.DIAMOND)));
		addTask(bingo -> new BingoCraftItem(bingo, Material.IRON_HOE, "Craft an Iron Hoe", "Useless!\nOr is it?", "craft_iron_hoe", item -> item.getType().equals(Material.IRON_HOE)));
		addTask(bingo -> new BingoCraftItem(bingo, Material.CAKE, "Make a Cake", "I hope it's not a lie", "make_cake", item -> item.getType().equals(Material.CAKE)));
		addTask(bingo -> new BingoKillEntity(bingo, Material.IRON_AXE, "Kill a Chicken", "Don't get the egg!", "kill_chicken", entity -> entity instanceof Chicken));
		addTask(bingo -> new BingoObtainItem(bingo, Material.EGG, "Obtain an Egg", "Don't kill the chicken!", "get_egg", item -> item.getType().equals(Material.EGG)));
		addTask(bingo -> new BingoPlayerLevels(bingo, Material.EXPERIENCE_BOTTLE, "Get 15 EXP Levels", "Can't be that hard. Can it ?", "get_lvls", 15));
		addTask(bingo -> new BingoBreedAnimals(bingo, Material.WHEAT, "Breed Animals Once", "<redacted>", "breed_animals", entity -> true));
		addTask(bingo -> new BingoObtainItem(bingo, Material.COD, "Get a Fish", "You don't have to fish for a fish", "get_fish", item -> Tag.ITEMS_FISHES.isTagged(item.getType())));
		addTask(bingo -> new BingoTameAnimal(bingo, Material.BONE, "Tame an Animal", "Get a companion for life", "tame_animal", entity -> true));
		// TODO: actually test this one, I think it can return null
		addTask(bingo -> new BingoWearEquipment(bingo, Material.CARVED_PUMPKIN, "Wear a Pumpkin on Your Head", "Watch out for Curse of Binding", "wear_pumpkin", equip -> Material.CARVED_PUMPKIN.equals(equip.getItem(EquipmentSlot.HEAD).getType())));
//		addTask(bingo -> ); // Get stack of cobble
		addTask(bingo -> new BingoInteractEntity(bingo, Material.SHEARS, "Shear a Snowman", "You might wanna kill it afterwards", "shear_snowman", (player, entity) ->
		{
			if (!(entity instanceof Snowman snowman)) return false;

			ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
			if (item == null) return false;
			return item.getType().equals(Material.SHEARS) && !snowman.isDerp();
		}));
		addTask(bingo -> new BingoCraftItem(bingo, Material.TARGET, "Craft a Target", "", "craft_target", item -> item.getType().equals(Material.TARGET)));
//		addTask(bingo -> new BingoBreakBlock(bingo, Material.BLUE_ICE, "Break Blue Ice", "It's blue. You can't miss it", "break_blue_ice", block -> block.getType().equals(Material.BLUE_ICE)));
		addTask(bingo -> new BingoWearEquipment(bingo, Material.LEATHER_CHESTPLATE, "Wear a Full Set of Armor", "Any armor will do", "wear_armor", equip ->
		{
			Predicate<ItemStack> isEmpty = item -> !(item == null || item.getType().isAir());
			return isEmpty.test(equip.getItem(EquipmentSlot.HEAD)) && isEmpty.test(equip.getItem(EquipmentSlot.CHEST)) && isEmpty.test(equip.getItem(EquipmentSlot.LEGS)) && isEmpty.test(equip.getItem(EquipmentSlot.FEET));
		}));

		addTask(bingo -> new BingoPlayerLocation(bingo, Material.LIGHT_BLUE_CONCRETE, "320 is the Limit", "Reach Y >= +318", "reach_y_p318", loc -> loc.getY() >= 318));
		addTask(bingo -> new BingoPlayerLocation(bingo, Material.BEDROCK, "-64 is the Limit", "Reach Y <= -62", "reach_y_n62", loc -> loc.getY() <= -62));
		addTask(bingo -> new BingoKillEntity(bingo, Material.WOODEN_AXE, "Kill a Creeper", "Don't explode!", "kill_creeper", entity -> entity instanceof Creeper));
		addTask(bingo -> new BingoBreakBlock(bingo, Material.GREEN_GLAZED_TERRACOTTA, "Colored Glazed Terracotta", "Break any COLORED glazed terracotta", "break_glazed_terracotta", block -> block.getType().name().toLowerCase(Locale.ROOT).endsWith("GLAZED_TERRACOTTA".toLowerCase(Locale.ROOT))));
		addTask(bingo -> new BingoBurnItemLava(bingo, Material.LAVA_BUCKET, "Throw Diamond into Lava", "Etho approved!", "throw_diamond_lava", item -> item.getType().equals(Material.DIAMOND)));
		addTask(bingo -> new BingoCraftItem(bingo, Material.FLOWER_POT, "Craft a Flower Pot", "So you can put a pretty flower inside", "craft_flower_pot", item -> item.getType().equals(Material.FLOWER_POT)));
		addTask(bingo -> new BingoCraftItem(bingo, Material.PISTON, "Craft a Piston", "Can elevate your mood", "craft_piston", item -> item.getType().equals(Material.PISTON)));
		addTask(bingo -> new BingoCraftItem(bingo, Material.PURPLE_DYE, "Craft a Purple Dye", "Red 'n Blue", "craft_purple_dye", item -> item.getType().equals(Material.PURPLE_DYE)));
		addTask(bingo -> new BingoDieByCause(bingo, Material.WATER_BUCKET, "Drown", "Read the title", "die_by_drown", cause -> cause == EntityDamageEvent.DamageCause.DROWNING));
		addTask(bingo -> new BingoConsumeItem(bingo, Material.SPIDER_EYE, "Eat a Spider Eye", "One of the eight will suffice", "eat_spider_eye", item -> item.equals(Material.SPIDER_EYE)));
		addTask(bingo -> new BingoConsumeItem(bingo, Material.SUSPICIOUS_STEW, "Eat a Suspicious Stew", "Sus", "eat_sus_stew", item -> item.equals(Material.SUSPICIOUS_STEW)));
		addTask(bingo -> new BingoKillEntity(bingo, Material.PLAYER_HEAD, "Kill a Player", "Blame akma for this one", "kill_player", entity -> entity instanceof Player));
		addTask(bingo -> new BingoObtainItem(bingo, Material.ENDER_PEARL, "Get an Ender Pearl", "\"Balls\" -OndarDJ", "get_ender_pearl", item -> item.getType().equals(Material.ENDER_PEARL)));
		addTask(bingo -> new BingoHavePotionEffect(bingo, Material.POTION, "Have any Potion Effect", "\"Should be easy\" -akmatras", "have_potion_effect", effect -> true));
		addTask(bingo -> new BingoObtainItem(bingo, Material.TNT, "Get a TNT", "\"Tuh-un-tuh\" -2 villagers by a railway", "get_tnt", item -> item.getType().equals(Material.TNT)));
		addTask(bingo -> new BingoVehicleMove(bingo, Material.ACACIA_BOAT, "Row a Boat", "According to all known laws of aviation...", "row_a_boat", vehicle -> vehicle instanceof Boat));
	}
}
