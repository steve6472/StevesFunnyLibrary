package steve6472.standalone.exnulla.items;

import net.minecraft.util.Mth;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.GenericItem;
import steve6472.funnylib.item.events.ItemInvEvents;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.*;
import steve6472.standalone.exnulla.blocks.SilkLeavesBlock;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 11/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class PocketFurnaceItem extends GenericItem implements ItemInvEvents, TickInHandEvent
{
	public PocketFurnaceItem()
	{
		super("pocket_furnace", Material.FURNACE, "Pocket Furnace", 0);
	}

	@Override
	protected ItemStack item()
	{
		// Make them unstackable
		return ItemStackBuilder.editNonStatic(super.item()).setString("uuid", UUID.randomUUID().toString()).buildItemStack();
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (context.getBlock().getType().isInteractable())
			return;

		if (context.getHandItem().getType().isBlock())
			result.cancel();
	}

	@Override
	public void clickInInventoryEvent(PlayerItemContext context, Inventory inventory, int slot, InventoryAction action, InventoryClickEvent theEvent)
	{
		PocketData data = new PocketData(context.getItemStack());

		// Take out output
		if (theEvent.getClick() == ClickType.CONTROL_DROP)
		{
			// Drop if the furnace is empty
			if (data.fuel.getType().isAir() && data.input.getType().isAir())
			{
				return;
			}

			if (!data.fuel.getType().isAir())
			{
				context.getPlayer().getInventory().addItem(data.fuel.clone());
				data.fuel = MiscUtil.AIR;
			}
			if (!data.input.getType().isAir())
			{
				context.getPlayer().getInventory().addItem(data.input.clone());
				data.input = MiscUtil.AIR;
			}
			data.cookTime = 0;
			data.cookTimeTotal = 0;
			theEvent.setCancelled(true);
		}

		if (theEvent.getClick() == ClickType.RIGHT || theEvent.getClick() == ClickType.SHIFT_RIGHT)
		{

			theEvent.setCancelled(true);

			// Fuel
			if (theEvent.isShiftClick() && theEvent.getCursor() != null && !theEvent.getCursor().getType().isAir())
			{
				if (Checks.isFuel(theEvent.getCursor().getType()) || theEvent.getCursor().getType() == Material.BUCKET)
				{
					addOrStack(data.fuel, theEvent.getCursor(), s -> data.fuel = s, theEvent::setCursor);
				}
			}
			// Input
			else if (!theEvent.isShiftClick() && theEvent.getCursor() != null && !theEvent.getCursor().getType().isAir())
			{
				boolean addedToInput = addOrStack(data.input, theEvent.getCursor(), s -> data.input = s, theEvent::setCursor);
				// Attempts to add to fuel
				if (!addedToInput)
				{
					if (Checks.isFuel(theEvent.getCursor().getType()) || theEvent.getCursor().getType() == Material.BUCKET)
					{
						addOrStack(data.fuel, theEvent.getCursor(), s -> data.fuel = s, theEvent::setCursor);
					}
				}
			}
		}
	}

	private boolean addOrStack(ItemStack current, ItemStack other, Consumer<ItemStack> setCurrent, Consumer<ItemStack> setOther)
	{
		if (current.getType().isAir())
		{
			setCurrent.accept(other);
			setOther.accept(new ItemStack(Material.AIR));
			return true;
		} else if (current.isSimilar(other))
		{
			if (current.getAmount() == current.getMaxStackSize())
				return false;

			int dataSize = current.getAmount() + other.getAmount();
			int cursorSize = other.getAmount() - dataSize;

			if (dataSize > current.getMaxStackSize())
				cursorSize = -cursorSize;

			cursorSize = Math.max(0, cursorSize);

			current.setAmount(Math.min(dataSize, current.getMaxStackSize()));
			if (cursorSize == 0)
			{
				setOther.accept(new ItemStack(Material.AIR));
			} else
			{
				other.setAmount(cursorSize);
			}
			return true;
		}
		return false;
	}

	// TODO: tick in inventory
	@Override
	public void tickInHand(PlayerItemContext context)
	{
		PocketData data = new PocketData(context.getItemStack());

		boolean isLit = data.burnTime > 0;
		boolean isCooking = data.cookTimeTotal > 0;

		if (isLit)
		{
			data.burnTime--;
		}

		// not currently cooking -> try to start cooking
		if (!isCooking)
		{
			// Check for valid recipe
			int cookTime = Checks.getCookTime(data.input);
			if (cookTime != 0)
			{
				if (isLit)
				{
					data.cookTimeTotal = cookTime;
				} else
				{
					// Check for valid fuel
					int burnTime = Checks.getBurnTime(data.fuel.getType());
					if (burnTime != 0)
					{
						// Start cooking
						data.burnTime = burnTime;
						data.fuel.setAmount(data.fuel.getAmount() - 1);
						if (data.fuel.getAmount() <= 0)
							data.fuel = new ItemStack(Material.AIR);
					}
				}
			}
		}

		if (isCooking && !isLit)
		{
			// Check for valid fuel
			int burnTime = Checks.getBurnTime(data.fuel.getType());
			if (burnTime != 0)
			{
				// Start cooking
				data.burnTime = burnTime;
				data.fuel.setAmount(data.fuel.getAmount() - 1);
				if (data.fuel.getAmount() <= 0)
					data.fuel = new ItemStack(Material.AIR);
				isLit = true;
			}
		}

		if (isLit && isCooking)
		{
			data.cookTime++;
			if (data.cookTime >= data.cookTimeTotal)
			{
				data.cookTimeTotal = 0;
				data.cookTime = 0;
				data.input.setAmount(data.input.getAmount() - 1);
				FurnaceRecipe recipe = Checks.getFurnaceRecipeByInput(data.input);
				context.getPlayer().getInventory().addItem(recipe.getResult());

				if (data.input.getAmount() <= 0)
					data.input = new ItemStack(Material.AIR);

				int j = Mth.floor(recipe.getExperience());
				float f1 = Mth.frac(recipe.getExperience());
				if (f1 != 0.0F && Math.random() < (double) f1)
				{
					++j;
				}

				//					context.getPlayer().giveExp(j);
				int finalJ = j;
				context.getWorld().spawn(context.getLocation(), ExperienceOrb.class, e -> e.setExperience(finalJ));
			}
		}
	}

	protected static class PocketData
	{
		private final NBT data;

		ItemStack fuel = MiscUtil.AIR;
		ItemStack input = MiscUtil.AIR;
		int burnTime;
		int cookTime;
		int cookTimeTotal;

		public PocketData(ItemStack itemStack)
		{
			data = ItemNBT.create(itemStack);
			cookTime = data.getInt("burnTime");
			burnTime = data.getInt("cookTime");
			cookTimeTotal = data.getInt("cookTimeTotal");
		}

		public void save()
		{
			data.save();
		}

		public ItemStack updateLore(ItemStack itemStack)
		{
			return ItemStackBuilder
				.editNonStatic(itemStack)
				.removeLore()
				.addLore(JSONMessage.create("Input: ").color(ChatColor.GRAY).then(input.getType().isAir() ? JSONMessage.create("none").color(ChatColor.DARK_GRAY) : MiscUtil.nameToComponent(input, ChatColor.WHITE).then(" x " + input.getAmount()).color(ChatColor.GRAY)))
				.addLore(JSONMessage.create("Fuel: ").color(ChatColor.GRAY).then(fuel.getType().isAir() ? JSONMessage.create("none").color(ChatColor.DARK_GRAY) : MiscUtil.nameToComponent(fuel, ChatColor.WHITE).then(" x " + fuel.getAmount()).color(ChatColor.GRAY)))
				.addLore(JSONMessage.create("Remaining Fuel: ").color(ChatColor.GRAY).then("" + burnTime).color("#" + Integer.toHexString(getFireColor(burnTime))))
				.addLore(JSONMessage.create("Progress: ").color(ChatColor.GRAY).then("" + cookTime).color(ChatColor.WHITE).then("/").color(ChatColor.WHITE).then("" + cookTimeTotal).color(ChatColor.WHITE))
				.buildItemStack();
		}
	}

	private static final int[] FIRE_COLORS = new int[] {0xff0000, 0xff5a00, 0xff9a00, 0xffce00, 0xffe808};

	private static int blend(int c1, int c2, float ratio)
	{
		return SilkLeavesBlock.blend(c1, c2, ratio);
	}

	private static int getFireColor(int burnTime)
	{
		if (burnTime == 0)
			return FIRE_COLORS[0];

		int loopTime = 200;

		float findex = (burnTime % loopTime) * (4f / (float) loopTime);
		findex = clamp(findex + (float) ((Math.random() - 0.5) * 0.75), 0, 4);

		if (burnTime % (loopTime * 2) > (burnTime % loopTime))
		{
			findex = 4.0f - findex;
		}

		int lowerIndex = (int) Math.floor(findex);
		int upperIndex = (int) Math.ceil(findex);

		float ratio = findex - lowerIndex;

		return blend(FIRE_COLORS[lowerIndex], FIRE_COLORS[upperIndex], ratio);
	}

	public static float clamp(float val, float min, float max)
	{
		return Math.max(min, Math.min(max, val));
	}
}
