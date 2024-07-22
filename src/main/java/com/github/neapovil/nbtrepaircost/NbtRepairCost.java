package com.github.neapovil.nbtrepaircost;

import com.github.neapovil.nbtrepaircost.mixin.ForgingScreenHandlerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class NbtRepairCost implements ClientModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger("NbtRepairCost");
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient()
    {
        ItemTooltipCallback.EVENT.register(this::getTooltip);
    }

    private void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines)
    {
        final ItemStack repairitem = Registries.ITEM.stream()
            .filter(i -> stack.getItem().canRepair(stack, i.getDefaultStack()))
            .map(i -> new ItemStack(i, 64))
            .findFirst()
            .orElse(ItemStack.EMPTY);

        final ItemStack repairitem1 = Registries.ITEM.stream()
            .filter(i -> stack.isOf(i))
            .map(i -> new ItemStack(i))
            .findFirst()
            .orElse(ItemStack.EMPTY);

        if (repairitem.isOf(Items.AIR))
        {
            return;
        }

        int repaircost = this.getRepairCost(stack, repairitem);
        int repaircost1 = this.getRepairCost(stack, repairitem1);

        if (repaircost == 0)
        {
            return;
        }

        if (stack.get(DataComponentTypes.CUSTOM_NAME) != null)
        {
            repaircost--;
        }

        lines.add(1, Text.literal("Repair Level Cost").setStyle(Style.EMPTY.withUnderline(true)));
        lines.add(2, Text.of(repairitem.getName().getString() + ": " + repaircost));
        lines.add(3, Text.of(repairitem1.getName().getString() + ": " + repaircost1));
    }

    private int getRepairCost(ItemStack stack, ItemStack stack1)
    {
        final AnvilScreenHandler s = new AnvilScreenHandler(0, client.player.getInventory(),
            ScreenHandlerContext.create(client.world, client.player.getBlockPos()));

        ((ForgingScreenHandlerAccessor) s).getInput().setStack(0, stack);
        ((ForgingScreenHandlerAccessor) s).getInput().setStack(1, stack1);

        s.updateResult();

        return s.getLevelCost();
    }
}
