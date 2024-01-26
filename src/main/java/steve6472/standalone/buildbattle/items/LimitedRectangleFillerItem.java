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
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.Items;
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
			.addLore(JSONMessage.create(""))
			.addLore(JSONMessage.create("Limited Edition!").color(ChatColor.GOLD))
			.buildItemStack();
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (!(FunnyLib.currentGame instanceof BuildBattleGame bbg))
		{
			JSONMessage.create("Minigame not running!").actionbar(context.getPlayer());
			result.setCancelled(true);
			return;
		}

		Plot plot = bbg.getPlayersPlot(context.getPlayer());
		if (plot == null)
		{
			JSONMessage.create("You do not have a plot!").actionbar(context.getPlayer());
			result.setCancelled(true);
			return;
		}

		Vector vector = context.getBlockLocation().toVector();
		if (context.isPlayerSneaking())
			vector.add(context.getFace().getDirection());

		if (!plot.locationInPlot(vector.toVector3i()))
		{
			JSONMessage.create("Selected location out of plot boundary!").actionbar(context.getPlayer());
			result.setCancelled(true);
			return;
		}

		super.useOnBlock(context, useType, result);
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (!(FunnyLib.currentGame instanceof BuildBattleGame bbg))
		{
			JSONMessage.create("Minigame not running!").actionbar(context.getPlayer());
			return;
		}

		Plot plot = bbg.getPlayersPlot(context.getPlayer());
		if (plot == null)
		{
			JSONMessage.create("You do not have a plot!").actionbar(context.getPlayer());
			return;
		}

		Vector3i pos1 = new Vector3i();
		Vector3i pos2 = new Vector3i();

		adjustPositions(context, pos1, pos2);

		if (!plot.locationInPlot(pos1) || !plot.locationInPlot(pos2))
		{
			JSONMessage.create("Selected location out of plot boundary!").actionbar(context.getPlayer());
			return;
		}

		super.tickInHand(context);
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
}
