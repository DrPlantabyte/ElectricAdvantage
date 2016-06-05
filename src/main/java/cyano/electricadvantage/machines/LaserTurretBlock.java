package cyano.electricadvantage.machines;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class LaserTurretBlock extends ElectricMachineBlock {

	private final AxisAlignedBB baseBounds = new AxisAlignedBB(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
	private final AxisAlignedBB shaftBounds = new AxisAlignedBB(0.25f, 0.25f, 0.25f, 0.75f, 1.0f, 0.75f);

	// Evil laser turrets target players and not hostile mobs
	public final boolean evil;
	public LaserTurretBlock(boolean isEvil) {
		super();
		this.setHardness(5.0F).setResistance(100.0F);
		this.evil = isEvil;
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World arg0, int arg1) {
		return new LaserTurretTileEntity();
	}




	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState bs, final IBlockAccess world, final BlockPos coord) {
		return baseBounds;
	}
	@Override
	public void addCollisionBoxToList(final IBlockState bs, final World world, final BlockPos coord,
									  final AxisAlignedBB box, final List<AxisAlignedBB> collisionBoxList,
									  final Entity entity) {

		final EnumFacing orientation = (EnumFacing) world.getBlockState(coord).getValue(FACING);
		super.addCollisionBoxToList(coord, box, collisionBoxList, baseBounds);
		super.addCollisionBoxToList(coord, box, collisionBoxList, shaftBounds);
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
