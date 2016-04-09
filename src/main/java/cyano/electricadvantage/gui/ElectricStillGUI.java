package cyano.electricadvantage.gui;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.machines.ElectricStillTileEntity;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class ElectricStillGUI extends SimpleMachineGUI{

	public ElectricStillGUI() {
		super(new ResourceLocation(ElectricAdvantage.MODID+":textures/gui/container/electric_still_gui.png"), 
				new Integer2D[0]);
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
		if(srcEntity instanceof ElectricStillTileEntity){
			ElectricStillTileEntity te = (ElectricStillTileEntity)srcEntity;
			boolean on = te.isPowered();
			GUIHelper.drawIndicatorLight(on, guiContainer,x,y);
			
			FluidStack inputFluid = te.getInputTank().getFluid();
			float inputAmount = (float)te.getInputTank().getFluidAmount() / (float)te.getInputTank().getCapacity();
			
			FluidStack outputFluid = te.getOutputTank().getFluid();
			float outputAmount = (float)te.getOutputTank().getFluidAmount() / (float)te.getOutputTank().getCapacity();

			GUIHelper.drawFluidBar(inputFluid, inputAmount, 27, 42, guiContainer, x, y, z, super.guiDisplayImage);
			GUIHelper.drawFluidBar(outputFluid, outputAmount, 133, 42, guiContainer, x, y, z, super.guiDisplayImage);
		}
	}
}
