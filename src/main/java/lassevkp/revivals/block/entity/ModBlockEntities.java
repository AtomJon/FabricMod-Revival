package lassevkp.revivals.block.entity;

import lassevkp.revivals.Revivals;
import lassevkp.revivals.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<RitualTableBlockEntity> RITUAL_TABLE;

    public static void registerBlockEntities() {
        RITUAL_TABLE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Revivals.MOD_ID, "ritual_table"),
                FabricBlockEntityTypeBuilder.create(RitualTableBlockEntity::new,
                        ModBlocks.RITUAL_TABLE).build(null));
    }
}
