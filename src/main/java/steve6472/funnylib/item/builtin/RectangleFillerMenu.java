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
public class RectangleFillerMenu extends Menu
{
	private final ItemStack itemStack;

	public RectangleFillerMenu(ItemStack itemStack)
	{
		super(4, "Rectangle Filler", false);
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
			.addRow("---------")
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
		if (item == null || !(Items.getCustomItem(item) instanceof RectangleFillerItem))
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
