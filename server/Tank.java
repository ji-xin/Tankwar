package game.server;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import game.client.*;

public class Tank{
	public int x,y; //position
	public int dir; //direction{left37, up38, right39, down40}
	boolean mine; //my tank or enemy
	public static final int size=30, gun_size=6, speed=3;
	Color army_green = new Color(77, 153, 0);
	Color dark_green = new Color(102, 51, 0);
	public boolean moving;
	public boolean alive;
	boolean server; //whether on server
	boolean colliding;//with walls or other tanks
	Game parent;
	Client cparent;
	boolean fire_ready;

	public Tank(int xx, int yy, int d, Client c){
		server = false;
		cparent = c;
		mine = true;
		x = xx;
		y = yy;
		dir = d;
		moving = false;
		alive = true;
		fire_ready = true;
	}

	public Tank(int xx, int yy, int d, Game p){
		server = true;
		mine = true;
		x = xx;
		y = yy;
		dir = d;
		parent = p;
		moving = false;
		alive = true;
		fire_ready = true;
	}


	public void draw(Graphics g){
		if (mine)
			g.setColor(army_green);
		else
			g.setColor(Color.red);
		g.fillRect(x, y, size, size);

		if (mine)
			g.setColor(dark_green);
		else
			g.setColor(Color.orange);
		switch(dir)
		{
			case 0:				
				g.fillRect(x-size/2, y-gun_size/2+size/2, size, gun_size);
				break;
			case 1:
				g.fillRect(x+size/2-gun_size/2, y-size/2, gun_size, size);
				break;
			case 2:
				g.fillRect(x+size/2, y-gun_size/2+size/2, size, gun_size);
				break;
			case 3:
				g.fillRect(x+size/2-gun_size/2, y+size/2, gun_size, size);
				break;
		}
	}

	public void pressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key)
		{
			case 37:
				dir = 0;
				moving = true;
				break;
			case 38:
				dir = 1;
				moving = true;
				break;
			case 39:
				dir = 2;
				moving = true;
				break;
			case 40:
				dir = 3;
				moving = true;
				break;

			case 32://fire
				fire();
				break;
		}
	}

	public void fire(){
		if (fire_ready)
		{
			Bullet bul=null;
			switch(dir)
			{
				case 0:
					bul = new Bullet(
						x-Tank.size/2,
						y+Tank.size/2-Tank.gun_size/2,
						dir, mine);
					break;
				case 1:
					bul = new Bullet(
						x+Tank.size/2-Tank.gun_size/2,
						y-Tank.size/2,
						dir, mine);
					break;
				case 2:
					bul = new Bullet(
						x+Tank.size*3/2,
						y+Tank.size/2-Tank.gun_size/2,
						dir, mine);
					break;
				case 3:
					bul = new Bullet(
						x+Tank.size/2-Tank.gun_size/2,
						y+Tank.size*3/2,
						dir, mine);
					break;
			}
			if (server)
			{
				if (mine)
					parent.cBullets.add(bul);
				else
					parent.enemyBullets.add(bul);
			}
			else
				cparent.myBullets.add(bul);
			(new FireWait()).start();
		}
	}

	private class FireWait extends Thread{
		public void run(){
			fire_ready = false;
			try{Thread.sleep(700);}catch(Exception e){}
			fire_ready = true;
		}
	}

	public void released(KeyEvent e){
		int key = e.getKeyCode();
		if (37<=key && key<=40)
			moving = false;
	}

	public boolean move(){
		if (moving && !this.out() && !colliding)//prevent it goes out or into the wall
			switch(dir)
			{
				case 0:
					x-=speed;
					return true;
				case 1:
					y-=speed;
					return true;
				case 2:
					x+=speed;
					return true;
				case 3:
					y+=speed;
					return true;
			}
		return false;
	}

	public boolean out(){
		if ((x<0 && dir==0) ||
			(x>Game.width-Tank.size && dir==2) ||
			(y<0 && dir==1) ||
			(y>Game.height-Tank.size && dir==3))//prevent it gets stuck in walls
			return true;

		return false;
	}

	public String show(){
		return String.valueOf(x)+"\t"+String.valueOf(y)+"\t"+String.valueOf(dir);
	}
}