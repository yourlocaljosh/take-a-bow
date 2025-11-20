package com.klism.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ShortBowItem extends Item{

    private static final float PULL_PROGRESS = 0.65F; // 65% power
    private static final int COOLDOWN_TICKS = 10; //10 tick cooldown between shot

    public ShortBowItem(Settings settings){
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand){
        ItemStack bowStack = user.getStackInHand(hand);

        if(user.getItemCooldownManager().isCoolingDown(bowStack)){
            return ActionResult.PASS;
        }

        ItemStack arrowStack = user.getProjectileType(bowStack);

        if(arrowStack.isEmpty() && !user.getAbilities().creativeMode){
            return ActionResult.FAIL;
        }

        if(world instanceof ServerWorld serverWorld){
            if(arrowStack.isEmpty()){
                arrowStack = new ItemStack(Items.ARROW);
            }

            shootArrow(serverWorld, user, hand, bowStack, arrowStack);
        }

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + PULL_PROGRESS * 0.5F
        );

        user.incrementStat(Stats.USED.getOrCreateStat(this));

        //Set firing cooldown
        user.getItemCooldownManager().set(bowStack, COOLDOWN_TICKS);

        return ActionResult.CONSUME;
    }

    private void shootArrow(ServerWorld world, PlayerEntity shooter, Hand hand,
                            ItemStack bowStack, ItemStack arrowStack){
        ArrowEntity arrowEntity = new ArrowEntity(world, shooter, arrowStack.copyWithCount(1), bowStack);

        arrowEntity.setVelocity(
                shooter,
                shooter.getPitch(),
                shooter.getYaw(),
                0.0F,
                PULL_PROGRESS * 3.0F,
                1.0F
        );

        world.spawnEntity(arrowEntity);

        if(!shooter.getAbilities().creativeMode){
            arrowStack.decrement(1);
            if(arrowStack.isEmpty()){
                shooter.getInventory().removeOne(arrowStack);
            }
        }

        EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        bowStack.damage(1, shooter, slot);
    }

    public int getRange(){
        return 15;
    }
}