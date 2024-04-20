package steve6472.funnylib.workdistro.impl;

import org.bukkit.World;
import org.bukkit.entity.Player;
import steve6472.funnylib.workdistro.UndoManager;
import steve6472.funnylib.workdistro.UndoWorkload;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 4/18/2024
 * Project: StevesFunnyLibrary <br>
 */
public abstract class WorldUndoWorkload extends WorldWorkload implements UndoWorkload
{
    private final UUID undoOwner;
    private final UndoManager.UndoType undoType;

    public WorldUndoWorkload(World world, Player undoOwner, UndoManager.UndoType undoType)
    {
        super(world);
        this.undoOwner = undoOwner.getUniqueId();
        this.undoType = undoType;
    }

    @Override
    public UUID uuid()
    {
        return undoOwner;
    }

    @Override
    public UndoManager.UndoType type()
    {
        return undoType;
    }
}
