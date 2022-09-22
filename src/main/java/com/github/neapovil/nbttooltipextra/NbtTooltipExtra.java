package com.github.neapovil.nbttooltipextra;

import java.util.List;

import com.github.neapovil.nbttooltipextra.mixin.ForgingScreenHandlerAccessor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

public class NbtTooltipExtra implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ItemTooltipCallback.EVENT.register(NbtTooltipExtra::tooltip);
    }

    private static void tooltip(ItemStack stack, TooltipContext context, List<Text> lines)
    {
        final MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null)
        {
            return;
        }

        final ItemStack repairitem = Registry.ITEM.stream()
                .filter(i -> stack.getItem().canRepair(stack, i.getDefaultStack()))
                .map(i -> new ItemStack(i, 64))
                .findFirst()
                .orElse(ItemStack.EMPTY);

        if (repairitem.isOf(Items.AIR))
        {
            return;
        }

        final AnvilScreenHandler s = new AnvilScreenHandler(0, client.player.getInventory(),
                ScreenHandlerContext.create(client.world, client.player.getBlockPos()));

        ((ForgingScreenHandlerAccessor) s).getInput().setStack(0, stack);
        ((ForgingScreenHandlerAccessor) s).getInput().setStack(1, repairitem);

        s.updateResult();

        int repaircost = s.getLevelCost();

        if (repaircost == 0)
        {
            return;
        }

        if (stack.hasCustomName())
        {
            repaircost--;
        }
        
        final Text text = new LiteralText("Repair Cost: " + repaircost)
                .setStyle(Style.EMPTY.withColor(Formatting.GRAY).withUnderline(true));

        lines.add(1, new LiteralText(""));
        lines.add(1, text);
    }
}
