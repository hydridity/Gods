//package com.dogonfire.gods;
//
//import net.minecraft.server.v1_7_R3.DamageSource;
//import net.minecraft.server.v1_7_R3.Entity;
//import net.minecraft.server.v1_7_R3.EntityLargeFireball;
//import net.minecraft.server.v1_7_R3.EntityLiving;
//import net.minecraft.server.v1_7_R3.GameRules;
//import net.minecraft.server.v1_7_R3.MovingObjectPosition;
//import net.minecraft.server.v1_7_R3.World;
//import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
//import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
//import org.bukkit.craftbukkit.v1_7_R3.event.CraftEventFactory;
//import org.bukkit.entity.Explosive;
//import org.bukkit.entity.Fireball;
//import org.bukkit.event.entity.ExplosionPrimeEvent;
//import org.bukkit.plugin.PluginManager;
//
//public class LimitedFireball
//  extends EntityLargeFireball
//{
//  private double speedup;
//  private int maxSurvivalCounter;
//  
//  public LimitedFireball(World world)
//  {
//    super(world);
//    
//    this.speedup = 1.0D;
//    this.maxSurvivalCounter = 100;
//  }
//  
//  public LimitedFireball(World world, EntityLiving entityliving, double d0, double d1, double d2)
//  {
//    super(world, entityliving, d0, d1, d2);
//    
//    this.speedup = 1.0D;
//    this.maxSurvivalCounter = 100;
//  }
//  
//  public boolean damageEntity(DamageSource damageSource, float i)
//  {
//    if (!damageSource.translationIndex.equals("onFire"))
//    {
//      FireballRebounceEvent event = new FireballRebounceEvent((Fireball)getBukkitEntity(), FireballRebounceEvent.RebounceReason.ARROW, damageSource.getEntity() == null ? null : damageSource.getEntity().getBukkitEntity());
//      
//      event = (FireballRebounceEvent)CraftEventFactory.callEvent(event);
//      if (event.isCancelled()) {
//        return false;
//      }
//    }
//    Q();
//    if (damageSource.getEntity() != null) {
//      if ((damageSource.getEntity() instanceof LimitedEnderDragon)) {
//        return false;
//      }
//    }
//    return false;
//  }
//  
//  public void h()
//  {
//    speedUp();
//    if (--this.maxSurvivalCounter < 0)
//    {
//      die();
//      return;
//    }
//    super.h();
//  }
//  
//  private void speedUp()
//  {
//    this.motX *= this.speedup;
//    this.motY *= this.speedup;
//    this.motZ *= this.speedup;
//    
//    Q();
//  }
//  
//  public void speedUp(double speed)
//  {
//    this.speedup = speed;
//    speedUp();
//  }
//  
//  protected void a(MovingObjectPosition movingobjectposition)
//  {
//    float fireballDamage = 3.0F;
//    if (movingobjectposition.entity != null) {
//      movingobjectposition.entity.damageEntity(DamageSource.fireball(this, this.shooter), fireballDamage);
//    }
//    ExplosionPrimeEvent event = new ExplosionPrimeEvent((Explosive)CraftEntity.getEntity(this.world.getServer(), this));
//    
//    this.world.getServer().getPluginManager().callEvent(event);
//    if (!event.isCancelled()) {
//      this.world.createExplosion(this, this.locX, this.locY, this.locZ, event.getRadius(), event.getFire(), this.world.getGameRules().getBoolean("mobGriefing"));
//    }
//    die();
//  }
//}
//
////
///* Location:           C:\temp\Gods.jar//
// * Qualified Name:     com.dogonfire.gods.LimitedFireball//
// * JD-Core Version:    0.7.0.1//
// */