package steve6472.funnylib.item.builtin.worldtools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.entity.display.FrameDisplayEntity;
import steve6472.funnylib.entity.display.SphereFrameDisplayEntity;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.builtin.worldtools.menu.FillFunctions;
import steve6472.funnylib.item.builtin.worldtools.menu.FillerMenu;
import steve6472.funnylib.item.events.SwapHandEvent;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.MiscUtil;

import java.awt.*;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class SphereFillerItem extends CustomItem implements TickInHandEvent, SwapHandEvent, Boundable
{
	private static final double RAY_DISTANCE = 96;

	@Override
	public String id()
	{
		return "sphere_filler";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.POPPED_CHORUS_FRUIT)
			.setName(JSONMessage.create("Sphere Filler").color(ChatColor.DARK_AQUA))
			.addLore(JSONMessage.create(""))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.drop", ChatColor.BLUE).then("] to open menu", ChatColor.WHITE).setItalic(false))
			.addLore(JSONMessage.create(""))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.use", ChatColor.BLUE).then("] to apply", ChatColor.WHITE).setItalic(false))
			.buildItemStack();
	}

	@Override
	public void onDrop(PlayerItemContext context, CancellableResult result)
	{
		result.cancel();

		new FillerMenu(this, "Sphere Filler", context.getItemStack(), true).showToPlayer(context.getPlayer());
	}

	@Override
	public void swapHands(Player player, ItemStack customMainHand, ItemStack offHand, CancellableResult result)
	{
		result.cancel();
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		apply(context.playerContext(), useType);
		result.setCancelled(true);
	}

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		apply(context, useType);
		result.setCancelled(true);
	}

	private void apply(PlayerItemContext playerContext, UseType useType)
	{
		if (!useType.isRight()) return;

		ItemNBT itemData = playerContext.getItemData();
		if (itemData == null) return;

		boolean advanced = itemData.protectedData().getBoolean("advanced_fill", false);

		FillFunctions fills = new FillFunctions(null)
		{
			@Override
			public ItemNBT getItemNBT()
			{
				return itemData;
			}
		};

		if (advanced)
			fills.applyAdvancedSphere(playerContext.getPlayer(), this);
		else
			fills.applySphere(playerContext.getPlayer(), this);
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (context.getHand() != EquipmentSlot.HAND)
			return;

		Vector vector = rayTrace(context.getPlayer());
		if (vector == null)
			return;

		PdcNBT playerNbt = PdcNBT.fromPDC(context.getPlayer().getPersistentDataContainer());
		playerNbt.set3i("sphere_filler_center", vector.toVector3i());
		//		context.getItemData().protectedData().set3i("center", vector.toVector3i());
		int radius = context.getItemData().protectedData().getInt("radius", 2);

		if (!playerNbt.has3i("sphere_filler_center"))
			return;

		if (!isInBounds(context.playerContext(), vector.toVector3i()))
			return;

		SphereFrameDisplayEntity frame = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(
				context.getPlayer(),
				nbt -> nbt.getBoolean("sphere_select_frame", false),
				() -> createFrame(context, vector.toVector3i(), radius + 0.5f));

		FrameDisplayEntity frameBlock = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(context.getPlayer(),
				nbt -> nbt.getBoolean("sphere_select_block_frame", false),
				() -> createBlockFrame(context, vector.toVector3i()));

		frame.setWidth(1f / 16f);
		frame.setRadius(radius + 0.5f);

		frame.move(vector.getBlockX() + 0.5f, vector.getBlockY() + 0.5f, vector.getBlockZ() + 0.5f);
		frameBlock.move(vector.getBlockX() + 0.5f, vector.getBlockY() + 0.5f, vector.getBlockZ() + 0.5f);
	}

	protected Vector rayTrace(Player player)
	{
		RayTraceResult rayTraceResult = player.rayTraceBlocks(RAY_DISTANCE);
		if (rayTraceResult == null || rayTraceResult.getHitBlock() == null || rayTraceResult.getHitBlockFace() == null)
			return null;

		Block hitBlock = rayTraceResult.getHitBlock();
		return hitBlock.getLocation().toVector();
	}

	public SphereFrameDisplayEntity createFrame(PlayerItemContext context, Vector3i pos, float radius)
	{
		SphereFrameDisplayEntity fde = new SphereFrameDisplayEntity(
			new Location(context.getWorld(), pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f),
			radius);

		fde.teleportDuration = 2;

		// Player is holding this item
		// player is looking at ground within the plot
		UUID ownerUUID = context.getPlayer().getUniqueId();
		fde.setAliveCondition(context.getPlayer(), () -> {
//			if (!FrameDisplayEntity.holdingCustomItemCondition(context.getPlayer(), FunnyLib.SPHERE_FILLER).get())
//				return false;
//
//			Vector vector = rayTrace(context.getPlayer());
//			if (vector == null)
//				return false;
//
//			return isInBounds(context.playerContext(), vector.toVector3i());
			Player player = Bukkit.getPlayer(ownerUUID);
			if (player == null)
				return false;

			PdcNBT playerNbt = PdcNBT.fromPDC(player.getPersistentDataContainer());
			if (!playerNbt.has3i("sphere_filler_center"))
				return false;

			Vector3i sphereFillerCenter = playerNbt.get3i("sphere_filler_center");
			if (!isInBounds(context.playerContext(), sphereFillerCenter))
				return false;

			Vector vector = rayTrace(player);
			if (vector == null)
				return false;

			return FrameDisplayEntity.holdingCustomItemCondition(player, itemCheck()).get();
		});
		fde.getEntityPDC().setBoolean("sphere_select_frame", true);
		return fde;
	}

	public FrameDisplayEntity createBlockFrame(PlayerItemContext context, Vector3i pos)
	{
		FrameDisplayEntity frame = new FrameDisplayEntity(
			context.getPlayer(),
			new Location(context.getWorld(), pos.x, pos.y, pos.z),
			FrameDisplayEntity.FrameType.AQUA_MARINE, 1f / 12f);
		frame.setScale(0.5f, 0.5f, 0.5f);

		UUID ownerUUID = context.getPlayer().getUniqueId();
		frame.setAliveCondition(context.getPlayer(), () -> {
			Player player = Bukkit.getPlayer(ownerUUID);
			if (player == null)
				return false;

			PdcNBT playerNbt = PdcNBT.fromPDC(player.getPersistentDataContainer());
			if (!playerNbt.has3i("sphere_filler_center"))
				return false;

			Vector3i sphereFillerCenter = playerNbt.get3i("sphere_filler_center");
			if (!isInBounds(context.playerContext(), sphereFillerCenter))
				return false;

			Vector vector = rayTrace(player);
			if (vector == null)
				return false;

			return FrameDisplayEntity.holdingCustomItemCondition(player, itemCheck()).get();
		});
		frame.getEntityPDC().setBoolean("sphere_select_block_frame", true);
		return frame;
	}

	public CustomItem itemCheck()
	{
		return FunnyLib.SPHERE_FILLER;
	}
}
