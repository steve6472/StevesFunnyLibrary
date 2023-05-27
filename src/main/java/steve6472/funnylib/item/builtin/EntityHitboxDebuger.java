package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.ParticleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by steve6472
 * Date: 5/24/2023
 * Project: StevesFunnyLibrary <br>
 */
public class EntityHitboxDebuger extends CustomItem implements TickInHandEvent
{
	private static final Particle.DustOptions HITBOX_SETTINGS = new Particle.DustOptions(Color.WHITE, 0.3f);
	private static final Particle.DustOptions INTERSECTION_SETTINGS = new Particle.DustOptions(Color.RED, 0.4f);

	@Override
	public String id()
	{
		return "entity_hitbox_debugger";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.SPECTRAL_ARROW).setName("Entity Hitbox Debugger", ChatColor.DARK_AQUA).glow().buildItemStack();
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		Collection<Entity> nearbyEntities = context
			.getWorld()
			.getNearbyEntities(context.getPlayer().getLocation(), 8, 8, 8, e -> e != context.getPlayer());

		if (nearbyEntities.size() > 8)
		{
			ArrayList<Entity> list = new ArrayList<>(nearbyEntities);
			Collections.shuffle(list);
			nearbyEntities = list.stream().limit(32).toList();
		}

		nearbyEntities.forEach(e ->
		{
			ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, e.getBoundingBox().clone(), 0.0, 0.125, HITBOX_SETTINGS);

			RayTraceResult rayTraceResult = e
				.getBoundingBox()
				.rayTrace(context.getPlayer().getEyeLocation().toVector(), context
					.getPlayer()
					.getEyeLocation()
					.getDirection(), 10);

			if (rayTraceResult != null)
			{
				context.getPlayer().spawnParticle(Particle.REDSTONE, rayTraceResult.getHitPosition().getX(), rayTraceResult.getHitPosition().getY(), rayTraceResult.getHitPosition().getZ(), 1, INTERSECTION_SETTINGS);
			}
		});
	}
}
