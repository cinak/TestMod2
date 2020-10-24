package com.cinak.test.util;

import com.cinak.test.Test;
import com.cinak.test.client.render.BorsusRenderer;
import com.cinak.test.client.render.CrawlerRenderer;
import com.cinak.test.entities.ModEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Test.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent

    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.CRAWLER.get(), CrawlerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BORSUS.get(), BorsusRenderer::new);
    }

}
