package com.klism.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class ShortBowItem extends BowItem{

    private static final float PULL_PROGRESS = 0.65F;

    public ShortBowItem(Settings settings){
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand){
        ItemStack bowStack = user.getStackInHand(hand);
        ItemStack arrowStack = user.getProjectileType(bowStack);

        if(arrowStack.isEmpty() && !user.getAbilities().creativeMode){
            return ActionResult.FAIL;
        }

        if(world instanceof ServerWorld serverWorld){
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

        return ActionResult.CONSUME;
    }

    private void shootArrow(ServerWorld world, PlayerEntity shooter, Hand hand,
                            ItemStack bowStack, ItemStack arrowStack){
        ItemStack projectileStack = arrowStack.isEmpty() ? new ItemStack(Items.ARROW) : arrowStack.copy();

        List<ItemStack> projectiles = List.of(projectileStack);

        this.shootAll(
                world,
                shooter,
                hand,
                bowStack,
                projectiles,
                PULL_PROGRESS * 3.0F,
                1.0F,
                false,
                null
        );

        if(!shooter.getAbilities().creativeMode){
            arrowStack.decrement(1);
            if(arrowStack.isEmpty()){
                shooter.getInventory().removeOne(arrowStack);
            }
        }

        EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        bowStack.damage(1, shooter, slot);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user){
        return 0;
    }

    @Override
    public int getRange(){
        return 15;
    }
}