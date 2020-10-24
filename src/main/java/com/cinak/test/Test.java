package com.cinak.test;


import com.cinak.test.entities.BorsusEntity;
import com.cinak.test.entities.CrawlerEntity;
import com.cinak.test.entities.ModEntityTypes;
import com.cinak.test.util.RegistryHandler;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("testermodding")
public class Test
{

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "testermodding";

    public Test() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);


        ModEntityTypes.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        RegistryHandler.init();




        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(ModEntityTypes.CRAWLER.get(), CrawlerEntity.setCustomAttributes());
            GlobalEntityTypeAttributes.put(ModEntityTypes.BORSUS.get(), BorsusEntity.setCustomAttributes());
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) { }

    public static final ItemGroup TAB = new ItemGroup("testTab") {
        @Override
        public ItemStack createIcon(){
            return new ItemStack(RegistryHandler.AMBER.get());
        }

    };
    public static final ItemGroup TAR = new ItemGroup("testBlockTab") {
        @Override
        public ItemStack createIcon(){
            return new ItemStack(RegistryHandler.AMBER.get());
        }

    };

}
