package steve6472.standalone.bingo;

import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import steve6472.funnylib.minigame.EventHolder;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.JSONMessage;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class BingoTask extends EventHolder
{
	protected final Bingo bingo;
	final Material icon;
	final String name;
	final String description;
	final String key;
	private Advancement advancement;

	public BingoTask(Bingo bingo, Material icon, String name, String description, String id)
	{
		super(bingo.getPlugin());
		this.bingo = bingo;
		this.key = id;
		this.icon = icon;
		this.name = name;
		this.description = description;
	}

	public void createAdvancement(float x, float y)
	{
		AdvancementDisplay display = new AdvancementDisplay(icon, name, description, AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.ALWAYS);
		display.setCoordinates(x, y);
		display.setPositionOrigin(bingo.root);
		advancement = new Advancement(bingo.root, new NameKey(bingo.getPlugin().getName().toLowerCase(Locale.ROOT), key), display, AdvancementFlag.SHOW_TOAST);
		bingo.advancementManager.addAdvancement(advancement);
	}

	public Advancement getAdvancement()
	{
		return advancement;
	}

	protected void finishTask(Player player)
	{
		// Award task only to participants in the game
		if (!bingo.getGame().getPlayers().contains(player))
			return;

		if (!advancement.isGranted(player))
		{
			JSONMessage.create(player.getName()).suggestCommand("/tell " + player.getName()).then(" finished the task ").then(
				JSONMessage.create("[").color(ChatColor.GREEN).then(name).color(ChatColor.GREEN).tooltip(JSONMessage.create(description).color(ChatColor.GREEN))
			).then("]", ChatColor.GREEN).send(bingo.getGame().getPlayers());
		}

		PdcNBT nbt = PdcNBT.fromPDC(player.getPersistentDataContainer());
		NBT playerTasks = nbt.getOrCreateCompound(bingo.bingoKey);

		playerTasks.setBoolean(key, true);

		nbt.setCompound(bingo.bingoKey, playerTasks);

		bingo.advancementManager.grantAdvancement(player, advancement);
		checkAllTasks(player);
	}

	/*
	 * TODO: move to Bingo class
	 */
	private void checkAllTasks(Player player)
	{
		if (bingo.tasks.countCompletedTasks(player) != bingo.tasks.getTaskCount())
			return;

//		if (advancement.isGranted(player))
//			return;

		// Announce winner
		Bukkit.broadcastMessage("" + ChatColor.GOLD + "---------------------------------------");
		Bukkit.broadcastMessage("           " + ChatColor.AQUA + player.getName() + ChatColor.RESET + " finished all tasks!");
		Bukkit.broadcastMessage("           You have a minute to finish");
		Bukkit.broadcastMessage("           as many tasks as you can!");
		Bukkit.broadcastMessage("" + ChatColor.GOLD + "---------------------------------------");

		// Grant the root BINGO advancement
		bingo.advancementManager.grantAdvancement(player, bingo.root);

		// Make the player a spectator
		player.getInventory().clear();
		player.setGameMode(GameMode.SPECTATOR);

		bingo.startEndGame(player);
	}

	protected abstract void setupEvents();

	public String getKey()
	{
		return key;
	}
}
