package steve6472.funnylib.packgen;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.packgen.gens.ModelGen;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 2/14/2024
 * Project: StevesFunnyLibrary <br>
 */
public class Model
{
	public final String modelId;
	public Material materialOverride;
	public int customModelData;
	public ModelGen modelGenerator;

	public Model(String modelId)
	{
		this.modelId = modelId;
	}

	@Override
	public String toString()
	{
		return "Model{" + "materialOverride=" + materialOverride + ", customModelData=" + customModelData + ", modelGenerator=" + modelGenerator + '}';
	}

	public ItemStack makeItem()
	{
		// Custom model data get set in NbtPacketEditor
		ItemStackBuilder itemStackBuilder = ItemStackBuilder.create(materialOverride);
		itemStackBuilder.setCustomModelData(customModelData);
		itemStackBuilder.nbt().protectedData().setString("_model", modelId);
		return itemStackBuilder.buildItemStack();
	}
}
