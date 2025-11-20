package com.klism.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class IronBowItem extends BowItem{

    private static final int FULL_CHARGE_TICKS = 40;
    private static final float VELOCITY_MULTIPLIER = 4.0F;

    public IronBowItem(Settings settings){
        super(settings);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks){
        if(!(user instanceof PlayerEntity playerEntity)){
            return false;
        }

        ItemStack arrowStack = playerEntity.getProjectileType(stack);
        if(arrowStack.isEmpty() && !playerEntity.getAbilities().creativeMode){
            return false;
        }

        int useTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float pullProgress = getPullProgress(useTicks);

        //NO quick firing this bow
        if(pullProgress < 0.5F){
            return false;
        }

        if(world instanceof ServerWorld serverWorld){
            List<ItemStack> projectiles = arrowStack.isEmpty()
                    ? List.of(stack.copyWithCount(1)) : List.of(arrowStack.copyWithCount(1));

            this.shootAll(
                    serverWorld,
                    playerEntity,
                    playerEntity.getActiveHand(),
                    stack,
                    projectiles,
                    pullProgress * VELOCITY_MULTIPLIER,
                    1.0F,
                    pullProgress == 1.0F,
                    null
            );
        }

        world.playSound(
                null,
                playerEntity.getX(),
                playerEntity.getY(),
                playerEntity.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F
        );

        if(!playerEntity.getAbilities().creativeMode){
            arrowStack.decrement(1);
            if(arrowStack.isEmpty()){
                playerEntity.getInventory().removeOne(arrowStack);
            }
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        return true;
    }

    public static float getPullProgress(int useTicks){
        float progress = (float)useTicks / (float)FULL_CHARGE_TICKS;
        progress = (progress * progress + progress * 2.0F) / 3.0F;
        if(progress > 1.0F){
            progress = 1.0F;
        }
        return progress;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user){
        return 72000;
    }

    @Override
    public int getRange(){
        return 20;
    }
}