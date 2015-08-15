package cyano.electricadvantage.init;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.machines.*;

public abstract class Entities {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();

		registerTileEntity(ElectricFurnaceTileEntity.class);
		registerTileEntity(PhotovoltaicGeneratorTileEntity.class);
		registerTileEntity(SteamPoweredElectricGeneratorTileEntity.class);
		registerTileEntity(ElectricBatteryArrayTileEntity.class);
		
		
		initDone = true;
	}
	
	private static void registerTileEntity(Class tileEntityClass){
		String name = tileEntityClass.getSimpleName();
		if(name.endsWith("TileEntity")){
			name = name.substring(0, name.lastIndexOf("TileEntity"));
		}
		GameRegistry.registerTileEntity(tileEntityClass,ElectricAdvantage.MODID+"."+toUnderscoreStyle(name));
	}
	
	private static String toUnderscoreStyle(String camelCase){
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toLowerCase(camelCase.charAt(0)));
		for(int i = 1; i < camelCase.length(); i++){
			char c = camelCase.charAt(i);
			if(Character.isUpperCase(c)){
				sb.append('_').append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenderers(){
		//ClientRegistry.bindTileEntitySpecialRenderer(MyTileEntity.class, new cyano.electricadvantage.graphics.MyRenderer());
	}
}
