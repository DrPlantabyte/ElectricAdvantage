package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.*;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

public class ElectricDrillBlock extends GUIBlock implements ITypedConduit {



	private final ConduitType type;
	private final ConduitType[] types = new ConduitType[1];

	/**
	 * Blockstate property
	 */
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");


	public ElectricDrillBlock(){
		super(Material.piston);
		this.type = Power.ELECTRIC_POWER;
		types[0] = this.type;
		super.setHardness(0.75f);
		this.setDefaultState(getDefaultState().withProperty(ACTIVE, false).withProperty(FACING, EnumFacing.DOWN));
	}


	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ACTIVE,FACING });
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new ElectricDrillTileEntity();
	}

	

	/**
	 * Override of default block behavior
	 */
	@Override
	public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(world, world.provider.getDimension(), coord, type);
	}
	/**
	 * This method is called when the block is removed from the world by an entity.
	 */
	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimension(), coord, type);
	}
	/**
	 * This method is called when the block is destroyed by an explosion.
	 */
	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimension(), coord, type);
	}

	/**
	 * Used to decides whether or not a conduit should connect to this block 
	 * based on its energy type.
	 * @return The type of energy for this block 
	 */
	@Override
	public ConduitType[] getTypes() {
		return types;
	}

	@Override
	public boolean isPowerSink(ConduitType conduitType) {
		return true;
	}

	@Override
	public boolean isPowerSource(ConduitType conduitType) {
		return false;
	}


	@Override
	public boolean canAcceptConnection(PowerConnectorContext connection) {
		return ConduitType.areSameType( type, connection.powerType);
	}


	/**
	 * Override of default block behavior
	 */
	@Override
	public Item getItemDropped(final IBlockState state, final Random prng, final int i3) {
		return Item.getItemFromBlock(this);
	}



	/**
	 * Creates the blockstate of this block when it is placed in the world
	 */
	@Override
	public IBlockState onBlockPlaced(final World world, final BlockPos coord, final EnumFacing facing, 
			final float f1, final float f2, final float f3, 
			final int meta, final EntityLivingBase player) {
		return this.getDefaultState().withProperty( FACING, facing.getOpposite());
	}

	/**
	 * Creates the blockstate of this block when it is placed in the world
	 */
	@Override
	public void onBlockPlacedBy(final World world, final BlockPos coord, final IBlockState bs, 
			final EntityLivingBase placer, final ItemStack srcItemStack) {
		if (srcItemStack.hasDisplayName()) {
			final TileEntity tileEntity = world.getTileEntity(coord);
			if (tileEntity instanceof PoweredEntity){
				((PoweredEntity)tileEntity).setCustomInventoryName(srcItemStack.getDisplayName());
			}
		}
	}




	/**
	 * Sets the default blockstate
	 * @param w World instance
	 * @param coord Block coordinate
	 * @param state Block state
	 */
	protected void setDefaultFacing(final World w, final BlockPos coord, final IBlockState state) {
		if (w.isRemote) {
			return;
		}
		EnumFacing enumFacing = EnumFacing.DOWN;
		w.setBlockState(coord, state.withProperty((IProperty) FACING, (Comparable)enumFacing), 2);
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
				.withProperty(ACTIVE, (metaValue & 0x8) != 0);
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
		return facingToMeta((EnumFacing)bs.getValue( FACING)) | extraBit;
	}
	
	private int facingToMeta(EnumFacing f){
		return f.getIndex();
	}
	private EnumFacing metaToFacing(int i){
		int f = i & 0x07;
		return EnumFacing.values()[f];
	}


	@Override
	public int getComparatorInputOverride(IBlockState bs, World w, BlockPos p) {
		TileEntity te = w.getTileEntity(p);
		if(te instanceof ElectricMachineTileEntity){
			return ((ElectricMachineTileEntity)te).getComparatorOutput();
		}
		return 0;

	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState bs) {
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
