package stoneworld;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JVariant;
import net.devtech.arrp.json.loot.JCondition;
import net.devtech.arrp.json.loot.JEntry;
import net.devtech.arrp.json.loot.JLootTable;
import net.devtech.arrp.json.loot.JPool;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import stoneworld.blocks.tank.ModTanks;
import stoneworld.fluid.SaltWater;
import stoneworld.fluid.SulfuricAcid;
import stoneworld.registry.FluidRegistry;
import stoneworld.registry.ItemRegistry;

import java.util.Map;

import static stoneworld.registry.FoodRegistry.EDIBLE_1;
import static stoneworld.registry.FoodRegistry.POISONOUS_1;

public class Stoneworld implements ModInitializer {


    public static final String MOD_ID = "stoneworld";
    public static final String MOD_NAME = "Stoneworld";
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("stoneworld:general");
    public static final Logger LOGGER = LogManager.getLogger("Stone World");

    public static final int FLAG_BLOCK_LOOT = 1;
    public static final int FLAG_BLOCK_MODEL = 1 << 1;
    public static final int FLAG_BLOCK_ITEM_MODEL = 1 << 2;

    public static final ItemGroup STONEWORLD_ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier(Stoneworld.MOD_ID, "stoneworld"))
            .icon(() -> new ItemStack(Stoneworld.AMANITA_VIROSA))
            .build();

    public static FlowableFluid STILL_SALTWATER;
    public static FlowableFluid FLOWING_SALTWATER;
    public static FlowableFluid STILL_SULFURIC_ACID;
    public static FlowableFluid FLOWING_SULFURIC_ACID;

    public static Item SULFURIC_ACID_BUCKET;
    public static Item SALTWATER_BUCKET;
    public static Item AMANITA_VIROSA = new Item(createGroup().food(POISONOUS_1));
    public static Item AMANITA_MUSCARIA = new Item(createGroup().food(POISONOUS_1));
    public static Item BUNA_SHIMEJI = new Item(createGroup().food(EDIBLE_1));

    public static Block SULFURIC_ACID;
    public static Block SALTWATER;



    @Override
    public void onInitialize() {
        setupItems();
        ModTanks.setup();
        FluidRegistry.setupFluids();
        RRPCallback.EVENT.register(a -> {
            a.add(RESOURCE_PACK);
        });

        //FLUIDS
        STILL_SALTWATER = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "saltwater"), new SaltWater.Still());
        FLOWING_SALTWATER = Registry.register(Registry.FLUID, new Identifier(MOD_ID,"flowing_saltwater"),  new SaltWater.Flowing());
        SALTWATER_BUCKET = Registry.register(Registry.ITEM, new Identifier(MOD_ID,"saltwater_bucket"), new BucketItem(STILL_SALTWATER, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        SALTWATER = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "saltwater"), new FluidBlock(STILL_SALTWATER, FabricBlockSettings.copy(Blocks.WATER)){});

        STILL_SULFURIC_ACID = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "sulfuric_acid"), new SulfuricAcid.Still());
        FLOWING_SULFURIC_ACID = Registry.register(Registry.FLUID, new Identifier(MOD_ID,"flowing_sulfuric_acid"),  new SulfuricAcid.Flowing());
        SULFURIC_ACID_BUCKET = Registry.register(Registry.ITEM, new Identifier(MOD_ID,"sulfuric_acid_bucket"), new BucketItem(STILL_SULFURIC_ACID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        SULFURIC_ACID = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "sulfuric_acid"), new FluidBlock(STILL_SULFURIC_ACID, FabricBlockSettings.copy(Blocks.WATER)){});
        //GENERATION
        ConfiguredFeature<?, ?> SULFURIC_LAKE_CONFIGURED = Feature.LAKE
                .configure(new SingleStateFeatureConfig(SULFURIC_ACID.getDefaultState()))
                .decorate(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(1)));

        RegistryKey<ConfiguredFeature<?, ?>> sulfurLake = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN,
                new Identifier("stoneworld", "sulfur_lake"));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, sulfurLake.getValue(), SULFURIC_LAKE_CONFIGURED);
        BiomeModifications.addFeature(ctx -> ctx.getBiome().getCategory() == Biome.Category.SWAMP, GenerationStep.Feature.LAKES, sulfurLake);
    }

    //REGISTER BLOCKS

    public static void registerBlock(Block block, Item item, String id, int flag) {
        Identifier identifier = new ModIdentifier(id);
        Registry.register(Registry.BLOCK, identifier, block);
        if (!Registry.ITEM.containsId(identifier)) {
            Registry.register(Registry.ITEM, identifier, item);
        }
        if ((flag & FLAG_BLOCK_LOOT) != 0) {
            registerBlockLoot(id);
        }
        // TODO: client side?
        RESOURCE_PACK.addBlockState(JState.state().add(new JVariant().put("", new JBlockModel(MOD_ID + ":block/" + id))), identifier);

        if ((flag & FLAG_BLOCK_MODEL) != 0)
            RESOURCE_PACK.addModel(JModel.model().parent("block/cube_all").textures(new JTextures().var("all", MOD_ID + ":blocks/" + id)),
                    new ModIdentifier("block/" + id));

        if ((flag & FLAG_BLOCK_ITEM_MODEL) != 0)
            RESOURCE_PACK.addModel(JModel.model().parent(MOD_ID + ":block/" + id), new ModIdentifier("item/" + id));

    }
    public static void registerBlock(Block block, Item item, String id) {
        registerBlock(block, item, id, FLAG_BLOCK_LOOT | FLAG_BLOCK_ITEM_MODEL | FLAG_BLOCK_MODEL);
    }
    private static void registerBlockLoot(String id) {
        RESOURCE_PACK.addLootTable(new ModIdentifier("blocks/" + id), JLootTable.loot("minecraft:block").pool(new JPool().rolls(1)
                .entry(new JEntry().type("minecraft:item").name(MOD_ID + ":" + id)).condition(new JCondition("minecraft:survives_explosion"))));
    }

    //SETUP ITEMS
    private void setupItems() {
        for (Map.Entry<String, Item> entry : ItemRegistry.items.entrySet()) {
            registerItem(entry.getValue(), entry.getKey());
        }
        registerItem(AMANITA_VIROSA, "amanita_virosa");
        registerItem(AMANITA_MUSCARIA, "amanita_muscaria");
        registerItem(BUNA_SHIMEJI, "buna_shimeji");
    }



    //REGISTER ITEMS
    public static void registerItem(Item item, String id, boolean handheld) {
        registerItem(item, new ModIdentifier(id), handheld);
    }
    public static void registerItem(Item item, Identifier id, boolean handheld) {
        Registry.register(Registry.ITEM, id, item);
        RESOURCE_PACK.addModel(
                JModel.model().parent(handheld ? "minecraft:item/handheld" : "minecraft:item/generated")
                        .textures(new JTextures().layer0(id.getNamespace() + ":items/" + id.getPath())),
                new Identifier(id.getNamespace() + ":item/" + id.getPath()));
    }
    public static void registerItem(Item item, String id) {
        registerItem(item, id, false);
    }
    //ITEMGROUP
    public static Item.Settings createGroup() {
        return new Item.Settings().group(STONEWORLD_ITEM_GROUP);
    }
}