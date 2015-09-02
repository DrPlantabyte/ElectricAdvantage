package cyano.electricadvantage.util.farming;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.FMLLog;

public class VirtualCrop {

	
	public static VirtualCrop createVirtualCrop(ItemStack stack, World w, BlockPos pos){
		ItemStack seed = stack.copy();
		seed.stackSize = 1;
		FMLLog.info("%s: Making virual crop for %s", VirtualCrop.class.getName(), stack);// TODO: remove
		Item i = stack.getItem();
		if(i instanceof IPlantable){
			FMLLog.info("%s: %s", VirtualCrop.class.getName(), "is plantable");// TODO: remove
			IPlantable plantable = (IPlantable)i;
			IBlockState startingState = plantable.getPlant(w, pos);
			Block block = startingState.getBlock();
			int numStages = getNumberOfGrowthStages(startingState);
			if(block instanceof BlockStem){
				FMLLog.info("%s: %s", VirtualCrop.class.getName(), "is stem plant");// TODO: remove
				Block product = getBlockFieldByReflection(block);
				if(product == null) {
					FMLLog.info("%s: %s", VirtualCrop.class.getName(), "Reflection failed");// TODO: remove
					return null;
				}
				return new VirtualCrop(seed,numStages,Arrays.asList(new ItemStack(product,1,0)));
			} else if(block instanceof BlockBush){
				FMLLog.info("%s: %s", VirtualCrop.class.getName(), "is bush/crop plant");// TODO: remove
				IBlockState maxAge = ageToMax(startingState);
				List<ItemStack> harvest = block.getDrops(w, pos, maxAge, 0);
				return new VirtualCrop(seed,numStages,harvest);
			} 
			// TODO: reeds, cactus, custom definitions, plant megapack
		}
		FMLLog.info("%s: %s", VirtualCrop.class.getName(), "Failed to make virtual crop");// TODO: remove
		return null;
	}
	
	

	private static int getNumberOfGrowthStages(IBlockState startingState) {
		for(Object o : startingState.getProperties().entrySet()){
			Map.Entry e = (Map.Entry)o;
			if(e.getKey() instanceof PropertyInteger){
				PropertyInteger prop = (PropertyInteger)e.getKey();
				if("age".equals(prop.getName())){
					return prop.getAllowedValues().size();
				}
			}
		}
		return 8;
	}
	

	private static IBlockState ageToMax(IBlockState startingState) {
		for(Object o : startingState.getProperties().entrySet()){
			Map.Entry e = (Map.Entry)o;
			if(e.getKey() instanceof PropertyInteger){
				PropertyInteger prop = (PropertyInteger)e.getKey();
				if("age".equals(prop.getName())){
					int max = 0;
					for(Object i : prop.getAllowedValues()){
						if((Integer)i > max){max = (Integer)i;}
					}
					return startingState.withProperty(prop, Integer.valueOf(max));
				}
			}
		}
		return startingState;
	}
	
	private static Block getBlockFieldByReflection(Object target) {
		Class type = Block.class;
		Field[] fields = target.getClass().getFields();
		try {
			for (Field f : fields){
				if(type.isAssignableFrom(f.getType())){
					// found a block member variable, I hope it is the right one!
					f.setAccessible(true);
					Object got;
					got = f.get(target);
					return (Block)got;
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			FMLLog.info("%s: %s", VirtualCrop.class.getName(), "Reflection failed");// TODO: remove
			return null;
		}
		// no block member variables found
		return null;
	}

	private final byte numberOfStages;
	private byte stage = 0;
	private final List<ItemStack> harvest;
	private final List<ItemStack> prematureHarvest;
	
	public VirtualCrop(ItemStack item, int numGrowthStages, Collection<ItemStack> harvest){
		FMLLog.info("%s: Made VirtualCrop instance with %s states from item %s that produces %s", VirtualCrop.class.getName(), numGrowthStages,item,Arrays.toString(harvest.toArray()));// TODO: remove
		this.numberOfStages = (byte)(numGrowthStages & 0x7F);
		this.prematureHarvest = Arrays.asList(item);
		this.harvest = new ArrayList<>(harvest);
	}
	
	public List<ItemStack> getHarvest(){
		if(stage >= numberOfStages){
			return harvest;
		} else {
			return prematureHarvest;
		}
	}
	/**
	 * Grows this crop by 1 growth stage
	 * @return True if the crop is ready for harvest, false otherwise
	 */
	public boolean grow(){
		stage++;
		return stage >= numberOfStages;
	}
	
	
}
