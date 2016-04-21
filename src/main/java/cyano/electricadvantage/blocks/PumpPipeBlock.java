package cyano.electricadvantage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PumpPipeBlock extends Block{


	private static final AxisAlignedBB bounds = new AxisAlignedBB(0.25,0.00,0.25,0.75,1.00,0.75);

	public PumpPipeBlock() {
		super(Material.IRON);
		this.setHardness(5.0F).setResistance(10.0F);
	}

	@Override
	public Item getItemDropped(IBlockState bs, Random rand, int fortune){
		return null;
	}
	
	@Override 
	public int quantityDropped(IBlockState state, int fortune, Random random){
		return 0;
	}
	
	@Override public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		return Collections.EMPTY_LIST;
	}


	//And this tell it that you can see through this block, and neighbor blocks should be rendered.
	@Override
	public boolean isOpaqueCube(IBlockState bs)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState bs){
		return false;
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return bounds;
	}
	@Override
	public void addCollisionBoxToList(final IBlockState bs, final World world, final BlockPos coord,
									  final AxisAlignedBB box, final List collisionBoxList,
									  final Entity entity) {
		super.addCollisionBoxToList(coord, box, collisionBoxList, bounds);
	}




	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		destroyNeighbors(w,coord,w.getBlockState(coord));
		super.onBlockDestroyedByPlayer(w, coord, state);
	}

	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		destroyNeighbors(w,coord,w.getBlockState(coord));
		super.onBlockDestroyedByExplosion(w, coord, boom);
	}

	
	private void destroyNeighbors(World w, BlockPos coord, IBlockState state) {
		if(w.isRemote) return;
		// destroy connected drill bits
		BlockPos c = coord.down();
		while(c.getY() > 0 && w.getBlockState(c).getBlock() == this){
			w.setBlockToAir(c);
			c = c.down();
		}
	}
	
}
