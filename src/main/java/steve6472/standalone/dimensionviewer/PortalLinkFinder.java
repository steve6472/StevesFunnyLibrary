package steve6472.standalone.dimensionviewer;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.portal.PortalForcer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorldBorder;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 10/13/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PortalLinkFinder
{
	public static Location findPortal(Location location, World otherWorld)
	{
		Optional<BlockUtil.FoundRectangle> portalAround
			= new PortalForcer(
				((CraftWorld) otherWorld).getHandle())
			.findPortalAround(
				new BlockPos(
					location.getBlockX(),
					location.getBlockY(),
					location.getBlockZ()),
				otherWorld.getEnvironment() == World.Environment.NORMAL,
				((CraftWorldBorder) location.getWorld().getWorldBorder()).getHandle());

		if (portalAround.isEmpty())
			return null;

		BlockUtil.FoundRectangle foundRectangle = portalAround.get();
		return new Location(otherWorld, foundRectangle.minCorner.getX(), foundRectangle.minCorner.getY(), foundRectangle.minCorner.getZ());
	}
}
