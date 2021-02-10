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

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidApi;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Movement;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Participant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionResult;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import stoneworld.inventory.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import stoneworld.api.FastBlockEntity;
import stoneworld.util.NbtHelper;


public class TankBlockEntity extends FastBlockEntity
        implements Storage<Fluid>, StorageView<Fluid>, Participant<FluidState>, BlockEntityClientSerializable {
    Fluid fluid = Fluids.EMPTY;
    long amount;
    long capacity;
    private int version = 0;

    public TankBlockEntity() {
        super(ModTanks.BLOCK_ENTITY_TYPE);
    }

    public boolean isEmpty() {
        return amount == 0;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fluid = NbtHelper.getFluidCompatible(tag, "fluid");
        if (tag.contains("amount")) {
            amount = tag.getInt("amount") * 81;
            capacity = tag.getInt("capacity") * 81;
        } else {
            amount = tag.getLong("amt");
            capacity = tag.getLong("cap");
        }
        if (fluid == Fluids.EMPTY) {
            amount = 0;
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        NbtHelper.putFluid(tag, "fluid", fluid);
        tag.putLong("amt", amount);
        tag.putLong("cap", capacity);
        return tag;
    }

    public void onChanged() {
        version++;
        markDirty();
        if (!world.isClient)
            sync();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        toClientTag(tag);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        fromClientTag(tag);
        super.fromTag(state, tag);
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public boolean onPlayerUse(PlayerEntity player) {
        Storage<Fluid> handIo = FluidApi.ITEM.get(ItemKey.of(player.getMainHandStack()), ContainerItemContext.ofPlayerHand(player, Hand.MAIN_HAND));
        if (handIo != null) {
            // move from hand into this tank
            if (Movement.move(handIo, this, f -> true, Integer.MAX_VALUE) > 0)
                return true;
            // move from this tank into hand
            if (Movement.move(this, handIo, f -> true, Integer.MAX_VALUE) > 0)
                return true;
        }
        return false;
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public long insert(Fluid fluid, long maxAmount, Transaction transaction) {
        FluidPreconditions.notEmptyNotNegative(fluid, maxAmount);
        if (this.fluid == Fluids.EMPTY || TankBlockEntity.this.fluid == fluid) {
            long inserted = Math.min(maxAmount, capacity - amount);
            if (inserted > 0) {
                transaction.enlist(TankBlockEntity.this);
                amount += inserted;
                TankBlockEntity.this.fluid = fluid;
            }
            return inserted;
        }
        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public long extract(Fluid fluid, long maxAmount, Transaction transaction) {
        FluidPreconditions.notEmptyNotNegative(fluid, maxAmount);
        if (fluid == TankBlockEntity.this.fluid) {
            long extracted = Math.min(maxAmount, amount);
            if (extracted > 0) {
                transaction.enlist(TankBlockEntity.this);
                amount -= extracted;
                if (amount == 0) {
                    TankBlockEntity.this.fluid = Fluids.EMPTY;
                }
            }
            return extracted;
        }
        return 0;
    }

    @Override
    public Fluid resource() {
        return fluid;
    }

    @Override
    public long amount() {
        return amount;
    }

    @Override
    public boolean forEach(Visitor<Fluid> visitor, Transaction transaction) {
        if (fluid != Fluids.EMPTY) {
            return visitor.accept(this);
        }
        return false;
    }

    @Override
    public int getVersion() {
        return version;
    }


    @Override
    public FluidState onEnlist() {
        return new FluidState(fluid, amount);
    }

    @Override
    public void onClose(FluidState state, TransactionResult result) {
        if (result.wasAborted()) {
            this.fluid = state.fluid;
            this.amount = state.amount;
        }
    }

    @Override
    public void onFinalCommit() {
        onChanged();
    }
}
