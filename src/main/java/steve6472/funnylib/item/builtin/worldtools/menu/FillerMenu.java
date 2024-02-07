package steve6472.funnylib.item.builtin.worldtools.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.RectangleFillerItem;
import steve6472.funnylib.item.builtin.worldtools.SphereFillerItem;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.components.NumberScroller;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.ItemSwapSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.menu.slots.buttons.ToggleButtonSlot;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.workdistro.impl.PlaceBlockWorkload;
import steve6472.funnylib.workdistro.impl.ReplaceBlockWorkload;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class FillerMenu extends Menu
{
	private UUID viewer = null;

	private static final int MIN_RADIUS = 2;
	private static final int MAX_RADIUS = 8;

	private ItemStack itemStack;

	public FillerMenu(String title, ItemStack itemStack)
	{
		super(4, title, false);
		this.itemStack = itemStack.clone();
		allowPlayerInventory();
	}

	@Override
	protected void setup()
	{
		ItemNBT data = ItemNBT.create(itemStack);

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

		Material match = matOrAir(Material.matchMaterial(data.getString("match", Material.AIR.toString())));
		Material place = matOrAir(Material.matchMaterial(data.getString("place", Material.AIR.toString())));

		addSetSlot(1, 1, "match", new ItemStack(match));
		addSetSlot(4, 1, "place", new ItemStack(place));

		setSlot(0, 3,
			new ToggleButtonSlot(ItemStackBuilder.quick(Material.LIME_DYE, "Advanced Fill"), ItemStackBuilder.quick(Material.RED_DYE, "Advanced Fill"), true)
				.setClick((click, state) -> {
					ItemNBT itemData = nbtFromPlayersHandOrStack(null);
					if (itemData == null) return Response.cancel();
					boolean advancedFill = !itemData.protectedData().getBoolean("advanced_fill", false);
					itemData.protectedData().setBoolean("advanced_fill", advancedFill);
					itemData.save();

					((ToggleButtonSlot) click.menu().getSlot(click.slot().getX() + 1, click.slot().getY())).getDisableComponent().setDisabled(!advancedFill);

					return Response.cancel();
				})
				.setToggled(data.protectedData().getBoolean("advanced_fill", false))
		);

		// Visible only if Advanced Mask is true
		setSlot(1, 3, new ToggleButtonSlot(ItemStackBuilder.quick(Material.LIME_DYE, "Percentage Fill"), ItemStackBuilder.quick(Material.RED_DYE, "Percentage Fill"), true)
			.setClick((click, state) -> {
				ItemNBT itemData = nbtFromPlayersHandOrStack(null);
				if (itemData == null) return Response.cancel();
				itemData.protectedData().setBoolean("percentage_fill", !itemData.protectedData().getBoolean("percentage_fill", false));
				itemData.save();

				return Response.cancel();
			})
			.setToggled(data.getBoolean("percentage_fill", false))
			.setDisabledIcon(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""))
			.setDisabled(!data.protectedData().getBoolean("advanced_fill", false))
		);

		NumberScroller numberScroller = new NumberScroller(
			() -> MIN_RADIUS,
			() -> MAX_RADIUS,
			() ->
			{
				ItemNBT itemData = nbtFromPlayersHandOrStack(itemStack);
				if (itemData == null)
					return 0;

				return itemData.protectedData().getInt("radius", 2);
				},
			(newValue) ->
			{
				ItemNBT itemData = nbtFromPlayersHandOrStack(itemStack);
				if (itemData == null)
					return;

				itemData.protectedData().setInt("radius", newValue);
				itemData.save();
			});
		numberScroller.setWindowPosition(6, 3);
		addWindow(numberScroller);

		// Set itemStack back to null so it can not be used
		itemStack = null;
	}

	private ItemNBT nbtFromPlayersHandOrStack(ItemStack itemStack)
	{
		if (itemStack != null)
		{
			if (!(Items.getCustomItem(itemStack) instanceof SphereFillerItem))
				return null;

			return ItemNBT.create(itemStack);
		}

		Player player = getViewer();
		if (player == null)
			return null;

		ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
		if (item == null || !(Items.getCustomItem(item) instanceof SphereFillerItem))
			return null;

		return ItemNBT.create(item);
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

	private void addSetSlot(int x, int y, String id, ItemStack a)
	{
		setSlot(x, y,
			new ItemSwapSlot(a, true, true)
				.setItemCheck(item -> item.getType().isBlock())
				.onPlace(item ->
				{
					ItemNBT itemData = nbtFromPlayersHandOrStack(null);
					if (itemData == null) return;
					itemData.protectedData().setString(id, item.getType().toString());
					itemData.save();
				})
				.onClear(() ->
				{
					ItemNBT itemData = nbtFromPlayersHandOrStack(null);
					if (itemData == null) return;
					itemData.protectedData().remove(id);
					itemData.save();
				})
		);
	}

	@Override
	public void showToPlayer(Player player)
	{
		if (viewer == null)
		{
			viewer = player.getUniqueId();
			super.showToPlayer(player);
		} else
		{
			throw new IllegalStateException("This menu already has a viewer!");
		}
	}

	private Player getViewer()
	{
		if (viewer == null)
			return null;
		return Bukkit.getPlayer(viewer);
	}
}
