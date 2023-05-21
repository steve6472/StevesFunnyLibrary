package steve6472.funnylib.category;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.StructureItem;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Messages;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 2/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Structures extends GenericStorage
{
	public Structures()
	{
		super(Structures::new);

		this.registerType("structure", GameStructure::new, obj -> ItemStackBuilder
			.create(obj.icon())
			.setName(obj.name() == null ? "Structure" : obj.name(), ChatColor.DARK_AQUA)
			.addLore(Messages.createLocationMessage("Size: ", obj.getSize().x, obj.getSize().y, obj.getSize().z))
			.buildItemStack(),
			GameStructure::toItem,
			itemStack ->
			{
				ItemNBT data = ItemNBT.create(itemStack);
				String icon = data.getString("icon", null);
				NBT compound = data.getCompound(StructureItem.KEY);
				compound.set3i("start", data.get3i("start"));
				compound.set3i("end", data.get3i("end"));
				GameStructure structure = GameStructure.structureFromNBT(compound);
				structure.setName(data.getString("name", null));
				structure.setIcon(icon == null ? Material.BOOK : Material.valueOf(icon));
				return structure;
			},
			itemStack ->
			{
				if (Items.getCustomItem(itemStack) != FunnyLib.STRUCTURE) return false;

				ItemNBT nbt = ItemNBT.create(itemStack);

				if (!nbt.hasCompound(StructureItem.KEY)) return false;

				return nbt.has3i("start") && nbt.has3i("end");
			});
	}

	@Override
	protected ItemStack createAddSlot()
	{
		return ItemStackBuilder
			.create(Material.MAP)
			.setName("Add Structure", ChatColor.WHITE)
			.addLore(JSONMessage.create("Click with Structure item on cursor").color(ChatColor.GRAY).setItalic(JSONMessage.ItalicType.FALSE))
			.addLore(JSONMessage.create("to add it to the end of the list").color(ChatColor.GRAY).setItalic(JSONMessage.ItalicType.FALSE))
			.buildItemStack();
	}

	@Override
	protected String storageId()
	{
		return "structures";
	}

	@Override
	protected String itemName()
	{
		return "Structure";
	}
}
