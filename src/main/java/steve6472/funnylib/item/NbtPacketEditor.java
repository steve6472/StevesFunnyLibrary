package steve6472.funnylib.item;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.packgen.Model;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 6/17/2023
 * Project: StevesFunnyLibrary <br>
 */
public class NbtPacketEditor implements Listener
{
	/**
	 * If this setting is off, shit breaks
	 * I fucking hate how client side the creative inventory is
	 */
	private static final boolean CREATIVE_SHOW_DATA = true;

	private final Plugin plugin;
	private final List<PacketAdapter> adapters = new LinkedList<>();

	public NbtPacketEditor(Plugin plugin)
	{
		this.plugin = plugin;
		registerListener(plugin);
	}

	private void registerListener(Plugin plugin)
	{
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		PacketAdapter setSlotListener = new PacketAdapter(plugin, PacketType.Play.Server.SET_SLOT)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (CREATIVE_SHOW_DATA && event.getPlayer().getGameMode() == GameMode.CREATIVE)
					return;

				PacketContainer packet = event.getPacket();
				ItemStack item = packet.getItemModifier().read(0);
				item = removeNBT(item);
				packet.getItemModifier().write(0, item);
			}
		};
		manager.addPacketListener(setSlotListener);
		adapters.add(setSlotListener);

		PacketAdapter windowItemsListener = new PacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				if (CREATIVE_SHOW_DATA && event.getPlayer().getGameMode() == GameMode.CREATIVE)
					return;

				PacketContainer packet = event.getPacket();
				List<ItemStack> items = packet.getItemListModifier().readSafely(0);

				items.replaceAll(itemStack -> removeNBT(itemStack));

				packet.getItemListModifier().write(0, items);
			}
		};
		manager.addPacketListener(windowItemsListener);
		adapters.add(windowItemsListener);
	}

	public void unregister()
	{
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		for (PacketAdapter adapter : adapters)
		{
			manager.removePacketListener(adapter);
		}
	}

	private ItemStack removeNBT(ItemStack itemStack)
	{
		if (!itemStack.hasItemMeta())
			return itemStack;

		ItemStack copy = itemStack.clone();
		ItemMeta meta = copy.getItemMeta();
		ItemNBT nbt = ItemNBT.create(copy, meta);

		if (nbt.hasCompound("_protected"))
		{
			NBT aProtected = nbt.getCompound("_protected");
			nbt.remove("_protected");

			String modelId = aProtected.getString("_model", null);
			if (modelId != null)
			{
				Model model = FunnyLib.packEngine.models.getModel(modelId);
				if (model != null)
				{
					meta.setCustomModelData(model.customModelData);
				}
			}
		} else
		{
			return itemStack;
		}

		nbt.save();
		return nbt.getItemStack();
	}

	@EventHandler
	public void changeGameMode(PlayerGameModeChangeEvent e)
	{
		if (CREATIVE_SHOW_DATA)
			Bukkit.getScheduler().runTaskLater(plugin, () -> e.getPlayer().updateInventory(), 0);
	}
}
