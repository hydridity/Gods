package com.dogonfire.gods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class EndChunks
{
  private final Gods plugin;
  private final Map<String, ExtendedChunk> chunks;
  private final Set<World> endWorlds;
  
  public EndChunks(Gods plugin, World endWorld)
  {
    this.plugin = plugin;
    this.chunks = new HashMap();
    this.endWorlds = new HashSet();
    this.endWorlds.add(endWorld);
  }
  
  public void addChunk(ExtendedChunk chunk)
  {
    String coords = chunk.getWorld() + ';' + chunk.getX() + ';' + chunk.getZ();
    this.chunks.put(coords, chunk);
  }
  
  public ExtendedChunk addChunk(Chunk c)
  {
    String coords = c.getWorld().getName() + ';' + c.getX() + ';' + c.getZ();
    ExtendedChunk chunk = new ExtendedChunk(c);
    this.chunks.put(coords, chunk);
    return chunk;
  }
  
  public ExtendedChunk getChunk(String worldName, int x, int z)
  {
    String coords = worldName + ';' + x + ';' + z;
    return (ExtendedChunk)this.chunks.get(coords);
  }
  
  public ExtendedChunk getChunk(Chunk c)
  {
    return getChunk(c.getWorld().getName(), c.getX(), c.getZ());
  }
  
  public void regen(String worldName)
  {
    for (ExtendedChunk c : this.chunks.values()) {
      c.setToBeRegen(c.getWorld().equals(worldName));
    }
  }
  
  public Collection<ExtendedChunk> getIterableChunks()
  {
    return this.chunks.values();
  }
  
  public void save(File f_endChunks)
  {
    List coords = new ArrayList();
    for (String coord : this.chunks.keySet()) {
      coords.add(coord + ';' + ((ExtendedChunk)this.chunks.get(coord)).isProtected());
    }
    Collections.sort(coords);
    YamlConfiguration endChunks = new YamlConfiguration();
    endChunks.set("chunks", coords);
    try
    {
      if (!f_endChunks.exists()) {
        f_endChunks.createNewFile();
      }
      endChunks.save(f_endChunks);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void load(File f_endChunks)
  {
    if (f_endChunks.exists())
    {
      YamlConfiguration endChunks = new YamlConfiguration();
      try
      {
        endChunks.load(f_endChunks);
        if (endChunks.isList("chunks")) {
          for (String coord : endChunks.getStringList("chunks")) {
            try
            {
              String[] split = coord.split(";");
              String worldName = split[0];
              int x = Integer.parseInt(split[1]);
              int z = Integer.parseInt(split[2]);
              ExtendedChunk c = new ExtendedChunk(x, z, worldName);
              boolean isProtected = Boolean.parseBoolean(split[3]);
              c.setProtected(isProtected);
              this.chunks.put(worldName + ';' + x + ';' + z, c);
            }
            catch (Exception e)
            {
              this.plugin.getLogger().severe("ERROR loading endChunks.yml, invalid chunk description found : " + coord);
              e.printStackTrace();
            }
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}


/* Location:           C:\temp\Gods.jar
 * Qualified Name:     com.dogonfire.gods.EndChunks
 * JD-Core Version:    0.7.0.1
 */