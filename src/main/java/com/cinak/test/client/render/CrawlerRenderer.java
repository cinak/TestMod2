package com.cinak.test.client.render;

import com.cinak.test.Test;
import com.cinak.test.client.model.CrawlerModel;
import com.cinak.test.entities.CrawlerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CrawlerRenderer extends MobRenderer<CrawlerEntity, CrawlerModel<CrawlerEntity>> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Test.MOD_ID,"textures/entity/crawler.png");

    public CrawlerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new CrawlerModel<>(), 0.5F);
    }

    @Override
    public ResourceLocation getEntityTexture(CrawlerEntity entity) {
        return TEXTURE;
    }
}
