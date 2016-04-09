package cyano.electricadvantage.machines;

import cyano.electricadvantage.blocks.ElectricConduitBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class LEDBlock extends ElectricConduitBlock implements ITileEntityProvider{

	private final float pipeRadius = 2f/16f;
	public static final PropertyBool LIT = PropertyBool.create("lit");
	
	public LEDBlock(){
		super();
		this.setDefaultState(getDefaultState().withProperty(LIT, false));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new LEDTileEntity();
	}
	
	@Override
	public int getLightValue(IBlockState bs, IBlockAccess world, BlockPos pos){
		if(bs.getBlock() == this && (Boolean)bs.getValue(LIT) == true){
			return 15;
		} else {
			return 0;
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState bs){
		if(bs.getBlock() == this && (Boolean)bs.getValue(LIT) == true){
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public IBlockState getStateFromMeta(int m){
		return this.getDefaultState().withProperty(LIT, (m & 1) != 0);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { WEST, DOWN, SOUTH, EAST, UP, NORTH, LIT });
	}
	
	@Override
	public boolean isPowerSink(){
		return true;
	}


	/**
	 * Calculates the collision boxes for this block.
	 */
	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState bs, final IBlockAccess world, final BlockPos coord) {
		IBlockState oldBS = bs;
		final boolean connectNorth = this.canConnectTo(world,coord,oldBS,EnumFacing.NORTH, coord.north());
		final boolean connectSouth = this.canConnectTo(world,coord,oldBS,EnumFacing.SOUTH, coord.south());
		final boolean connectWest = this.canConnectTo(world,coord,oldBS,EnumFacing.WEST, coord.west());
		final boolean connectEast = this.canConnectTo(world,coord,oldBS,EnumFacing.EAST, coord.east());
		final boolean connectUp = this.canConnectTo(world,coord,oldBS,EnumFacing.UP, coord.up());
		final boolean connectDown = this.canConnectTo(world,coord,oldBS,EnumFacing.DOWN, coord.down());
		final boolean allFalse = !(connectNorth || connectSouth || connectWest || connectEast || connectUp || connectDown);

		float radius = pipeRadius;
		float rminus = 0.5f - radius;
		float rplus = 0.5f + radius;

		float x1 = rminus;
		float x2 = rplus;
		float y1 = rminus;
		float y2 = rplus;
		float z1 = rminus;
		float z2 = rplus;
		if (connectNorth) {
			z1 = 0.0f;
		}
		if (connectSouth) {
			z2 = 1.0f;
		}
		if (connectWest) {
			x1 = 0.0f;
		}
		if (connectEast) {
			x2 = 1.0f;
		}
		if(connectDown){
			y1 = 0.0f;
		}
		if(connectUp){
			y2 = 1.0f;
		}
		if(allFalse){ // Horizontal '+' when making no connections
			z1 = 0.0f;
			z2 = 1.0f;
			x1 = 0.0f;
			x2 = 1.0f;
		}

		return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
	}

	/**
	 * Calculates the collision boxes for this block.
	 */
	@Override
	public void addCollisionBoxToList(final IBlockState bs, final World world, final BlockPos coord,
									  final AxisAlignedBB box, final List<AxisAlignedBB> collisionBoxList,
									  final Entity entity) {
		IBlockState oldBS = bs;
		final boolean connectNorth = this.canConnectTo(world,coord,oldBS,EnumFacing.NORTH, coord.north());
		final boolean connectSouth = this.canConnectTo(world,coord,oldBS,EnumFacing.SOUTH, coord.south());
		final boolean connectWest = this.canConnectTo(world,coord,oldBS,EnumFacing.WEST, coord.west());
		final boolean connectEast = this.canConnectTo(world,coord,oldBS,EnumFacing.EAST, coord.east());
		final boolean connectUp = this.canConnectTo(world,coord,oldBS,EnumFacing.UP, coord.up());
		final boolean connectDown = this.canConnectTo(world,coord,oldBS,EnumFacing.DOWN, coord.down());
		final boolean allFalse = !(connectNorth || connectSouth || connectWest || connectEast || connectUp || connectDown);
// Horizontal '+' when making no connections

		float radius = pipeRadius;
		float rminus = 0.5f - radius;
		float rplus = 0.5f + radius;

		AxisAlignedBB newBox;
		newBox = new AxisAlignedBB(rminus, rminus, rminus, rplus, rplus, rplus);
		super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);

		if(connectUp){
			newBox = new AxisAlignedBB(rminus, rminus, rminus, rplus, 1f, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if(connectDown){
			newBox = new AxisAlignedBB(rminus, 0f, rminus, rplus, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if(allFalse || connectEast){
			newBox = new AxisAlignedBB(rminus, rminus, rminus, 1f, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if(allFalse || connectWest){
			newBox = new AxisAlignedBB(0f, rminus, rminus, rplus, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if(allFalse || connectSouth){
			newBox = new AxisAlignedBB(rminus, rminus, rminus, rplus, rplus, 1f);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if(allFalse || connectNorth){
			newBox = new AxisAlignedBB(rminus, rminus, 0f, rplus, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
	}
	
}
