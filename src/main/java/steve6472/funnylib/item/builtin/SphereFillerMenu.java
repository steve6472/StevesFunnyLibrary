package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.menu.slots.buttons.ToggleButtonSlot;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.workdistro.impl.PlaceBlockWorkload;
import steve6472.funnylib.workdistro.impl.ReplaceBlockWorkload;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class SphereFillerMenu extends Menu
{
	private static final int MIN_RADIUS = 2;
	private static final int MAX_RADIUS = 8;

	private final ItemStack itemStack;

	public SphereFillerMenu(ItemStack itemStack)
	{
		super(4, "Sphere Filler", false);
		this.itemStack = itemStack;
		allowPlayerInventory();
	}

	@Override
	protected void setup()
	{
		Mask background = new Mask()
			.addRow("RRRFFFAAA")
			.addRow("R RF FAAA")
			.addRow("RRRFFFAAA")
			.addRow("  ----   ")
			.addItem('F', () -> new IconSlot(ItemStackBuilder.quick(Material.CYAN_STAINED_GLASS_PANE, "Fill"), true))
			.addItem('R', () -> new IconSlot(ItemStackBuilder.quick(Material.RED_STAINED_GLASS_PANE, "Replace"), true))
			.addItem('A', () -> new ButtonSlot(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "Apply"), true).setClick(this::apply))
			.addItem('-', () -> new IconSlot(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""), true));

		applyMask(background);

		ItemNBT data = ItemNBT.create(itemStack);
		Material match = matOrAir(Material.matchMaterial(data.getString("match", Material.AIR.toString())));
		Material place = matOrAir(Material.matchMaterial(data.getString("place", Material.AIR.toString())));

		setSlot(1, 1, new ButtonSlot(new ItemStack(match), true).setClick(click -> itemSwap(click, i -> i.getType().isBlock(), false)));
		setSlot(4, 1, new ButtonSlot(new ItemStack(place), true).setClick(click -> itemSwap(click, i -> i.getType().isBlock(), true)));

		setSlot(0, 3, new ToggleButtonSlot(ItemStackBuilder.quick(Material.LIME_DYE, "Advanced Fill"), ItemStackBuilder.quick(Material.RED_DYE, "Advanced Fill"), true).setClick((click, state) ->
		{
			return Response.cancel();
		}));

		// Visible only if Advanced Mask is true
		setSlot(1, 3, new ToggleButtonSlot(ItemStackBuilder.quick(Material.LIME_DYE, "Percentage Fill"), ItemStackBuilder.quick(Material.RED_DYE, "Percentage Fill"), true));

		setSlot(6, 3, new ButtonSlot(ItemStackBuilder.quick(Material.REDSTONE, "Decrease Size to: " + clampRadius(data.getInt("radius", 2) - 1)), true).setClick(click ->
		{
			ItemStack item = click.player().getInventory().getItem(EquipmentSlot.HAND);
			if (item == null || !(Items.getCustomItem(item) instanceof SphereFillerItem))
				return Response.exit();

			ItemNBT itemData = ItemNBT.create(item);
			int decreasedRadius = clampRadius(itemData.getInt("radius", 2) - 1);
			itemData.setInt("radius", decreasedRadius);
			itemData.save();
			if (decreasedRadius <= MIN_RADIUS)
			{
				click.slot().updateSlot(ItemStackBuilder.quick(Material.FIREWORK_STAR, "Minimum Radius Reached (" + MIN_RADIUS + ")"));
			} else
			{
				click.slot().updateSlot(ItemStackBuilder.quick(Material.REDSTONE, "Decrease Size to: " + clampRadius(decreasedRadius - 1)));
			}
			click.menu().getSlot(click.slot().getX() + 1, click.slot().getY()).updateSlot(ItemStackBuilder.quick(Material.HEART_OF_THE_SEA, "Current Size: " + itemData.getInt("radius", 2)));
			click.menu().getSlot(click.slot().getX() + 2, click.slot().getY()).updateSlot(ItemStackBuilder.quick(Material.EMERALD, "Increase Size to: " + clampRadius(itemData.getInt("radius", 2) + 1)));

			return Response.cancel();
		}));

		setSlot(7, 3, new IconSlot(ItemStackBuilder.quick(Material.HEART_OF_THE_SEA, "Current Size: " + data.getInt("radius", 2)), true));

		setSlot(8, 3, new ButtonSlot(ItemStackBuilder.quick(Material.EMERALD, "Increase Size to: " + clampRadius(data.getInt("radius", 2) + 1)), true).setClick(click ->
		{
			ItemStack item = click.player().getInventory().getItem(EquipmentSlot.HAND);
			if (item == null || !(Items.getCustomItem(item) instanceof SphereFillerItem))
				return Response.exit();

			ItemNBT itemData = ItemNBT.create(item);
			int increasedRadius = clampRadius(itemData.getInt("radius", 2) + 1);
			itemData.setInt("radius", increasedRadius);
			itemData.save();
			if (increasedRadius >= MAX_RADIUS)
			{
				click.slot().updateSlot(ItemStackBuilder.quick(Material.FIREWORK_STAR, "Maximum Radius Reached (" + MAX_RADIUS + ")"));
			} else
			{
				click.slot().updateSlot(ItemStackBuilder.quick(Material.EMERALD, "Increase Size to: " + clampRadius(increasedRadius + 1)));
			}
			click.menu().getSlot(click.slot().getX() - 1, click.slot().getY()).updateSlot(ItemStackBuilder.quick(Material.HEART_OF_THE_SEA, "Current Size: " + itemData.getInt("radius", 2)));
			click.menu().getSlot(click.slot().getX() - 2, click.slot().getY()).updateSlot(ItemStackBuilder.quick(Material.REDSTONE, "Decrease Size to: " + clampRadius(itemData.getInt("radius", 2) - 1)));

			return Response.cancel();
		}));
	}

	private static int clampRadius(int value)
	{
		return clamp(value, MIN_RADIUS, MAX_RADIUS);
	}

	private static int clamp(int value, int min, int max)
	{
		return Math.max(min, Math.min(max, value));
	}

	private Material matOrAir(Material material)
	{
		return material == null ? Material.AIR : material;
	}

	private Response apply(Click click)
	{
		Vector3i pos1 = new Vector3i();
		Vector3i pos2 = new Vector3i();

		ItemStack item = click.player().getInventory().getItem(EquipmentSlot.HAND);
		if (item == null || !(Items.getCustomItem(item) instanceof RectangleFillerItem))
			return Response.exit();

		ItemNBT data = ItemNBT.create(item);

		if (!data.has3i("pos1") || !data.has3i("pos2"))
		{
			return Response.exit();
		}

		data.get3i("pos1", pos1);
		data.get3i("pos2", pos2);

		Vector3i minPos = pos1.min(pos2, new Vector3i());
		Vector3i maxPos = pos1.max(pos2, new Vector3i()).add(1, 1, 1);

		Material match = Material.matchMaterial(data.getString("match", Material.AIR.toString()));
		Material place = Material.matchMaterial(data.getString("place", Material.AIR.toString()));

		boolean hasMatch = data.hasString("match");

		for (int i = minPos.x; i < maxPos.x; i++)
		{
			for (int j = minPos.y; j < maxPos.y; j++)
			{
				for (int k = minPos.z; k < maxPos.z; k++)
				{
					if (hasMatch)
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new ReplaceBlockWorkload(click.player().getWorld(), i, j, k, match, place));
					} else
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new PlaceBlockWorkload(click.player().getWorld(), i, j, k, place));
					}
				}
			}
		}

		return Response.exit();
	}

	private Response itemSwap(Click click, Predicate<ItemStack> itemCheck, boolean isFill)
	{
		ItemStack item = click.player().getInventory().getItem(EquipmentSlot.HAND);
		if (item == null || !(Items.getCustomItem(item) instanceof SphereFillerItem))
			return Response.exit();

		ItemNBT data = ItemNBT.create(item);

		if (click.type().isRightClick())
		{
			data.remove(isFill ? "place" : "match");
			data.save();
			click.slot().updateSlot(new ItemStack(Material.AIR));
			return Response.cancel();
		}

		if (click.itemOnCursor() == null || click.itemOnCursor().getType().isAir())
		{
			return Response.setItemToCursor(new ItemStack(matOrAir(Material.matchMaterial(data.getString(isFill ? "place" : "match", "AIR")))));
		}

		if (itemCheck.test(click.itemOnCursor()))
		{
			data.setString(isFill ? "place" : "match", click.itemOnCursor().getType().toString());
			data.save();

			click.slot().updateSlot(ItemStackBuilder.create(click.itemOnCursor().getType()).addLore(JSONMessage.create("Right click to clear (set air)").color(ChatColor.GRAY)).buildItemStack());
			return Response.cancel();
		}

		return Response.cancel();
	}
}
