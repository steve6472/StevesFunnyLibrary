package steve6472.funnylib.menu.windows;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.Preconditions;
import steve6472.standalone.interactable.ex.ExpItems;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 7/9/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Popup extends Menu
{
	final boolean background;
	final int maxScroll;

	private PopupUserMenu userMenu;
	private Consumer<Click> closeFunction;

	public Popup(boolean background)
	{
		this(background, Integer.MAX_VALUE - 5);
	}

	public Popup(boolean background, int maxScroll)
	{
		super(6, "popup", true);
		this.background = background;
		this.maxScroll = maxScroll;
		setStickyWindow(true);
		userMenu = new PopupUserMenu(this);
	}

	@Override
	protected void setup()
	{
		Mask mask = new Mask()
			.addRow(".._____..")
			.addRow(".______X.")
			.addRow("._______.")
			.addRow(".______U.")
			.addRow(".______D.")
			.addRow("....P....")
			.addItem('_', () -> new IconSlot(new ItemStack(Material.AIR), true))
			.addItem('X', () -> new ButtonSlot(ExpItems.POPUP_CLOSE.newItemStack(), true)
				.setClick(c ->
				{
					if (closeFunction != null)
						closeFunction.accept(c);
					removeWindow();
					return Response.cancel();
				})
			)
			.addItem('U', () -> new ButtonSlot(MiscUtil.AIR, true)
				.setClick(c ->
				{
					userMenu.move(0, -1);
					// Update itself
					((ButtonSlot) c.slot()).setIcon(userMenu.getOffsetY() <= 0 ? MiscUtil.AIR : ExpItems.POPUP_UP.newItemStack());
					// Update down slot
					((ButtonSlot) c.menu()
						.getSlot(c.slot().getX(), c.slot().getY() + 1))
						.setIcon(userMenu.getOffsetY() >= userMenu.getOffsetLimits().w ? MiscUtil.AIR : ExpItems.POPUP_DOWN.newItemStack());
					return Response.cancel();
				})
			)
			.addItem('D', () -> new ButtonSlot(userMenu.getOffsetY() >= userMenu.getOffsetLimits().w ? MiscUtil.AIR : ExpItems.POPUP_DOWN.newItemStack(), true)
				.setClick(c ->
				{
					userMenu.move(0, 1);
					// Update itself
					((ButtonSlot) c.slot()).setIcon(userMenu.getOffsetY() >= userMenu.getOffsetLimits().w ? MiscUtil.AIR : ExpItems.POPUP_DOWN.newItemStack());
					// Update up slot
					((ButtonSlot) c.menu().getSlot(c.slot().getX(), c.slot().getY() - 1)).setIcon(userMenu.getOffsetY() <= 0 ? MiscUtil.AIR : ExpItems.POPUP_UP.newItemStack());
					return Response.cancel();
				})
			)
			.addItem('P', () -> new IconSlot(background ? ExpItems.POPUP_BACKGROUND.newItemStack() : ExpItems.POPUP_NO_BACKGROUND.newItemStack(), true))
			;
		applyMask(mask);
		limitOffset(0, 0, 0, 0);

		addWindow(userMenu);
	}

	/**
	 * This slot is right under the close button
	 * @param slot slot
	 */
	public void setExtraOption(Slot slot)
	{
		Preconditions.checkTrue(slot.isSticky(), "Extra slot has to be sticky!");
		setSlot(7, 2, slot);
		slot.updateSlot();
	}

	public void addCloseFunction(Consumer<Click> closeFunction)
	{
		this.closeFunction = closeFunction;
	}

	public void setPopupSlot(int x, int y, Slot slot)
	{
		Preconditions.checkTrue(x >= 0 && x < 6, "x is not within bounds (0 >= x < 6)");
		Preconditions.checkTrue(y >= 0 && y < userMenu.getOffsetLimits().w + 4, "y is not within bounds (0 >= " + y + " < " + (userMenu.getOffsetLimits().w + 4) + ")");
		userMenu.setSlot(x, y, slot);
	}

	private static class PopupUserMenu extends Menu
	{
		public PopupUserMenu(Popup popup)
		{
			super(4, "popup_user_menu", false);
			setStickyWindow(true);
			setWindowBounds(6, 4);
			setWindowPosition(1, 1);
			limitOffset(0, 0, 0, popup.maxScroll);
		}

		@Override
		protected void setup()
		{

		}
	}
}
