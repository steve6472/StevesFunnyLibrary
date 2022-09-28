package steve6472.standalone.interactable.worldbutton;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.*;
import org.bukkit.util.Vector;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.util.MetaUtil;

import java.util.*;

/**
 * Created by steve6472
 * Date: 9/25/2022
 * Project: StevesFunnyLibrary <br>
 */
public class WorldButtonListener implements Listener
{
	private static final Map<World, Set<WorldButton>> WORLD_BUTTONS = new HashMap<>();

	public static void addWorldButton(WorldButton button)
	{
		World key = button.hitbox.getWorld();
		Set<WorldButton> worldButtons = WORLD_BUTTONS.get(key);
		if (worldButtons == null)
		{
			worldButtons = new HashSet<>();
			worldButtons.add(button);
			WORLD_BUTTONS.put(key, worldButtons);
		} else
		{
			worldButtons.add(button);
		}
	}

	public static void removeWorldButton(WorldButton button)
	{
		Set<WorldButton> worldButtons = WORLD_BUTTONS.get(button.hitbox.getWorld());
		if (worldButtons != null)
		{
			worldButtons.remove(button);
		}
	}

	@EventHandler
	public void damageEntity(EntityDamageByEntityEvent e)
	{
		if (!(e.getDamager() instanceof Player player)) return;
		if (!(e.getEntity() instanceof Slime slime)) return;
		if (slime.getSize() != 1) return;

		WorldButton worldButton = MetaUtil.getValue(slime, WorldButton.class, WorldButton.META_KEY);

		// Not a world button, just a normal slime
		if (worldButton == null) return;

		worldButton.click(player);
		e.setCancelled(true);
	}

	private boolean trySlimeInteract(PlayerInteractEntityEvent e)
	{
		if (!(e.getRightClicked() instanceof Slime slime)) return false;
		if (slime.getSize() != 1) return false;

		WorldButton worldButton = MetaUtil.getValue(slime, WorldButton.class, WorldButton.META_KEY);

		// Not a world button, just a normal slime
		if (worldButton == null) return false;

		worldButton.click(e.getPlayer());
		e.setCancelled(true);
		return true;
	}

	@EventHandler
	public void interactEntity(PlayerInteractEntityEvent e)
	{
		if (e.getHand() != EquipmentSlot.HAND) return;
		if (trySlimeInteract(e)) return;

		WorldButton button = MetaUtil.getValue(e.getPlayer(), WorldButton.class, "last_world_button");

		if (button == null) return;

		button.click(e.getPlayer());
	}

	@EventHandler
	public void interact(PlayerInteractEvent e)
	{
		if (e.getHand() != EquipmentSlot.HAND) return;

		WorldButton button = MetaUtil.getValue(e.getPlayer(), WorldButton.class, "last_world_button");

		if (button == null) return;

		button.click(e.getPlayer());
	}

	@EventHandler
	public void tick(ServerTickEvent e)
	{
		for (Map.Entry<World, Set<WorldButton>> entry : WORLD_BUTTONS.entrySet())
		{
			World world = entry.getKey();

			for (Player player : world.getPlayers())
			{
				Location playerEyeLocation = player.getEyeLocation();
				Vector playerEyePosition = playerEyeLocation.toVector();
				Vector playerEyeDirection = playerEyeLocation.getDirection();

				WorldButton lastForPlayer = MetaUtil.getValue(player, WorldButton.class, "last_world_button");

				WorldButton closest = null;
				double minDistance = Double.MAX_VALUE;

				WorldButton closestLook = null;
				Vector closestLookRay = null;
				double minDistanceLook = Double.MAX_VALUE;

				double maxPlayerDistance = player.getGameMode() == GameMode.CREATIVE ? 4.5 : 3.0;

				BoundingBox testingBox = new BoundingBox();

				for (Iterator<WorldButton> iter = entry.getValue().iterator(); iter.hasNext(); )
				{
					WorldButton worldButton = iter.next();
					if (worldButton.hitbox.isDead())
					{
						iter.remove();
						continue;
					}

					double maxDistance = worldButton.isRemote ? 16.0 : maxPlayerDistance;

					double size = worldButton.hitbox.getSize() * 0.51;
					testingBox.resize(0, 0, 0, size, size, size);
					testingBox.shift(worldButton.hitbox.getLocation());
					testingBox.shift(-size / 2.0, 0, -size / 2.0);

					Vector center = testingBox.getCenter();
					double distance = playerEyePosition.distance(center) + ((size / 2.0) * Math.sqrt(2.0) - (size / 2.0));
					Vector rayToPlayer = center.subtract(playerEyePosition).normalize();

					if (worldButton.followClosestPlayer)
					{
						if (distance < minDistanceLook && distance < maxDistance)
						{
							minDistanceLook = distance;
							closestLook = worldButton;
							closestLookRay = rayToPlayer;
						}
					}

					RayTraceResult rayTraceResult = testingBox.rayTrace(playerEyePosition, playerEyeDirection, maxDistance);
					if (rayTraceResult == null)
						continue;

					distance = rayTraceResult.getHitPosition().distance(playerEyePosition);

					if (distance < minDistance && distance < maxDistance)
					{
						minDistance = distance;
						closest = worldButton;
					}
				}

				if (closestLook != null)
				{
					if (closestLook.followClosestPlayer)
					{
						followClosestPlayer(closestLookRay, closestLook);
					}
				}

				if (closest != null)
				{
					closest.displayLabel(player);
				}

				// Player is still looking at the same button
				if (closest != null && closest == lastForPlayer)
				{
					continue;
				} else
				{
					if (lastForPlayer != null)
					{
						lastForPlayer.deactivate();
						MetaUtil.removeMeta(player, "last_world_button");
					}
				}

				if (closest != null)
				{
					closest.activate();
					MetaUtil.setMeta(player, "last_world_button", closest);
				}
			}
		}
	}

	private void followClosestPlayer(Vector ray, WorldButton worldButton)
	{
		if (ray == null)
			return;

		final double _2PI = 2 * Math.PI;
		final double x = ray.getX();
		final double z = ray.getZ();

		double pitch, yaw;

		if (x == 0 && z == 0)
		{
			pitch = ray.getY() > 0 ? -90 : 90;
		} else
		{
			double x2 = NumberConversions.square(x);
			double z2 = NumberConversions.square(z);
			double xz = Math.sqrt(x2 + z2);
			pitch = Math.atan(-ray.getY() / xz);
		}

		double theta = Math.atan2(-x, z);
		yaw = (theta + _2PI) % _2PI;

		worldButton.icon.setHeadPose(new EulerAngle(pitch, yaw, 0));
	}
}
