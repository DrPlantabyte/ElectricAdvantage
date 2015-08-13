package cyano.electricadvantage.machines;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class PhotovoltaicGeneratorBlock extends ElectricGeneratorBlock{

	public PhotovoltaicGeneratorBlock(){
		super(Material.rock);
		this.setStepSound(soundTypeGlass);
	}
	
	@Override
	public ElectricGeneratorTileEntity createNewTileEntity(World arg0, int arg1) {
		net.minecraft.block.BlockDaylightDetector k;
		return new PhotovoltaicGeneratorTileEntity();
	}
	

	@Override
	public boolean isFullCube()
	{
		return true;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return true;
	}


}
