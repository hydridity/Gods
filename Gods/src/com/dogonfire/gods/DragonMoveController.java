//package com.dogonfire.gods;
//
//import java.util.List;
//import java.util.Random;
//import net.minecraft.server.v1_7_R3.AxisAlignedBB;
//import net.minecraft.server.v1_7_R3.EntityComplexPart;
//import net.minecraft.server.v1_7_R3.EntityHuman;
//import net.minecraft.server.v1_7_R3.EntityLiving;
//import net.minecraft.server.v1_7_R3.Explosion;
//import net.minecraft.server.v1_7_R3.MathHelper;
//import net.minecraft.server.v1_7_R3.World;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.util.Vector;
//
//public class DragonMoveController
//{
//  protected LimitedEnderDragon dragon;
//  protected Random random;
//  protected Gods plugin;
//  protected final Explosion explosionSource;
//  protected boolean doNothingLock;
//  protected Vector oldSpeed;
//  protected Vector oldTarget;
//  
//  public DragonMoveController(Gods plugin, LimitedEnderDragon dragon)
//  {
//    this.explosionSource = new Explosion(null, this.dragon, (0.0D / 0.0D), (0.0D / 0.0D), (0.0D / 0.0D), (0.0F / 0.0F));
//    
//    this.dragon = dragon;
//    this.random = new Random();
//    this.plugin = plugin;
//  }
//  
//  public boolean checkDragonSits()
//  {
//    org.bukkit.entity.Entity target = this.dragon.getTarget();
//    
//    return target == null;
//  }
//  
//  public void knockbackNearbyEntities(List<net.minecraft.server.v1_7_R3.Entity> entities)
//  {
//    double pointX = (this.dragon.br.boundingBox.a + this.dragon.br.boundingBox.d) / 2.0D;
//    double pointZ = (this.dragon.br.boundingBox.c + this.dragon.br.boundingBox.f) / 2.0D;
//    for (net.minecraft.server.v1_7_R3.Entity entity : entities) {
//      if ((entity instanceof EntityLiving))
//      {
//        double motX = entity.locX - pointX;
//        double motY = 0.2D;
//        double motZ = entity.locZ - pointZ;
//        
//        double normalizer = motX * motX + motZ * motZ;
//        motX = motX / normalizer * 4.0D;
//        motZ = motZ / normalizer * 4.0D;
//        
//        entity.g(motX, motY, motZ);
//      }
//    }
//  }
//  
//  public void e(float sideMot, float forMot)
//  {
//    forMot = -forMot;
//    sideMot = -sideMot;
//    
//    float f2 = 0.91F;
//    if (this.dragon.onGround) {
//      f2 = this.dragon.world.getType(MathHelper.floor(this.dragon.locX), MathHelper.floor(this.dragon.boundingBox.b) - 1, MathHelper.floor(this.dragon.locZ)).frictionFactor * 0.91F;
//    }
//    float f3 = 0.162771F / (f2 * f2 * f2);
//    
//
//
//
//
//
//
//
//
//
//
//
//    f2 = 0.91F;
//    if (this.dragon.onGround) {
//      f2 = this.dragon.world.getType(MathHelper.floor(this.dragon.locX), MathHelper.floor(this.dragon.boundingBox.b) - 1, MathHelper.floor(this.dragon.locZ)).frictionFactor * 0.91F;
//    }
//    if (this.dragon.h_())
//    {
//      float f5 = 5.5F;
//      if (this.dragon.motX < -f5) {
//        this.dragon.motX = (-f5);
//      }
//      if (this.dragon.motX > f5) {
//        this.dragon.motX = f5;
//      }
//      if (this.dragon.motZ < -f5) {
//        this.dragon.motZ = (-f5);
//      }
//      if (this.dragon.motZ > f5) {
//        this.dragon.motZ = f5;
//      }
//      this.dragon.fallDistance = 0.0F;
//      if (this.dragon.motY < -f5) {
//        this.dragon.motY = (-f5);
//      }
//      if (this.dragon.motY > f5) {
//        this.dragon.motY = f5;
//      }
//    }
//    this.dragon.yaw = MathHelper.g(this.dragon.passenger.yaw - 180.0F);
//    this.dragon.pitch = this.dragon.passenger.pitch;
//    
//    this.dragon.move(this.dragon.motX, this.dragon.motY, this.dragon.motZ);
//    if ((this.dragon.positionChanged) && (this.dragon.h_())) {
//      this.dragon.motY = 0.2D;
//    }
//    if ((forMot > 0.1D) || (forMot < -0.1D))
//    {
//      float movementChange = (float)(this.dragon.passenger.pitch * 0.0001D);
//      if (forMot < 0.0F) {
//        this.dragon.motY -= movementChange;
//      } else {
//        this.dragon.motY += movementChange;
//      }
//    }
//    this.dragon.motY *= 0.9800000190734863D;
//    this.dragon.motX *= f2;
//    this.dragon.motZ *= f2;
//    
//    this.dragon.aF = this.dragon.aG;
//    double d0 = this.dragon.locX - this.dragon.lastX;
//    double d1 = this.dragon.locZ - this.dragon.lastZ;
//    float f6 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
//    if (f6 > 1.0F) {
//      f6 = 1.0F;
//    }
//    this.dragon.aG += (f6 - this.dragon.aG) * 0.4F;
//  }
//  
//  public boolean playerMovedEntity(float sideMot, float forMot)
//  {
//    if ((this.dragon.passenger == null) || (!(this.dragon.passenger instanceof EntityHuman))) {
//      return true;
//    }
//    this.dragon.lastYaw = (this.dragon.yaw = MathHelper.g(this.dragon.passenger.yaw - 180.0F));
//    this.dragon.pitch = (this.dragon.passenger.pitch * 0.5F);
//    
//    this.dragon.b(this.dragon.yaw, this.dragon.pitch);
//    this.dragon.aP = (this.dragon.aN = this.dragon.yaw);
//    
//
//
//    sideMot = ((EntityLiving)this.dragon.passenger).be * 0.5F;
//    if (forMot <= 0.0F) {
//      forMot *= 0.25F;
//    }
//    sideMot *= 0.75F;
//    
//    forMot *= 10.0F;
//    sideMot *= 10.0F;
//    
//    float speed = 5.0F;
//    this.dragon.i(speed);
//    
//    e(sideMot, forMot);
//    return false;
//  }
//  
//  public boolean checkHitBlocks(AxisAlignedBB axisalignedbb)
//  {
//    return false;
//  }
//  
//  public void moveDragon()
//  {
//    this.dragon.yaw = MathHelper.g(this.dragon.yaw);
//    if (this.dragon.bo < 0) {
//      for (int d05 = 0; d05 < this.dragon.bn.length; d05++)
//      {
//        this.dragon.bn[d05][0] = this.dragon.yaw;
//        this.dragon.bn[d05][1] = this.dragon.locY;
//      }
//    }
//    if (++this.dragon.bo == this.dragon.bn.length) {
//      this.dragon.bo = 0;
//    }
//    this.dragon.bn[this.dragon.bo][0] = this.dragon.yaw;
//    this.dragon.bn[this.dragon.bo][1] = this.dragon.locY;
//    
//    double oldTargetDistanceX = this.dragon.h - this.dragon.locX;
//    double oldTargetDistanceY = this.dragon.i - this.dragon.locY;
//    double oldTargetDistanceZ = this.dragon.j - this.dragon.locZ;
//    double oldTargetDistancePythagoras = oldTargetDistanceX * oldTargetDistanceX + oldTargetDistanceY * oldTargetDistanceY + oldTargetDistanceZ * oldTargetDistanceZ;
//    
//    net.minecraft.server.v1_7_R3.Entity currentTarget = this.dragon.getTargetController().getCurrentTarget();
//    
//    boolean attackingMode = true;
//    if (currentTarget != null)
//    {
//      this.plugin.logDebug("Dragon Current target is " + currentTarget.getName());
//      
//      this.dragon.h = currentTarget.locX;
//      this.dragon.j = currentTarget.locZ;
//      
//      double newDragonDistanceX = this.dragon.h - this.dragon.locX;
//      double newDragonDistanceY = this.dragon.j - this.dragon.locZ;
//      double newDragonDistancePythagoras = Math.sqrt(newDragonDistanceX * newDragonDistanceX + newDragonDistanceY * newDragonDistanceY);
//      
//      double attackAngleAsHeight = 0.4D + (newDragonDistancePythagoras / 80.0D - 1.0D);
//      if (attackAngleAsHeight > 10.0D) {
//        attackAngleAsHeight = 10.0D;
//      }
//      this.dragon.i = (currentTarget.boundingBox.b + attackAngleAsHeight);
//    }
//    else
//    {
//      boolean shouldSitDown = true;
//      if ((!this.dragon.getTargetController().hasTargets()) && (!this.dragon.getTargetController().isFlyingHome()) && (shouldSitDown))
//      {
//        attackingMode = false;
//        this.oldSpeed = new Vector().setX(this.dragon.motX).setY(this.dragon.motY).setZ(this.dragon.motZ);
//        
//        this.dragon.motX = 0.0D;
//        this.dragon.motY = 0.0D;
//        this.dragon.motZ = 0.0D;
//        
//        this.oldTarget = new Vector().setX(this.dragon.h).setY(this.dragon.i).setZ(this.dragon.j);
//        
//        this.dragon.h = this.dragon.locX;
//        this.dragon.i = this.dragon.locY;
//        this.dragon.j = this.dragon.locZ;
//        this.dragon.yaw = 0.0F;
//        
//        Location loc = this.dragon.getLocation().clone();
//        loc.subtract(0.0D, 1.0D, 0.0D);
//        if (loc.getBlock().getType() == Material.AIR)
//        {
//          this.dragon.motY = -0.2D;
//          this.dragon.i = (this.dragon.locY - 0.2D);
//        }
//        else
//        {
//          this.doNothingLock = true;
//        }
//      }
//      else
//      {
//        this.dragon.h += this.random.nextGaussian() * 2.0D;
//        this.dragon.j += this.random.nextGaussian() * 2.0D;
//      }
//    }
//    if ((this.dragon.bz) || (oldTargetDistancePythagoras < 100.0D) || (oldTargetDistancePythagoras > 22500.0D) || (this.dragon.positionChanged) || (this.dragon.G)) {
//      this.dragon.getTargetController().changeTarget();
//    }
//    oldTargetDistanceY /= MathHelper.sqrt(oldTargetDistanceX * oldTargetDistanceX + oldTargetDistanceZ * oldTargetDistanceZ);
//    
//    float angleAbs = 0.6F;
//    if (oldTargetDistanceY < -angleAbs) {
//      oldTargetDistanceY = -angleAbs;
//    }
//    if (oldTargetDistanceY > angleAbs) {
//      oldTargetDistanceY = angleAbs;
//    }
//    this.dragon.motY += oldTargetDistanceY * 0.1D;
//    this.dragon.yaw = MathHelper.g(this.dragon.yaw);
//    
//    double toTargetAngle = 180.0D - Math.atan2(oldTargetDistanceX, oldTargetDistanceZ) * 180.0D / 3.141592653589793D;
//    double toTurnAngle = MathHelper.g(toTargetAngle - this.dragon.yaw);
//    if (toTurnAngle > 50.0D) {
//      toTurnAngle = 50.0D;
//    }
//    if (toTurnAngle < -50.0D) {
//      toTurnAngle = -50.0D;
//    }
//    double directionDegree = this.dragon.yaw * 3.141592653589793D / 180.0D;
//    
//
//
//
//
//
//
//
//
//    this.dragon.bg *= 0.8F;
//    float motionPythagoras = MathHelper.sqrt(this.dragon.motX * this.dragon.motX + this.dragon.motZ * this.dragon.motZ) + 1.0F;
//    if (motionPythagoras > 40.0F) {
//      motionPythagoras = 40.0F;
//    }
//    LimitedEnderDragon tmp1125_1122 = this.dragon;
//    tmp1125_1122.bg = ((float)(tmp1125_1122.bg + toTurnAngle * (0.7D / motionPythagoras / motionPythagoras)));
//    LimitedEnderDragon tmp1153_1150 = this.dragon;
//    tmp1153_1150.yaw = ((float)(tmp1153_1150.yaw + this.dragon.bg * 0.1D));
//    directionDegree = this.dragon.yaw * 3.141592653589793D / 180.0D;
//    float f6 = (float)(2.0D / (motionPythagoras + 1.0D));
//    if (!this.doNothingLock) {
//      if (this.dragon.bA) {
//        this.dragon.move(this.dragon.motX * 0.8D, this.dragon.motY * 0.8D, this.dragon.motZ * 0.8D);
//      } else {
//        this.dragon.move(this.dragon.motX, this.dragon.motY, this.dragon.motZ);
//      }
//    }
//    float scaledMotionLength = 0.0F;
//    
//    scaledMotionLength *= 0.15F;
//    scaledMotionLength += 0.8F;
//    
//    this.dragon.motX *= scaledMotionLength;
//    this.dragon.motZ *= scaledMotionLength;
//    this.dragon.motY *= 0.91D;
//    
//    this.dragon.aN = this.dragon.yaw;
//    
//    this.dragon.bq.width = (this.dragon.bq.length = 3.0F);
//    this.dragon.bs.width = (this.dragon.bs.length = 2.0F);
//    this.dragon.bt.width = (this.dragon.bt.length = 2.0F);
//    this.dragon.bu.width = (this.dragon.bu.length = 2.0F);
//    
//    this.dragon.br.length = 3.0F;
//    this.dragon.br.width = 5.0F;
//    this.dragon.bv.length = 2.0F;
//    this.dragon.bv.width = 4.0F;
//    this.dragon.bw.length = 3.0F;
//    this.dragon.bw.width = 4.0F;
//    
//    float f1 = (float)((this.dragon.b(5, 1.0F)[1] - this.dragon.b(10, 1.0F)[1]) * 10.0D / 180.0D * 3.141592653589793D);
//    
//    float f2 = MathHelper.cos(f1);
//    float f9 = -MathHelper.sin(f1);
//    
//    float f11 = MathHelper.sin((float)directionDegree);
//    float f12 = MathHelper.cos((float)directionDegree);
//    
//    this.dragon.br.h();
//    this.dragon.br.setPositionRotation(this.dragon.locX + f11 * 0.5F, this.dragon.locY, this.dragon.locZ - f12 * 0.5F, 0.0F, 0.0F);
//    
//    this.dragon.bv.h();
//    this.dragon.bv.setPositionRotation(this.dragon.locX + f12 * 4.5F, this.dragon.locY + 2.0D, this.dragon.locZ + f11 * 4.5F, 0.0F, 0.0F);
//    
//    this.dragon.bw.h();
//    this.dragon.bw.setPositionRotation(this.dragon.locX - f12 * 4.5F, this.dragon.locY + 2.0D, this.dragon.locZ - f11 * 4.5F, 0.0F, 0.0F);
//    if ((this.dragon.hurtTicks == 0) && (attackingMode))
//    {
//      this.dragon.getDragonMoveController().knockbackNearbyEntities(this.dragon.world.getEntities(this.dragon, this.dragon.bv.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
//      this.dragon.getDragonMoveController().knockbackNearbyEntities(this.dragon.world.getEntities(this.dragon, this.dragon.bw.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
//      this.dragon.getDragonHealthController().damageEntities(this.dragon.world.getEntities(this.dragon, this.dragon.bq.boundingBox.grow(1.0D, 1.0D, 1.0D)));
//    }
//    this.dragon.getFireballController().checkSpitFireBall();
//    
//    double[] adouble = this.dragon.b(5, 1.0F);
//    double[] adouble1 = this.dragon.b(0, 1.0F);
//    
//    float f19 = MathHelper.sin((float)(directionDegree - this.dragon.bg * 0.01F));
//    float f13 = MathHelper.cos((float)directionDegree - this.dragon.bg * 0.01F);
//    
//    this.dragon.bq.h();
//    this.dragon.bq.setPositionRotation(this.dragon.locX + f19 * 5.5F * f2, this.dragon.locY + (adouble1[1] - adouble[1]) + f9 * 5.5F, this.dragon.locZ - f13 * 5.5F * f2, 0.0F, 0.0F);
//    for (int j = 0; j < 3; j++)
//    {
//      EntityComplexPart entitycomplexpart = null;
//      if (j == 0) {
//        entitycomplexpart = this.dragon.bs;
//      }
//      if (j == 1) {
//        entitycomplexpart = this.dragon.bt;
//      }
//      if (j == 2) {
//        entitycomplexpart = this.dragon.bu;
//      }
//      double[] adouble2 = this.dragon.b(12 + j * 2, 1.0F);
//      float f14 = (float)(directionDegree + MathHelper.g(adouble2[0] - adouble[0]) * 3.141592653589793D / 180.0D);
//      float f15 = MathHelper.sin(f14);
//      float f16 = MathHelper.cos(f14);
//      float f17 = 1.5F;
//      float f18 = (j + 1) * 2.0F;
//      
//      entitycomplexpart.h();
//      entitycomplexpart.setPositionRotation(this.dragon.locX - (f11 * f17 + f15 * f18) * f2, this.dragon.locY + (adouble2[1] - adouble[1]) * 1.0D - (f18 + f17) * f9 + 1.5D, this.dragon.locZ + (f12 * f17 + f16 * f18) * f2, 0.0F, 0.0F);
//    }
//    this.dragon.bA = (this.dragon.getDragonMoveController().checkHitBlocks(this.dragon.bq.boundingBox) | this.dragon.getDragonMoveController().checkHitBlocks(this.dragon.br.boundingBox));
//  }
//  
//  public void restoreOldDataIfPossible()
//  {
//    this.doNothingLock = false;
//    if (this.oldSpeed != null)
//    {
//      this.dragon.motX = this.oldSpeed.getX();
//      this.dragon.motY = this.oldSpeed.getY();
//      this.dragon.motZ = this.oldSpeed.getZ();
//      
//      this.oldSpeed = null;
//    }
//    if (this.oldTarget != null)
//    {
//      this.dragon.h = this.oldTarget.getX();
//      this.dragon.i = this.oldTarget.getY();
//      this.dragon.j = this.oldTarget.getZ();
//      
//      this.oldTarget = null;
//    }
//  }
//}
//
////
///* Location:           C:\temp\Gods.jar//
// * Qualified Name:     com.dogonfire.gods.DragonMoveController//
// * JD-Core Version:    0.7.0.1//
// */