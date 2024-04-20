package steve6472.funnylib.workdistro.impl;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/**
 * Created by steve6472
 * Date: 4/18/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PlaceBlockDataWorkload extends WorldWorkload
{
    private final int x, y, z;
    private final BlockData block;
    private final boolean applyPhysics;

    public PlaceBlockDataWorkload(World world, int x, int y, int z, BlockData block, boolean applyPhysics)
    {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.applyPhysics = applyPhysics;
    }

    @Override
    public void compute()
    {
        World world = getWorld();
        if (world == null) return;

        if (applyPhysics)
        {
            world.setBlockData(x, y, z, block.clone());
        } else
        {
            world.getBlockAt(x, y, z).setBlockData(block.clone(), false);
        }
    }
}
