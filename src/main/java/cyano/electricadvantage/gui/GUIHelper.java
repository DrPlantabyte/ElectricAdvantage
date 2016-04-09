package cyano.electricadvantage.gui;

import cyano.poweradvantage.api.simple.SimpleMachineGUI.GUIContainer;
import cyano.poweradvantage.gui.FluidTankGUI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class GUIHelper {
	private static final int INDICATOR_LED_X = 182;
	private static final int INDICATOR_LED_Y = 2;
	private static final int INDICATOR_LED_W = 7;
	private static final int INDICATOR_LED_H = 7;
	

	private static final int LED_BAR_X = 180;
	private static final int LED_BAR_Y = 27;
	private static final int LED_BAR_H = 9;
	private static final int LED_BAR_W8 = 4;
	

	private static final Map<ResourceLocation,ResourceLocation> realTextureLocationCache = new HashMap<>();
	
	public static void drawIndicatorLight(boolean on,GUIContainer gc, int xOffset, int yOffset){
		if(on){
			gc.drawTexturedModalRect(8+xOffset, 8+yOffset, INDICATOR_LED_X, INDICATOR_LED_Y, INDICATOR_LED_W, INDICATOR_LED_H);
		}
	}
	
	public static void drawProgressBar(int x, int y, float progress, GUIContainer gc){
		int n = (int)(8 * (progress + 0.0625f) );
		if(n <= 0) return;
		int w = 1 + (n * LED_BAR_W8);
		gc.drawTexturedModalRect(x, y, LED_BAR_X, LED_BAR_Y, w, LED_BAR_H);
	}
	

	public static void drawFluidBar(FluidStack fs, float barHeight, int xPos, int yPos, 
			GUIContainer guiContainer, int x, int y, float z,
			ResourceLocation displayImage){

		FluidTankGUI.drawFluidBar(fs,barHeight,xPos,yPos,displayImage,guiContainer,x,y,z);
	}
}
