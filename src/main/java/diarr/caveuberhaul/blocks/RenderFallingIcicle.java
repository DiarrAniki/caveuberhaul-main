package diarr.caveuberhaul.blocks;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TextureFX;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.Side;
import org.lwjgl.opengl.GL11;

public class RenderFallingIcicle extends EntityRenderer<Entity> {
    public RenderFallingIcicle() {
        this.shadowSize = 0.5F;
    }

    public void doRenderFallingStalactite(EntityFallingIcicle entityFallingIcicle, double d, double d1, double d2, float f, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        Block block = Block.blocksList[entityFallingIcicle.blockID];
        this.loadTexture("/terrain.png");
        int j = block.getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
        Tessellator tessellator = Tessellator.instance;
        int k = j % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int l = j / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = (float)k / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d4 = ((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d5 = (float)l / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d6 = ((float)l + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d7 = -0.5;
        double d8 = 0.5;
        double d9 = -0.5;
        double d10 = 0.5;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(d7, + 0.5, d9, d3, d5);
        tessellator.addVertexWithUV(d7, - 0.5, d9, d3, d6);
        tessellator.addVertexWithUV(d8, - 0.5, d10, d4, d6);
        tessellator.addVertexWithUV(d8, + 0.5, d10, d4, d5);
        tessellator.addVertexWithUV(d8, + 0.5, d10, d3, d5);
        tessellator.addVertexWithUV(d8, - 0.5, d10, d3, d6);
        tessellator.addVertexWithUV(d7, - 0.5, d9, d4, d6);
        tessellator.addVertexWithUV(d7, + 0.5, d9, d4, d5);
        tessellator.addVertexWithUV(d7, + 0.5, d10, d3, d5);
        tessellator.addVertexWithUV(d7, - 0.5, d10, d3, d6);
        tessellator.addVertexWithUV(d8, - 0.5, d9, d4, d6);
        tessellator.addVertexWithUV(d8, + 0.5, d9, d4, d5);
        tessellator.addVertexWithUV(d8, + 0.5, d9, d3, d5);
        tessellator.addVertexWithUV(d8, - 0.5, d9, d3, d6);
        tessellator.addVertexWithUV(d7, - 0.5, d10, d4, d6);
        tessellator.addVertexWithUV(d7, + 0.5, d10, d4, d5);
        tessellator.draw();
        GL11.glPopMatrix();
    }


    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        this.doRenderFallingStalactite((EntityFallingIcicle) entity, d, d1, d2, f, f1);
    }
}
