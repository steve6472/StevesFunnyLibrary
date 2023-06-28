package steve6472.standalone.interactable.blocks;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.PressureSensor;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.builtin.TeleportButtonBlock;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.generated.BlockGen;
import steve6472.standalone.interactable.blocks.data.CheckpointBlockData;
import steve6472.standalone.interactable.Interactable;

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
		if (context.testDataType(CheckpointBlockData.class))
		{
			CheckpointBlockData data = context.getBlockData(CheckpointBlockData.class);
			return data.material.createBlockData();
		} else
		{
			return BlockGen.LightWeightedPressurePlate(0);
		}
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (!context.isCreative() || !context.isPlayerSneaking() || context.getHand() != EquipmentSlot.HAND)
			return;

		if (context.isCustomItem() && context.getCustomItem() == Interactable.CHECKPOINT_ITEM)
			return;

		SETTINGS
			.setData("data", context.getBlockData(CheckpointBlockData.class))
			.setData("player", context.getPlayer())
			.setData("location", context.getBlockLocation())
			.build()
			.showToPlayer(context.getPlayer());
	}

	@Override
	public void tick(BlockContext context)
	{
		// todo: if enabled, global constants

		World world = context.getWorld();
		Collection<Entity> nearbyEntities = world.getNearbyEntities(context.getLocation().clone().add(0.5, 0.25, 0.5), 0.5, 0.125, 0.5, e -> e instanceof Player);

		CheckpointBlockData blockData = context.getBlockData(CheckpointBlockData.class);

		for (Entity nearbyEntity : nearbyEntities)
		{
			Player player = (Player) nearbyEntity;
			for (ItemStack content : player.getInventory().getContents())
			{
				CustomItem customItem = Items.getCustomItem(content);
				if (customItem == null || customItem != Interactable.CHECKPOINT_ITEM)
					continue;

				ItemNBT nbt = ItemNBT.create(content);
				String parkourId = nbt.getString("parkour_id", null);
				if (!blockData.parkourId.equals(parkourId))
					continue;

				int reachedCheckpoint = nbt.getInt("reached_checkpoint", 0);

				if (reachedCheckpoint + 1 == blockData.order)
				{
					Location location = context.getLocation();
					nbt.setInt("reached_checkpoint", blockData.order);
					nbt.setBoolean("use_player_facing", blockData.usePlayerFacing);
					Location newLoc = location.clone().add(0.5, 0.01, 0.5);
					newLoc.setYaw(blockData.yaw);
					newLoc.setPitch(blockData.pitch);
					nbt.setLocation("loc", newLoc);
					nbt.save();

					if (blockData.end)
					{
						player.sendMessage(ChatColor.GREEN + "You have finished this parkour!");
					} else
					{
						player.sendMessage(ChatColor.GREEN + "You reached checkpoint " + blockData.order + "!");
					}
					break;
				}
			}
		}
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new CheckpointBlockData();
	}

	private static final Mask MASK = Mask.createMask()
		.addRow("RRRGGGBBB")
		.addRow("RRRGGGBBB")
		.addRow("RRRGGGBBB")
		.addItem('R', SlotBuilder.create(ItemStackBuilder.create(Material.RED_STAINED_GLASS_PANE).setName("").buildItemStack()))
		.addItem('G', SlotBuilder.create(ItemStackBuilder.create(Material.GREEN_STAINED_GLASS_PANE).setName("").buildItemStack()))
		.addItem('B', SlotBuilder.create(ItemStackBuilder.create(Material.BLUE_STAINED_GLASS_PANE).setName("").buildItemStack()));

	private static SlotBuilder orderSlot(int order)
	{
		return SlotBuilder.buttonSlot_(ItemStackBuilder.quick(order == 0 ? Material.GOLD_NUGGET : Material.IRON_NUGGET, "Checkpoint Order: " + order, ChatColor.WHITE, order == 0 ? 1 : order), (c, m) -> {});
	}

	private static SlotBuilder facingSlot(float pitch, float yaw)
	{
		return SlotBuilder.buttonSlot_(ItemStackBuilder.quick(Material.ENDER_EYE, "Facing: " + pitch + "/" + yaw), (c, m) -> {});
	}

	private static final MenuBuilder SETTINGS = MenuBuilder.create(3, "Checkpoint Settings")
		.applyMask(MASK)
		.allowPlayerInventory()
		.customBuilder(b ->
		{
			CheckpointBlockData data = b.getData("data", CheckpointBlockData.class);

			b.slot(1, 0, SlotBuilder.buttonSlot(Material.NAME_TAG, "Parkour ID: " + data.parkourId, (c, m) ->
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

			b.slot(4, 1, orderSlot(data.order));
			b.slot(7, 1, facingSlot(data.pitch, data.yaw));
		})
		.slot(1, 2, m ->
		{
			CheckpointBlockData data = m.getData("data", CheckpointBlockData.class);
			Location location = m.getData("location", Location.class);

			return SlotBuilder.create(ItemStackBuilder.quick(data.material, "Material"))
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.SWAP_WITH_CURSOR)
				.allow(ClickType.LEFT)
				.onClick((c, cm) -> {

					ItemStack item = c.itemOnCursor();
					if (item.getType().isAir() || !item.getType().isBlock())
					{
						return Response.cancel();
					}

					data.material = item.getType();
					location.getBlock().setBlockData(Interactable.CHECKPOINT_BLOCK.getVanillaState(new BlockContext(location)));
					c.slot().setItem(ItemStackBuilder.quick(data.material, "Material"));

					return Response.cancel();
				});
		})

		.slot(4, 0, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Increase Order", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.order++;
			m.setSlot(4, 1, orderSlot(data.order));
			m.reload();
		}))
		.slot(4, 2, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Decrease Order", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.order = Math.max(data.order - 1, 0);
			m.setSlot(4, 1, orderSlot(data.order));
			m.reload();
		}))
		.slot(5, 1, SlotBuilder.toggleSlot("End", data -> data.getData("data", CheckpointBlockData.class).end, (data, bool) -> data.getData("data", CheckpointBlockData.class).end = bool))

		.slot(6, 1, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Left", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.yaw -= 5;
			m.setSlot(7, 1, facingSlot(data.pitch, data.yaw));
			m.reload();
		}))
		.slot(8, 1, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Right", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.yaw += 5;
			m.setSlot(7, 1, facingSlot(data.pitch, data.yaw));
			m.reload();
		}))

		.slot(7, 2, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Yaw Up", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.pitch -= 5;
			m.setSlot(7, 1, facingSlot(data.pitch, data.yaw));
			m.reload();
		}))
		.slot(7, 0, SlotBuilder.buttonSlot(Material.NETHER_STAR, "Yaw Down", (c, m) ->
		{
			CheckpointBlockData data = m.getPassedData().getData("data", CheckpointBlockData.class);
			data.pitch += 5;
			m.setSlot(7, 1, facingSlot(data.pitch, data.yaw));
			m.reload();
		}))
		.slot(8, 2, SlotBuilder.toggleSlot("Use Player Facing", data -> data.getData("data", CheckpointBlockData.class).usePlayerFacing, (data, bool) -> data.getData("data", CheckpointBlockData.class).usePlayerFacing = bool))
		;

}
