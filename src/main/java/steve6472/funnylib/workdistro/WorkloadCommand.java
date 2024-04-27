package steve6472.funnylib.workdistro;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.util.JSONMessage;

import java.util.*;

/**
 * Created by steve6472
 * Date: 4/24/2024
 * Project: StevesFunnyLibrary <br>
 */
public class WorkloadCommand extends BrigitCommand
{
    @Override
    public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
    {
        commandDispatcher.register(literal(getName())
            .then(literal("undo")
                .then(literal("list")
                    .then(argument("player", singlePlayer())
                        .executes(c -> {
                            Player executor = getPlayer(c);

                            Player player = getSinglePlayer(c, "player");
                            List<UndoManager.UndoQueue> queues = FunnyLib
                                .getWorkloadRunnable()
                                .undoManager()
                                .getQueues(player.getUniqueId());

                            JSONMessage.create("Queues for player ").then(player.getName()).tooltip(JSONMessage.create(player.getUniqueId().toString())).send(executor);

                            for (UndoManager.UndoQueue queue : queues)
                            {
                                int[] totalCount = new int[1];
                                queue.getActions().forEach(a -> totalCount[0] += a.getWorkloadCount());
                                JSONMessage.create(queue.getType() + "", ChatColor.GOLD).then(" Actions: ").then("" + queue.getActions().size()).tooltip(JSONMessage.create("Workloads: " + totalCount[0]));

                                if (queue.getCurrentBuilding() != null)
                                {
                                    JSONMessage.create(queue.getType() + "", ChatColor.AQUA).tooltip("Not yet finished!").then(" Workloads: ").then("" + queue.getCurrentBuilding().getWorkloadCount());
                                }
                            }
                            return 0;
                        })
                    )
                ).then(literal("clear")
                    .then(argument("player", singlePlayer())
                        .executes(c -> {
                            FunnyLib.getWorkloadRunnable().undoManager().clearQueue(getSinglePlayer(c, "player"));
                            return 0;
                        })
                    )
                )
            ).then(literal("undoable")
                .then(literal("list")
                    .then(literal("byType")
                        .executes(c -> {
                            Player executor = getPlayer(c);
                            List<UndoWorkload> workloadsCopy = FunnyLib.getWorkloadRunnable().getWorkloadsCopy().stream()
                                .filter(workload -> workload instanceof UndoWorkload)
                                .map(workload -> (UndoWorkload) workload).toList();

                            Map<Class<?>, List<UndoWorkload>> workloads = new HashMap<>();

                            for (UndoWorkload workload : workloadsCopy)
                            {
                                workloads.computeIfAbsent(workload.getClass(), key -> new ArrayList<>()).add(workload);
                            }

                            workloads.forEach((type, list) -> {
                                JSONMessage.create(type.getName(), ChatColor.GOLD).then(": ").then("" + list.size(), ChatColor.GRAY).send(executor);
                            });

                            return workloads.size();
                        })
                    ).then(literal("byUUID")
                        .executes(c -> {
                            Player executor = getPlayer(c);
                            List<UndoWorkload> workloadsCopy = FunnyLib.getWorkloadRunnable().getWorkloadsCopy().stream()
                                .filter(workload -> workload instanceof UndoWorkload)
                                .map(workload -> (UndoWorkload) workload).toList();

                            Map<UUID, List<UndoWorkload>> workloads = new HashMap<>();

                            for (UndoWorkload workload : workloadsCopy)
                            {
                                workloads.computeIfAbsent(workload.uuid(), key -> new ArrayList<>()).add(workload);
                            }

                            workloads.forEach((uuid, list) ->
                            {
                                Player owner = Bukkit.getPlayer(uuid);
                                JSONMessage.create(owner == null ? uuid.toString() : owner.getName(), ChatColor.GOLD).then(": ").then("" + list.size(), ChatColor.GRAY).send(executor);
                            });

                            return workloads.size();
                        })
                    )
                ).then(literal("cancel")
                    .then(literal("byUUID")
                        .then(argument("player", singlePlayer())
                            .executes(c -> {
                                UUID uuid = getSinglePlayer(c, "player").getUniqueId();

                                int[] removedCount = new int[1];

                                FunnyLib.getWorkloadRunnable().removeWorkloadIf(workload ->
                                {
                                    if (workload instanceof UndoWorkload undo)
                                    {
                                        removedCount[0]++;
                                        return undo.uuid().equals(uuid);
                                    }
                                    return false;
                                });

                                JSONMessage.create("Removed " + removedCount[0] + " workloads!").send(getPlayer(c));

                                return removedCount[0];
                            })
                        )
                    )
                )
            )
        );
    }

    @Override
    public String getName()
    {
        return "workload";
    }

    @Override
    public int getPermissionLevel()
    {
        return 4;
    }
}
