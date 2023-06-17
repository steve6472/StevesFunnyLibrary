package steve6472.standalone.interactable.blocks;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.menu.ISlotBuilder;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.generated.BlockGen;
import steve6472.standalone.interactable.blocks.data.CheckpointBlockData;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by steve6472
 * Date: 5/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CheckpointBlock extends CustomBlock implements IBlockData, BlockTick, BlockClickEvents
{
	@Override
	public String id()
	{
		return "checkpoint";
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return BlockGen.LightWeightedPressurePlate(0);
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (!context.isCreative() || !context.isPlayerSneaking() || context.getHand() != EquipmentSlot.HAND)
			return;

		SETTINGS
			.setData("data", context.getBlockData(CheckpointBlockData.class))
			.setData("player", context.getPlayer())
			.build()
			.showToPlayer(context.getPlayer());
	}

	@Override
	public void tick(BlockContext context)
	{
		// todo: if enabled, global constants

		World world = context.getWorld();
		Collection<Entity> nearbyEntities = world.getNearbyEntities(context.getLocation().clone().add(0.5, 0, 0.5), 0.5, 0.5, 0.5, e -> e instanceof Player);

		for (Entity nearbyEntity : nearbyEntities)
		{
			Player player = (Player) nearbyEntity;

			player.sendMessage(ChatColor.GREEN + "You reached checkpoint " + context.getBlockData(CheckpointBlockData.class).order + "!");
		}
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new CheckpointBlockData();
	}

	private static final Mask MASK = Mask.createMask()
		.addRow("XXXXX XXX")
		.addRow("XXX X XXX")
		.addRow("XXXXX XXX")
		.addItem('X', SlotBuilder.create(ItemStackBuilder.create(Material.GRAY_STAINED_GLASS_PANE).setName("").buildItemStack()));

	private static SlotBuilder orderSlot(int order)
	{
		return SlotBuilder.buttonSlot_(ItemStackBuilder.quick(Material.NAME_TAG, "Checkpoint Order: " + order), (c, m) -> {});
	}

	private static final MenuBuilder SETTINGS = MenuBuilder.create(3, "Checkpoint Settings")
		.applyMask(MASK)
		.customBuilder(b ->
		{
			CheckpointBlockData data = b.getData("data", CheckpointBlockData.class);

			b.slot(3, 1, SlotBuilder.buttonSlot(Material.NAME_TAG, "Parkour ID: " + data.parkourId, (c, m) ->
			{
				Player player = m.getPassedData().getData("player", Player.class);
				new AnvilGUI.Builder()
					.onComplete((completion) ->
					{
						m.getPassedData().getData("data", CheckpointBlockData.class).parkourId = completion.getText();
						return Collections.singletonList(AnvilGUI.ResponseAction.close());
					})
					.text(" ")
					.itemLeft(new ItemStack(Material.PAPER))
					.title("Enter Parkour ID")
					.plugin(FunnyLib.getPlugin())
					.open(player);
			}));

			b.slot(5, 1, orderSlot(data.order));
		})
		.slot(5, 0, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Increase Order", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.order++;
			m.setSlot(5, 1, orderSlot(data.order));
			m.reload();
		}))
		.slot(5, 2, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Decrease Order", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.order--;
			m.setSlot(5, 1, orderSlot(data.order));
			m.reload();
		}));

}
