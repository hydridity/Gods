package com.dogonfire.gods.tasks;

import com.dogonfire.gods.Gods;
import com.dogonfire.gods.QuestManager;

public class SetupGlobalQuestTask
  implements Runnable
{
  private Gods plugin;
  
  public SetupGlobalQuestTask(Gods instance)
  {
    this.plugin = instance;
  }
  
  public void run()
  {
    this.plugin.getQuestManager().GodSayNewGlobalQuest();
  }
}


/* Location:           C:\temp\Gods.jar
 * Qualified Name:     com.dogonfire.gods.tasks.SetupGlobalQuestTask
 * JD-Core Version:    0.7.0.1
 */