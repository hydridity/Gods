//package com.dogonfire.gods;
//
//import java.util.LinkedList;
//
//import net.minecraft.server.v1_8_R1.Entity;
//
//import org.bukkit.Location;
//import org.bukkit.entity.Player;
//
//public class FireballController
//{
//  protected TargetController targetController;
//  protected int fireballTicks;
//  protected Gods plugin;
//  
//  public FireballController(Gods plugin, TargetController targetController)
//  {
//    this.targetController = targetController;
//    this.fireballTicks = 0;
//    this.plugin = plugin;
//  }
//  
//  public void forceSpitFireball()
//  {
//    this.fireballTicks = 210;
//    checkSpitFireBall();
//  }
//  
//  public void checkSpitFireBall()
//  {
//    if (!checkActive()) {
//      return;
//    }
//    this.fireballTicks += 1;
//    
//    int fireEveryX = 2;
//    if (this.fireballTicks > fireEveryX * 20)
//    {
//      this.fireballTicks = 0;
//      if (!this.targetController.hasTargets()) {
//        return;
//      }
//      int maxDistanceSquared = 8;
//      maxDistanceSquared *= maxDistanceSquared;
//      
//      int maxFireballTargets = 2;
//      LinkedList<Entity> entities = this.targetController.getTargetsInRange(maxFireballTargets, maxDistanceSquared);
//      for (Entity target : entities) {
//        if ((target != null) && ((target.getBukkitEntity() instanceof Player))) {
//          if (!checkFiredirectionHeight(target.locY, this.targetController.getDragonLocation().getY()))
//          {
//            this.fireballTicks = (fireEveryX * 20 / 2);
//          }
//          else
//          {
//            Player player = (Player)target.getBukkitEntity();
//            Location playerLocation = player.getLocation();
//            Location dragonLocation = this.targetController.getDragonLocation();
//            if (playerLocation.distanceSquared(dragonLocation) <= maxDistanceSquared) {
//              fireFireball(target);
//            }
//          }
//        }
//      }
//    }
//  }
//  
//  protected boolean checkFiredirectionHeight(double heightTarget, double heightDragon)
//  {
//    double directionHeight = heightTarget - heightDragon;
//    if (directionHeight >= 0.0D) {
//      return false;
//    }
//    return true;
//  }
//  
//  protected boolean checkActive()
//  {
//    boolean fireFireBall = true;
//    
//    return fireFireBall;
//  }
//  
//  public void fireFireball(Entity entity)
//  {
//    Location locDragon = this.targetController.getDragonLocation();
//    Location loc = new Location(locDragon.getWorld(), entity.locX - locDragon.getBlockX(), entity.locY - locDragon.getBlockY(), entity.locZ - locDragon.getBlockZ());
//    
//    fireFireballToDirection(loc);
//  }
//  
//  public void fireFireballToDirection(Location direction)
//  {
//    if (direction.getWorld() != this.targetController.getDragonLocation().getWorld()) {
//      return;
//    }
//    LimitedFireball fireBall = new LimitedFireball(this.targetController.getDragon().world, this.targetController.getDragon(), direction.getBlockX(), direction.getBlockY(), direction.getBlockZ());
//    
//    this.targetController.getDragon().world.addEntity(fireBall);
//    double fireBallSpeedup = 1.1D;
//    fireBall.speedUp(fireBallSpeedup);
//  }
//  
//  public void fireFireballOnLocation(Location location)
//  {
//    Location direction = location.clone();
//    direction = direction.subtract(this.targetController.getDragonLocation().clone());
//    if (direction.getWorld() != this.targetController.getDragonLocation().getWorld()) {
//      return;
//    }
//    LimitedFireball fireBall = new LimitedFireball(this.targetController.getDragon().world, this.targetController.getDragon(), direction.getBlockX(), direction.getBlockY(), direction.getBlockZ());
//    
//    this.targetController.getDragon().world.addEntity(fireBall);
//    double fireBallSpeedup = 1.1D;
//    fireBall.speedUp(fireBallSpeedup);
//  }
//}
//
////
///* Location:           C:\temp\Gods.jar//
// * Qualified Name:     com.dogonfire.gods.FireballController//
// * JD-Core Version:    0.7.0.1//
// */