package cyano.electricadvantage.machines;

import java.util.Random;

import com.google.common.base.Predicate;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerConsumer;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElectricDrillBlock extends GUIBlock implements ITypedConduit {



	private final ConduitType type;

	/**
	 * Blockstate property
	 */
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool POWERED = PropertyBool.create("powered");


	public ElectricDrillBlock(){
		super(Material.piston);
		this.type = Power.ELECTRIC_POWER;
		super.setHardness(0.75f);
	}


	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { ACTIVE,FACING,POWERED });
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new ElectricDrillTileEntity();
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
	 * Override of default block behavior
	 */
	@Override
	public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
		this.setDefaultFacing(world, coord, state);
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(world, world.provider.getDimensionId(), coord, getType());
	}
	/**
	 * This method is called when the block is removed from the world by an entity.
	 */
	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, getType());
	}
	/**
	 * This method is called when the block is destroyed by an explosion.
	 */
	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, getType());
	}

	/**
	 * Used to decides whether or not a conduit should connect to this block 
	 * based on its energy type.
	 * @return The type of energy for this block 
	 */
	@Override
	public ConduitType getType() {
		return type;
	}

	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 * @param type The type of energy in the conduit
	 * @param blockFace The side through-which the energy is flowing
	 * @return true if this conduit can flow the given energy type through the given face, false 
	 * otherwise
	 */
	public boolean canAcceptType(ConduitType type, EnumFacing blockFace){
		return ConduitType.areSameType(getType(), type);
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	public boolean canAcceptType(ConduitType type){
		return ConduitType.areSameType(getType(), type);
	}

	/**
	 * Determines whether this block/entity should receive energy 
	 * @return true if this block/entity should receive energy
	 */
	public boolean isPowerSink(){
		return true;
	}
	/**
	 * Determines whether this block/entity can provide energy 
	 * @return true if this block/entity can provide energy
	 */
	public boolean isPowerSource(){
		return false;
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
	public void onBlockPlacedBy(final World world, final BlockPos coord, final IBlockState bs, 
			final EntityLivingBase placer, final ItemStack srcItemStack) {
		world.setBlockState(coord, bs.withProperty((IProperty) FACING, (Comparable)placer.getHorizontalFacing().getOpposite()), 2);
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
	 * Override of default block behavior
	 */
	@Override
	public int getRenderType() {
		return 3;
	}

	/**
	 * Converts metadata into blockstate
	 */
	@Override
	public IBlockState getStateFromMeta(final int metaValue) {
		EnumFacing enumFacing = EnumFacing.getFront(metaValue);
		if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
			enumFacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty( FACING, enumFacing);
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
		if((Boolean)(bs.getValue(POWERED))){
			extraBit = extraBit | 0x10;
		}
		return ((EnumFacing)bs.getValue( FACING)).getIndex() | extraBit;
	}


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

	///// CLIENT-SIDE CODE /////

	/**
	 * (Client-only) Gets the blockstate used for GUI and such.
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public IBlockState getStateForEntityRender(final IBlockState bs) {
		return this.getDefaultState().withProperty( FACING, EnumFacing.SOUTH);
	}

	/**
	 * (Client-only) Override of default block behavior
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(final World world, final BlockPos coord) {
		return Item.getItemFromBlock(this);
	}



}
