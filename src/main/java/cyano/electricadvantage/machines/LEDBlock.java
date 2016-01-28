package cyano.electricadvantage.machines;

import java.util.List;

import cyano.electricadvantage.blocks.ElectricConduitBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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
	public int getLightValue(IBlockAccess world, BlockPos pos){
		IBlockState bs = world.getBlockState(pos);
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
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { WEST, DOWN, SOUTH, EAST, UP, NORTH, LIT });
	}
	
	@Override
	public boolean isPowerSink(){
		return true;
	}
	

    /**
     * Calculates the collision boxes for this block.
     */
	@Override
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final BlockPos coord) {
		IBlockState oldState = world.getBlockState(coord);
        final boolean connectNorth = this.canConnectTo(world,coord,oldState,EnumFacing.NORTH, coord.north());
        final boolean connectSouth = this.canConnectTo(world,coord,oldState,EnumFacing.SOUTH, coord.south());
        final boolean connectWest =  this.canConnectTo(world,coord,oldState,EnumFacing.WEST,  coord.west());
        final boolean connectEast =  this.canConnectTo(world,coord,oldState,EnumFacing.EAST,  coord.east());
        final boolean connectUp =    this.canConnectTo(world,coord,oldState,EnumFacing.UP,    coord.up());
        boolean       connectDown =  this.canConnectTo(world,coord,oldState,EnumFacing.DOWN,  coord.down());
        
        if(!(connectNorth || connectSouth || connectWest || connectEast || connectUp || connectDown)){
        	connectDown = true;
        }
        
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
        this.setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    /**
     * Calculates the collision boxes for this block.
     */
	@Override
    public void addCollisionBoxesToList(final World world, final BlockPos coord, 
    		final IBlockState bs, final AxisAlignedBB box, final List collisionBoxList, 
    		final Entity entity) {
		IBlockState oldState = bs;
        final boolean connectNorth = this.canConnectTo(world,coord,oldState,EnumFacing.NORTH, coord.north());
        final boolean connectSouth = this.canConnectTo(world,coord,oldState,EnumFacing.SOUTH, coord.south());
        final boolean connectWest =  this.canConnectTo(world,coord,oldState,EnumFacing.WEST,  coord.west());
        final boolean connectEast =  this.canConnectTo(world,coord,oldState,EnumFacing.EAST,  coord.east());
        final boolean connectUp =    this.canConnectTo(world,coord,oldState,EnumFacing.UP,    coord.up());
        boolean       connectDown =  this.canConnectTo(world,coord,oldState,EnumFacing.DOWN,  coord.down());
        
        if(!(connectNorth || connectSouth || connectWest || connectEast || connectUp || connectDown)){
        	connectDown = true;
        }
        
        float radius = pipeRadius;
        float rminus = 0.5f - radius;
        float rplus = 0.5f + radius;
        
        this.setBlockBounds(rminus, rminus, rminus, rplus, rplus, rplus);
        super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);

        if(connectUp){
            this.setBlockBounds(rminus, rminus, rminus, rplus, 1f, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectDown){
            this.setBlockBounds(rminus, 0f, rminus, rplus, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectEast){
            this.setBlockBounds(rminus, rminus, rminus, 1f, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectWest){
            this.setBlockBounds(0f, rminus, rminus, rplus, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectSouth){
            this.setBlockBounds(rminus, rminus, rminus, rplus, rplus, 1f);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectNorth){
            this.setBlockBounds(rminus, rminus, 0f, rplus, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
    }
	
}
