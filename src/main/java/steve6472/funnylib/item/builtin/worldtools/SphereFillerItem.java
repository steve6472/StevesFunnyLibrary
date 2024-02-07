package steve6472.funnylib.item.builtin.worldtools;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.entity.FrameDisplayEntity;
import steve6472.funnylib.entity.SphereFrameDisplayEntity;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.builtin.worldtools.menu.FillerMenu;
import steve6472.funnylib.item.events.SwapHandEvent;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

import java.util.*;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class SphereFillerItem extends CustomItem implements TickInHandEvent, SwapHandEvent
{
	private static final double RAY_DISTANCE = 64;
	private static final double FLOATING_MODE_DISTANCE = 3;

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
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.sneak", ChatColor.BLUE).then("] to select on face", ChatColor.WHITE).setItalic(false))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.swapOffhand", ChatColor.BLUE).then("] to switch modes", ChatColor.WHITE).setItalic(false))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.drop", ChatColor.BLUE).then("] to open menu", ChatColor.WHITE).setItalic(false))
			.buildItemStack();
	}


	@Override
	public void onDrop(PlayerItemContext context, CancellableResult result)
	{
		result.cancel();

		new FillerMenu("Sphere Filler", context.getItemStack()).showToPlayer(context.getPlayer());
	}

	@Override
	public void swapHands(Player player, ItemStack customMainHand, ItemStack offHand, CancellableResult result)
	{
		result.cancel();

		ItemStackBuilder edit = ItemStackBuilder.edit(customMainHand);
		boolean currentMode = edit.nbt().protectedData().getBoolean("is_floating", false);

		edit.nbt().protectedData().setBoolean("is_floating", !currentMode);

		// 'cause reasons
		ItemStack item = edit.buildItemStack();
		player.getInventory().setItem(EquipmentSlot.HAND, item);
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (context.getHand() != EquipmentSlot.HAND)
			return;

		Vector vector = rayTrace(context.getPlayer(), true, false);
		if (vector == null)
			return;

		int radius = context.getItemData().protectedData().getInt("radius", 2);

		SphereFrameDisplayEntity frame = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(
				context.getPlayer(),
				nbt -> nbt.getBoolean("sphere_select_frame", false),
				() -> createFrame(context, vector.toVector3i(), radius));

		frame.setWidth(1f / 16f + (float) Math.cos(Math.toRadians(FunnyLib.getUptimeTicks() % 3600)) * 0.01f);
		frame.setRadius(radius);

		frame.setRotation(applyRandomOffset(frame.getRotation(), (float) Math.toRadians(1f)));

		boolean isFloating = context.getItemData().protectedData().getBoolean("is_floating", false);
		JSONMessage.create((isFloating ? "Floating" : "Block")).actionbar(context.getPlayer());

		if (context.getItemData().protectedData().getBoolean("is_floating", false))
		{
			frame.move(vector.getBlockX() + 0.5f, vector.getBlockY() + 0.5f, vector.getBlockZ() + 0.5f);
		}
	}

	protected Vector rayTrace(Player player, boolean sneaking, boolean isFloating)
	{
		if (isFloating)
		{
			Location eyeLocation = player.getEyeLocation();
			Vector direction = eyeLocation.getDirection();

			Vector add = eyeLocation.toVector().clone().add(direction.multiply(FLOATING_MODE_DISTANCE));
			return new Vector(add.getBlockX(), add.getBlockY(), add.getBlockZ());
		}

		RayTraceResult rayTraceResult = player.rayTraceBlocks(RAY_DISTANCE);
		if (rayTraceResult == null || rayTraceResult.getHitBlock() == null || rayTraceResult.getHitBlockFace() == null)
			return null;

		Block hitBlock = rayTraceResult.getHitBlock();
		Vector vector = hitBlock.getLocation().toVector();
		if (sneaking)
			vector.add(rayTraceResult.getHitBlockFace().getDirection());
		return vector;
	}

	public SphereFrameDisplayEntity createFrame(PlayerItemContext context, Vector3i pos, float radius)
	{
		SphereFrameDisplayEntity fde = new SphereFrameDisplayEntity(
			new Location(context.getWorld(), pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f),
			radius);

		fde.teleportDuration = 3;
		fde.setRotation(createRandomRotation());

//		fde.createCenteredPart(new ItemStack(Material.HEART_OF_THE_SEA), t ->
//		{
//			t.getLeftRotation().rotateY((float) Math.toRadians((FunnyLib.getUptimeTicks() % 3600) / 250f));
//		});

		fde.setAliveCondition(context.getPlayer(), FrameDisplayEntity.holdingCustomItemCondition(context.getPlayer(), FunnyLib.SPHERE_FILLER));
		fde.getEntityPDC().setBoolean("sphere_select_frame", true);
		return fde;
	}

	private static Quaternionf createRandomRotation()
	{
		// Create a Quaternionf with random rotations on all axes
		float randomAngleX = (float) Math.toRadians(Math.random() * 360.0);
		float randomAngleY = (float) Math.toRadians(Math.random() * 360.0);
		float randomAngleZ = (float) Math.toRadians(Math.random() * 360.0);

		// Create individual quaternions for each axis
		Quaternionf quaternionX = new Quaternionf().rotationX(randomAngleX);
		Quaternionf quaternionY = new Quaternionf().rotationY(randomAngleY);
		Quaternionf quaternionZ = new Quaternionf().rotationZ(randomAngleZ);

		// Combine the individual quaternions to get the final random rotation
		return new Quaternionf().identity().mul(quaternionX).mul(quaternionY).mul(quaternionZ);
	}

	public static Quaternionf applyRandomOffset(Quaternionf originalQuaternion, float maxOffsetAngle)
	{
		// Create a random number generator
		Random random = new Random();

		// Generate random offsets for each axis
		float offsetX = random.nextFloat() * maxOffsetAngle;
		float offsetY = random.nextFloat() * maxOffsetAngle;
		float offsetZ = random.nextFloat() * maxOffsetAngle;

		// Create a quaternion representing the random offset
		Quaternionf offsetQuaternion = new Quaternionf()
			.rotateAxis(offsetX, 1, 0, 0)
			.rotateAxis(offsetY, 0, 1, 0)
			.rotateAxis(offsetZ, 0, 0, 1);

		// Apply the random offset to the original quaternion
		return new Quaternionf(originalQuaternion).mul(offsetQuaternion);
	}
}
