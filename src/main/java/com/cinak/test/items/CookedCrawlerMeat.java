package com.cinak.test.items;

import com.cinak.test.Test;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class CookedCrawlerMeat extends Item {
    public CookedCrawlerMeat() {
        super(new Item.Properties()
                .group(Test.TAB)
                .food(new Food.Builder()
                        .hunger(10)
                        .saturation(20.0F)
                        .effect(new EffectInstance(Effects.INSTANT_HEALTH, 1, 1), 1f)
                        .build())





        );
    }
}
