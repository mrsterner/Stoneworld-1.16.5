package stoneworld.storage;

import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.fluid.Fluid;

public class ModFluidStorage implements Storage<Fluid> {
    private final List<ConfigurableFluidStack> stacks;

    public ModFluidStorage(List<ConfigurableFluidStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    /**
     * @param filter    Return false to skip some ConfigurableFluidStacks.
     * @param lockSlots Whether to lock slots or not.
     */
    public long insert(Fluid fluid, long amount, Transaction tx, Predicate<ConfigurableFluidStack> filter, boolean lockSlots) {
        FluidPreconditions.notEmptyNotNegative(fluid, amount);
        for (int iter = 0; iter < 2; ++iter) {
            boolean insertIntoEmptySlots = iter == 1;
            for (ConfigurableFluidStack stack : stacks) {
                if (filter.test(stack) && stack.isValid(fluid)) {
                    if ((stack.getAmount() == 0 && insertIntoEmptySlots) || stack.getFluid() == fluid) {
                        long inserted = Math.min(amount, stack.getRemainingSpace());

                        if (inserted > 0) {
                            tx.enlist(stack);
                            stack.decrement(inserted);

                            if (lockSlots) {
                                stack.enableMachineLock(fluid);
                            }

                            return inserted;
                        }
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public long insert(Fluid fluid, long amount, Transaction tx) {
        return insert(fluid, amount, tx, ConfigurableFluidStack::canPipesInsert, false);
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public long extract(Fluid fluid, long maxAmount, Transaction transaction) {
        FluidPreconditions.notEmptyNotNegative(fluid, maxAmount);
        long amount = 0L;

        for (int i = 0; i < stacks.size() && amount < maxAmount; ++i) {
            amount += this.stacks.get(i).extract(fluid, maxAmount - amount, transaction);
        }

        return amount;
    }

    @Override
    public boolean forEach(Storage.Visitor<Fluid> visitor, Transaction transaction) {
        for (ConfigurableFluidStack stack : stacks) {
            if (stack.getAmount() > 0) {
                if (visitor.accept(stack)) {
                    return true;
                }
            }
        }

        return false;
    }
}