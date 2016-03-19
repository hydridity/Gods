package com.dogonfire.gods;

import java.util.Random;

public class EndWorldConfig
{
  private final Gods plugin;
  private boolean regenOnStop;
  private boolean regenOnRespawn;
  private int actionOnRegen;
  private int respawnTimer;
  private int TASK_respawnTimerTask;
  private int xpRewardingType;
  private int xpReward;
  private int actualNbMaxEnderDragon;
  private int nbEnderDragon;
  private int nbMinEnderDragon;
  private int nbMaxEnderDragon;
  private int enderDragonHealth;
  private int preventPortals;
  private String regenMessage;
  private String[] respawnMessages;
  private String[] expMessage1;
  private String[] expMessage2;
  private int customEggHandling;
  private String eggMessage;
  private double enderDragonDamageMultiplier;
  private int nbEd;
  
  public EndWorldConfig(Gods plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean regenOnStop()
  {
    return this.regenOnStop;
  }
  
  public void setRegenOnStop(boolean regenOnStop)
  {
    this.regenOnStop = regenOnStop;
  }
  
  public boolean regenOnRespawn()
  {
    return this.regenOnRespawn;
  }
  
  public void setRegenOnRespawn(boolean regenOnRespawn)
  {
    this.regenOnRespawn = regenOnRespawn;
  }
  
  public int getActionOnRegen()
  {
    return this.actionOnRegen;
  }
  
  public void setActionOnRegen(int actionOnRegen)
  {
    this.actionOnRegen = actionOnRegen;
  }
  
  public int getRespawnTimer()
  {
    return this.respawnTimer;
  }
  
  public void setRespawnTimer(int respawnTimer)
  {
    this.respawnTimer = respawnTimer;
  }
  
  public int getNbMinEnderDragon()
  {
    return this.nbMinEnderDragon;
  }
  
  public void setNbMinEnderDragon(int nbMinEnderDragon)
  {
    this.nbMinEnderDragon = nbMinEnderDragon;
  }
  
  public int getNbMaxEnderDragon()
  {
    return this.nbMaxEnderDragon;
  }
  
  public void setNbMaxEnderDragon(int nbMaxEnderDragon)
  {
    this.nbMaxEnderDragon = nbMaxEnderDragon;
  }
  
  public int getRespawnTimerTask()
  {
    return this.TASK_respawnTimerTask;
  }
  
  public void setRespawnTimerTask(int respawnTimerTask)
  {
    this.TASK_respawnTimerTask = respawnTimerTask;
  }
  
  public int getXpRewardingType()
  {
    return this.xpRewardingType;
  }
  
  public void setXpRewardingType(int xpRewardingType)
  {
    this.xpRewardingType = xpRewardingType;
  }
  
  public int getXpReward()
  {
    return this.xpReward;
  }
  
  public void setXpReward(int xpReward)
  {
    this.xpReward = xpReward;
  }
  
  public int getActualNbMaxEnderDragon()
  {
    return this.actualNbMaxEnderDragon;
  }
  
  public void setActualNbEnderDragon(int actualNbEnderDragon)
  {
    this.actualNbMaxEnderDragon = actualNbEnderDragon;
  }
  
  public int getEnderDragonHealth()
  {
    return this.enderDragonHealth;
  }
  
  public void setEnderDragonHealth(int enderDragonHealth)
  {
    this.enderDragonHealth = enderDragonHealth;
  }
  
  public int getPreventPortals()
  {
    return this.preventPortals;
  }
  
  public void setPreventPortals(int preventPortals)
  {
    this.preventPortals = preventPortals;
  }
  
  public String getRegenMessage()
  {
    return this.regenMessage;
  }
  
  public void setRegenMessage(String regenMessage)
  {
    this.regenMessage = regenMessage;
  }
  
  public String[] getRespawnMessages()
  {
    return this.respawnMessages;
  }
  
  public void setRespawnMessages(String[] respawnMessages)
  {
    this.respawnMessages = respawnMessages;
  }
  
  public String[] getExpMessage1()
  {
    return this.expMessage1;
  }
  
  public void setExpMessage1(String[] expMessage1)
  {
    this.expMessage1 = expMessage1;
  }
  
  public String[] getExpMessage2()
  {
    return this.expMessage2;
  }
  
  public void setExpMessage2(String[] expMessage2)
  {
    this.expMessage2 = expMessage2;
  }
  
  public int getNbEnderDragon()
  {
    return this.nbEnderDragon;
  }
  
  public void setNbEnderDragon(int nbEnderDragon)
  {
    this.nbEnderDragon = nbEnderDragon;
  }
  
  public boolean newActualNumber()
  {
    if (this.nbMaxEnderDragon - this.nbMinEnderDragon < 0)
    {
      this.nbMinEnderDragon = 1;
      this.nbMaxEnderDragon = 1;
      this.actualNbMaxEnderDragon = 1;
      return false;
    }
    this.actualNbMaxEnderDragon = (this.nbMinEnderDragon + new Random().nextInt(this.nbMaxEnderDragon - this.nbMinEnderDragon + 1));
    return true;
  }
  
  public int getNbEd()
  {
    return this.nbEd;
  }
  
  public void setNbEd(int nbEd)
  {
    this.nbEd = nbEd;
  }
  
  public int getCustomEggHandling()
  {
    return this.customEggHandling;
  }
  
  public void setCustomEggHandling(int customEggHandling)
  {
    this.customEggHandling = customEggHandling;
  }
  
  public String getEggMessage()
  {
    return this.eggMessage;
  }
  
  public void setEggMessage(String eggMessage)
  {
    this.eggMessage = eggMessage;
  }
  
  public double getEnderDragonDamageMultiplier()
  {
    return this.enderDragonDamageMultiplier;
  }
  
  public void setEnderDragonDamageMultiplier(double enderDragonDamageMultiplier)
  {
    this.enderDragonDamageMultiplier = enderDragonDamageMultiplier;
  }
}


/* Location:           C:\temp\Gods.jar
 * Qualified Name:     com.dogonfire.gods.EndWorldConfig
 * JD-Core Version:    0.7.0.1
 */