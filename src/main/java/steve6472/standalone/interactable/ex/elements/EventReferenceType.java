package steve6472.standalone.interactable.ex.elements;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.json.INBT;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.interactable.ex.Expression;
import steve6472.standalone.interactable.ex.event.InputType;

import java.util.Collections;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public class EventReferenceType<T> implements IElementType, INBT
{
	private final String label;
	private final int ordinal;
	private final InputType<T> inputType;
	private String inputId;

	public EventReferenceType(String label, int ordinal, InputType<T> inputType)
	{
		this.label = label;
		this.ordinal = ordinal;
		this.inputType = inputType;
	}

	@Override
	public String label()
	{
		return label;
	}

	@Override
	public int ordinal()
	{
		return ordinal;
	}

	@Override
	public ItemStack item()
	{
		if (inputId == null || inputId.isEmpty())
			return inputType.getEmptyIcon();
		else
			return ItemStackBuilder.editNonStatic(inputType.getEmptyIcon()).addLore(JSONMessage.create("Set to: ").color(ChatColor.GRAY).then(inputId, ChatColor.GOLD)).buildItemStack();
	}

	public InputType<T> getInputType()
	{
		return inputType;
	}

	public String getInputId()
	{
		return inputId;
	}

	public Response action(Click click, Menu menu, Expression expression)
	{
		if (click.itemOnCursor() != null && !click.itemOnCursor().getType().isAir())
			return Response.cancel();

//		openPopup(click.player(), menu);

		new AnvilGUI.Builder()
			.onComplete((completion) ->
			{
				inputId = completion.getText();
				return Collections.singletonList(AnvilGUI.ResponseAction.close());
			})
			.text(" ")
			.itemLeft(new ItemStack(Material.PAPER))
			.title("Reference")
			.plugin(FunnyLib.getPlugin())
			.open(click.player());

		// Open selection
		return Response.cancel();
	}

	/*
	private Menu openPopup(Player player, Menu menu)
	{
		menu.applyMask(ExpressionMenu.POPUP);
		menu.applyMask(ExpressionMenu.POPUP_NO_BACKGROUND);
		MenuBuilder builder = MenuBuilder.create(3, "POPUP_MENU_REFERENCE");
		builder.limitOffset(0, 0, 0, Integer.MAX_VALUE);
//		exp.createPopup(builder);

		Menu popupMenu = builder.build();
		menu.setMetadata("popup", popupMenu);
//		MetaUtil.setMeta(player, "target_exp", exp);
		MetaUtil.setMeta(player, "target_exp_type", ordinal());
		popupMenu.overlay(menu, 1, 1, 6, 4);

		return popupMenu;
	}*/

	@Override
	public void toNBT(NBT compound)
	{
		compound.setString("input_id", inputId);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		inputId = compound.getString("input_id", null);
	}
}
