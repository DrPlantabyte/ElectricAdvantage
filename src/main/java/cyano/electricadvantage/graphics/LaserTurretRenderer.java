package cyano.electricadvantage.graphics;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.machines.LaserTurretTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
		final Tessellator tessellator = Tessellator.getInstance();
		
		
		final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		final float sideU0 = 0;
		final float sideU1 = 0.5f;
		final float sideV0 = 0;
		final float sideV1 = 0.5f;
		final float endU0 = 0.5f;
		final float endU1 = 1;
		final float endV0;
		final float endV1;
		if(e.isActive()){
			endV0 = 0;
			endV1 = 0.5f;
		}else{
			endV0 = 0.5f;
			endV1 = 1.0f;
		}

		final float laserU0 = 0;
		final float laserU1 = 0.5f;
		final float laserV0 = 0.5f;
		final float laserV1 = 1.0f;

		final float radius = 0.25f;
		final float laserRadius = 0.125f;
		

		float tickRemainder = 1 - partialTick;
		float yaw = -1*(tickRemainder * e.rotOldYaw + partialTick * e.rotYaw) - 90;
		float pitch = tickRemainder * e.rotOldPitch + partialTick * e.rotPitch;

		GlStateManager.translate(0.5f, 0.75f, 0.5f);
		GlStateManager.rotate(yaw, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(pitch, 1.0f, 0.0f, 0.0f);

		worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		
		worldRenderer.pos( radius, radius, -radius).tex(endU0, endV0).endVertex();
		worldRenderer.pos( radius,-radius, -radius).tex(endU0, endV1).endVertex();
		worldRenderer.pos(-radius,-radius, -radius).tex(endU1, endV1).endVertex();
		worldRenderer.pos(-radius, radius, -radius).tex(endU1, endV0).endVertex();

		worldRenderer.pos(-radius, radius,  radius).tex(sideU0, sideV0).endVertex();
		worldRenderer.pos(-radius,-radius,  radius).tex(sideU0, sideV1).endVertex();
		worldRenderer.pos( radius,-radius,  radius).tex(sideU1, sideV1).endVertex();
		worldRenderer.pos( radius, radius,  radius).tex(sideU1, sideV0).endVertex();

		worldRenderer.pos(-radius, radius, -radius).tex(sideU0, sideV0).endVertex();
		worldRenderer.pos(-radius,-radius, -radius).tex(sideU0, sideV1).endVertex();
		worldRenderer.pos(-radius,-radius,  radius).tex(sideU1, sideV1).endVertex();
		worldRenderer.pos(-radius, radius,  radius).tex(sideU1, sideV0).endVertex();

		worldRenderer.pos( radius, radius,  radius).tex(sideU0, sideV0).endVertex();
		worldRenderer.pos( radius,-radius,  radius).tex(sideU0, sideV1).endVertex();
		worldRenderer.pos( radius,-radius, -radius).tex(sideU1, sideV1).endVertex();
		worldRenderer.pos( radius, radius, -radius).tex(sideU1, sideV0).endVertex();

		worldRenderer.pos(-radius, radius, -radius).tex(sideU0, sideV0).endVertex();
		worldRenderer.pos(-radius, radius,  radius).tex(sideU0, sideV1).endVertex();
		worldRenderer.pos( radius, radius,  radius).tex(sideU1, sideV1).endVertex();
		worldRenderer.pos( radius, radius, -radius).tex(sideU1, sideV0).endVertex();

		worldRenderer.pos(-radius,-radius,  radius).tex(sideU0, sideV0).endVertex();
		worldRenderer.pos(-radius,-radius, -radius).tex(sideU0, sideV1).endVertex();
		worldRenderer.pos( radius,-radius, -radius).tex(sideU1, sideV1).endVertex();
		worldRenderer.pos( radius,-radius,  radius).tex(sideU1, sideV0).endVertex();

		tessellator.draw();
		if(e.showLaserLine() && e.laserBlastLength > 0){
			final double x1, y1, z1, x2, y2, z2,  x3, y3, z3, x4, y4, z4;
			x1 = 0;
			y1 = -laserRadius;
			z1 = 0;
			x2 = 0;
			y2 = laserRadius;
			z2 = -e.laserBlastLength;
			
			x3 = -laserRadius;
			y3 = 0;
			z3 = z1;
			x4 = laserRadius;
			y4 = 0;
			z4 = z2;

			int lightValue = 15 << 20 | 15 << 4;
			int lmapX = lightValue >> 16 & 0xFFFF;
			int lmapY = lightValue & 0xFFFF;
			worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			
			worldRenderer.pos( x1, y1, z1).tex(laserU0, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x2, y1, z2).tex(laserU1, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x2, y2, z2).tex(laserU1, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x1, y2, z1).tex(laserU0, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			
			worldRenderer.pos( x1, y2, z1).tex(laserU0, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x2, y2, z2).tex(laserU1, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x2, y1, z2).tex(laserU1, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x1, y1, z1).tex(laserU0, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();


			worldRenderer.pos( x3, y3, z3).tex(laserU0, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x3, y4, z4).tex(laserU1, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x4, y4, z4).tex(laserU1, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x4, y3, z3).tex(laserU0, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			
			worldRenderer.pos( x4, y3, z3).tex(laserU0, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x4, y4, z4).tex(laserU1, laserV1).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x3, y4, z4).tex(laserU1, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			worldRenderer.pos( x3, y3, z3).tex(laserU0, laserV0).lightmap(lmapX, lmapY).color(1f, 1f, 1f, 1f).endVertex();
			
			tessellator.draw();
		}
		
	}

	
}
