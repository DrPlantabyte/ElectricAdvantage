package cyano.electricadvantage.graphics;

import org.lwjgl.opengl.GL11;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.machines.ElectricDrillTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) // This is needed for classes that extend client-only classes
public class LaserDrillRenderer extends TileEntitySpecialRenderer{

	
	private final ResourceLocation texture = new ResourceLocation(ElectricAdvantage.MODID+":textures/materials/laser_beam.png");

	private static final float RADIANS_TO_DEGREES = (float)(180 / Math.PI);
	
	
	public LaserDrillRenderer() {
		super();
	}

	
	@Override
	public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float partialTick, int meta) {
		if(te instanceof ElectricDrillTileEntity){
			// partialTick is guaranteed to range from 0 to 1
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)x, (float)y, (float)z);

			render((ElectricDrillTileEntity)te,te.getWorld(),te.getPos(),partialTick);
			
			GlStateManager.popMatrix();
		}
	}
	
	private void render(ElectricDrillTileEntity e, World world, BlockPos pos, float partialTick){
		float laserBlastLength = e.getDrillLength();

		if(laserBlastLength > 0){
			this.bindTexture(texture);
			final Tessellator tessellator = Tessellator.getInstance();

			final WorldRenderer worldRenderer = tessellator.getWorldRenderer();


			final float laserU0 = 0;
			final float laserU1 = 0;
			final float laserV0 = 1.0f;
			final float laserV1 = 1.0f;

			final float radius = 0.25f;
			final float laserRadius = 0.125f;


			float tickRemainder = 1 - partialTick;
			EnumFacing dir = e.getFacing();

			float rotY = 0, rotX = 0;
			switch(dir){
			case NORTH:{
				// do nothing
				break;
			}
			case EAST:{
				rotY = -90;
				break;
			}
			case SOUTH:{
				rotY = 180;
				break;
			}
			case WEST:{
				rotY = 90;
				break;
			}
			case UP:{
				rotX = 90;
				break;
			}
			case DOWN:{
				rotX = -90;
				break;
			}
			default:{
				// do nothing
				break;
			}
			}
			
			GlStateManager.translate(0.5f, 0.5f, 0.5f);
			GlStateManager.rotate(rotY, 0.0f, 1.0f, 0.0f);
			GlStateManager.rotate(rotX, 1.0f, 0.0f, 0.0f);

			worldRenderer.startDrawingQuads();
			worldRenderer.setNormal(0.0f, 1.0f, 0.0f);
			worldRenderer.setBrightness(240);

			final double x1, y1, z1, x2, y2, z2,  x3, y3, z3, x4, y4, z4;
			x1 = 0;
			y1 = -laserRadius;
			z1 = 0;
			x2 = 0;
			y2 = laserRadius;
			z2 = -laserBlastLength;

			x3 = -laserRadius;
			y3 = 0;
			z3 = z1;
			x4 = laserRadius;
			y4 = 0;
			z4 = z2;

			worldRenderer.addVertexWithUV( x1, y1, z1, laserU0, laserV1);
			worldRenderer.addVertexWithUV( x2, y1, z2, laserU1, laserV1);
			worldRenderer.addVertexWithUV( x2, y2, z2, laserU1, laserV0);
			worldRenderer.addVertexWithUV( x1, y2, z1, laserU0, laserV0);

			worldRenderer.addVertexWithUV( x1, y2, z1, laserU0, laserV1);
			worldRenderer.addVertexWithUV( x2, y2, z2, laserU1, laserV1);
			worldRenderer.addVertexWithUV( x2, y1, z2, laserU1, laserV0);
			worldRenderer.addVertexWithUV( x1, y1, z1, laserU0, laserV0);


			worldRenderer.addVertexWithUV( x3, y3, z3, laserU0, laserV1);
			worldRenderer.addVertexWithUV( x3, y4, z4, laserU1, laserV1);
			worldRenderer.addVertexWithUV( x4, y4, z4, laserU1, laserV0);
			worldRenderer.addVertexWithUV( x4, y3, z3, laserU0, laserV0);

			worldRenderer.addVertexWithUV( x4, y3, z3, laserU0, laserV1);
			worldRenderer.addVertexWithUV( x4, y4, z4, laserU1, laserV1);
			worldRenderer.addVertexWithUV( x3, y4, z4, laserU1, laserV0);
			worldRenderer.addVertexWithUV( x3, y3, z3, laserU0, laserV0);

			tessellator.draw();

		}
	}

	
}
