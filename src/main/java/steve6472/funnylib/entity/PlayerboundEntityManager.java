package steve6472.funnylib.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 12/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PlayerboundEntityManager implements Listener
{
	private final Map<UUID, Set<MultiDisplayEntity>> entityMap = new HashMap<>();

	public PlayerboundEntityManager()
	{

	}

	public <T extends MultiDisplayEntity> T getOrCreateMultiEntity(Player player, Predicate<NBT> predicate, Supplier<T> create)
	{
		Optional<MultiDisplayEntity> first = getEntitiesForPlayer(player)
			.stream()
			.filter(t -> predicate.test(t.getEntityPDC()))
			.findFirst();

		if (first.isPresent())
		{
			//noinspection unchecked
			return (T) first.get();
		} else
		{
			T entity = create.get();
			if (entity != null)
				spawnEntity(player, entity);
			return entity;
		}
	}

	public <T extends MultiDisplayEntity> T getMultiEntity(Player player, Predicate<NBT> predicate)
	{
		Optional<MultiDisplayEntity> first = getEntitiesForPlayer(player)
			.stream()
			.filter(t -> predicate.test(t.getEntityPDC()))
			.findFirst();

		//noinspection unchecked
		return (T) first.orElse(null);
	}

	public <T extends MultiDisplayEntity> void removeMultiEntity(Player player, Predicate<NBT> predicate)
	{
		Optional<MultiDisplayEntity> first = getEntitiesForPlayer(player)
			.stream()
			.filter(t -> predicate.test(t.getEntityPDC()))
			.findFirst();

		if (first.isPresent())
		{
			entityMap.get(player.getUniqueId()).remove(first.get());
			first.get().remove();
		}
	}

	public void removeMultiEntity(Player player, MultiDisplayEntity frameDisplayEntity)
	{
		Set<MultiDisplayEntity> set = entityMap.get(player.getUniqueId());
		if (set != null)
		{
			frameDisplayEntity.remove();
			set.remove(frameDisplayEntity);
		}
	}

	public void spawnEntity(Player player, MultiDisplayEntity entity)
	{
		Set<MultiDisplayEntity> set = entityMap.computeIfAbsent(player.getUniqueId(), uuid -> new HashSet<>());
		set.add(entity);
	}

	public Set<MultiDisplayEntity> getEntitiesForPlayer(Player player)
	{
		return Set.copyOf(entityMap.computeIfAbsent(player.getUniqueId(), uuid -> new HashSet<>()));
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		getEntitiesForPlayer(player).forEach(MultiDisplayEntity::remove);
		entityMap.remove(player.getUniqueId());
	}

	@EventHandler
	public void playerLeave(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		getEntitiesForPlayer(player).forEach(MultiDisplayEntity::remove);
		entityMap.remove(player.getUniqueId());
	}

	@EventHandler
	public void playerLeave(ServerTickEvent event)
	{
		entityMap.values().forEach(set ->
		{
			// Copy so I can modify the list when iterating over it mhmm
			Set.copyOf(set).forEach(MultiDisplayEntity::tick);
		});
	}
}
