/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package stoneworld.blocks.tank;



import java.util.Arrays;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidApi;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import stoneworld.ModIdentifier;
import stoneworld.Stoneworld;
import stoneworld.model.block.ModelProvider;

public enum ModTanks {
    BRONZE("bronze", 4),
    STEEL("steel", 8),
    ALUMINUM("aluminum", 16),
    STAINLESS_STEEL("stainless_steel", 32);

    public static BlockEntityType<TankBlockEntity> BLOCK_ENTITY_TYPE;

    public final String type;
    public final TankBlock block;
    public final TankItem item;
    public final int bucketCapacity;

    ModTanks(String type, int bucketCapacity) {
        this.type = type;
        this.block = new TankBlock(FabricBlockSettings.of(Material.METAL).hardness(4.0f));
        this.item = new TankItem(block, new Item.Settings().group(Stoneworld.STONEWORLD_ITEM_GROUP), 81000 * bucketCapacity);
        this.bucketCapacity = bucketCapacity;
    }

    public static void setup() {
        for (ModTanks tank : values()) {
            Stoneworld.registerBlock(tank.block, tank.item, "tank_" + tank.type, 0);
        }
        BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new ModIdentifier("tank"),
                BlockEntityType.Builder.create(TankBlockEntity::new, getBlocks()).build(null));

        // Fluid API
        FluidApi.SIDED.registerForBlockEntities((be, direction) -> be instanceof TankBlockEntity ? (TankBlockEntity) be : null, BLOCK_ENTITY_TYPE);
        for (ModTanks tank : values()) {
            tank.item.registerItemApi();
        }
    }

    public static void setupClient() {
        for (ModTanks tank : values()) {
            UnbakedModel tankModel = new TankModel(tank.type);
            ModelProvider.modelMap.put(new ModIdentifier("block/tank_" + tank.type), tankModel);
            ModelProvider.modelMap.put(new ModIdentifier("item/tank_" + tank.type), tankModel);
        }
        UnbakedModel creativeTankModel = new TankModel("creative");
        ModelProvider.modelMap.put(new ModIdentifier("block/creative_tank"), creativeTankModel);
        ModelProvider.modelMap.put(new ModIdentifier("item/creative_tank"), creativeTankModel);

        BlockEntityRendererRegistry.INSTANCE.register(BLOCK_ENTITY_TYPE, TankRenderer::new);
    }

    private static Block[] getBlocks() {
        return Arrays.stream(values()).map(x -> x.block).toArray(Block[]::new);
    }
}
