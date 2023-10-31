package diarr.caveuberhaul.gen.cavebiomes;

import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.HashMap;

public class CaveBiomeChunkMap {
    public static final HashMap<ChunkPosition, CaveBiomeProvider> map;
    static{
        map = new HashMap<>();
    }
}
