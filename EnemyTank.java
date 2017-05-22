package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EnemyTank extends Tank{

	public EnemyTank(int xx, int yy, int d, Game p){
		super(xx, yy, d, p);
		mine = false;
		(new Auto()).start();
	}

	private class Auto extends Thread{
		public void run(){
			Random rand = new Random();
			try{Thread.sleep(rand.nextInt(400));}catch(Exception e){}
			while (alive)
			{
				dir = rand.nextInt(4);
				moving = true;

				fire();
				try{Thread.sleep(1000);}catch(Exception e){}
			}
		}
	}
}