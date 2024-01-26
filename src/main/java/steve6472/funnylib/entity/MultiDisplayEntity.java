package steve6472.funnylib.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.standalone.interactable.ReflectionHacker;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 12/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class MultiDisplayEntity
{
	public static final int MAX_DISPLAYS = 1024;

	WeakReference<Entity> rootEntity;
	private Supplier<Boolean> aliveCondition;
	private UUID owner;
	private final Vector3d position = new Vector3d();
	public int teleportDuration = 0;

	public void setAliveCondition(Player owner, Supplier<Boolean> aliveCondition)
	{
		this.owner = owner.getUniqueId();
		this.aliveCondition = aliveCondition;
	}

	public static Supplier<Boolean> holdingCustomItemCondition(Player player, CustomItem item)
	{
		return () ->
		{
			ItemStack itemStack = player.getInventory().getItem(EquipmentSlot.HAND);
			return Items.getCustomItem(itemStack) == item;
		};
	}

	public static Supplier<Boolean> holdingCustomItemWithNBTCondition(Player player, CustomItem item, Function<ItemNBT, Boolean> test)
	{
		return () ->
		{
			ItemStack itemStack = player.getInventory().getItem(EquipmentSlot.HAND);
			if (Items.getCustomItem(itemStack) != item)
				return false;
			if (itemStack == null || itemStack.getType().isAir())
				return false;
			ItemNBT itemNBT = ItemNBT.create(itemStack);
			return test.apply(itemNBT);
		};
	}

	protected boolean shouldLive()
	{
		return aliveCondition != null && !aliveCondition.get();
	}

	protected void checkAlive()
	{
		if (shouldLive())
		{
			Player player = Bukkit.getPlayer(owner);
			if (player != null)
				FunnyLib.getPlayerboundEntityManager().removeMultiEntity(player, this);
		}
	}

	public MultiDisplayEntity(Entity root)
	{
		rootEntity = new WeakReference<>(root);
		position.set(root.getLocation().getX(), root.getLocation().getY(), root.getLocation().getZ());
	}

	/**
	 * Transformation for the whole "structure"
	 */
	private final Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf());

	public WeakReference<Entity> getRootEntity()
	{
		return rootEntity;
	}

	public PdcNBT getEntityPDC()
	{
		Entity entity = getRootEntity().get();
		if (entity == null)
		{
			throw new RuntimeException("Entity unloaded");
		}
		return PdcNBT.fromPDC(entity.getPersistentDataContainer());
	}

	public void tick()
	{
		checkAlive();
	}

	public void move(double x, double y, double z)
	{
		Entity root = rootEntity.get();
		if (root == null) return;
		if (root instanceof Display d)
			d.setTeleportDuration(teleportDuration);
		ReflectionHacker.callEntityMoveTo(root, x, y, z, 0, 0);
		position.set(x, y, z);
	}

	/**
	 * @return copy of position
	 */
	public Vector3d getPosition()
	{
		return new Vector3d(position);
	}

	public <T extends Display> void addDisplay(Class<T> clazz, Consumer<T> function)
	{
		Entity root = rootEntity.get();
		if (root == null) return;
		if (root.getPassengers().size() > MAX_DISPLAYS)
			return;
		T spawn = root.getWorld().spawn(root.getLocation(), clazz, function);
		NBT nbt = PdcNBT.fromPDC(root.getPersistentDataContainer());
		nbt.set3f("original_translation", spawn.getTransformation().getTranslation());
		root.addPassenger(spawn);
	}

	public void remove()
	{
		Entity root = rootEntity.get();
		if (root == null) return;

		iteratePassengers(Entity::remove);
		root.remove();
	}

	public void iteratePassengers(Consumer<Display> function)
	{
		Entity root = rootEntity.get();
		if (root == null) return;

		for (Entity passenger : root.getPassengers())
		{
			if (!(passenger instanceof Display display)) continue;
			function.accept(display);
		}
	}

	public void iterateAll(Consumer<Display> function)
	{
		Entity root = rootEntity.get();
		if (root == null) return;

		if (root instanceof BlockDisplay rootDisplay)
			function.accept(rootDisplay);

		for (Entity passenger : root.getPassengers())
		{
			if (!(passenger instanceof Display display)) continue;
			function.accept(display);
		}
	}

	public void scale(double newScale, int delay, int duration)
	{
		iterateAll(d ->
		{
			NBT nbt = PdcNBT.fromPDC(d.getPersistentDataContainer());
			Vector3f originalTranslation = nbt.get3f("original_translation", new Vector3f());
			transformation.getLeftRotation().transform(originalTranslation);

			Transformation transformation = d.getTransformation();
			transformation.getScale().set(newScale);
			transformation.getTranslation().set(originalTranslation.mul((float) newScale));
			d.setTransformation(transformation);
			d.setInterpolationDelay(delay);
			d.setInterpolationDuration(duration);
		});

		transformation.getScale().set(newScale);
	}

	public void rotate(Quaternionf quat, int delay, int duration)
	{
		iterateAll(d ->
		{
			Transformation transformation = d.getTransformation();
			transformation.getLeftRotation().set(quat);
			Vector3f originalTranslation = PdcNBT.fromPDC(d.getPersistentDataContainer()).get3f("original_translation", new Vector3f());
			originalTranslation.mul(transformation.getScale());
			transformation.getLeftRotation().transform(originalTranslation);
			transformation.getTranslation().set(originalTranslation);
			d.setTransformation(transformation);
			d.setInterpolationDelay(delay);
			d.setInterpolationDuration(duration);
		});

		transformation.getLeftRotation().set(quat);
	}
}
