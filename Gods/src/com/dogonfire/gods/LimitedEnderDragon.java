//package com.dogonfire.gods;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import net.minecraft.server.v1_7_R3.AttributeInstance;
//import net.minecraft.server.v1_7_R3.DamageSource;
//import net.minecraft.server.v1_7_R3.EntityEnderDragon;
//import net.minecraft.server.v1_7_R3.EntityLiving;
//import net.minecraft.server.v1_7_R3.GenericAttributes;
//import net.minecraft.server.v1_7_R3.LocaleI18n;
//import net.minecraft.server.v1_7_R3.NBTTagCompound;
//import net.minecraft.server.v1_7_R3.World;
//import org.bukkit.Chunk;
//import org.bukkit.Location;
//import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
//import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
//import org.bukkit.craftbukkit.v1_7_R3.event.CraftEventFactory;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.util.Vector;
//
//public class LimitedEnderDragon
//  extends EntityEnderDragon
//{
//  public float bg;
//  private Gods plugin;
//  
//  public LimitedEnderDragon(Gods plugin, Location location, World world, String ageType)
//  {
//    super(world);
//    
//    this.bg = 0.0F;
//    
//    this.plugin = plugin;
//    
//    this.logicCall = 0;
//    
//    this.doNothingLock = false;
//    
//    setPosition(location.getX(), location.getY(), location.getZ());
//    createAllControllers(ageType, location);
//  }
//  
//  public LimitedEnderDragon(Gods plugin, Location location, World world, UUID uid, String ageType)
//  {
//    super(world);
//    
//    this.bg = 0.0F;
//    
//    this.plugin = plugin;
//    
//    this.logicCall = 0;
//    
//    this.doNothingLock = false;
//    
//    changeUUID(uid);
//    
//    setPosition(location.getX(), location.getY(), location.getZ());
//    createAllControllers(ageType, location);
//  }
//  
//  public LimitedEnderDragon(Gods plugin, World world)
//  {
//    super(world);
//    
//    this.bg = 0.0F;
//    
//    this.plugin = plugin;
//    
//    this.logicCall = 0;
//    
//    this.doNothingLock = false;
//  }
//  
//  private void createAllControllers(NBTTagDragonStore.DragonNBTReturn returnContainer)
//  {
//    this.targetController = new TargetController(this.plugin, returnContainer.getHomeLocation(), this, true);
//    
//    this.fireballController = new FireballController(this.plugin, this.targetController);
//    
//
//    this.dragonMoveController = new DragonMoveController(this.plugin, this);
//    
//    this.uniqueID = returnContainer.getUuid();
//    
//    initStats();
//  }
//  
//  private void createAllControllers(String ageType, Location homeLocation)
//  {
//    boolean hostile = true;
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
//    this.targetController = new TargetController(this.plugin, homeLocation, this, hostile);
//    this.fireballController = new FireballController(this.plugin, this.targetController);
//    
//    this.dragonHealthController = new DragonHealthController(this.plugin, this);
//    this.dragonMoveController = new DragonMoveController(this.plugin, this);
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
//
//
//
//
//
//
//    initStats();
//  }
//  
//  private void initStats()
//  {
//    getAttributeInstance(GenericAttributes.a).setValue(1000.0D);
//    this.plugin.getBossManager().registerDragon(this);
//    
//    String dragonName = "The World Eater";
//    if (dragonName.length() > 30) {
//      dragonName = dragonName.substring(0, 30);
//    }
//    setCustomName(dragonName);
//  }
//  
//  protected void dropDeathLoot(boolean flag, int i)
//  {
//    List<ItemStack> loot = null;
//    
//    CraftEventFactory.callEntityDeathEvent(this, loot);
//  }
//  
//  public String getName()
//  {
//    return LocaleI18n.get("entity.EnderDragon.name");
//  }
//  
//  public boolean dealDamage(DamageSource damagesource, float i)
//  {
//    this.dragonHealthController.rememberDamage(damagesource, i);
//    restoreOldDataIfPossible();
//    return super.dealDamage(damagesource, i);
//  }
//  
//  private void restoreOldDataIfPossible()
//  {
//    this.doNothingLock = false;
//    if (this.oldSpeed != null)
//    {
//      this.motX = this.oldSpeed.getX();
//      this.motY = this.oldSpeed.getY();
//      this.motZ = this.oldSpeed.getZ();
//      
//      this.oldSpeed = null;
//    }
//    if (this.oldTarget != null)
//    {
//      this.h = this.oldTarget.getX();
//      this.i = this.oldTarget.getY();
//      this.j = this.oldTarget.getZ();
//      
//      this.oldTarget = null;
//    }
//  }
//  
//  public void e()
//  {
//    try
//    {
//      internalLogicTick();
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//    }
//  }
//  
//  public void internalLogicTick()
//  {
//    this.logicCall += 1;
//    this.bx = this.by;
//    
//    this.dragonHealthController.recheckHealthNotOvercaped();
//    if (this.doNothingLock) {
//      return;
//    }
//    if (getHealth() <= 0.0F)
//    {
//      this.plugin.logDebug("getHealth is 0");
//      return;
//    }
//    this.dragonHealthController.checkRegainHealth();
//    
//    this.dragonMoveController.moveDragon();
//  }
//  
//  public void e(float motX, float motY)
//  {
//    if (this.dragonMoveController.playerMovedEntity(motX, motY)) {
//      super.e(motX, motY);
//    }
//  }
//  
//  public void b(float f1, float f2)
//  {
//    super.b(f1, f2);
//  }
//  
//  public boolean spitFireBallOnTarget(net.minecraft.server.v1_7_R3.Entity target)
//  {
//    if (target == null) {
//      return false;
//    }
//    this.fireballController.fireFireball(target);
//    
//    return true;
//  }
//  
//  public boolean spitFireBallOnTarget(Location location)
//  {
//    if (location == null) {
//      return false;
//    }
//    this.fireballController.fireFireballOnLocation(location);
//    
//    return true;
//  }
//  
//  public void a(NBTTagCompound compound)
//  {
//    super.a(compound);
//  }
//  
//  public void b(NBTTagCompound compound)
//  {
//    super.b(compound);
//  }
//  
//  public void remove()
//  {
//    getBukkitEntity().remove();
//  }
//  
//  public int getExpReward()
//  {
//    return 100;
//  }
//  
//  public Location getLocation()
//  {
//    return getBukkitEntity().getLocation();
//  }
//  
//  public boolean spawn()
//  {
//    return spawnCraftBukkit();
//  }
//  
//  private boolean spawnCraftBukkit()
//  {
//    World world = ((CraftWorld)getLocation().getWorld()).getHandle();
//    Chunk chunk = getLocation().getChunk();
//    if (!chunk.isLoaded()) {
//      getLocation().getChunk().load();
//    }
//    if (!world.addEntity(this)) {
//      return false;
//    }
//    setPosition(this.locX, this.locY, this.locZ);
//    
//    return true;
//  }
//  
//  public Location getHomeLocation()
//  {
//    return this.targetController.getHomeLocation();
//  }
//  
//  public int getID()
//  {
//    return getBukkitEntity().getEntityId();
//  }
//  
//  public boolean isFlyingHome()
//  {
//    return this.targetController.isFlyingHome();
//  }
//  
//  public void setTarget(LivingEntity entity)
//  {
//    EntityLiving convertedEntity = (EntityLiving)((CraftEntity)entity).getHandle();
//    this.targetController.forceTarget(convertedEntity);
//  }
//  
//  public org.bukkit.entity.Entity getTarget()
//  {
//    net.minecraft.server.v1_7_R3.Entity entity = this.targetController.getCurrentTarget();
//    if (entity == null) {
//      return null;
//    }
//    return entity.getBukkitEntity();
//  }
//  
//  public int getLogicCalls()
//  {
//    int calls = this.logicCall;
//    this.logicCall = 0;
//    return calls;
//  }
//  
//  public void goToLocation(Location location)
//  {
//    this.targetController.setNewTarget(location, true);
//  }
//  
//  public void changeUUID(UUID uID)
//  {
//    this.uniqueID = UUID.fromString(uID.toString());
//  }
//  
//  public UUID getUUID()
//  {
//    return getBukkitEntity().getUniqueId();
//  }
//  
//  public Location getForceLocation()
//  {
//    return this.targetController.getForceGoTo();
//  }
//  
//  public void addEnemy(org.bukkit.entity.Entity entity)
//  {
//    CraftEntity craftEntity = (CraftEntity)entity;
//    
//    this.targetController.addTarget((EntityLiving)craftEntity.getHandle());
//  }
//  
//  public boolean isInRange(Location loc, double range)
//  {
//    return this.targetController.isInRange(loc, range);
//  }
//  
//  public Map<String, Float> getPlayerDamageDone()
//  {
//    return this.dragonHealthController.getPlayerDamage();
//  }
//  
//  public String getLastPlayerAttacked()
//  {
//    return this.dragonHealthController.getLastPlayerAttacked();
//  }
//  
//  public float getDamageByPlayer(String player)
//  {
//    return this.dragonHealthController.getDamageByPlayer(player);
//  }
//  
//  public double getMeeleDamage()
//  {
//    return 10.0D;
//  }
//  
//  public boolean isHostile()
//  {
//    return true;
//  }
//  
//  public void forceFlyHome(boolean flyingHome)
//  {
//    this.targetController.forceFlyingHome(flyingHome);
//  }
//  
//  public void setNewHome(Location newHomeLocation)
//  {
//    this.targetController.setHomeLocation(newHomeLocation);
//  }
//  
//  public List<net.minecraft.server.v1_7_R3.Entity> getAllTargets()
//  {
//    return this.targetController.getAllCurrentTargets();
//  }
//  
//  public Location getTargetLocation()
//  {
//    return this.targetController.getTargetLocation();
//  }
//  
//  public FireballController getFireballController()
//  {
//    return this.fireballController;
//  }
//  
//  public void setFireballController(FireballController fireballController)
//  {
//    this.fireballController = fireballController;
//  }
//  
//  public TargetController getTargetController()
//  {
//    return this.targetController;
//  }
//  
//  public void setTargetController(TargetController targetController)
//  {
//    this.targetController = targetController;
//  }
//  
//  public DragonHealthController getDragonHealthController()
//  {
//    return this.dragonHealthController;
//  }
//  
//  public void setDragonHealthController(DragonHealthController dragonHealthController)
//  {
//    this.dragonHealthController = dragonHealthController;
//  }
//  
//  public DragonMoveController getDragonMoveController()
//  {
//    return this.dragonMoveController;
//  }
//  
//  public void setDragonMoveController(DragonMoveController dragonMoveController)
//  {
//    this.dragonMoveController = dragonMoveController;
//  }
//  
//  public static int broadcastedError = 0;
//  private int logicCall;
//  protected FireballController fireballController;
//  protected TargetController targetController;
//  protected DragonHealthController dragonHealthController;
//  protected DragonMoveController dragonMoveController;
//  protected boolean doNothingLock;
//  protected Vector oldSpeed;
//  protected Vector oldTarget;
//}
//
////
///* Location:           C:\temp\Gods.jar//
// * Qualified Name:     com.dogonfire.gods.LimitedEnderDragon//
// * JD-Core Version:    0.7.0.1//
// */