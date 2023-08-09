package steve6472.standalone.hideandseek.playerstate;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.minigame.AbstractPlayerState;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

import java.util.Comparator;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class SeekerPlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "seeker";
	}

	@Override
	public void apply(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
		player.getInventory().addItem(getCompassItem());

		/*
		 * Disable dropping of special compass
		 */
		registerEvents(PlayerDropItemEvent.class, event ->
		{
			ItemNBT nbt = ItemNBT.create(event.getItemDrop().getItemStack());

			if ("hider_tracker".equals(nbt.getString("custom_id")))
			{
				event.setCancelled(true);
			}
		});

		/*
		 * Set compass target to nearest player
		 */
		scheduleRepeatingTask(task -> tracker.game
			.getPlayers()
			.stream()
			.filter(p -> p != player)
			.min(Comparator.comparingDouble(e -> e.getLocation().distance(player.getLocation())))
			.ifPresent(p -> player.setCompassTarget(p.getLocation())), 0, 5);
	}

	private ItemStack getCompassItem()
	{
		return ItemStackBuilder
			.create(Material.COMPASS)
			.setName("Hider Tracker 3000", ChatColor.AQUA)
			.setCustomId("hider_tracker")
			.addLore(JSONMessage.create("Don't put into chests and such").color(ChatColor.DARK_GRAY))
			.addLore(JSONMessage.create("I have not guarded against that").color(ChatColor.DARK_GRAY))
			.buildItemStack();
	}

	@Override
	public void revert(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
		dispose();
	}
}
