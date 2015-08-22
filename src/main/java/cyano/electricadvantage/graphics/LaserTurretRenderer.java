package cyano.electricadvantage.graphics;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.entities.LaserTurretTileEntity;
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
public class LaserTurretRenderer extends TileEntitySpecialRenderer{

	
	private final ResourceLocation texture = new ResourceLocation(ElectricAdvantage.MODID+":textures/entity/laser_turret.png");

	private static final float RADIANS_TO_DEGREES = (float)(180 / Math.PI);
	
	public LaserTurretRenderer() {
		super();
	}

	
	@Override
	public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float partialTick, int meta) {
		if(te instanceof LaserTurretTileEntity){
			// partialTick is guaranteed to range from 0 to 1
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)x, (float)y, (float)z);

			render((LaserTurretTileEntity)te,te.getWorld(),te.getPos(),partialTick);
			
			GlStateManager.popMatrix();
		}
	}
	
	private void render(LaserTurretTileEntity e, World world, BlockPos pos, float partialTick){
		this.bindTexture(texture);
		final Tessellator instance = Tessellator.getInstance();
		
		//This will make your block brightness dependent from surroundings lighting.
		instance.getWorldRenderer().setBrightness(world.getCombinedLight(pos, 0));
		instance.getWorldRenderer().setColorOpaque_F(1f, 1f, 1f);
		
		final WorldRenderer worldRenderer = instance.getWorldRenderer();
		final float sideU0 = 0;
		final float sideU1 = 0.5f;
		final float sideV0 = 0;
		final float sideV1 = 0.5f;
		final float endU0 = 0.5f;
		final float endU1 = 1;
		final float endV0 = 0;
		final float endV1 = 0.5f;
		final float radius = 0.25f;

		float tickRemainder = 1 - partialTick;
		float yaw = -1*(tickRemainder * e.rotOldYaw + partialTick * e.rotYaw) - 90;
		float pitch = tickRemainder * e.rotOldPitch + partialTick * e.rotPitch;

		GlStateManager.translate(0.5f, 1.0f, 0.5f);
		GlStateManager.rotate(yaw, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(pitch, 1.0f, 0.0f, 0.0f);

		worldRenderer.startDrawingQuads();
		worldRenderer.setNormal(0.0f, 1.0f, 0.0f);
		worldRenderer.addVertexWithUV( radius, radius, -radius, endU0, endV0);
		worldRenderer.addVertexWithUV( radius,-radius, -radius, endU0, endV1);
		worldRenderer.addVertexWithUV(-radius,-radius, -radius, endU1, endV1);
		worldRenderer.addVertexWithUV(-radius, radius, -radius, endU1, endV0);

		worldRenderer.addVertexWithUV(-radius, radius,  radius, sideU0, sideV0);
		worldRenderer.addVertexWithUV(-radius,-radius,  radius, sideU0, sideV1);
		worldRenderer.addVertexWithUV( radius,-radius,  radius, sideU1, sideV1);
		worldRenderer.addVertexWithUV( radius, radius,  radius, sideU1, sideV0);

		worldRenderer.addVertexWithUV(-radius, radius, -radius, sideU0, sideV0);
		worldRenderer.addVertexWithUV(-radius,-radius, -radius, sideU0, sideV1);
		worldRenderer.addVertexWithUV(-radius,-radius,  radius, sideU1, sideV1);
		worldRenderer.addVertexWithUV(-radius, radius,  radius, sideU1, sideV0);

		worldRenderer.addVertexWithUV( radius, radius,  radius, sideU0, sideV0);
		worldRenderer.addVertexWithUV( radius,-radius,  radius, sideU0, sideV1);
		worldRenderer.addVertexWithUV( radius,-radius, -radius, sideU1, sideV1);
		worldRenderer.addVertexWithUV( radius, radius, -radius, sideU1, sideV0);

		worldRenderer.addVertexWithUV(-radius, radius, -radius, sideU0, sideV0);
		worldRenderer.addVertexWithUV(-radius, radius,  radius, sideU0, sideV1);
		worldRenderer.addVertexWithUV( radius, radius,  radius, sideU1, sideV1);
		worldRenderer.addVertexWithUV( radius, radius, -radius, sideU1, sideV0);

		worldRenderer.addVertexWithUV(-radius,-radius,  radius, sideU0, sideV0);
		worldRenderer.addVertexWithUV(-radius,-radius, -radius, sideU0, sideV1);
		worldRenderer.addVertexWithUV( radius,-radius, -radius, sideU1, sideV1);
		worldRenderer.addVertexWithUV( radius,-radius,  radius, sideU1, sideV0);
		
		instance.draw();
	}

	
}
