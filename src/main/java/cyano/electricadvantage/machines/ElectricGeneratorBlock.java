package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ElectricGeneratorBlock extends cyano.poweradvantage.api.simple.BlockSimplePowerMachine{

	/**
	 * Blockstate property
	 */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public ElectricGeneratorBlock() {
		this(Material.piston);
	}
	

	public ElectricGeneratorBlock(Material m) {
		super(m, 0.75f, Power.ELECTRIC_POWER);
		this.setDefaultState(getDefaultState().withProperty(ACTIVE, false).withProperty(FACING, EnumFacing.NORTH));
	}
	
	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ACTIVE,FACING });
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
	public int getComparatorInputOverride(IBlockState bs, World w, BlockPos p){
		return getComparatorInputOverride(w,p);
	}
	public int getComparatorInputOverride(World w, BlockPos p) {
		TileEntity te = w.getTileEntity(p);
		if(te instanceof ElectricGeneratorTileEntity){
			return ((ElectricGeneratorTileEntity)te).getComparatorOutput();
		}
		return 0;

	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState bs){
		return hasComparatorInputOverride();
	}
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


	@Override
	public boolean isPowerSink(ConduitType e){
		return false;
	}

	@Override
	public boolean isPowerSource(ConduitType e){
		return true;
	}

	public ConduitType getType() {return Power.ELECTRIC_POWER;}
}
