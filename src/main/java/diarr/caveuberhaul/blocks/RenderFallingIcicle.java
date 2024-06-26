package diarr.caveuberhaul.blocks;


import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import org.lwjgl.opengl.GL11;

public class RenderFallingIcicle extends EntityRenderer<EntityFallingIcicle> {
    public RenderFallingIcicle() {
        this.shadowSize = 0.5F;
    }
    public void doRender(Tessellator tessellator, EntityFallingIcicle entity, double d, double d1, double d2, float f, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        Block block = Block.blocksList[entity.blockID];
        BlockModel<?> blockModel = BlockModelDispatcher.getInstance().getDispatch(block);
        IconCoordinate coordinate = blockModel.getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
        coordinate.parentAtlas.bindTexture();
        double minU = coordinate.getIconUMin();
        double maxU = coordinate.getIconUMax();
        double minV = coordinate.getIconVMin();
        double maxV = coordinate.getIconVMax();
        double d7 = -0.5;
        double d8 = 0.5;
        double d9 = -0.5;
        double d10 = 0.5;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(d7, + 0.5, d9, minU, minV);
        tessellator.addVertexWithUV(d7, - 0.5, d9, minU, maxV);
        tessellator.addVertexWithUV(d8, - 0.5, d10, maxU, maxV);
        tessellator.addVertexWithUV(d8, + 0.5, d10, maxU, minV);
        tessellator.addVertexWithUV(d8, + 0.5, d10, minU, minV);
        tessellator.addVertexWithUV(d8, - 0.5, d10, minU, maxV);
        tessellator.addVertexWithUV(d7, - 0.5, d9, maxU, maxV);
        tessellator.addVertexWithUV(d7, + 0.5, d9, maxU, minV);
        tessellator.addVertexWithUV(d7, + 0.5, d10, minU, minV);
        tessellator.addVertexWithUV(d7, - 0.5, d10, minU, maxV);
        tessellator.addVertexWithUV(d8, - 0.5, d9, maxU, maxV);
        tessellator.addVertexWithUV(d8, + 0.5, d9, maxU, minV);
        tessellator.addVertexWithUV(d8, + 0.5, d9, minU, minV);
        tessellator.addVertexWithUV(d8, - 0.5, d9, minU, maxV);
        tessellator.addVertexWithUV(d7, - 0.5, d10, maxU, maxV);
        tessellator.addVertexWithUV(d7, + 0.5, d10, maxU, minV);
        tessellator.draw();
        GL11.glPopMatrix();
    }
}
