package stoneworld.registry;

import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.minecraft.util.registry.Registry;
import stoneworld.ModIdentifier;
import stoneworld.fluid.CraftingFluid;

import static stoneworld.Stoneworld.MOD_ID;
import static stoneworld.Stoneworld.RESOURCE_PACK;

public class FluidRegistry {
    public static final CraftingFluid ETHANOL = new CraftingFluid("ethanol",0xff603405);
    public static final CraftingFluid[] FLUIDS = new CraftingFluid[] {
            ETHANOL
    };
    public static void setupFluids() {

    }

    static {
        for(CraftingFluid fluid : FLUIDS) {
            registerFluid(fluid);
        }
    }

    private static void registerFluid(CraftingFluid fluid) {
        String id = fluid.name;
        Registry.register(Registry.FLUID, new ModIdentifier(id), fluid);
        Registry.register(Registry.ITEM, new ModIdentifier("bucket_" + id), fluid.getBucketItem());
        RESOURCE_PACK.addModel(JModel.model().parent("minecraft:item/generated").textures(new JTextures().layer0(MOD_ID + ":items/bucket/" + id)), new ModIdentifier("item/bucket_" + id));
    }
}
