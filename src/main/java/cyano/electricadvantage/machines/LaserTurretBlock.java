package cyano.electricadvantage.machines;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitBlock;
import cyano.poweradvantage.api.ConduitType;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class LaserTurretBlock extends ConduitBlock implements ITileEntityProvider {

	
	public LaserTurretBlock() {
		super(Material.iron);
		this.setHardness(5.0F).setResistance(100.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new LaserTurretTileEntity();
	}

	
	/**
	 * 3 = normal block (model specified in assets folder as .json model)<br>
	 * -1 = special renderer
	 */
	@Override
	public int getRenderType()
	{
		return 3;
	}

	//And this tell it that you can see through this block, and neighbor blocks should be rendered.
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(){
		return false;
	}
	
	@Override
	public void addCollisionBoxesToList(final World w, final BlockPos coord, final IBlockState bs, 
			final AxisAlignedBB bb, final List list, final Entity e) {
		this.setBlockBoundsBasedOnState(w,coord);
		super.addCollisionBoxesToList(w, coord, bs, bb, list, e);
		this.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 1.0f, 0.75f);
		super.addCollisionBoxesToList(w, coord, bs, bb, list, e);
	}

	@Override
	public void setBlockBoundsBasedOnState(final IBlockAccess bs, final BlockPos coord) {
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
	}


	
	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		w.removeTileEntity(coord);
	}

	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		w.removeTileEntity(coord);
	}

	@Override
	public boolean canAcceptType(ConduitType t) {
		return ConduitType.areSameType(t, Power.ELECTRIC_POWER);
	}

	@Override
	public boolean canAcceptType(ConduitType t, EnumFacing face) {
		return face != EnumFacing.UP && ConduitType.areSameType(t, Power.ELECTRIC_POWER);
	}

	@Override
	public ConduitType getType() {
		return Power.ELECTRIC_POWER;
	}

	@Override
	public boolean isPowerSink() {
		return true;
	}

	@Override
	public boolean isPowerSource() {
		return false;
	}

	
	
}
