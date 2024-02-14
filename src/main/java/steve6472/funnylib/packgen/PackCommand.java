package steve6472.funnylib.packgen;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import steve6472.brigit.BrigitCommand;
import steve6472.funnylib.FunnyLib;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 2/12/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PackCommand extends BrigitCommand
{
	private final PackEngine packEngine;

	public PackCommand(PackEngine packEngine)
	{
		this.packEngine = packEngine;
	}

	@Override
	public void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		commandDispatcher.register(literal(getName())
			.then(literal("zip").executes(c ->
			{
				packEngine.zipper.zip(() -> {});
				return 0;
			})).then(literal("get").executes(c ->
			{
				packEngine.getPackAddress(url ->
				{
					for (Player player : Bukkit.getOnlinePlayers())
					{
						player.setResourcePack(FunnyLib.getSettings().packUUID, url, null, "Get this or else", true);
					}
				});

				return 0;
			})).executes(c ->
			{
				packEngine.zipper.zip(() -> packEngine.getPackAddress(url ->
				{
					for (Player player : Bukkit.getOnlinePlayers())
					{
						player.setResourcePack(FunnyLib.getSettings().packUUID, url, null, "Get this or else", true);
					}
				}));
				return 0;
			}).then(literal("remove").executes(c ->
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					player.removeResourcePack(FunnyLib.getSettings().packUUID);
				}
				return 0;
			})).then(literal("give").then(argument("model", string()).suggests((c, b) -> suggest(listModels(), b)).executes(c -> {
				String modelId = getString(c, "model");
				Player player = getPlayer(c);
				Optional<Model> first = packEngine.models
					.getModels()
					.stream()
					.filter(model -> model.modelId.equals(modelId))
					.findFirst();

				first.ifPresent(model -> {
					player.getInventory().addItem(model.makeItem());
				});

				return 0;
			}))));
	}

	private List<String> listModels()
	{
		return packEngine.models.getModels().stream().map(model -> model.modelId).toList();
	}

	@Override
	public String getName()
	{
		return "pack";
	}

	@Override
	public int getPermissionLevel()
	{
		return 0;
	}
}
