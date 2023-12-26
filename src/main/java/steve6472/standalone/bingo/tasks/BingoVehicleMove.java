package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoVehicleMove extends BingoTask
{
	private final Predicate<Vehicle> predicate;

	public BingoVehicleMove(Bingo bingo, Material icon, String name, String description, String id, Predicate<Vehicle> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(VehicleMoveEvent.class, event ->
		{
			if (!predicate.test(event.getVehicle())) return;
			for (Entity passenger : event.getVehicle().getPassengers())
			{
				if (passenger instanceof Player player)
					finishTask(player);
			}
		});
	}
}
