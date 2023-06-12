package diarr.caveuberhaul;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class ItemFlowstoneItem extends Item {
    public ItemFlowstoneItem(int i) {
        super(i);
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit == null) {
            int x = mc.objectMouseOver.blockX;
            int y = mc.objectMouseOver.blockY;
            int z = mc.objectMouseOver.blockZ;

            //Block block = Block.blocksList[mc.theWorld.getBlockId(x, y, z)];
            mc.theWorld.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtite.blockID);

        }

        entityplayer.swingItem();
        return itemstack;
    }
}
