package steve6472.standalone.interactable.script;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Description;
import steve6472.funnylib.command.Usage;
import steve6472.scriptit.Script;
import steve6472.scriptit.transformer.JavaTransformer;
import steve6472.standalone.interactable.Interactable;

/**
 * Created by steve6472
 * Date: 8/25/2022
 * Project: AkmaShorts
 */
public class ScriptCommands
{
	@Command
	@Description("Reloads scripts & removes then from ticking")
	@Usage("/reloadScripts")
	public static boolean reloadScripts(@NotNull Player player, @NotNull String[] args)
	{
		JavaTransformer.CACHE.clear();
		JavaTransformer.FUNCTION_GENERATED.clear();
		JavaTransformer.initCache();
		Interactable.scriptRepository.reload(player);
		return true;
	}

	@Command
	@Description("Prints ClassTransformer cache into the server console")
	@Usage("/printScriptTypeCache")
	public static boolean printScriptTypeCache(@NotNull Player player, @NotNull String[] args)
	{
		JavaTransformer.CACHE.forEach((k, v) -> System.out.println(k + " -> " + v));
		return true;
	}

	@Command
	@Description("Starts ticking a script")
	@Usage("/tickScript <script>")
	public static boolean tickScript(@NotNull Player player, @NotNull String[] args)
	{
		ScriptRepository repository = Interactable.scriptRepository;
		repository.tickScripts.add(repository.scripts.get(args[0]));
		return true;
	}

	@Command
	@Description("Stops ticking a script")
	@Usage("/untickScript <script>")
	public static boolean untickScript(@NotNull Player player, @NotNull String[] args)
	{
		ScriptRepository repository = Interactable.scriptRepository;
		repository.tickScripts.remove(repository.scripts.get(args[0]));
		return true;
	}

	@Command
	@Description("Runs script once")
	@Description("Stops at delay or return")
	@Usage("/runScript <script>")
	public static boolean runScript(@NotNull Player player, @NotNull String[] args)
	{
		ScriptRepository repository = Interactable.scriptRepository;
		Script script = repository.scripts.get(args[0]);
		player.sendMessage(ScriptRepository.runTillDelay(script).toString());

		return true;
	}
}
