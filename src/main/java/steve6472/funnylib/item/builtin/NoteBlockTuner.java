package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.CustomNoteBlocks;
import steve6472.funnylib.blocks.builtin.CustomNoteBlockData;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 5/24/2023
 * Project: StevesFunnyLibrary <br>
 */
public class NoteBlockTuner extends CustomItem implements TickInHandEvent
{
	@Override
	public String id()
	{
		return "note_block_tuner";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.DEBUG_STICK).setName("Note Block Tuner", ChatColor.DARK_AQUA).buildItemStack();
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (!useType.isRight()) return;
		if (!context.isPlayerSneaking()) return;
		BlockData blockData = context.getBlock().getBlockData();
		if (blockData instanceof NoteBlock)
		{
			CustomBlockData customBlockData = Blocks.getBlockData(context.getBlockLocation());

			if (!FunnyLib.getSettings().enableCustomNoteBlocks && customBlockData != null)
			{
				result.setCancelled(true);
				return;
			}

			/*entityEditor
				.setData("loc", context.getBlockLocation())
				.setData("player", context.getPlayer())
				.build()
				.showToPlayer(context.getPlayer());*/
		}
		result.setCancelled(true);
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		RayTraceResult rayTraceResult = context.getPlayer().rayTraceBlocks(5, FluidCollisionMode.NEVER);
		if (rayTraceResult == null) return;
		Block hitBlock = rayTraceResult.getHitBlock();
		if (hitBlock == null) return;
		if (hitBlock.getType() != Material.NOTE_BLOCK) return;

		if (FunnyLib.getSettings().enableCustomNoteBlocks)
		{
			CustomBlockData blockData = Blocks.getBlockData(hitBlock.getLocation());
			if (blockData == null)
				return;
			if (blockData instanceof CustomNoteBlockData noteData)
			{
				Note note = noteData.getNote();
				noteBlockTitle(context.getPlayer(), note.getTone() + (note.isSharped() ? "#" : ""), CustomNoteBlocks.colorByNote(note), noteData.getInstrument().name());
			}
		} else
		{
			BlockData blockData = hitBlock.getBlockData();
			if (blockData instanceof NoteBlock nb)
			{
				Note note = nb.getNote();
				noteBlockTitle(context.getPlayer(), note.getTone() + (note.isSharped() ? "#" : ""), CustomNoteBlocks.colorByNote(note), nb.getInstrument().name());
			}
		}
	}

	private static void noteBlockTitle(Player player, String note, int color, String instrument)
	{
		player.sendTitle(JSONMessage.create("Note: ").then(note).color(Color.fromRGB(color)).toLegacy(), JSONMessage.create("Instrument: " + instrument).toLegacy(), 0, 5, 0);
	}

	/*private static SlotBuilder note(int noteId)
	{
		Note note = new Note(noteId);
		int color = CustomNoteBlocks.colorByNote(note);
		String label = note.getTone() + (note.isSharped() ? "#" : "");

		ItemStack item = ItemStackBuilder
			.create(Material.LEATHER_CHESTPLATE)
			.setHideFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
			.setArmorColor(color)
			.setName(JSONMessage.create(label).color(Color.fromRGB(color)))
			.buildItemStack();

		return SlotBuilder.create(item).allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_ALL).allow(ClickType.LEFT, ClickType.RIGHT).onClick((c, m) ->
		{
			ArbitraryData data = m.getPassedData();
			Location loc = data.getData("loc", Location.class);

			Player player = data.getData("player", Player.class);
			if (FunnyLib.getSettings().enableCustomNoteBlocks)
			{
				CustomBlockData blockData = Blocks.getBlockData(loc);
				if (blockData == null)
				{
					player.sendMessage(ChatColor.RED + "Old block, upgrading");
					BlockData oldData = loc.getBlock().getBlockData();
					Blocks.setBlockState(loc, FunnyLib.NOTE_BLOCK.getDefaultState());
					blockData = Blocks.getBlockData(loc);

					if (blockData instanceof CustomNoteBlockData noteBlockData)
					{
						if (oldData instanceof NoteBlock nb)
						{
							noteBlockData.setInstrument(nb.getInstrument());
							noteBlockData.setNote(nb.getNote());
							noteBlockData.setPowered(nb.isPowered());
						} else
						{
							player.sendMessage(ChatColor.RED + "Old not a Noteblock ?");
							return Response.exit();
						}
					} else
					{
						player.sendMessage(ChatColor.RED + "Not a Custom Noteblock ?");
						return Response.exit();
					}
				}

				if (blockData instanceof CustomNoteBlockData noteBlockData)
				{
					noteBlockData.setNote(note);
					noteBlockData.playNote(new BlockContext(loc), player);
					return Response.cancel();
				} else
				{
					player.sendMessage(ChatColor.RED + "Not a Custom Noteblock ?");
					return Response.exit();
				}
			} else
			{
				// Vanilla
				Block block = loc.getBlock();
				BlockData blockData = block.getBlockData();
				if (blockData instanceof NoteBlock noteBlock)
				{
					noteBlock.setNote(note);
					block.setBlockData(noteBlock);
					player.playNote(loc, noteBlock.getInstrument(), note);
					return Response.cancel();
				} else
				{
					return Response.exit();
				}
			}
		});
	}

	MenuBuilder entityEditor = MenuBuilder.create(6, "Note Block Tuner")
		.slot(2, 1, note(0))
		.slot(3, 1, note(1))
		.slot(5, 1, note(2))
		.slot(6, 1, note(3))

		.slot(1, 2, note(4))
		.slot(2, 2, note(5))
		.slot(3, 2, note(6))
		.slot(4, 2, note(7))
		.slot(5, 2, note(8))
		.slot(6, 2, note(9))
		.slot(7, 2, note(10))

		.slot(1, 3, note(11))
		.slot(2, 3, note(12))
		.slot(3, 3, note(13))
		.slot(4, 3, note(14))
		.slot(5, 3, note(15))
		.slot(6, 3, note(16))
		.slot(7, 3, note(17))

		.slot(1, 4, note(18))
		.slot(2, 4, note(19))
		.slot(3, 4, note(20))
		.slot(4, 4, note(21))
		.slot(5, 4, note(22))
		.slot(6, 4, note(23))
		.slot(7, 4, note(24))

		.customBuilder((m) -> {
			Location loc = m.getData("loc", Location.class);
			Block block = loc.getBlock();
			BlockData blockData = block.getBlockData();
			if (blockData instanceof NoteBlock noteBlock)
			{
				Material type;
				Material topType = loc.clone().add(0, 1, 0).getBlock().getType();
				Material bottomType = loc.clone().add(0, -1, 0).getBlock().getType();

				if (topType == Material.ZOMBIE_HEAD ||
					topType == Material.SKELETON_SKULL ||
					topType == Material.CREEPER_HEAD ||
					topType == Material.DRAGON_HEAD ||
					topType == Material.WITHER_SKELETON_SKULL ||
					topType == Material.PIGLIN_HEAD ||
					topType == Material.PLAYER_HEAD)
					type = topType;
				else
					type = bottomType;

				if (type == Material.AIR)
					type = Material.STRUCTURE_VOID;

				if (!type.isItem())
					return;
				m.slot(4, 1, SlotBuilder.create(ItemStackBuilder.create(type).setName(noteBlock.getInstrument().name()).buildItemStack()));
			}
		});*/
}
