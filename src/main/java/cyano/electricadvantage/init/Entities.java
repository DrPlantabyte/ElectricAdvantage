package cyano.electricadvantage.init;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.entities.LaserTurretEntity;
import cyano.electricadvantage.graphics.LaserTurretRenderer;
import cyano.electricadvantage.machines.ElectricBatteryArrayTileEntity;
import cyano.electricadvantage.machines.ElectricCrusherTileEntity;
import cyano.electricadvantage.machines.ElectricFurnaceTileEntity;
import cyano.electricadvantage.machines.PhotovoltaicGeneratorTileEntity;
import cyano.electricadvantage.machines.SteamPoweredElectricGeneratorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Entities {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();

		registerTileEntity(ElectricFurnaceTileEntity.class);
		registerTileEntity(ElectricCrusherTileEntity.class);
		
		registerTileEntity(PhotovoltaicGeneratorTileEntity.class);
		registerTileEntity(SteamPoweredElectricGeneratorTileEntity.class);
		registerTileEntity(ElectricBatteryArrayTileEntity.class);
		
		registerEntity(LaserTurretEntity.class);
		
		initDone = true;
	}
	
	

	private static void registerTileEntity(Class tileEntityClass){
		String name = tileEntityClass.getSimpleName();
		if(name.endsWith("TileEntity")){
			name = name.substring(0, name.lastIndexOf("TileEntity"));
		}
		GameRegistry.registerTileEntity(tileEntityClass,ElectricAdvantage.MODID+"."+toUnderscoreStyle(name));
	}

	private static int entityIndex = 0;
	private static void registerEntity(Class entityClass) {
		String name=ElectricAdvantage.MODID+"."+entityClass.getSimpleName();
		if(name.endsWith("Entity")){
			name = name.substring(0, name.lastIndexOf("Entity"));
		} else if(name.startsWith("Entity")){
			name = name.substring("Entity".length(),name.length());
		}
		EntityRegistry.registerGlobalEntityID(entityClass,name,EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(entityClass, name, entityIndex++, ElectricAdvantage.INSTANCE, 64, 1, true);
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
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		
		RenderingRegistry.registerEntityRenderingHandler(LaserTurretEntity.class,new LaserTurretRenderer(rm));
		//ClientRegistry.bindTileEntitySpecialRenderer(MyTileEntity.class, new cyano.electricadvantage.graphics.MyRenderer());
	}
}
