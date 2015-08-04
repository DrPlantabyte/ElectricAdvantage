package cyano.electricadvantage.machines;

import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.PoweredEntity;

public abstract class ElectricGeneratorBlock extends cyano.poweradvantage.api.simple.BlockSimplePowerConsumer{

	/**
	 * Blockstate property
	 */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public ElectricGeneratorBlock() {
		super(Material.piston, 0.75f, Power.electric_power);
		this.setDefaultState(getDefaultState().withProperty(ACTIVE, false).withProperty(FACING, EnumFacing.NORTH));
	}
	
	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { ACTIVE,FACING });
	}

	/**
	 * Converts metadata into blockstate
	 */
	@Override
	public IBlockState getStateFromMeta(final int metaValue) {
		EnumFacing enumFacing = EnumFacing.getFront(metaValue & 0x7);
		if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
			enumFacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty( FACING, enumFacing).withProperty(ACTIVE, (metaValue & 0x8) != 0);
	}
	
	/**
	 * Converts blockstate into metadata
	 */
	@Override
	public int getMetaFromState(final IBlockState bs) {
		int extraBit;
		if((Boolean)(bs.getValue(ACTIVE))){
			extraBit = 0x8;
		} else {
			extraBit = 0;
		}
		return ((EnumFacing)bs.getValue( FACING)).getIndex() | extraBit;
	}
	
	@Override
	public abstract ElectricGeneratorTileEntity createNewTileEntity(World arg0, int arg1);

	@Override
	public int getComparatorInputOverride(World w, BlockPos p) {
		TileEntity te = w.getTileEntity(p);
		if(te instanceof ElectricGeneratorTileEntity){
			return ((ElectricGeneratorTileEntity)te).getComparatorOutput();
		}
		return 0;
		
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

}
