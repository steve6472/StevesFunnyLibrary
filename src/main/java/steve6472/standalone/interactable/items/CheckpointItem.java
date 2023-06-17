package steve6472.standalone.interactable.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.blocks.data.CheckpointBlockData;

/**
 * Created by steve6472
 * Date: 5/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CheckpointItem extends CustomItem
{
	@Override
	public String id()
	{
		return "checkpoint_returner";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.ENDER_PEARL).setName("Return To Checkpoint", ChatColor.DARK_AQUA).buildItemStack();
	}

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		result.setCancelled(true);
		if (context.isCreative() && context.isSneaking() && useType.isRight())
		{

		}
	}

	private static final MenuBuilder SETTINGS = MenuBuilder.create(3, "Checkpoint Item Settings")
		.customBuilder(b ->
		{
			CheckpointBlockData data = b.getData("data", CheckpointBlockData.class);

			b.slot(1, 5, SlotBuilder.buttonSlot_(ItemStackBuilder.create(Material.NAME_TAG).setName("Parkour ID: " + data.parkourId).buildItemStack(), (c, m) ->
			{

			}));
		})
		;
}
