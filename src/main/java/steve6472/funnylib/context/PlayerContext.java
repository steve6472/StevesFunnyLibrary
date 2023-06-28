package steve6472.funnylib.context;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class PlayerContext implements Context
{
	protected final Player player;
	protected final EquipmentSlot hand;

	public PlayerContext(Player player, EquipmentSlot hand)
	{
		this.player = player;
		this.hand = hand;
	}

	public Player getPlayer()
	{
		return player;
	}

	public EquipmentSlot getHand()
	{
		return hand;
	}

	public World getWorld()
	{
		return player.getWorld();
	}

	/*
	 * Fancy helpers
	 */

	public Location getLocation()
	{
		return player.getLocation();
	}

	public Chunk getPlayerChunk()
	{
		return getLocation().getChunk();
	}

	public boolean isCreative()
	{
		return getPlayer().getGameMode() == GameMode.CREATIVE;
	}

	public boolean isSurvival()
	{
		return getPlayer().getGameMode() == GameMode.SURVIVAL;
	}

	public boolean isSneaking()
	{
		return getPlayer().isSneaking();
	}
}
