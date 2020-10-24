package com.cinak.test.entities;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import com.cinak.test.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.event.ParticleKeyFrameEvent;
import software.bernie.geckolib.event.SoundKeyframeEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

public class BorsusEntity extends TameableEntity implements IAngerable, IAnimatedEntity {
    private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.createKey(BorsusEntity.class, DataSerializers.BOOLEAN);
    private float clientSideStandAnimation0;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private static final RangedInteger field_234217_by_ = TickRangeConverter.func_233037_a_(20, 39);    private int field_234218_bz_;
    private UUID field_234216_bA_;
    public EntityAnimationManager animationControllers = new EntityAnimationManager();
    private AnimationController moveController = new EntityAnimationController(this, "moveController", 10F, this::moveController);

    public BorsusEntity(EntityType<? extends BorsusEntity> type, World worldIn) {
        super(type, worldIn);
        registerAnimationControllers();
    }


    public static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(RegistryHandler.RAW_CRAWLER_MEAT_REGISTRY_OBJECT.get(), Items.PORKCHOP, Items.WHEAT, Items.MUTTON
            ,Items.SALMON, Items.SALMON_BUCKET, Items.COD, Items.COD_BUCKET, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.PUFFERFISH_BUCKET);


    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */


    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return item.isFood() && item.getFood().isMeat();
    }


    public static AttributeModifierMap setCustomAttributes() {
        return MobEntity.func_233666_p_().func_233815_a_(Attributes.field_233818_a_,
                40.0D).func_233815_a_(Attributes.field_233819_b_,
                20.0D).func_233815_a_(Attributes.field_233821_d_, 0.5D)
                .func_233815_a_(Attributes.field_233823_f_, 10.0D);
    }
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new BorsusEntity.MeleeAttackGoal());
        this.goalSelector.addGoal(3, new BorsusEntity.PanicGoal());
        this.goalSelector.addGoal(4,  new BreedGoal(this, 0.4D));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 0.5D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 0.5D));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 0.5D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(10, new FollowBoatGoal(this));
        this.goalSelector.addGoal(11,  new TemptGoal(this, 0.6,false,TEMPTATION_ITEMS ));
        this.targetSelector.addGoal(1, new BorsusEntity.HurtByTargetGoal());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new BorsusEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, FoxEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, WolfEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, CrawlerEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, ShoulderRidingEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PolarBearEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(11, new ResetAngerGoal<>(this, false));
    }



    public static boolean func_223320_c(EntityType<PolarBearEntity> p_223320_0_, IWorld p_223320_1_, SpawnReason reason, BlockPos p_223320_3_, Random p_223320_4_) {
        Optional<RegistryKey<Biome>> optional = p_223320_1_.func_242406_i(p_223320_3_);
        if (!Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN))) {
            return canAnimalSpawn(p_223320_0_, p_223320_1_, reason, p_223320_3_, p_223320_4_);
        } else {
            return p_223320_1_.getLightSubtracted(p_223320_3_, 0) > 8 && p_223320_1_.getBlockState(p_223320_3_.down()).isIn(Blocks.ICE);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */

    @Override
    public int func_230256_F__() {
        return 0;
    }

    @Override
    public void func_230260_a__(int p_230260_1_) {

    }

    @Nullable
    @Override
    public UUID func_230257_G__() {
        return null;
    }

    @Override
    public void func_230259_a_(@Nullable UUID p_230259_1_) {

    }

    public void func_230258_H__() {
        this.setAngerTime(field_234217_by_.func_233018_a_(this.rand));
    }

    public void setAngerTime(int time) {
        this.field_234218_bz_ = time;
    }

    public int getAngerTime() {
        return this.field_234218_bz_;
    }

    public void setAngerTarget(@Nullable UUID target) {
        this.field_234216_bA_ = target;
    }

    public UUID getAngerTarget() {
        return this.field_234216_bA_;
    }

    protected SoundEvent getAmbientSound() {
        return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_POLAR_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.getSoundPitch());
            this.warningSoundTicks = 40;
        }

    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(IS_STANDING, false);
    }


    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        if (tamed) {
            this.getAttribute(Attributes.field_233818_a_).setBaseValue(46.0D);
            this.setHealth(20.0F);
        } else {
            this.getAttribute(Attributes.field_233818_a_).setBaseValue(8.0D);
        }

        this.getAttribute(Attributes.field_233823_f_).setBaseValue(14.0D);
    }
    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
        Item item = itemstack.getItem();
        if (this.world.isRemote) {
            boolean flag = this.isOwner(p_230254_1_) || this.isTamed() || item == RegistryHandler.CRAWLER_BILE.get() && !this.isTamed() && !this.func_233678_J__();
            return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            if (this.isTamed()) {
                if (this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!p_230254_1_.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }

                    this.heal((float)item.getFood().getHealing());
                    return ActionResultType.SUCCESS;
                }

                if (!(item instanceof DyeItem)) {
                    ActionResultType actionresulttype = super.func_230254_b_(p_230254_1_, p_230254_2_);
                    if ((!actionresulttype.isSuccessOrConsume() || this.isChild()) && this.isOwner(p_230254_1_)) {
                        this.func_233687_w_(!this.func_233685_eM_());
                        this.isJumping = false;
                        this.navigator.clearPath();
                        this.setAttackTarget((LivingEntity)null);
                        return ActionResultType.SUCCESS;
                    }

                    return actionresulttype;
                }

            } else if (item == RegistryHandler.CRAWLER_BILE.get() && !this.func_233678_J__()) {
                if (!p_230254_1_.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }

                if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
                    this.setTamedBy(p_230254_1_);
                    this.navigator.clearPath();
                    this.setAttackTarget((LivingEntity)null);
                    this.func_233687_w_(true);
                    this.world.setEntityState(this, (byte)7);
                } else {
                    this.world.setEntityState(this, (byte)6);
                }

                return ActionResultType.SUCCESS;
            }

            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
    }

    public BorsusEntity createChild(AgeableEntity ageable) {
        BorsusEntity BorsusEntity = ModEntityTypes.BORSUS.get().create(this.world);
        UUID uuid = this.getOwnerId();
        if (uuid != null) {
            BorsusEntity.setOwnerId(uuid);
            BorsusEntity.setTamed(true);
        }

        return BorsusEntity;
    }




    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean canMateWith(AnimalEntity otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(otherAnimal instanceof BorsusEntity)) {
            return false;
        } else {
            BorsusEntity BorsusEntity = (BorsusEntity)otherAnimal;
            if (!BorsusEntity.isTamed()) {
                return false;
            } else if (BorsusEntity.func_233684_eK_()) {
                return false;
            } else {
                return this.isInLove() && BorsusEntity.isInLove();
            }
        }
    }


    public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
            if (target instanceof BorsusEntity) {
                BorsusEntity BorsusEntity = (BorsusEntity)target;
                return !BorsusEntity.isTamed() || BorsusEntity.getOwner() != owner;
            } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).canAttackPlayer((PlayerEntity)target)) {
                return false;
            } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity)target).isTame()) {
                return false;
            } else {
                return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
            }
        } else {
            return false;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimation0) {
                this.recalculateSize();
            }

            this.clientSideStandAnimation0 = this.clientSideStandAnimation;
            if (this.isStanding()) {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            } else {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }

        if (!this.world.isRemote) {
            this.func_241359_a_((ServerWorld)this.world, true);
        }

    }

    public EntitySize getSize(Pose poseIn) {
        if (this.clientSideStandAnimation > 0.0F) {
            float f = this.clientSideStandAnimation / 6.0F;
            float f1 = 1.0F + f;
            return super.getSize(poseIn).scale(1.0F, f1);
        } else {
            return super.getSize(poseIn);
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.func_233637_b_(Attributes.field_233823_f_)));
        if (flag) {
            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    public boolean isStanding() {
        return this.dataManager.get(IS_STANDING);
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(IS_STANDING, standing);
    }

    @OnlyIn(Dist.CLIENT)
    public float getStandingAnimationScale(float p_189795_1_) {
        return MathHelper.lerp(p_189795_1_, this.clientSideStandAnimation0, this.clientSideStandAnimation) / 6.0F;
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableEntity.AgeableData(1.0F);
        }

        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }



    private <E extends BorsusEntity> boolean moveController(AnimationTestEvent<E> event) {
        if(event.isWalking()) {
            moveController.setAnimation(new AnimationBuilder().addAnimation("animation.testermodding.borsusWalk").addAnimation("animation.testermodding.borsusIdle", true));
        return true;
        } else {
            moveController.setAnimation(new AnimationBuilder().addAnimation("animation.testermodding.borsusIdle", true));
            return true;
        }
    }

    @Override
    public EntityAnimationManager getAnimationManager()
    {
        return animationControllers;
    }

    public void registerAnimationControllers()
    {
        if(world.isRemote)
        {
            this.animationControllers.addAnimationController(moveController);
            moveController.registerSoundListener(this::flapListener);
        }
    }


    private <E extends Entity> void particleListener(ParticleKeyFrameEvent<E> event) {
        if (event.effect.equals("bite")) {
            event.getEntity().getEntityWorld().addParticle(new RedstoneParticleData(0.2f, 0.7f, 0.5f, 1.0f),
                    event.getEntity().getPosX(), event.getEntity().getPosY(), event.getEntity().getPosZ(), 0.0f, 0.4f,
                    0.0f);
        }
    }


    private <ENTITY extends Entity> SoundEvent flapListener(SoundKeyframeEvent<ENTITY> event) {
        if (event.sound.equals("breathing")) {
            //return whatever sound you want to play here, or return null and handle sounds yourself
            return SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
        }

        return null;
    }

    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(BorsusEntity.this, PlayerEntity.class, 20, true, true, (Predicate<LivingEntity>)null);

        }




        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (BorsusEntity.this.isChild()) {
                return false;
            } else {
                if (super.shouldExecute()) {
                    for(BorsusEntity BorsusEntity : BorsusEntity.this.world.getEntitiesWithinAABB(BorsusEntity.class, BorsusEntity.this.getBoundingBox().grow(4.0D, 2.0D, 4.0D))) {
                        if (BorsusEntity.isChild()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        protected double getTargetDistance() {
            return super.getTargetDistance() * 0.5D;
        }
    }

    class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(BorsusEntity.this);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            super.startExecuting();
            if (BorsusEntity.this.isChild()) {
                this.alertOthers();
                this.resetTask();
            }

        }

        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
            if (mobIn instanceof BorsusEntity && !mobIn.isChild()) {
                super.setAttackTarget(mobIn, targetIn);
            }

        }
    }

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
        public MeleeAttackGoal() {
            super(BorsusEntity.this, 0.5D, true);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.func_234040_h_()) {
                this.func_234039_g_();
                this.attacker.attackEntityAsMob(enemy);
                BorsusEntity.this.setStanding(false);
            } else if (distToEnemySqr <= d0 * 2.0D) {
                if (this.func_234040_h_()) {
                    BorsusEntity.this.setStanding(false);
                    this.func_234039_g_();
                }

                if (this.func_234041_j_() <= 10) {
                    BorsusEntity.this.setStanding(true);
                    BorsusEntity.this.playWarningSound();
                }
            } else {
                this.func_234039_g_();
                BorsusEntity.this.setStanding(false);
            }

        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            BorsusEntity.this.setStanding(false);
            super.resetTask();
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return (double)(4.0F + attackTarget.getWidth());
        }
    }

    class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(BorsusEntity.this, 0.5D);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return !BorsusEntity.this.isChild() && !BorsusEntity.this.isBurning() ? false : super.shouldExecute();
        }
    }
}