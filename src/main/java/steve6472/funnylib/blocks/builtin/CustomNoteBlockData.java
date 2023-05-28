package steve6472.funnylib.blocks.builtin;

import org.bukkit.GameEvent;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Entity;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.CustomNoteBlocks;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.NMS;
import steve6472.funnylib.util.SafeNMS;

import javax.annotation.Nullable;

/**
 * Created by steve6472
 * Date: 5/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CustomNoteBlockData extends CustomBlockData
{
	boolean powered;
	Note note;
	Instrument instrument;

	@Override
	public void toNBT(NBT compound)
	{
		compound.setBoolean("powered", powered);
		compound.setByte("note", note.getId());
		compound.setEnum("instrument", instrument);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		this.powered = compound.getBoolean("powered", false);
		this.note = new Note(compound.getByte("note"));
		this.instrument = compound.getEnum(Instrument.class, "instrument", Instrument.PIANO);
	}

	public void togglePowered(BlockContext blockContext)
	{
		powered = !powered;

		if (powered)
		{
			if (blockContext.getBlock().getBlockData() instanceof NoteBlock)
			{
				playNote(blockContext, null);
			}
		}
	}

	public void playNote(BlockContext blockContext, @Nullable Entity entity)
	{
		if (CustomNoteBlocks.requiresAirAbove(instrument) && blockContext.getLocation().clone().add(0, 1, 0).getBlock().getType().isAir() || !CustomNoteBlocks.requiresAirAbove(instrument))
		{
			CustomNoteBlocks.playNoteInWorld(blockContext.getWorld(), blockContext.getLocation(), instrument, note);
			SafeNMS.nmsFunctionSilent(() -> NMS.fireGameEvent(entity, blockContext.getLocation(), GameEvent.NOTE_BLOCK_PLAY));
		}
	}

	public void setPowered(boolean powered)
	{
		this.powered = powered;
	}

	public boolean isPowered()
	{
		return powered;
	}

	public Instrument getInstrument()
	{
		return instrument;
	}

	public Note getNote()
	{
		return note;
	}

	public void setNote(Note note)
	{
		this.note = note;
	}

	public void setInstrument(Instrument instrument)
	{
		this.instrument = instrument;
	}
}
