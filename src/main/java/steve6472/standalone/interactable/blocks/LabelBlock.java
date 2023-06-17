package steve6472.standalone.interactable.blocks;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.builtin.VirtualBlock;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.standalone.interactable.blocks.data.LabelBlockData;

import java.util.Collections;

/**
 * Created by steve6472
 * Date: 6/17/2023
 * Project: StevesFunnyLibrary <br>
 */
public class LabelBlock extends VirtualBlock implements BlockClickEvents
{
	@Override
	public String id()
	{
		return "distance_label";
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new LabelBlockData();
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		new AnvilGUI.Builder()
			.onComplete((completion) ->
			{
				LabelBlockData blockData = context.getBlockData(LabelBlockData.class);
				blockData.label = completion.getText();
				blockData.despawnEntities(context.blockContext());
				blockData.spawnEntities(context.blockContext());
				return Collections.singletonList(AnvilGUI.ResponseAction.close());
			})
			.text(" ")
			.itemLeft(new ItemStack(Material.PAPER))
			.title("New Label")
			.plugin(FunnyLib.getPlugin())
			.open(context.getPlayer());

		e.setCancelled(true);
	}
}
