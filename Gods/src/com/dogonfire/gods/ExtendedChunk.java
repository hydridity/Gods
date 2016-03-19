//package com.dogonfire.gods;
//
//import org.bukkit.Chunk;
//import org.bukkit.World;
//
//public class ExtendedChunk
//{
//  private final int x;
//  private final int z;
//  private final String world;
//  private boolean hasToBeRegen;
//  private boolean isProtected;
//  
//  public ExtendedChunk(int x, int z, String world)
//  {
//    this.x = x;
//    this.z = z;
//    this.world = world;
//    this.hasToBeRegen = false;
//    this.isProtected = false;
//  }
//  
//  public ExtendedChunk(Chunk bukkitChunk)
//  {
//    this.x = bukkitChunk.getX();
//    this.z = bukkitChunk.getZ();
//    this.world = bukkitChunk.getWorld().getName();
//    this.hasToBeRegen = false;
//    this.isProtected = false;
//  }
//  
//  public boolean hasToBeRegen()
//  {
//    return this.isProtected ? false : this.hasToBeRegen;
//  }
//  
//  public void setToBeRegen(boolean value)
//  {
//    this.hasToBeRegen = (this.isProtected ? false : value);
//  }
//  
//  public boolean isProtected()
//  {
//    return this.isProtected;
//  }
//  
//  public void setProtected(boolean value)
//  {
//    this.isProtected = value;
//  }
//  
//  public int getX()
//  {
//    return this.x;
//  }
//  
//  public int getZ()
//  {
//    return this.z;
//  }
//  
//  public String getWorld()
//  {
//    return this.world;
//  }
//}
//
////
///* Location:           C:\temp\Gods.jar//
// * Qualified Name:     com.dogonfire.gods.ExtendedChunk//
// * JD-Core Version:    0.7.0.1//
// */