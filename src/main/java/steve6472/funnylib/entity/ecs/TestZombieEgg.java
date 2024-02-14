package steve6472.funnylib.entity.ecs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.entity.ecs.components.LocationComp;
import steve6472.funnylib.entity.ecs.components.ParticleDebugComp;
import steve6472.funnylib.entity.ecs.components.UUIDEntityComp;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 2/12/2024
 * Project: StevesFunnyLibrary <br>
 */
public class TestZombieEgg extends CustomItem
{
	@Override
	public String id()
	{
		return "ecs_zombie";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.ROTTEN_FLESH).setName(JSONMessage.create("ECS Zombie", ChatColor.DARK_AQUA)).glow().buildItemStack();
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		LocationComp locComp = new LocationComp(context.getBlockLocation().clone().add(context.blockContext().getFace().getDirection()).add(0.5, 0.1, 0.5));
		UUIDEntityComp entComp = new UUIDEntityComp(Creeper.class);

		FunnyLib.ecs.addEntity(locComp, entComp, new ParticleDebugComp(Particle.END_ROD));
	}
}
