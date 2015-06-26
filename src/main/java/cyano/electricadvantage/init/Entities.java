package cyano.electricadvantage.init;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.machines.*;

public class Entities {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();
		

		
		
		initDone = true;
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenderers(){
		//ClientRegistry.bindTileEntitySpecialRenderer(MyTileEntity.class, new cyano.electricadvantage.graphics.MyRenderer());
	}
}
