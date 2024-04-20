package steve6472.funnylib.workdistro;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 4/18/2024
 * Project: StevesFunnyLibrary <br>
 */
public interface UndoWorkload
{
    Workload createUndo();
    UUID uuid();
    UndoManager.UndoType type();
}
