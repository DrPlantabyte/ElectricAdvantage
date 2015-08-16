package cyano.electricadvantage.graphics;

import cyano.electricadvantage.ElectricAdvantage;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LaserTurretRenderer  extends RenderLiving{

	private static final ResourceLocation texture = new ResourceLocation(ElectricAdvantage.MODID+":textures/entity/laser_turret.png");
	
	public LaserTurretRenderer(RenderManager renderManager) {
		super(renderManager,new LaserTurretModel(),0.5f);
		net.minecraft.client.renderer.entity.RenderCreeper h;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
	

}
