package me.pajic.cherryontop.mixin.whetstone;

import me.pajic.cherryontop.Main;
import me.pajic.cherryontop.item.CoTItems;
import me.pajic.cherryontop.util.CoTUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin extends CustomRecipe {

    public RepairItemRecipeMixin(CraftingBookCategory category) {
        super(category);
    }

    @Inject(
            method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void matchesWhetstoneRecipe(CraftingInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (Main.CONFIG.whetstone.enableWhetstone()) {
            List<ItemStack> whetstones = input.items().stream().filter(itemStack -> itemStack.is(CoTItems.WHETSTONE)).toList();
            if (whetstones.size() == 1) {
                List<ItemStack> repairableItems = input.items().stream().filter(itemStack ->
                        itemStack.isDamageableItem() && !itemStack.is(CoTItems.WHETSTONE)).toList();
                if (!repairableItems.isEmpty()) {
                    ItemStack itemToRepair = repairableItems.getFirst();
                    if (
                            itemToRepair.isDamaged() &&
                            itemToRepair.getEnchantments().equals(whetstones.getFirst().getOrDefault(
                                    DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                            )
                    ) {
                        int unitCost = CoTUtil.determineUnitCost(itemToRepair);
                        int damageRepairedPerUnit = itemToRepair.getMaxDamage() / unitCost;
                        int unitsToMaxRepair = itemToRepair.getDamageValue() / damageRepairedPerUnit;
                        List<ItemStack> repairMaterials = input.items().stream().filter(itemStack ->
                                itemToRepair.getItem().isValidRepairItem(itemToRepair, itemStack)).toList();
                        if (!repairMaterials.isEmpty() && repairMaterials.size() <= unitsToMaxRepair + 1) {
                            cir.setReturnValue(true);
                        }
                    }
                }
            }
        }
    }

    @Inject(
            method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assembleWhetstoneRecipe(CraftingInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        if (Main.CONFIG.whetstone.enableWhetstone()) {
            List<ItemStack> whetstones = input.items().stream().filter(itemStack -> itemStack.is(CoTItems.WHETSTONE)).toList();
            if (whetstones.size() == 1) {
                List<ItemStack> repairableItems = input.items().stream().filter(itemStack ->
                        itemStack.isDamageableItem() && !itemStack.is(CoTItems.WHETSTONE)).toList();
                if (!repairableItems.isEmpty()) {
                    ItemStack itemToRepair = repairableItems.getFirst();
                    if (
                            itemToRepair.isDamaged() &&
                            itemToRepair.getEnchantments().equals(whetstones.getFirst().getOrDefault(
                                    DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                            )
                    ) {
                        int unitCost = CoTUtil.determineUnitCost(itemToRepair);
                        int damageRepairedPerUnit = itemToRepair.getMaxDamage() / unitCost;
                        int unitsToMaxRepair = itemToRepair.getDamageValue() / damageRepairedPerUnit;
                        List<ItemStack> repairMaterials = input.items().stream().filter(itemStack ->
                                itemToRepair.getItem().isValidRepairItem(itemToRepair, itemStack)).toList();
                        if (!repairMaterials.isEmpty() && repairMaterials.size() <= unitsToMaxRepair + 1) {
                            ItemStack outputItem = itemToRepair.copy();
                            outputItem.setDamageValue(outputItem.getDamageValue() - ((outputItem.getMaxDamage() / unitCost) * repairMaterials.size()));
                            cir.setReturnValue(outputItem);
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        if (Main.CONFIG.whetstone.enableWhetstone()) {
            List<ItemStack> whetstones = input.items().stream().filter(itemStack -> itemStack.is(CoTItems.WHETSTONE)).toList();
            if (whetstones.size() == 1) {
                List<ItemStack> repairableItems = input.items().stream().filter(itemStack ->
                        itemStack.isDamageableItem() && !itemStack.is(CoTItems.WHETSTONE)).toList();
                if (!repairableItems.isEmpty()) {
                    ItemStack itemToRepair = repairableItems.getFirst();
                    if (
                            itemToRepair.isDamaged() &&
                            itemToRepair.getEnchantments().equals(whetstones.getFirst().getOrDefault(
                                    DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                            )
                    ) {
                        int unitCost = CoTUtil.determineUnitCost(itemToRepair);
                        int damageRepairedPerUnit = itemToRepair.getMaxDamage() / unitCost;
                        int unitsToMaxRepair = itemToRepair.getDamageValue() / damageRepairedPerUnit;
                        List<ItemStack> repairMaterials = input.items().stream().filter(itemStack ->
                                itemToRepair.getItem().isValidRepairItem(itemToRepair, itemStack)).toList();
                        if (!repairMaterials.isEmpty() && repairMaterials.size() <= unitsToMaxRepair + 1) {
                            List<ItemStack> otherGear = repairableItems.subList(1, repairableItems.size());
                            NonNullList<ItemStack> remainingItems = NonNullList.withSize(input.size(), ItemStack.EMPTY);
                            for (int i = 0; i < remainingItems.size(); i++) {
                                ItemStack itemStack = input.getItem(i);
                                if (itemStack.is(CoTItems.WHETSTONE)) {
                                    itemStack.setDamageValue(itemStack.getDamageValue() + repairMaterials.size());
                                    if (itemStack.getDamageValue() < itemStack.getMaxDamage()) {
                                        remainingItems.set(i, itemStack.copy());
                                    }
                                } else if (otherGear.contains(itemStack)) {
                                    remainingItems.set(i, itemStack.copy());
                                }
                            }
                            return remainingItems;
                        }
                    }
                }
            }
        }
        return super.getRemainingItems(input);
    }
}
