package steve6472.funnylib.blocks;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.builtin.CustomNoteBlockData;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.util.generated.BlockGen;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 5/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CustomNoteBlocks implements Listener
{
	private static final Set<Location> UPDATED_THIS_TICK = new HashSet<>();

	@EventHandler
	public void tick(ServerTickEvent e)
	{
		UPDATED_THIS_TICK.clear();
	}

	@EventHandler
	public void place(BlockPlaceEvent e)
	{
		if (e.getBlock().getType() != Material.NOTE_BLOCK) return;
		BlockData oldData = e.getBlock().getBlockData();
		Blocks.setBlockState(e.getBlock().getLocation(), FunnyLib.NOTE_BLOCK.getDefaultState());

		if (oldData instanceof NoteBlock nb)
		{
			CustomNoteBlockData customBlockData = Blocks.getBlockData(e.getBlock().getLocation(), CustomNoteBlockData.class);
			customBlockData.setPowered(nb.isPowered());
			customBlockData.setNote(nb.getNote());
			customBlockData.setInstrument(nb.getInstrument());
		}
	}

	@EventHandler
	public void leftClick(PlayerInteractEvent e)
	{
		if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (e.getClickedBlock() == null) return;
		if (e.getClickedBlock().getType() != Material.NOTE_BLOCK) return;
		if (e.getHand() != EquipmentSlot.HAND) return;

		Location loc = e.getClickedBlock().getLocation();
		BlockData newData = e.getClickedBlock().getBlockData();
		if (!(newData instanceof NoteBlock nb)) return;

		CustomBlockData customBlockData = Blocks.getBlockData(loc);
		/*
		 * Updater
		 */
		if (customBlockData == null)
		{
			Blocks.setBlockState(loc, FunnyLib.NOTE_BLOCK.getDefaultState());
			customBlockData = Blocks.getBlockData(loc);
			if (customBlockData instanceof CustomNoteBlockData noteBlockData)
			{
				noteBlockData.setInstrument(nb.getInstrument());
				noteBlockData.setNote(nb.getNote());
				noteBlockData.setPowered(nb.isPowered());
				noteBlockData.playNote(new BlockContext(loc, noteBlockData), e.getPlayer());
			}
		}

		if (!(customBlockData instanceof CustomNoteBlockData noteData)) return;
		noteData.playNote(new BlockContext(loc, noteData), e.getPlayer());
	}

	@EventHandler
	public void note(NotePlayEvent e)
	{
		// Cancel vanilla note
		// TODO: run game event, call block event
		e.setCancelled(true);
	}

	// Handle other "place" events, pistons moving... idk what else, maybe endermans? idfk

	@EventHandler
	public void physics(BlockPhysicsEvent e)
	{
		if (e.getSourceBlock().getType() != Material.NOTE_BLOCK) return;
		BlockData newData = e.getSourceBlock().getBlockData();
		if (!(newData instanceof NoteBlock nb)) return;
		Location loc = e.getBlock().getLocation();

		// Prevent multiple recursive calls from preventing state update by setting a different state
		if (UPDATED_THIS_TICK.contains(loc))
			return;

		UPDATED_THIS_TICK.add(loc);

		// Cancel the event
		e.getBlock().setBlockData(BlockGen.NoteBlock(Instrument.PIANO, new Note(0), false), false);

		CustomBlockData customBlockData = Blocks.getBlockData(loc);
		/*
		 * Updater
		 */
		if (customBlockData == null)
		{
			Blocks.setBlockState(loc, FunnyLib.NOTE_BLOCK.getDefaultState());
			customBlockData = Blocks.getBlockData(loc);
			if (customBlockData instanceof CustomNoteBlockData noteBlockData)
			{
				noteBlockData.setInstrument(nb.getInstrument());
				noteBlockData.setNote(nb.getNote());
				noteBlockData.setPowered(nb.isPowered());
			}
		}
		if (!(customBlockData instanceof CustomNoteBlockData noteData)) return;

		BlockContext blockContext = new BlockContext(loc.clone());

		// Toggle powered state
		if (nb.isPowered() != noteData.isPowered())
		{
			noteData.togglePowered(blockContext);
		}

		// Cycle notes via left click
		if (nb.getNote().getId() == 1)
		{
			noteData.setNote(new Note((noteData.getNote().getId() + 1) % 24));
			noteData.playNote(blockContext, null);
		}
	}

	public static void playNoteInWorld(World world, Location location, Instrument instrument, Note note)
	{
		float f = (float)Math.pow(2.0, ((double)note.getId() - 12.0) / 12.0);
		world.playSound(location, getInstrumentSound(instrument), SoundCategory.RECORDS, 3.0f, f);
		world.spawnParticle(Particle.NOTE, location.getBlockX() + 0.5, location.getBlockY() + 1.1, location.getBlockZ() + 0.5, 0, note.getId() / 24d, 0, 0);
	}

	public static String getInstrumentSound(Instrument instrument)
	{
		String sound = switch (instrument.ordinal()) {
			case 0 -> "harp";
			case 1 -> "basedrum";
			case 2 -> "snare";
			case 3 -> "hat";
			case 4 -> "bass";
			case 5 -> "flute";
			case 6 -> "bell";
			case 7 -> "guitar";
			case 8 -> "chime";
			case 9 -> "xylophone";
			case 10 -> "iron_xylophone";
			case 11 -> "cow_bell";
			case 12 -> "didgeridoo";
			case 13 -> "bit";
			case 14 -> "banjo";
			case 15 -> "pling";
			case 16 -> "zombie";
			case 17 -> "skeleton";
			case 18 -> "creeper";
			case 19 -> "dragon";
			case 20 -> "wither_skeleton";
			case 21 -> "piglin";
			default -> "custom_head";
		};

		return "block.note_block." + sound;
	}

	public static boolean requiresAirAbove(Instrument instrument)
	{
		// Ugly code lol
		return instrument.ordinal() <= Instrument.PLING.ordinal();
	}

	private static final int[] COLOR_BY_NOTE = new int[]{
		0x77d700, 0x95C000, 0xB2A500, 0xCC8600, 0xE26500,
		0xF34100, 0xFC1E00, 0xFE000F, 0xF70033, 0xE8005A,
		0xCF0083, 0xAE00A9, 0x8600CC, 0x8600CC, 0x5B00E7,
		0x2D00F9, 0x020AFE, 0x0037F6, 0x0068E0, 0x009ABC,
		0x00C68D, 0x00E958, 0x00FC21, 0x1FFC00, 0x59E800
	};

	public static int colorByNote(Note note)
	{
		return colorByNoteId(note.getId());
	}

	public static int colorByNoteId(int id)
	{
		return COLOR_BY_NOTE[id];
	}
}
