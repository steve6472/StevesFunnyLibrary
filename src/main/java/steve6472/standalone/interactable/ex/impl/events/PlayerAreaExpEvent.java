package steve6472.standalone.interactable.ex.impl.events;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.AreaSelection;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.Pair;
import steve6472.funnylib.util.ParticleUtil;
import steve6472.standalone.interactable.ex.elements.ElementType;
import steve6472.standalone.interactable.ex.ExpContext;
import steve6472.standalone.interactable.ex.elements.IElementType;
import steve6472.standalone.interactable.ex.event.ExpressionEventData;
import steve6472.standalone.interactable.ex.event.ExpressionEvent;
import steve6472.standalone.interactable.ex.event.InputType;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PlayerAreaExpEvent extends ExpressionEvent
{
	private static final Pattern UUID_REGEX = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");

	private final Set<UUID> checkedPlayers = new HashSet<>();
	public AreaSelection area;
	public boolean activateOnLeave = false;
	public boolean activateOnEntry = true;

	private final ElementType AREA = new ElementType("area", 0, () ->
	{
		if (area == null)
			return ItemStackBuilder.quick(Material.STRUCTURE_VOID, "Click with item to set area", ChatColor.GRAY);
		return area.toItem();
	});

	@Override
	public void createEvents(ExpContext executingContext, List<ExpressionEventData> eventsToQueue)
	{
		if (area == null) return;
		BoundingBox box = area.toBoundingBox();

		checkedPlayers.removeIf(u -> {
			Player player = Bukkit.getPlayer(u);
			if (player == null) return true;
			ParticleUtil.boxAbsolute(player, Particle.REDSTONE, box, 0, 0.2, box.overlaps(player.getBoundingBox()) ? new Particle.DustOptions(Color.GREEN, 0.5f) : new Particle.DustOptions(Color.RED, 0.5f));
			if (!box.overlaps(player.getBoundingBox()))
			{
				if (activateOnLeave)
				{
					eventsToQueue.add(createEvent(player));
				}
				return true;
			}
			return false;
		});

		Set<Pair<Player, UUID>> currentlyInArea = executingContext
			.getWorld()
			.getNearbyEntities(box, e -> e instanceof Player)
			.stream()
			.map((e) -> new Pair<>((Player) e, e.getUniqueId()))
			.collect(Collectors.toSet());

		for (Pair<Player, UUID> current : currentlyInArea)
		{
			if (!checkedPlayers.contains(current.b()))
			{
				checkedPlayers.add(current.b());
				if (activateOnEntry)
				{
					eventsToQueue.add(createEvent(current.a()));
				}
			}
		}
	}

	@Override
	protected void populateOutputs(List<Pair<String, InputType<?>>> outputs)
	{
		outputs.add(new Pair<>("player", InputType.PLAYER));
		outputs.add(new Pair<>("area", InputType.AREA));
	}

	@Override
	public Response action(IElementType type, Click click)
	{
		if (type == AREA)
		{
			if (click.itemOnCursor() == null || click.itemOnCursor().getType().isAir())
			{
				area = null;
			} else
			{
				CustomItem customItem = Items.getCustomItem(click.itemOnCursor());
				if (customItem == FunnyLib.AREA_LOCATION_MARKER)
				{
					area = AreaSelection.fromItem(click.itemOnCursor());
				}
			}

			if (area == null)
			{
				click.slot().setItem(ItemStackBuilder.quick(Material.STRUCTURE_VOID, "Click with item to set", ChatColor.GRAY));
			} else
			{
				click.slot().setItem(area.toItem());
			}
		}
		return Response.cancel();
	}

	@Override
	public IElementType[] getTypes()
	{
		return new IElementType[] {AREA};
	}

	private ExpressionEventData createEvent(Player player)
	{
		return event()
			.with(InputType.PLAYER, "player", player)
			.with(InputType.AREA, "area", area);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		super.fromNBT(compound);

		checkedPlayers.clear();
		Arrays
			.stream(compound.getString("players", "").split(";"))
			.filter(s -> UUID_REGEX.matcher(s).matches())
			.map(UUID::fromString)
			.forEach(checkedPlayers::add);
		if (compound.hasCompound("area"))
		{
			area = new AreaSelection(compound.getCompound("area"));
		}
	}

	@Override
	public void toNBT(NBT compound)
	{
		super.toNBT(compound);
		String checkedPlayersList = checkedPlayers.stream().map(UUID::toString).collect(Collectors.joining(";"));
		compound.setString("players", checkedPlayersList);
		if (area != null)
		{
			NBT areaCompound = compound.createCompound();
			area.toNBT(areaCompound);
			compound.setCompound("area", areaCompound);
		}
	}
}
