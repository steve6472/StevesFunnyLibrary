package steve6472.funnylib.item.builtin.worldtools.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.worldtools.Boundable;
import steve6472.funnylib.item.builtin.worldtools.RectangleFillerItem;
import steve6472.funnylib.item.builtin.worldtools.SphereFillerItem;
import steve6472.funnylib.menu.Mask;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.components.NumberScroller;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.ItemSwapSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.menu.slots.buttons.ToggleButtonSlot;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class FillerMenu extends Menu
{
	UUID viewer = null;
	ItemStack itemStack;

	public boolean isSphere;
	private final FillFunctions fills;
	private final Boundable boundable;

	public static final int MIN_RADIUS = 2;
	public static final int MAX_RADIUS = 12;
	public static final double RADIUS_OFFSET = 0.1;

	public FillerMenu(Boundable boundable, String title, ItemStack itemStack, boolean sphere)
	{
		super(4, title, false);
		this.boundable = boundable;
		this.itemStack = itemStack.clone();
		allowPlayerInventory();
		this.isSphere = sphere;
		this.fills = new FillFunctions(this);
	}

	@Override
	protected void setup()
	{
		ItemNBT data = ItemNBT.create(itemStack);
		PdcNBT protData = data.protectedData();

		Mask background = new Mask()
			.addRow("RRRFFFAAA")
			.addRow("R RF FAAA")
			.addRow("R RFFFAAA")
			.addRow("  -- -   ")
			.addItem('F', () -> new IconSlot(ItemStackBuilder.quick(Material.CYAN_STAINED_GLASS_PANE, "Fill"), false))
			.addItem('R', () -> new IconSlot(ItemStackBuilder.quick(Material.RED_STAINED_GLASS_PANE, "Replace"), false))
			.addItem('A', () -> new ButtonSlot(ItemStackBuilder.quick(Material.LIME_STAINED_GLASS_PANE, "Apply"), false).setClick(click ->
			{
				ItemNBT itemData = nbtFromPlayersHandOrStack(null);
				if (itemData == null) return Response.cancel();
				boolean advanced = itemData.protectedData().getBoolean("advanced_fill", false);

				if (!isSphere)
				{
					if (advanced)
						fills.applyAdvancedRectangle(click, boundable);
					else
						fills.applyRectangle(click, boundable);
					return Response.exit();
				}

				if (advanced)
					fills.applyAdvancedSphere(click.player(), boundable);
				else
					fills.applySphere(click.player(), boundable);
				return Response.exit();
			}))
			.addItem('-', () -> new IconSlot(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""), false));

		applyMask(background);

		if (!isSphere)
		{
			setSlot(6, 3, new IconSlot(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""), false));
			setSlot(7, 3, new IconSlot(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""), false));
			setSlot(8, 3, new IconSlot(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""), false));
		}

		setSlot(1, 2,
			new ToggleButtonSlot(ItemStackBuilder.quick(Material.LIME_DYE, "Current: Fill"), ItemStackBuilder.quick(Material.RED_DYE, "Current: Replace"), false)
				.setClick((click, state) -> {

					ItemNBT itemData = nbtFromPlayersHandOrStack(null);
					if (itemData == null) return Response.cancel();
					itemData.protectedData().setBoolean("match_any_block", state);
					itemData.save();

					ItemSwapSlot slot = (ItemSwapSlot) getSlot(1, 1);
					slot.setDisabled(state);

					return Response.cancel();
				})
				.setToggled(protData.getBoolean("match_any_block", true))
		);

		PercentageFillComponent percentageFillComponent = new PercentageFillComponent(this);
		percentageFillComponent.setWindowPosition(3, 0);
		percentageFillComponent.isEnabled = percentageFillEnabled(data);
		addWindow(percentageFillComponent);

		AdvancedFillComponent advancedFillComponent = new AdvancedFillComponent(this);
		advancedFillComponent.setWindowPosition(3, 0);
		advancedFillComponent.isEnabled = advancedFillEnabled(data);
		addWindow(advancedFillComponent);

		Material match = matOrAir(Material.matchMaterial(protData.getString("match", Material.AIR.toString())));
		Material place = matOrAir(Material.matchMaterial(protData.getString("place", Material.AIR.toString())));

		addSetSlot(1, "match", new ItemStack(match));
		addSetSlot(4, "place", new ItemStack(place));

		((ItemSwapSlot) getSlot(1, 1)).setDisabled(protData.getBoolean("match_any_block", true));

		setSlot(0, 3,
			new ToggleButtonSlot(ItemStackBuilder.quick(Material.LIME_DYE, "Advanced Fill"), ItemStackBuilder.quick(Material.RED_DYE, "Advanced Fill"), true)
				.setClick((click, state) -> {
					ItemNBT itemData = nbtFromPlayersHandOrStack(null);
					if (itemData == null) return Response.cancel();
					itemData.protectedData().setBoolean("advanced_fill", state);
					itemData.save();

					// TODO: finish percentage fill
//					((ToggleButtonSlot) click.menu().getSlot(click.slot().getX() + 1, click.slot().getY())).getDisableComponent().setDisabled(!state);

					advancedFillComponent.isEnabled = advancedFillEnabled(itemData);
					percentageFillComponent.isEnabled = percentageFillEnabled(itemData);
					reload();

					return Response.cancel();
				})
				.setToggled(protData.getBoolean("advanced_fill", false))
		);

		// Visible only if Advanced Mask is true
		setSlot(1, 3,
			new ToggleButtonSlot(ItemStackBuilder.quick(Material.LIME_DYE, "Percentage Fill"), ItemStackBuilder.quick(Material.RED_DYE, "Percentage Fill"), true)
				.setClick((click, state) -> {
					ItemNBT itemData = nbtFromPlayersHandOrStack(null);
					if (itemData == null) return Response.cancel();
					itemData.protectedData().setBoolean("percentage_fill", !itemData.protectedData().getBoolean("percentage_fill", false));
					itemData.save();

					advancedFillComponent.isEnabled = advancedFillEnabled(itemData);
					percentageFillComponent.isEnabled = percentageFillEnabled(itemData);
					reload();

					return Response.cancel();
				})
//				.setToggled(protData.getBoolean("percentage_fill", false))
//				.setDisabledIcon(ItemStackBuilder.quick(Material.GRAY_STAINED_GLASS_PANE, ""))
//				.setDisabled(!protData.getBoolean("advanced_fill", false))
				.setDisabled(true)
				.setToggled(false)
		);

		setSlot(4, 3,
			new ItemSwapSlot(new ItemStack(itemStack.getType()), false, false)
				.setRightClickClear(true)
				.onPlace(item -> {
					Player player = getViewer();
					if (player == null) return;

					ItemStack hand = player.getInventory().getItem(EquipmentSlot.HAND);
					if (hand == null) return;
					hand.setType(item.getType());
					if (isSphere ? item.getType() != Material.POPPED_CHORUS_FRUIT : item.getType() != Material.SHULKER_SHELL)
					{
						ItemStackBuilder.modify(hand, ItemStackBuilder::glow);
					} else
					{
						ItemStackBuilder.modify(hand, ItemStackBuilder::removeGlow);
					}
					player.getInventory().setItem(EquipmentSlot.HAND, hand);
				})
				.setClearItem(() -> isSphere ? new ItemStack(Material.POPPED_CHORUS_FRUIT) : new ItemStack(Material.SHULKER_SHELL))
				.onClear(() -> {
					Player player = getViewer();
					if (player == null) return;

					ItemStack hand = player.getInventory().getItem(EquipmentSlot.HAND);
					if (hand == null) return;
					Material newType = isSphere ? Material.POPPED_CHORUS_FRUIT : Material.SHULKER_SHELL;
					hand.setType(newType);
					if (isSphere ? newType != Material.POPPED_CHORUS_FRUIT : newType != Material.SHULKER_SHELL)
					{
						ItemStackBuilder.modify(hand, ItemStackBuilder::glow);
					} else
					{
						ItemStackBuilder.modify(hand, ItemStackBuilder::removeGlow);
					}
					player.getInventory().setItem(EquipmentSlot.HAND, hand);
				})
		);

		if (!isSphere)
		{
			// Set itemStack back to null so it can not be used
			itemStack = null;
			return;
		}

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

	private boolean advancedFillEnabled(ItemNBT itemData)
	{
		PdcNBT data = itemData.protectedData();
		if (data.getBoolean("advanced_fill", false))
		{
			return !data.getBoolean("percentage_fill", false);
		}
		return false;
	}

	private boolean percentageFillEnabled(ItemNBT itemData)
	{
		PdcNBT data = itemData.protectedData();
		if (data.getBoolean("advanced_fill", false))
		{
			return data.getBoolean("percentage_fill", false);
		}
		return false;
	}

	ItemNBT nbtFromPlayersHandOrStack(ItemStack itemStack)
	{
		if (itemStack != null)
		{
			if (!(isSphere ? Items.getCustomItem(itemStack) instanceof SphereFillerItem : Items.getCustomItem(itemStack) instanceof RectangleFillerItem))
				return null;

			return ItemNBT.create(itemStack);
		}

		Player player = getViewer();
		if (player == null)
			return null;

		ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
		if (item == null || !(isSphere ? Items.getCustomItem(item) instanceof SphereFillerItem : Items.getCustomItem(item) instanceof RectangleFillerItem))
			return null;

		return ItemNBT.create(item);
	}

	private Material matOrAir(Material material)
	{
		return material == null ? Material.AIR : material;
	}

	private void addSetSlot(int x, String id, ItemStack a)
	{
		setSlot(x, 1,
			new ItemSwapSlot(a, true, false)
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
				.setDisabledIcon(ItemStackBuilder.quick(Material.RED_STAINED_GLASS_PANE, ""))
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
