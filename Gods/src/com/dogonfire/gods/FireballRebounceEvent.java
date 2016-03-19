package com.dogonfire.gods;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class FireballRebounceEvent
  extends EntityEvent
  implements Cancellable
{
  private boolean isCancelled;
  private RebounceReason reason;
  private Entity rebouncer;
  private static final HandlerList handlers = new HandlerList();
  
  public FireballRebounceEvent(Fireball fireball, RebounceReason reason, Entity rebouncer)
  {
    super(fireball);
    this.reason = reason;
    this.rebouncer = rebouncer;
    this.isCancelled = false;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public boolean isCancelled()
  {
    return this.isCancelled;
  }
  
  public void setCancelled(boolean cancelled)
  {
    this.isCancelled = cancelled;
  }
  
  public RebounceReason getReason()
  {
    return this.reason;
  }
  
  public Entity getRebouncer()
  {
    return this.rebouncer;
  }
  
  public static enum RebounceReason
  {
    MELEE_ATTACK,  ARROW;
  }
}


/* Location:           C:\temp\Gods.jar
 * Qualified Name:     com.dogonfire.gods.FireballRebounceEvent
 * JD-Core Version:    0.7.0.1
 */