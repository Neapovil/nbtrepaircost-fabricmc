package com.github.neapovil.nbttooltipextra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ForgingScreenHandler;

@Mixin(ForgingScreenHandler.class)
public interface ForgingScreenHandlerAccessor
{
    @Accessor("input")
    public Inventory getInput();
}
