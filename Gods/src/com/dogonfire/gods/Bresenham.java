package com.dogonfire.gods;

import java.util.LinkedList;
import java.util.Queue;
import org.bukkit.Location;
import org.bukkit.World;

public class Bresenham
{
	public static Queue<Location> line3D(Location a, Location b)
	{
		return line3D(a.getBlockX(), a.getBlockY(), a.getBlockZ(), b.getBlockX(), b.getBlockY(), b.getBlockZ(), a.getWorld());
	}

	public static Queue<Location> line3D(int startx, int starty, int startz, int endx, int endy, int endz, World world)
	{
		Queue result = new LinkedList();

		int dx = endx - startx;
		int dy = endy - starty;
		int dz = endz - startz;

		int ax = Math.abs(dx) << 1;
		int ay = Math.abs(dy) << 1;
		int az = Math.abs(dz) << 1;

		int signx = (int) Math.signum(dx);
		int signy = (int) Math.signum(dy);
		int signz = (int) Math.signum(dz);

		int x = startx;
		int y = starty;
		int z = startz;
		if (ax >= Math.max(ay, az))
		{
			int deltay = ay - (ax >> 1);
			int deltaz = az - (ax >> 1);
			for (;;)
			{
				result.offer(new Location(world, x, y, z));
				if (x == endx)
				{
					return result;
				}
				if (deltay >= 0)
				{
					y += signy;
					deltay -= ax;
				}
				if (deltaz >= 0)
				{
					z += signz;
					deltaz -= ax;
				}
				x += signx;
				deltay += ay;
				deltaz += az;
			}
		}
		if (ay >= Math.max(ax, az))
		{
			int deltax = ax - (ay >> 1);
			int deltaz = az - (ay >> 1);
			for (;;)
			{
				result.offer(new Location(world, x, y, z));
				if (y == endy)
				{
					return result;
				}
				if (deltax >= 0)
				{
					x += signx;
					deltax -= ay;
				}
				if (deltaz >= 0)
				{
					z += signz;
					deltaz -= ay;
				}
				y += signy;
				deltax += ax;
				deltaz += az;
			}
		}
		if (az >= Math.max(ax, ay))
		{
			int deltax = ax - (az >> 1);
			int deltay = ay - (az >> 1);
			for (;;)
			{
				result.offer(new Location(world, x, y, z));
				if (z == endz)
				{
					return result;
				}
				if (deltax >= 0)
				{
					x += signx;
					deltax -= az;
				}
				if (deltay >= 0)
				{
					y += signy;
					deltay -= az;
				}
				z += signz;
				deltax += ax;
				deltay += ay;
			}
		}
		return result;
	}
}

/*
 * Location: C:\temp\Gods.jar
 * 
 * Qualified Name: com.dogonfire.gods.Bresenham
 * 
 * JD-Core Version: 0.7.0.1
 */