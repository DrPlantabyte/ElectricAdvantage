package cyano.electricadvantage.machines;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import cyano.poweradvantage.init.Fluids;


/**
 * The coal boiler is a bit more complicated because it consumes fluids (water) and produces steam, 
 * making it a multi-type powered block.
 * @author DrCyano
 *
 */
public class GrowthChamberControllerBlock extends cyano.poweradvantage.api.simple.BlockSimplePowerSource{

	
	public GrowthChamberControllerBlock() {
		super(Material.piston, 0.75f, Power.GROWTHCHAMBER_POWER);
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new GrowthChamberControllerTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		if(world.getTileEntity(coord) instanceof GrowthChamberControllerTileEntity){
			return ((GrowthChamberControllerTileEntity)world.getTileEntity(coord)).getComparatorOutput();
		}
		return 0;
	}

	
	
	///// Overrides to make this a multi-type block /////
	
	/**
	 * This method is called whenever the block is placed into the world
	 */
	@Override
	public void onBlockAdded(World w, BlockPos coord, IBlockState state){
		super.onBlockAdded(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(w, w.provider.getDimensionId(), coord, cyano.poweradvantage.init.Fluids.fluidConduit_general);
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(w, w.provider.getDimensionId(), coord, Power.ELECTRIC_POWER);
	}
	
	/**
	 * This method is called when the block is removed from the world by an entity.
	 */
	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, cyano.poweradvantage.init.Fluids.fluidConduit_general);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, Power.ELECTRIC_POWER);
	}
	/**
	 * This method is called when the block is destroyed by an explosion.
	 */
	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, Fluids.fluidConduit_general);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, Power.ELECTRIC_POWER);
	}
	
	
	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 * @param type The type of energy in the conduit
	 * @param blockFace The side through-which the energy is flowing
	 * @return true if this conduit can flow the given energy type through the given face, false 
	 * otherwise
	 */
	public boolean canAcceptType(ConduitType type, EnumFacing blockFace){
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(Fluids.fluidConduit_general, type)
				 || ConduitType.areSameType(Power.ELECTRIC_POWER, type);
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	public boolean canAcceptType(ConduitType type){
		return  ConduitType.areSameType(getType(), type) || ConduitType.areSameType(Fluids.fluidConduit_general, type)
				 || ConduitType.areSameType(Power.ELECTRIC_POWER, type);
	}
	
	@Override
	public boolean isPowerSink(){
		return true;
	}
	@Override
	public boolean isPowerSource(){
		return true;
	}

	///// end multi-type overrides /////
}
