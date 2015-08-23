package cyano.electricadvantage.blocks;

import java.util.List;

import cyano.electricadvantage.init.Power;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ElectricScaffoldBlock extends cyano.poweradvantage.api.simple.BlockSimplePowerConduit{

	public ElectricScaffoldBlock() {
		super(Material.piston, 0.75f, 2f/16f, Power.ELECTRIC_POWER);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(final IBlockAccess world, final BlockPos coord) {
		this.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
	}
	@Override
	public void addCollisionBoxesToList(final World world, final BlockPos coord, 
			final IBlockState bs, final AxisAlignedBB box, final List collisionBoxList, 
			final Entity entity) {
		this.setBlockBoundsBasedOnState(world,coord);
		AxisAlignedBB aabb = this.getCollisionBoundingBox(world, coord, bs);
		if (aabb != null && box.intersectsWith(aabb)) {
			collisionBoxList.add(aabb);
		}
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	@Override
	public net.minecraft.util.EnumWorldBlockLayer getBlockLayer(){
		return net.minecraft.util.EnumWorldBlockLayer.CUTOUT;
	}

}
