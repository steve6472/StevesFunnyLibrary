package steve6472.standalone.buildbattle.items;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.worldtools.RectangleFillerItem;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class LimitedRectangleFillerItem extends RectangleFillerItem
{
	@Override
	public String id()
	{
		return "limited_" + super.id();
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder
			.editNonStatic(super.item())
			.addLore(JSONMessage.create())
			.addLore(JSONMessage.create("Limited Edition!").color(ChatColor.GOLD))
			.buildItemStack();
	}

	@Override
	public boolean canBeUsed(PlayerContext context)
	{
		if (!(FunnyLib.currentGame instanceof BuildBattleGame bbg))
			return false;

		Plot plot = bbg.getPlayersPlot(context.getPlayer());
		if (plot == null)
			return false;

		return super.canBeUsed(context);
	}

	@Override
	public JSONMessage canNotBeUsedMessage(PlayerContext context)
	{
		if (!(FunnyLib.currentGame instanceof BuildBattleGame bbg))
			return JSONMessage.create("Minigame not running!");

		Plot plot = bbg.getPlayersPlot(context.getPlayer());
		if (plot == null)
			return JSONMessage.create("You do not own a plot!");

		return super.canNotBeUsedMessage(context);
	}

	@Override
	public boolean isInBounds(PlayerContext context, Vector3i pos)
	{
		if (!(FunnyLib.currentGame instanceof BuildBattleGame bbg))
			return false;

		Plot plot = bbg.getPlayersPlot(context.getPlayer());
		if (plot == null)
			return false;

		return plot.locationInPlot(pos);
	}

	@Override
	public JSONMessage outOfBoundsMessage(PlayerContext context)
	{
		return JSONMessage.create("Out of your plot bounds!");
	}

	@Override
	protected boolean additionalAliveCondition(Player player)
	{
		Vector3i pos1 = new Vector3i();
		Vector3i pos2 = new Vector3i();

		if (!(FunnyLib.currentGame instanceof BuildBattleGame bbg))
			return false;

		Plot plot = bbg.getPlayersPlot(player);

		Items.callWithItemContext(player, EquipmentSlot.HAND, pic -> adjustPositions(pic, pos1, pos2));

		return plot.locationInPlot(pos1) && plot.locationInPlot(pos2);
	}

	@Override
	protected boolean additionalFloatingAliveCondition(Player player)
	{
		Vector vector = rayTrace(player, player.isSneaking(), true);

		Plot plot = ((BuildBattleGame) FunnyLib.currentGame).getPlayersPlot(player);
		if (!plot.locationInPlot(vector.toVector3i()))
		{
			return false;
		}

		return super.additionalFloatingAliveCondition(player);
	}
}
