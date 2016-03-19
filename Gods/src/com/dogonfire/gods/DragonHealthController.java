//package com.dogonfire.gods;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Random;
//import java.util.Set;
//
//import net.minecraft.server.v1_8_R1.DamageSource;
//import net.minecraft.server.v1_8_R1.NBTTagCompound;
//
//import org.bukkit.Bukkit;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
//import org.bukkit.event.entity.EntityRegainHealthEvent;
//import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
//import org.bukkit.plugin.PluginManager;
//
//public class DragonHealthController
//{
//	private Gods plugin;
//	private LimitedEnderDragon dragon;
//	private Random random;
//	private HashMap<String, Float> damageDoneByPlayer;
//	private String lastPlayerAttacked;
//
//	public DragonHealthController(Gods plugin, LimitedEnderDragon dragon)
//	{
//		this.lastPlayerAttacked = "";
//
//		this.plugin = plugin;
//		this.dragon = dragon;
//		this.random = new Random();
//
//		this.damageDoneByPlayer = new HashMap();
//	}
//
//	public DragonHealthController(LimitedEnderDragon dragon, NBTTagCompound playerMapCompound)
//	{
//		for (Iterator i$ = playerMapCompound.c().iterator(); i$.hasNext();)
//		{
//			Object key = i$.next();
//			try
//			{
//				if ((key instanceof String))
//				{
//					String playerName = (String) key;
//
//					float damage = playerMapCompound.getFloat(playerName);
//					if ((playerName != null) && (!"".equals(playerName)))
//					{
//						if (this.damageDoneByPlayer.containsKey(playerName))
//						{
//							damage += ((Float) this.damageDoneByPlayer.get(playerName)).floatValue();
//						}
//						this.damageDoneByPlayer.put(playerName, Float.valueOf(damage));
//					}
//				}
//			}
//			catch (Exception localException)
//			{
//			}
//		}
//	}
//
//	public void checkRegainHealth()
//	{
//		if (this.dragon.bC != null)
//		{
//			if (this.dragon.bC.dead)
//			{
//				this.dragon.a(this.dragon.bq, DamageSource.explosion(null), 10.0F);
//
//				this.dragon.bC = null;
//			}
//			else if ((this.dragon.ticksLived % 10 == 0) && (this.dragon.getHealth() < this.dragon.getMaxHealth()))
//			{
//				EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.dragon.getBukkitEntity(), 1.0D, EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
//
//				this.dragon.world.getServer().getPluginManager().callEvent(event);
//				if (!event.isCancelled())
//				{
//					float newDragonHealth = (float) (this.dragon.getHealth() + event.getAmount());
//					this.dragon.setHealth(newDragonHealth);
//				}
//			}
//		}
//		if (this.random.nextInt(10) == 0)
//		{
//			float range = 32.0F;
//
//			List<Entity> list = this.dragon.world.a(EntityEnderCrystal.class, this.dragon.boundingBox.grow(range, range, range));
//
//			EntityEnderCrystal entityendercrystal = null;
//			double nearestDistance = 1.7976931348623157E+308D;
//			for (Entity entity : list)
//			{
//				double currentDistance = entity.e(this.dragon);
//				if (currentDistance < nearestDistance)
//				{
//					nearestDistance = currentDistance;
//					entityendercrystal = (EntityEnderCrystal) entity;
//				}
//			}
//			this.dragon.bC = entityendercrystal;
//		}
//	}
//
//	public void damageEntities(List<Entity> list)
//	{
//		for (int i = 0; i < list.size(); i++)
//		{
//			Entity entity = (Entity) list.get(i);
//			if ((entity instanceof EntityLiving))
//			{
//				if (!(entity instanceof EntityHuman))
//				{
//					EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(this.dragon.getBukkitEntity(), entity.getBukkitEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, this.dragon.getMeeleDamage());
//
//					Bukkit.getPluginManager().callEvent(damageEvent);
//					if (!damageEvent.isCancelled())
//					{
//						entity.getBukkitEntity().setLastDamageCause(damageEvent);
//						entity.damageEntity(DamageSource.mobAttack(this.dragon), (float) damageEvent.getDamage());
//					}
//				}
//				else
//				{
//					double damageDone = 10.0D;
//					entity.damageEntity(DamageSource.mobAttack(this.dragon), (float) damageDone);
//				}
//			}
//		}
//	}
//
//	public int mapHealth()
//	{
//		double actualHealth = this.dragon.getHealth();
//		double maxHealth = this.dragon.getMaxHealth();
//
//		double percentage = actualHealth / maxHealth;
//		int mappedHealth = (int) Math.floor(percentage * 200.0D);
//		if (mappedHealth < 0)
//		{
//			mappedHealth = 0;
//		}
//		return mappedHealth;
//	}
//
//	public void rememberDamage(DamageSource source, float damage)
//	{
//		if ((source.getEntity() instanceof EntityPlayer))
//		{
//			EntityPlayer player = (EntityPlayer) source.getEntity();
//			rememberDamage(player.getName(), damage);
//		}
//	}
//
//	public void rememberDamage(String player, float damage)
//	{
//		float newDmg = damage;
//		if (this.damageDoneByPlayer.containsKey(player))
//		{
//			newDmg += ((Float) this.damageDoneByPlayer.get(player)).floatValue();
//		}
//		this.damageDoneByPlayer.put(player, Float.valueOf(newDmg));
//		this.lastPlayerAttacked = player;
//	}
//
//	public Map<String, Float> getPlayerDamage()
//	{
//		return this.damageDoneByPlayer;
//	}
//
//	public String getLastPlayerAttacked()
//	{
//		return this.lastPlayerAttacked;
//	}
//
//	public float getDamageByPlayer(String player)
//	{
//		if (this.damageDoneByPlayer.containsKey(player))
//		{
//			return ((Float) this.damageDoneByPlayer.get(player)).floatValue();
//		}
//		return 0.0F;
//	}
//
//	public void recheckHealthNotOvercaped()
//	{
//		float dragonMaxHealth = this.dragon.getMaxHealth();
//		float dragonCurrentHealth = this.dragon.getHealth();
//		if (dragonCurrentHealth > dragonMaxHealth)
//		{
//			this.dragon.setHealth(dragonMaxHealth);
//		}
//	}
//
//	public NBTTagCompound generatePlayerDamageMapAsNBT()
//	{
//		NBTTagCompound compound = new NBTTagCompound();
//		for (Map.Entry entry : this.damageDoneByPlayer.entrySet())
//		{
//			String playerName = (String) entry.getKey();
//			float dmg = ((Float) entry.getValue()).floatValue();
//
//			compound.setFloat(playerName, dmg);
//		}
//		return compound;
//	}
//}
//
///*
// * Location: C:\temp\Gods.jar
// * 
// * Qualified Name: com.dogonfire.gods.DragonHealthController
// * 
// * JD-Core Version: 0.7.0.1
// */