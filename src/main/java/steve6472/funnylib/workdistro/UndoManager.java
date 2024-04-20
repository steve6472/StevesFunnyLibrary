package steve6472.funnylib.workdistro;

import org.bukkit.entity.Player;
import steve6472.funnylib.FunnyLib;

import java.util.*;

/**
 * Created by steve6472
 * Date: 4/18/2024
 * Project: StevesFunnyLibrary <br>
 */
public final class UndoManager
{
    private int undoLimit;
    private final Map<UUID, Map<UndoType, UndoQueue>> queues = new HashMap<>();

    public UndoManager(int undoLimit)
    {
        this.undoLimit = undoLimit;
    }

    public void startQueue(UUID owner, UndoType undoType) { getQueue(owner, undoType).startNew(); }
    public void startQueue(Player player, UndoType undoType) { startQueue(player.getUniqueId(), undoType); }

    public void endQueue(UUID owner, UndoType undoType) { getQueue(owner, undoType).end(); }
    public void endQueue(Player player, UndoType undoType) { endQueue(player.getUniqueId(), undoType); }

    public void applyUndo(UUID owner, UndoType undoType) { getQueue(owner, undoType).undo(); }
    public void applyUndo(Player player, UndoType undoType) { applyUndo(player.getUniqueId(), undoType); }

    public void addWorkload(UUID owner, UndoType undoType, Workload workload) { getQueue(owner, undoType).addWorkload(workload); }

    public void setUndoLimit(int undoLimit)
    {
        this.undoLimit = undoLimit;
        queues.forEach((key, map) -> map.forEach((type, queue) -> queue.setUndoLimit(undoLimit)));
    }

    /**
     * Util method to get the Queue for a player and undo type
     * @param owner the owner of the undo queue
     * @param undoType the undo type
     * @return UndoQueue for given player and undo type
     */
    private UndoQueue getQueue(UUID owner, UndoType undoType)
    {
        Map<UndoType, UndoQueue> undoTypeMap = queues.computeIfAbsent(owner, uuid -> new HashMap<>());
        return undoTypeMap.computeIfAbsent(undoType, type -> new UndoQueue(undoLimit, type));
    }

    private static final class UndoQueue
    {
        private int undoLimit;
        private final UndoType type;
        private final List<UndoAction> actions;
        private UndoAction currentBuilding;

        private UndoQueue(int undoLimit, UndoType type)
        {
            this.undoLimit = undoLimit;
            this.type = type;
            this.actions = new ArrayList<>();
        }

        private void startNew()
        {
            end();

            currentBuilding = new UndoAction();

            // Clear oldest action if over the limit
            if (actions.size() >= undoLimit)
                actions.remove(0);
        }

        private void end()
        {
            if (currentBuilding == null)
                return;

            // Do not add empty undo
            if (!currentBuilding.isEmpty())
                actions.add(currentBuilding);

            currentBuilding = null;
        }

        /**
         * Creates new action if no action is currently in progress
         * @param workload workload to add to current action
         */
        private void addWorkload(Workload workload)
        {
            if (currentBuilding == null)
                startNew();

            currentBuilding.addWorkload(workload);
        }

        private void undo()
        {
            if (actions.isEmpty())
                return;

            UndoAction lastUndo = actions.remove(actions.size() - 1);

            // TODO: possibly reverse ? Idk yet
            for (Workload workload : lastUndo.workloads)
            {
                FunnyLib.getWorkloadRunnable().addWorkload(workload);
            }
        }

        private void setUndoLimit(int undoLimit)
        {
            this.undoLimit = undoLimit;
        }

        public UndoType getType()
        {
            return type;
        }
    }

    private static final class UndoAction
    {
        private final List<Workload> workloads = new ArrayList<>();

        //TODO: use to display how long ago this happened
        private final long undoTimestamp;

        private UndoAction()
        {
            undoTimestamp = System.currentTimeMillis();
        }

        private void addWorkload(Workload workload)
        {
            this.workloads.add(workload);
        }

        private boolean isEmpty()
        {
            return workloads.isEmpty();
        }
    }

    /**
     * Extendable interface to create custom Undo Types
     */
    public interface UndoType { }

    /**
     * Built-in undo types
     */
    public enum UndoTypes implements UndoType
    {
        SPHERE, RECTANGLE
    }

    private static abstract class UndoWoorkload implements Workload
    {
        protected final UUID ownerUUID;
        protected final UndoType undoType;

        public UndoWoorkload(UUID ownerUUID, UndoType undoType)
        {
            this.ownerUUID = ownerUUID;
            this.undoType = undoType;
        }

        public UndoWoorkload(Player owner, UndoType undoType)
        {
            this(owner.getUniqueId(), undoType);
        }
    }

    public static class EndUndoWorkload extends UndoWoorkload
    {
        public EndUndoWorkload(UUID ownerUUID, UndoType undoType)
        {
            super(ownerUUID, undoType);
        }

        public EndUndoWorkload(Player owner, UndoType undoType)
        {
            super(owner, undoType);
        }

        @Override
        public void compute()
        {
            FunnyLib.getWorkloadRunnable().undoManager().endQueue(ownerUUID, undoType);
        }
    }

    public static class StartUndoWorkload extends UndoWoorkload
    {
        public StartUndoWorkload(UUID ownerUUID, UndoType undoType)
        {
            super(ownerUUID, undoType);
        }

        public StartUndoWorkload(Player owner, UndoType undoType)
        {
            super(owner, undoType);
        }

        @Override
        public void compute()
        {
            FunnyLib.getWorkloadRunnable().undoManager().startQueue(ownerUUID, undoType);
        }
    }
}
