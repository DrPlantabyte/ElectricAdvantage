package cyano.electricadvantage.machines;

import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.PoweredEntity;

public abstract class ElectricMachineBlock extends cyano.poweradvantage.api.simple.BlockSimplePowerConsumer{

	/**
	 * Blockstate property
	 */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public ElectricMachineBlock() {
		this(Material.piston);
	}
	
	public ElectricMachineBlock(Material m) {
		super(m, 0.75f, Power.ELECTRIC_POWER);
		this.setDefaultState(getDefaultState().withProperty(ACTIVE, false).withProperty(POWERED, false).withProperty(FACING, EnumFacing.NORTH));
	}
	
	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { ACTIVE,FACING,POWERED });
	}

	/**
	 * Converts metadata into blockstate
	 */
	@Override
	public IBlockState getStateFromMeta(final int metaValue) {
		EnumFacing enumFacing = metaToFacing(metaValue);
		if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
			enumFacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty( FACING, enumFacing)
				.withProperty(ACTIVE, (metaValue & 0x4) != 0)
				.withProperty(POWERED, (metaValue & 0x8) != 0);
	}
	
	/**
	 * Converts blockstate into metadata
	 */
	@Override
	public int getMetaFromState(final IBlockState bs) {
		int extraBit;
		if((Boolean)(bs.getValue(ACTIVE))){
			extraBit = 0x4;
		} else {
			extraBit = 0;
		}
		if((Boolean)(bs.getValue(POWERED))){
			extraBit = extraBit | 0x8;
		}
		return facingToMeta((EnumFacing)bs.getValue( FACING)) | extraBit;
	}
	
	private int facingToMeta(EnumFacing f){
		switch(f){
			case NORTH: return 0;
			case WEST: return 1;
			case SOUTH: return 2;
			case EAST: return 3;
			default: return 0;
		}
	}
	private EnumFacing metaToFacing(int i){
		int f = i & 0x03;
		switch(f){
			case 0: return EnumFacing.NORTH;
			case 1: return EnumFacing.WEST;
			case 2: return EnumFacing.SOUTH;
			case 3: return EnumFacing.EAST;
			default: return EnumFacing.NORTH;
		}
	}
	
	@Override
	public abstract ElectricMachineTileEntity createNewTileEntity(World w, int m);

	@Override
	public int getComparatorInputOverride(World w, BlockPos p) {
		TileEntity te = w.getTileEntity(p);
		if(te instanceof ElectricMachineTileEntity){
			return ((ElectricMachineTileEntity)te).getComparatorOutput();
		}
		return 0;
		
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IInventory)
		{
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)te);
			((IInventory)te).clear();
			world.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(world, pos, state);
	}

}
