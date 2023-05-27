package steve6472.standalone.machinal.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import steve6472.funnylib.blocks.*;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.SkullCreator;
import steve6472.standalone.Skins;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 5/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class GenericFullHeadBlock extends CustomBlock implements IBlockData, BlockClickEvents
{
	private final String id;
	private final Function<PlayerBlockContext, Boolean> canBeBroken;
	private final Function<PlayerBlockContext, Integer> toolDamage;
	private BiConsumer<PlayerBlockContext, List<ItemStack>> dropsPlayer = (e, l) -> {};
	private BiConsumer<BlockContext, List<ItemStack>> dropsBlock = (e, l) -> {};
	protected boolean rotate = false;

	public GenericFullHeadBlock(String id, Function<PlayerBlockContext, Boolean> canBeBroken, Function<PlayerBlockContext, Integer> toolDamage)
	{
		this.id = id;
		this.canBeBroken = canBeBroken;
		this.toolDamage = toolDamage;
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return Material.BARRIER.createBlockData();
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new Data();
	}

	@Override
	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		this.dropsPlayer.accept(context, drops);
	}

	@Override
	public void getDrops(BlockContext blockContext, List<ItemStack> drops)
	{
		this.dropsBlock.accept(blockContext, drops);
	}

	public GenericFullHeadBlock setPlayerDrops(BiConsumer<PlayerBlockContext, List<ItemStack>> drops)
	{
		this.dropsPlayer = drops;
		return this;
	}

	public GenericFullHeadBlock setBlockDrops(BiConsumer<BlockContext, List<ItemStack>> drops)
	{
		this.dropsBlock = drops;
		return this;
	}

	public GenericFullHeadBlock rotate()
	{
		this.rotate = true;
		return this;
	}

	@Override
	public void leftClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (canBeBroken.apply(context))
		{
			ItemStackBuilder.edit(context.getHandItem()).dealDamage(toolDamage.apply(context)).buildItemStack();
			Blocks.callBlockBreak(context);
		}
	}

	private class Data extends CustomBlockData implements IBlockEntity
	{
		private static final Vector3f[] POSITIONS = {
			new Vector3f(0.25f, 0.5f, 0.25f),
			new Vector3f(0.25f, 0.5f, -0.25f),
			new Vector3f(-0.25f, 0.5f, -0.25f),
			new Vector3f(-0.25f, 0.5f, 0.25f),
			new Vector3f(0.25f, -0.25f, 0.5f),
			new Vector3f(0.5f, -0.25f, -0.25f),
			new Vector3f(-0.25f, -0.25f, -0.5f),
			new Vector3f(-0.5f, -0.25f, 0.25f)
		};

		private static final Vector3f[] POSITIONS_INVERTED = {
			new Vector3f(-0.25f, 0, -0.25f),
			new Vector3f(-0.25f, 0, 0.25f),
			new Vector3f(0.25f, 0, 0.25f),
			new Vector3f(0.25f, 0, -0.25f),
			new Vector3f(-0.25f, 0.25f, 0),
			new Vector3f(0, 0.25f, 0.25f),
			 new Vector3f(0.25f, 0.25f, 0),
			 new Vector3f(0, 0.25f, -0.25f)
		};

		private Vector3f getPos(int index)
		{
			return rotate ? POSITIONS_INVERTED[index % 8] : POSITIONS[index % 8];
		}

		ItemDisplay[] items = new ItemDisplay[8];

		@Override
		public void spawnEntities(BlockContext context)
		{
			Location loc = context.getLocation().clone().add(0.5, 0.5, 0.5);

			// Top South-East
			items[0] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(0));
				e.setTransformation(transformation);
			});

			// Top North-East
			items[1] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(1));
				transformation.getLeftRotation().set(0, 0.707f, 0, 0.707f);
				e.setTransformation(transformation);
			});

			// Top North-West
			items[2] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(2));
				transformation.getLeftRotation().set(0, 1, 0, 0);
				e.setTransformation(transformation);
			});

			// Top South-West
			items[3] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(3));
				transformation.getLeftRotation().set(0, -0.707f, 0, 0.707f);
				e.setTransformation(transformation);
			});



			// Bottom South-East
			items[4] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(4));
				transformation.getRightRotation().set(0.707f, -0, 0, 0.707f);
				e.setTransformation(transformation);
			});

			// Bottom North-East
			items[5] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(5));
				transformation.getLeftRotation().set(0, 0.707f, 0, 0.707f);
				transformation.getRightRotation().set(0.707f, -0, 0, 0.707f);
				e.setTransformation(transformation);
			});

			// Bottom North-West
			items[6] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(6));
				transformation.getLeftRotation().set(0, 1, 0, 0);
				transformation.getRightRotation().set(0.707f, -0, 0, 0.707f);
				e.setTransformation(transformation);
			});

			// Bottom South-West
			items[7] = context.getWorld().spawn(loc, ItemDisplay.class, e -> {
				e.setItemStack(SkullCreator.itemFromUrl(Skins.toUrl(Skins.Blocks.LIGHT_HEAVY_MACHINE_BLOCK)));
				Transformation transformation = e.getTransformation();
				transformation.getTranslation().set(getPos(7));
				transformation.getLeftRotation().set(0, -0.707f, 0, 0.707f);
				transformation.getRightRotation().set(0.707f, -0, 0, 0.707f);
				e.setTransformation(transformation);
			});
		}

		@Override
		public void despawnEntities(BlockContext context)
		{
			IBlockEntity.super.despawnEntities(context);
			items = new ItemDisplay[8];
		}

		@Override
		public Entity[] getEntities()
		{
			return items;
		}

		@Override public void toNBT(NBT compound) {}
		@Override public void fromNBT(NBT compound) {}
	}
}
