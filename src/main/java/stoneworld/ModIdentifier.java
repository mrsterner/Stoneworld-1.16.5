package stoneworld;

import net.minecraft.util.Identifier;

/**
 * An Identifier with the MI namespace.
 */
public class ModIdentifier extends Identifier {
    public ModIdentifier(String path) {
        super(Stoneworld.MOD_ID, path);
    }
}