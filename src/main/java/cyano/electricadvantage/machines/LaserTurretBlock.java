package cyano.electricadvantage.machines;

import java.util.List;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.api.ITypedConduit;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class LaserTurretBlock extends ElectricMachineBlock {

	
	public LaserTurretBlock() {
		super();
		this.setHardness(5.0F).setResistance(100.0F);
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World arg0, int arg1) {
		return new LaserTurretTileEntity();
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
	public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(w, pos, state, placer, stack);
		TileEntity te = w.getTileEntity(pos);
		if(placer instanceof EntityPlayer && te instanceof LaserTurretTileEntity){
			((LaserTurretTileEntity)te).setOwner((EntityPlayer)placer);
		}
	}

}
