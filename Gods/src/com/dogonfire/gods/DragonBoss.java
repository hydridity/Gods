//package com.dogonfire.gods;
//
//import java.io.PrintStream;
//import java.util.Random;
//
//import net.minecraft.server.v1_8_R1.EntityEnderDragon;
//
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.entity.Entity;
//
//public class DragonBoss extends EntityEnderDragon
//{
//	private Gods plugin = null;
//	private String name = null;
//	public double toX = 50.0D;
//	public double toY = 50.0D;
//	public double toZ = 50.0D;
//	public int maxY;
//	public boolean finalmove = false;
//	public double XTick;
//	public double YTick;
//	public double ZTick;
//	public double distanceX;
//	public double distanceY;
//	public double distanceZ;
//	public double startX;
//	public double startY;
//	public double startZ;
//	public double speed = 0.5D;
//	Entity entity;
//
//	public DragonBoss(Gods plugin, String name, Location location, World world)
//	{
//		super((net.minecraft.server.v1_8_R1.World) world);
//
//		this.plugin = plugin;
//		this.name = name;
//
//		location.setY(world.getHighestBlockYAt(location.getBlockX(), location.getBlockZ()) + 20);
//
//		this.maxY = 100;
//
//		setPosition(location.getX(), location.getY(), location.getZ());
//
//		this.yaw = (location.getYaw() + 180.0F);
//		while (this.yaw > 360.0F)
//		{
//			this.yaw -= 360.0F;
//		}
//		while (this.yaw < 0.0F)
//		{
//			this.yaw += 360.0F;
//		}
//		if ((this.yaw < 45.0F) || (this.yaw > 315.0F))
//		{
//			this.yaw = 0.0F;
//		}
//		else if (this.yaw < 135.0F)
//		{
//			this.yaw = 90.0F;
//		}
//		else if (this.yaw < 225.0F)
//		{
//			this.yaw = 180.0F;
//		}
//		else
//		{
//			this.yaw = 270.0F;
//		}
//		setNewTarget();
//	}
//
//	public String getName()
//	{
//		return this.name;
//	}
//
//	public void setNewTarget()
//	{
//		this.startX = this.locX;
//		this.startY = this.locY;
//		this.startZ = this.locZ;
//
//		this.toX = (this.random.nextInt(2001) - 1000);
//		this.toZ = (this.random.nextInt(2001) - 1000);
//		if (this.random.nextInt(3) == 0)
//		{
//			//this.toY = (this.world.getHighestBlockYAt((int) this.toX, (int) this.toZ) + 10);
//		}
//		else
//		{
//			this.toY = 100.0D;
//		}
//		System.out.println("NewTarget " + this.toX + "," + this.toY + "," + this.toZ);
//
//		this.yaw = getCorrectYaw(this.toX, this.toZ);
//
//		setupMove();
//
//		this.finalmove = false;
//	}
//
//	//public DragonBoss(World world)
//	//{
//	//	//super(world);
//	//}
//
//	public void setupMove()
//	{
//		this.distanceX = (this.startX - this.toX);
//		this.distanceY = (this.startY - this.toY);
//		this.distanceZ = (this.startZ - this.toZ);
//
//		double tick = Math.sqrt(this.distanceX * this.distanceX + this.distanceY * this.distanceY + this.distanceZ * this.distanceZ) / this.speed;
//
//		this.XTick = (Math.abs(this.distanceX) / tick);
//		this.ZTick = (Math.abs(this.distanceZ) / tick);
//	}
//
//	public float getCorrectYaw(double targetx, double targetz)
//	{
//		if (this.locZ > targetz)
//		{
//			return (float) -Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz)));
//		}
//		if (this.locZ < targetz)
//		{
//			return (float) -Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz))) + 180.0F;
//		}
//		return this.yaw;
//	}
//
//	public void c()
//	{
//		this.plugin.getBossManager().dragonUpdate(this);
//	}
//
//	public void travel()
//	{
//		Entity entity = getBukkitEntity();
//
//		entity.getPassenger();
//
//		double myX = this.locX;
//		double myY = this.locY;
//		double myZ = this.locZ;
//		if (this.finalmove)
//		{
//			if ((int) this.locY > (int) this.toY)
//			{
//				myY -= this.speed;
//			}
//			else if ((int) this.locY < (int) this.toY)
//			{
//				myY += this.speed;
//			}
//			else
//			{
//				if (this.random.nextInt(100) == 0)
//				{
//					setNewTarget();
//					return;
//				}
//				this.plugin.getBossManager().detectPlayer(this);
//			}
//			System.out.println("Finalmove : " + myX + " " + myY + " " + myZ);
//
//			setPosition(myX, myY, myZ);
//			return;
//		}
//		if ((int) this.locY < this.maxY)
//		{
//			myY += this.speed;
//		}
//		if (myX < this.toX)
//		{
//			myX += this.XTick;
//		}
//		else
//		{
//			myX -= this.XTick;
//		}
//		if (myZ < this.toZ)
//		{
//			myZ += this.ZTick;
//		}
//		else
//		{
//			myZ -= this.ZTick;
//		}
//		if ((Math.abs(myZ - this.toZ) < 50.0D) && (Math.abs(myX - this.toX) < 50.0D))
//		{
//			this.finalmove = true;
//		}
//		System.out.println("Normal move : " + myX + " " + myY + " " + myZ);
//
//		setPosition(myX, myY, myZ);
//	}
//}
//
///*
// * Location: C:\temp\Gods.jar
// * 
// * Qualified Name: com.dogonfire.gods.DragonBoss
// * 
// * JD-Core Version: 0.7.0.1
// */