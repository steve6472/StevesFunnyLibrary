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
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.entity.display.FrameDisplayEntity;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.worldtools.menu.FillerMenu;
import steve6472.funnylib.item.events.SwapHandEvent;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.MiscUtil;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public class RectangleFillerItem extends CustomItem implements TickInHandEvent, SwapHandEvent, Boundable
{
	private static final double RAY_DISTANCE = 64;
	private static final double FLOATING_MODE_DISTANCE = 3;
	private static final boolean ENABLE_CORNERS = true;

	@Override
	public String id()
	{
		return "rectangle_filler";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.SHULKER_SHELL)
			.setName(JSONMessage.create("Rectangle Filler").color(ChatColor.DARK_AQUA))
			.addLore(JSONMessage.create(""))
			.addLore(JSONMessage.create("Hold [", ChatColor.WHITE).thenKeybind("key.sneak", ChatColor.BLUE).then("] to select ", ChatColor.WHITE).then("on", ChatColor.WHITE).style(ChatColor.UNDERLINE).then(" block face", ChatColor.WHITE).setItalic(false))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.swapOffhand", ChatColor.BLUE).then("] to switch modes", ChatColor.WHITE).setItalic(false))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.drop", ChatColor.BLUE).then("] to open menu", ChatColor.WHITE).setItalic(false))
			.addLore(JSONMessage.create(""))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.attack", ChatColor.BLUE).then("] move PURPLE corner", ChatColor.WHITE).setItalic(false))
			.addLore(JSONMessage.create("[", ChatColor.WHITE).thenKeybind("key.use", ChatColor.BLUE).then("] move DEBUG corner", ChatColor.WHITE).setItalic(false))
			.buildItemStack();
	}

	@Override
	public void onDrop(PlayerItemContext context, CancellableResult result)
	{
		result.cancel();

		new FillerMenu("Rectangle Filler", context.getItemStack(), false).showToPlayer(context.getPlayer());
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
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		result.cancel();

		if (context.getHand() != EquipmentSlot.HAND)
			return;

		if (!canBeUsed(context.playerContext()))
		{
			canNotBeUsedMessage(context.playerContext()).actionbar(context.getPlayer());
			return;
		}

		ItemNBT data = context.getItemData();

		if (!data.protectedData().getBoolean("is_floating", false))
		{
			Vector loc = rayTrace(context.getPlayer(), context.isSneaking(), false);

			if (!isInBounds(context.playerContext(), loc.toVector3i()))
			{
				outOfBoundsMessage(context.playerContext()).actionbar(context.getPlayer());
				return;
			}

			if (useType.isLeft() && !data.protectedData().has3i("pos1"))
			{
				data.protectedData().set3i("pos1", loc.toVector3i());
			} else if (useType.isRight() && !data.has3i("pos2"))
			{
				data.protectedData().set3i("pos2", loc.toVector3i());
			}

			return;
		}

		Vector loc = rayTrace(context.getPlayer(), false, true);
		if (loc == null)
			return;

		if (!isInBounds(context.playerContext(), loc.toVector3i()))
		{
			outOfBoundsMessage(context.playerContext()).actionbar(context.getPlayer());
			return;
		}

		data.protectedData().set3i(useType.isLeft() ? "pos1" : "pos2", loc.toVector3i());
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		result.cancel();

		if (context.getHand() != EquipmentSlot.HAND)
			return;

		PlayerContext playerContext = context.playerContext().playerContext();
		if (!canBeUsed(playerContext))
		{
			canNotBeUsedMessage(playerContext).actionbar(context.getPlayer());
			return;
		}

		ItemNBT data = context.getItemData();

		if (data.protectedData().getBoolean("is_floating", false))
		{
			Vector loc = rayTrace(context.getPlayer(), context.isPlayerSneaking(), true);
			if (loc == null)
				return;

			if (!isInBounds(playerContext, loc.toVector3i()))
			{
				outOfBoundsMessage(playerContext).actionbar(context.getPlayer());
				return;
			}

			data.protectedData().set3i(useType.isLeft() ? "pos1" : "pos2", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());

			return;
		}

		Block block = context.getBlock();
		Vector loc = block.getLocation().toVector();

		if (context.isPlayerSneaking())
			loc.add(context.getFace().getDirection());

		if (!isInBounds(playerContext, loc.toVector3i()))
		{
			outOfBoundsMessage(playerContext).actionbar(context.getPlayer());
			return;
		}

		data.protectedData().set3i(useType.isLeft() ? "pos1" : "pos2", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (context.getHand() != EquipmentSlot.HAND)
			return;

		if (!canBeUsed(context.playerContext()))
		{
			canNotBeUsedMessage(context.playerContext()).actionbar(context.getPlayer());
			return;
		}

		Vector3i pos1 = new Vector3i();
		Vector3i pos2 = new Vector3i();

		adjustPositions(context, pos1, pos2);

		if (!isInBounds(context.playerContext(), pos1) || !isInBounds(context.playerContext(), pos2))
		{
			outOfBoundsMessage(context.playerContext()).actionbar(context.getPlayer());
			return;
		}

		Vector3i minPos = pos1.min(pos2, new Vector3i());
		Vector3i maxPos = pos1.max(pos2, new Vector3i()).add(1, 1, 1);
		Vector3i size = maxPos.sub(minPos, new Vector3i());

		FrameDisplayEntity frame = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(context.getPlayer(), nbt -> nbt.getBoolean("rectangle_select_frame", false), () -> createFrame(context, minPos, size));

		frame.setScale((size.x) / 2f, (size.y) / 2f, (size.z) / 2f);
		frame.setRadius((float) (Math.sin(Math.toRadians(FunnyLib.getUptimeTicks() % 3600)) * 0.25 + 1.5) * (1f / 16f));
		frame.move(minPos.x + (size.x) / 2f, minPos.y + (size.y) / 2f, minPos.z + (size.z) / 2f);

		ItemNBT data = context.getItemData();

		boolean isFloating = data.protectedData().getBoolean("is_floating", false);
		JSONMessage.create((isFloating ? "Floating" : "Block") + "    " + MiscUtil.prettyPrintLocation(minPos) + "    " + MiscUtil.prettyPrintLocation(maxPos)).actionbar(context.getPlayer());

		if (isFloating)
			renderFloatingPosition(context);
	}

	protected void renderFloatingPosition(PlayerItemContext context)
	{
		Vector vector = rayTrace(context.getPlayer(), context.isSneaking(), true);

		if (!isInBounds(context.playerContext(), vector.toVector3i()))
		{
			outOfBoundsMessage(context.playerContext()).actionbar(context.getPlayer());
			return;
		}

		FrameDisplayEntity highlight = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(context.getPlayer(), nbt -> nbt.getBoolean("rectangle_select_float_highlight", false), () ->
			{
				FrameDisplayEntity fde = new FrameDisplayEntity(
					context.getPlayer(),
					new Location(context.getWorld(), vector.getBlockX() + 0.5f, vector.getBlockY() + 0.5f, vector.getBlockZ() + 0.5f),
					FrameDisplayEntity.FrameType.UGLY_PURPLE,
					1f / 8f);

				fde.setScale(0.3f, 0.3f, 0.3f);

				fde.teleportDuration = 1;

				fde.setAliveCondition(context.getPlayer(), () ->
				{
					ItemStack itemStack = context.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
					if (itemStack == null || itemStack.getType().isAir())
						return false;
					return
						ItemNBT.create(itemStack).protectedData().getBoolean("is_floating", false) &&
							aliveCondition(fde, context.getPlayer()) &&
							additionalFloatingAliveCondition(context.getPlayer());
				});
				fde.getEntityPDC().setBoolean("rectangle_select_float_highlight", true);
				return fde;
			});

		highlight.setRadius((float) (Math.sin(Math.toRadians(FunnyLib.getUptimeTicks() % 3600)) * 0.25 + 1.5) * (1f / 8f));
		highlight.move(vector.getBlockX() + 0.5f, vector.getBlockY() + 0.5f, vector.getBlockZ() + 0.5f);
	}

	public void adjustPositions(PlayerItemContext context, Vector3i pos1, Vector3i pos2)
	{
		ItemNBT data = context.getItemData();
		PdcNBT protectedData = data.protectedData();

		if (protectedData.has3i("pos1") || protectedData.has3i("pos2"))
		{
			if (protectedData.has3i("pos1") && protectedData.has3i("pos2"))
			{
				protectedData.get3i("pos1", pos1);
				protectedData.get3i("pos2", pos2);
			} else
			{
				if (protectedData.has3i("pos1"))
					protectedData.get3i("pos1", pos1);
				else
					protectedData.get3i("pos2", pos1);

				Vector vector = rayTrace(context.getPlayer(), context.isSneaking(), protectedData.getBoolean("is_floating", false));
				if (vector == null)
					return;

				pos2.set((int) vector.getX(), (int) vector.getY(), (int) vector.getZ());
			}
		} else
		{
			Vector vector = rayTrace(context.getPlayer(), context.isSneaking(), protectedData.getBoolean("is_floating", false));
			if (vector == null)
				return;

			pos1.set((int) vector.getX(), (int) vector.getY(), (int) vector.getZ());
			pos2.set(pos1);
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

	public FrameDisplayEntity createFrame(PlayerItemContext context, Vector3i minPos, Vector3i size)
	{
		FrameDisplayEntity fde = new FrameDisplayEntity(
			context.getPlayer(),
			new Location(context.getWorld(), minPos.x + (size.x) / 2f, minPos.y + (size.y) / 2f, minPos.z + (size.z) / 2f),
			FrameDisplayEntity.FrameType.MEDIUMAQUA_MARINE,
			1f / 16f);

		fde.teleportDuration = 3;

		if (ENABLE_CORNERS)
		{
			Player player = context.getPlayer();

			fde.createFramePart(FrameDisplayEntity.FrameType.DEBUG, t ->
			{
				PlayerItemContext playerContext = new PlayerItemContext(player, EquipmentSlot.HAND);
				PdcNBT protectedData = playerContext.getItemData().protectedData();

				Vector3i pos1 = new Vector3i();
				if (protectedData.has3i("pos1"))
					pos1 = protectedData.get3i("pos1");

				t.getScale().set(fde.getRadius() * 3f);
				t.getTranslation().set(fde.getPosition().x - pos1.x() - 0.5, fde.getPosition().y - pos1.y() + fde.getRadius() - 0.5, fde.getPosition().z - pos1.z() - 0.5);
			});
			fde.createFramePart(FrameDisplayEntity.FrameType.UGLY_PURPLE, t ->
			{
				PlayerItemContext playerContext = new PlayerItemContext(player, EquipmentSlot.HAND);
				PdcNBT protectedData = playerContext.getItemData().protectedData();

				Vector3i pos2 = new Vector3i();
				if (protectedData.has3i("pos2"))
					pos2 = protectedData.get3i("pos2");

				t.getScale().set(fde.getRadius() * 3f);
				t.getTranslation().set(fde.getPosition().x - pos2.x() - 0.5, fde.getPosition().y - pos2.y() + fde.getRadius() - 0.5, fde.getPosition().z - pos2.z() - 0.5);
			});
		}

		fde.setAliveCondition(context.getPlayer(), () -> aliveCondition(fde, context.getPlayer()));
		fde.getEntityPDC().setBoolean("rectangle_select_frame", true);
		return fde;
	}

	public boolean aliveCondition(FrameDisplayEntity fde, Player player)
	{
		ItemStack itemStack = player.getInventory().getItem(EquipmentSlot.HAND);
		if (Items.getCustomItem(itemStack) != this)
			return false;
		double distance = fde
			.getPosition()
			.distance(player.getLocation().toVector().toVector3d());
		return distance < 256 && additionalAliveCondition(player);
	}

	protected boolean additionalFloatingAliveCondition(Player player)
	{
		return true;
	}

	protected boolean additionalAliveCondition(Player player)
	{
		return true;
	}
}
