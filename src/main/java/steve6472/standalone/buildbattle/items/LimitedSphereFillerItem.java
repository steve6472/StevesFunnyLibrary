package steve6472.standalone.buildbattle.items;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.builtin.worldtools.SphereFillerItem;
import steve6472.funnylib.minigame.Minigames;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.Plot;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class LimitedSphereFillerItem extends SphereFillerItem
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

		return plot.isLocationInPlot(pos);
	}

	@Override
	public JSONMessage outOfBoundsMessage(PlayerContext context)
	{
		return JSONMessage.create("Out of your plot bounds!");
	}

	@Override
	public CustomItem itemCheck()
	{
		return Minigames.FILL_SPHERE_LIMITED;
	}
}
