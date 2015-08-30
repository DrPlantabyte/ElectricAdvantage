package cyano.electricadvantage.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Ore extends Block {

	private final ItemStack dropItem;
	private final int dropRange;
	
	
	public Ore(ItemStack oreDrop, int pickLevel, int variation){
		super(Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(1.5f+pickLevel); // dirt is 0.5, grass is 0.6, stone is 1.5,iron ore is 3, obsidian is 50
		this.setResistance(5*(1+pickLevel)); // dirt is 0, iron ore is 5, stone is 10, obsidian is 2000
		this.setStepSound(Block.soundTypePiston); // sound for stone
		this.setHarvestLevel("pickaxe", pickLevel);
		dropItem = oreDrop;
		dropRange = variation+1;
	}
	
	public Ore( int pickLevel){
		super(Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(1.5f+pickLevel); // dirt is 0.5, grass is 0.6, stone is 1.5,iron ore is 3, obsidian is 50
		this.setResistance(5*(1+pickLevel)); // dirt is 0, iron ore is 5, stone is 10, obsidian is 2000
		this.setStepSound(Block.soundTypePiston); // sound for stone
		this.setHarvestLevel("pickaxe", pickLevel);
		dropItem = null;
		dropRange = 0;
	}
	
	@Override public Item getItemDropped(IBlockState bs, Random prng, int n)
	{
		if(dropItem == null){
			return super.getItemDropped(bs, prng, n);
		}
		return dropItem.getItem();
	}
	@Override public int quantityDropped(Random prng)
	{
		final int baseCount;
		if(dropItem == null){
			baseCount = 1;
		} else {
			baseCount = dropItem.stackSize;
		}
		if(dropRange > 1){
			return baseCount + prng.nextInt(dropRange);
		}
		return baseCount;
	}
}
