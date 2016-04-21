package cyano.electricadvantage.machines;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class PhotovoltaicGeneratorBlock extends ElectricGeneratorBlock{

	public PhotovoltaicGeneratorBlock(){
		super(Material.ROCK);
		this.setSoundType(SoundType.GLASS);
	}
	
	@Override
	public ElectricGeneratorTileEntity createNewTileEntity(World arg0, int arg1) {
		net.minecraft.block.BlockDaylightDetector k;
		return new PhotovoltaicGeneratorTileEntity();
	}
	

	@Override
	public boolean isFullCube(IBlockState bs)
	{
		return true;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState bs)
	{
		return true;
	}


}
