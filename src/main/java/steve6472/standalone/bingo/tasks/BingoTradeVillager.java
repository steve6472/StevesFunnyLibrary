package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.MerchantInventory;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoTradeVillager extends BingoTask
{
	public BingoTradeVillager(Bingo bingo)
	{
		super(bingo, Material.EMERALD, "Trade with a Villager", "Finish a trade with a villager", "trade_villager");
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(InventoryClickEvent.class, event ->
		{
			if (!(event.getInventory() instanceof MerchantInventory)) return;
			if (event.getSlotType() != InventoryType.SlotType.RESULT || event.getAction() == InventoryAction.NOTHING) return;

			finishTask(((Player) event.getWhoClicked()));
		});
	}
}
