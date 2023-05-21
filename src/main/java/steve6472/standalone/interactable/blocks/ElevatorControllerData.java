package steve6472.standalone.interactable.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveBool;
import steve6472.funnylib.json.codec.ann.SaveDouble;
import steve6472.funnylib.json.codec.ann.SaveInt;
import steve6472.funnylib.json.codec.codecs.ItemStackCodec;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.Interactable;
import steve6472.standalone.interactable.SolidBlockEntity;

/**
 * Created by steve6472
 * Date: 9/30/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ElevatorControllerData extends CustomBlockData
{
	public Marker pointA, pointB;

	@SaveDouble
	public double speed = 1d, progress;

	@SaveBool
	public boolean showPoints, dirChange, enabled, seatActivator = true, solidifyProtection;

	@SaveInt
	public int movingDirection; // 0 - none, 1 - to point A, 2 - to point B

	@Save(ItemStackCodec.class)
	public ItemStack elevatorData = MiscUtil.AIR;

	SolidBlockEntity sbe;
	int lx, ly, lz;

	public void createModel(Location location)
	{
		if (Items.getCustomItem(elevatorData) != Interactable.ELEVATOR_DATA_ITEM) return;

		if (sbe != null)
			sbe.destroy(0);

		sbe = new SolidBlockEntity(location.getWorld());

		Vector pos = getPosition();

		Location origin = new Location(location.getWorld(), pos.getX(), pos.getY(), pos.getZ());

		JSONObject json = ItemStackBuilder.edit(elevatorData).getCustomJson("data");
		JSONObject structure = json.getJSONObject("structure");
		lx = structure.getInt("lx");
		ly = structure.getInt("ly");
		lz = structure.getInt("lz");
		JSONArray blocks = structure.getJSONArray("blocks");
		/*
		List<StructureItem.BlockInfo> blockInfo = StructureItem.jsonToBlocks(new ArrayList<>(blocks.length()), blocks);
		for (StructureItem.BlockInfo info : blockInfo)
		{
			sbe.addBlock(info.data(), origin, info.position(), false);
		}

		JSONArray collisions = json.getJSONArray("collisions");
		for (int i = 0; i < collisions.length(); i++)
		{
			JSONObject object = collisions.getJSONObject(i);
			double x = object.getDouble("x");
			double y = object.getDouble("y");
			double z = object.getDouble("z");

			sbe.addCustomCollision(origin, x, y, z);
		}

		JSONArray seats = json.getJSONArray("seats");
		for (int i = 0; i < seats.length(); i++)
		{
			JSONObject seat = seats.getJSONObject(i);
			double x = seat.getDouble("x");
			double y = seat.getDouble("y");
			double z = seat.getDouble("z");

			sbe.addSeat(origin, x, y, z);
		}*/
	}

	public void solidify()
	{
		if (sbe == null) return;
		solidifyProtection = true;
		Bukkit.getScheduler().runTaskLater(FunnyLib.getPlugin(), () ->
		{
			sbe.destroy(0);
			sbe = null;
			solidifyProtection = false;
		}, 7);

		JSONObject json = new JSONObject(ItemStackBuilder.edit(elevatorData).getString("data"));
		Bukkit.getScheduler().runTaskLater(FunnyLib.getPlugin(), () ->
		{/*
			JSONObject structure = json.getJSONObject("structure");
			JSONArray blocks = structure.getJSONArray("blocks");
			List<StructureItem.BlockInfo> blockInfo = StructureItem.jsonToBlocks(new ArrayList<>(blocks.length()), blocks);

			Location loc = pos.clone();
			loc.setX((progress == 0 ? pointA : pointB).x());
			loc.setY((progress == 0 ? pointA : pointB).y());
			loc.setZ((progress == 0 ? pointA : pointB).z());

			for (StructureItem.BlockInfo info : blockInfo)
			{
				loc.clone().add(info.position()).getBlock().setBlockData(info.data());
			}*/
		}, 5);
	}

	public void activate(PlayerBlockContext context, int dir)
	{
		if (solidifyProtection)
			return;

		movingDirection = dir;

		if (enabled && sbe == null)
		{
			Vector position = getPosition();
			createModel(new Location(pos.getWorld(), position.getX(), position.getY(), position.getZ()));
		}

		if (seatActivator && enabled && sbe != null && (movingDirection == 0 || dirChange))
		{
			Vector p = getPosition();
			p.add(new Vector(-0.5, -0.5, -0.5));
			if (new BoundingBox(p.getX(), p.getY(), p.getZ(), p.getX() + lx + 2, p.getY() + ly + 2, p.getZ() + lz + 2)
				.contains(context
					.getPlayer().getBoundingBox()))
			{
				sbe.sit(0, context.getPlayer());
			}
		}
	}

	public Vector getPosition()
	{
		double x = lerp(pointA.x(), pointB.x(), progress);
		double y = lerp(pointA.y(), pointB.y(), progress);
		double z = lerp(pointA.z(), pointB.z(), progress);
		return new Vector(x, y, z);
	}

	public void updatePosition()
	{
		if (sbe != null)
		{
			Vector position = getPosition();
			sbe.move(new Location(pos.getWorld(), position.getX() + 0.5, position.getY(), position.getZ() + 0.5));
		}
	}

	private static double lerp(double a, double b, double percentage)
	{
		return a + percentage * (b - a);
	}

//	@Override
//	public void load(JSONObject json)
//	{
//	}
//
//	@Override
//	public void save(JSONObject json, boolean unloading)
//	{
//		if (unloading)
//		{
//			if (sbe != null)
//			{
//				sbe.destroy(0);
//				sbe = null;
//			}
//		}
//	}

	@Override
	public void toNBT(NBT compound)
	{

	}

	@Override
	public void fromNBT(NBT compound)
	{

	}
}
