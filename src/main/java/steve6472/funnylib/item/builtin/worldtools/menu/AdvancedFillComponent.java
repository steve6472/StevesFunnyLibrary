package steve6472.funnylib.item.builtin.worldtools.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Slot;
import steve6472.funnylib.menu.slots.ItemSwapSlot;
import steve6472.funnylib.menu.slots.buttons.ClickNumberScroller;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 2/7/2024
 * Project: StevesFunnyLibrary <br>
 */
public class AdvancedFillComponent extends Menu
{
	private final FillerMenu fillerMenu;
	private ClickNumberScroller airScroll;

	public boolean isEnabled = false;

	public AdvancedFillComponent(FillerMenu fillerMenu)
	{
		super(3, "advanced_fill", false);
		this.fillerMenu = fillerMenu;

		setWindowBounds(3, 3);
		limitOffset(0, 0, 0, 0);
	}

	@Override
	protected void setup()
	{
		ItemNBT data = ItemNBT.create(fillerMenu.itemStack);

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				if (i == 2 && j == 2) continue;
				String material = data.protectedData().getOrCreateCompound("advanced").getString("slot_" + (i + j * 3), Material.AIR.toString());
				addSetSlot(i, j, "slot_" + (i + j * 3), new ItemStack(matOrAir(Material.matchMaterial(material))));
			}
		}

		setSlot(2, 2,
			airScroll = new ClickNumberScroller(
				createAirIcon(),
				() -> 0,
				() -> {
					ItemNBT itemData = fillerMenu.nbtFromPlayersHandOrStack(fillerMenu.itemStack);
					if (itemData == null)
						return 9;

					int maxAir = 9;
					int slots = (int) itemData.protectedData().getOrCreateCompound("advanced").getKeys().stream().filter(p -> p.startsWith("slot_")).count();
					return maxAir - slots;
				},
				() -> {
					ItemNBT itemData = fillerMenu.nbtFromPlayersHandOrStack(fillerMenu.itemStack);
					if (itemData == null)
						return 0;

					return itemData.protectedData().getOrCreateCompound("advanced").getInt("air_count", 0);
				},
				(value) -> {
					ItemNBT itemData = fillerMenu.nbtFromPlayersHandOrStack(fillerMenu.itemStack);
					if (itemData == null) return;

					NBT advanced = itemData.protectedData().getOrCreateCompound("advanced");
					advanced.setInt("air_count", value);
					itemData.protectedData().setCompound("advanced", advanced);
					itemData.save();
				}
			)
		);
	}

	private void addSetSlot(int x, int y, String id, ItemStack a)
	{
		setSlot(x, y,
			new ItemSwapSlot(a, true, false)
				.setItemCheck(item -> FillFunctions.blockOrLiquid(item.getType()).isBlock())
				.onPlace(item ->
				{
					ItemNBT itemData = fillerMenu.nbtFromPlayersHandOrStack(null);
					if (itemData == null) return;
					NBT advanced = itemData.protectedData().getOrCreateCompound("advanced");
					advanced.setString(id, item.getType().toString());
					itemData.protectedData().setCompound("advanced", advanced);
					itemData.save();

					airScroll.update();
				})
				.onClear(() ->
				{
					ItemNBT itemData = fillerMenu.nbtFromPlayersHandOrStack(null);
					if (itemData == null) return;
					if (!itemData.protectedData().hasCompound("advanced")) return;
					NBT advanced = itemData.protectedData().getCompound("advanced");
					advanced.remove(id);
					itemData.protectedData().setCompound("advanced", advanced);
					itemData.save();

					airScroll.update();
				})
		);
	}

	private Material matOrAir(Material material)
	{
		return material == null ? Material.AIR : material;
	}

	@Override
	public Slot getSlot(int x, int y)
	{
		if (!isEnabled)
			return null;

		return super.getSlot(x, y);
	}

	private ItemStack createAirIcon()
	{
		return ItemStackBuilder.quick(Material.GLASS_BOTTLE, "Air");
	}
}
