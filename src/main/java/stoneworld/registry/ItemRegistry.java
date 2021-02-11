package stoneworld.registry;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import static stoneworld.Stoneworld.*;

public class ItemRegistry {

    public static SortedMap<String, Item> items = new TreeMap<>();

    public static Item of(String id) {
        return of(Item::new, id, 64);
    }

    public static Item of(Function<Item.Settings, Item> ctor, String id, int maxCount) {
        Item item = ctor.apply(new Item.Settings().maxCount(maxCount).group(STONEWORLD_ITEM_GROUP));
        if (items.put(id, item) != null) {
            throw new IllegalArgumentException("Item id already taken : " + id);
        }
        return item;
    }

    //FOOD
    /*
    public static Item AMANITA_VIROSA = register("amanita_virosa", new Item(createGroup().food(POISONOUS_1)));
    public static Item AMINITA_MUSCARIA = register("aminita_muscaria", new Item(createGroup().food(POISONOUS_1)));

    public static Item BUNA_SHIMEJI = register("buna_shimeji",new Item(createGroup().food(EDIBLE_1)));

    //CHEMICALS
    public static Item SODIUM_CARBONATE = register("sodium_carbonate",new Item(createGroup()));
    public static Item CALCIUM_CARBONATE = register("calcium_carbonate",new Item(createGroup()));
    public static Item SULFUR = register("sulfur",new Item(createGroup()));
    public static Item POTASSIUM_NITRATE = register("potassium_nitrate",new Item(createGroup()));
    public static Item PHOSPHORUS = register("phosphorus",new Item(createGroup()));
    public static Item SODIUMBISULFATE = register("sodiumbisulfate",new Item(createGroup()));
    public static Item SALT = register("salt",new Item(createGroup()));
    public static Item SULFANILAMIDE = register("sulfanilamide",new Item(createGroup()));
    public static Item POTASSIUM_CARBONATE = register("potassium_carbonate",new Item(createGroup()));
    public static Item QUARTZ_SAND = register("quartz_sand",new Item(createGroup()));
    public static Item ROCHELLE_SALT = register("rochelle_salt",new Item(createGroup()));
    public static Item BAKELITE_PLASTIC = register("quartz_sand",new Item(createGroup()));
*/  //FOOD
    //public static final Item  AMANITA_VIROSA = of("amanita_virosa");
    //public static final Item  AMINITA_MUSCARIA = of("aminita_muscaria");
    //public static final Item  BUNA_SHIMEJI = of("buna_shimeji");
    //CHEMICALS
    public static final Item  SODIUM_CARBONATE = of("sodium_carbonate");
    public static final Item  CALCIUM_CARBONATE = of("calcium_carbonate");
    public static final Item  SULFUR = of("sulfur");
    public static final Item  POTASSIUM_NITRATE = of("potassium_nitrate");
    public static final Item  PHOSPHORUS = of("phosphorus");
    public static final Item  SODIUMBISULFATE = of("sodiumbisulfate");
    public static final Item  SALT = of("salt");
    public static final Item  SULFANILAMIDE = of("sulfanilamide");
    public static final Item  POTASSIUM_CARBONATE = of("potassium_carbonate");
    public static final Item  QUARTZ_SAND = of("quartz_sand");
    public static final Item  ROCHELLE_SALT = of("rochelle_salt");
    public static final Item  BAKELITE_PLASTIC = of("bakelite_plastic");
    //MATERIALS
    public static final Item  ACTIVATED_CHARCOAL = of("activated_charcoal");
    public static final Item  COPPER_TUBE = of("copper_tube");
    public static final Item  COPPER_WIRE = of("copper_wire");
    public static final Item  GOLD_WIRE = of("gold_wire");
    public static final Item  IRON_STRAW = of("iron_straw");
    public static final Item  LEAD_ACID_BATTERY = of("lead_acid_battery");
    public static final Item  LIGHT_BULB = of("light_bulb");
    public static final Item  MORTAR = of("mortar");
    public static final Item  OBSIDIAN_FOAM = of("obsidian_foam");
    public static final Item  STRONG_MAGNET = of("strong_magnet");
    public static final Item  TUNGSTEN_FILAMENT = of("tungsten_filament");
    public static final Item  TUNGSTEN_PASTE = of("tungsten_paste");
    public static final Item  VACUUM_TUBE = of("vacuum_tube");
    public static final Item  ZINC_MANGANESE_BATTERY = of("zinc_manganese_battery");
    //ORES
    public static final Item  GOLD_CLUSTER = of("gold_cluster");
    public static final Item  MAGNETITE_CLUSTER = of("magnetite_cluster");
    public static final Item  GALENA_CLUSTER = of("galena_cluster");
    public static final Item  MALACHITE_CLUSTER = of("malachite_cluster");
    public static final Item  CHALCANTHITE_CLUSTER = of("chalcanthite_cluster");
    public static final Item  CINNABAR_CLUSTER = of("cinnabar_cluster");
    public static final Item  SILVER_CLUSTER = of("silver_cluster");
    public static final Item  SCHEELITE_CLUSTER = of("scheelite_cluster");
    public static final Item  SPHALERITE_CLUSTER = of("sphalerite_cluster");
    public static final Item  PYROLUSITE_CLUSTER = of("pyrolusite_cluster");
}
