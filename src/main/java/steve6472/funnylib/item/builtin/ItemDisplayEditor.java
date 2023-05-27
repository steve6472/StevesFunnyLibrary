package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.context.*;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.menu.ArbitraryData;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.Collection;

/**
 * Created by steve6472
 * Date: 5/24/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ItemDisplayEditor extends CustomItem
{
	@Override
	public String id()
	{
		return "item_display_editor";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.ARMOR_STAND).setName("Item Display Editor", ChatColor.DARK_AQUA).glow().buildItemStack();
	}

	public void interact(PlayerItemContext context)
	{
		Collection<Entity> nearbyEntities = context
			.getWorld()
			.getNearbyEntities(context.getLocation(), 2, 2, 2, e -> e instanceof ItemDisplay);

		if (nearbyEntities.isEmpty())
		{
			context.getPlayer().sendMessage(ChatColor.RED + "No Item Display not found.");
			return;
		}

		if (nearbyEntities.size() == 1)
		{
			entityEditor.setData("entity", nearbyEntities.stream().findAny().get()).build().showToPlayer(context.getPlayer());
			return;
		}

		entityList.setData("entities", nearbyEntities).build().showToPlayer(context.getPlayer());
	}

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		interact(context);
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		interact(context.playerContext());
	}

	@Override
	public void useOnEntity(PlayerEntityContext context, CancellableResult result)
	{
		interact(context.playerContext());
	}

	MenuBuilder entityEditor = MenuBuilder.create(6, "Item Display Editor")
		.customBuilder(b ->
		{
			Entity entity = b.getData("entity", Entity.class);
			if (entity instanceof ItemDisplay id)
			{
				ItemStack itemStack = id.getItemStack();
				if (itemStack == null || itemStack.getType().isAir())
					itemStack = ItemStackBuilder.create(Material.STRUCTURE_VOID).setName("none", ChatColor.AQUA).buildItemStack();
				b.slot(1, 1, SlotBuilder.create(itemStack).allow(InventoryAction.SWAP_WITH_CURSOR, InventoryAction.CLONE_STACK)
					.onClick(ClickType.LEFT, (click, menu) ->
					{
						((ItemDisplay) entity).setItemStack(click.itemOnCursor());
						return Response.cancel();
					})
					.onClick(ClickType.MIDDLE, (click, menu) -> Response.allow()));
			}
		})
		.allowPlayerInventory();

	MenuBuilder entityList = MenuBuilder.create(6, "Select Item Display")
		.customBuilder(b ->
		{
			Collection<Entity> entities = b.getArbitraryData().getData("entities", Collection.class);
			int i = 0;
			for (Entity entity : entities)
			{
				if (entity instanceof ItemDisplay id)
				{
					ItemStack itemStack = id.getItemStack();
					if (itemStack == null || itemStack.getType().isAir())
						itemStack = ItemStackBuilder.create(Material.STRUCTURE_VOID).setName("none", ChatColor.AQUA).buildItemStack();

					b.slot(i, SlotBuilder.create(itemStack).allow(InventoryAction.PICKUP_ALL).onClick(ClickType.LEFT, ((click, menu) ->
					{
						ArbitraryData redirectData = new ArbitraryData();
						redirectData.setData("entity", entity);
						return Response.redirect(entityEditor, redirectData);
					})));
				}
				i++;
			}
		});
}
