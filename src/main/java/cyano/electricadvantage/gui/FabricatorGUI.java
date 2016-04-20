package cyano.electricadvantage.gui;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.machines.ElectricMachineTileEntity;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;
import net.minecraft.util.ResourceLocation;

public class FabricatorGUI extends SimpleMachineGUI{

	public FabricatorGUI() {
		super(new ResourceLocation(ElectricAdvantage.MODID+":textures/gui/container/electric_fabricator_gui.png"), 
				Integer2D.fromCoordinates( 
						12,34, 33,34, 54,34, 
						12,55, 33,55, 54,55, 
						12,76, 33,76, 54,76, 
						12,97, 33,97, 54,97, 
						
						127,55, 148,55,
						
						91,34
						));
	}
	
	/**
	 * Override this method to draw on the GUI window.
	 * <br><br>
	 * This method is invoked when drawing the GUI so that you can draw 
	 * animations and other foreground decorations to the GUI.
	 * @param srcEntity This is the TileEntity (or potentially a LivingEntity) 
	 * for whom we are drawing this interface
	 * @param guiContainer This is the instance of GUIContainer that is drawing 
	 * the GUI. You need to use it to draw on the screen. For example:<br>
	   <pre>
guiContainer.mc.renderEngine.bindTexture(arrowTexture);
guiContainer.drawTexturedModalRect(x+79, y+35, 0, 0, arrowLength, 17); // x, y, textureOffsetX, textureOffsetY, width, height)
	   </pre>
	 * @param x This is the x coordinate (in pixels) from the top-left corner of 
	 * the GUI
	 * @param y This is the y coordinate (in pixels) from the top-left corner of 
	 * the GUI
	 * @param z This is the z coordinate (no units) into the depth of the screen
	 */
	@Override
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y, float  z){
		if(srcEntity instanceof ElectricMachineTileEntity){
			ElectricMachineTileEntity te = (ElectricMachineTileEntity)srcEntity;
			boolean power = te.isPowered();
			GUIHelper.drawIndicatorLight(power, guiContainer,x,y);
			float[] progressBars = te.getProgress();
			GUIHelper.drawProgressBar(x+83, y+59, progressBars[0], guiContainer);
		}
	}

}
