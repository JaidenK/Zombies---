import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import king.jaiden.util.*;


public class PlopPoint {
	IntCoord pos;
	PlopPoint top, right, bottom, left;
	int ti, ri, bi, li;
	int id;
	boolean street;
	Zombie zed;
	Life life;
	Ammo ammo;
	ArrayList<Player> players;
	PlopPoint(IntCoord pos,int id,boolean street){
		this.pos = pos;
		this.id = id;
		this.street = street;
		players = new ArrayList<Player>();
	}
	public void setConnections(PlopPoint top, PlopPoint right, PlopPoint bottom, PlopPoint left){
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
	public void setConnectionIds(int top, int right, int bottom, int left){
		ti = top;
		ri = right;
		bi = bottom;
		li = left;
	}
	public void draw(){
		glPushMatrix();
			glTranslated(pos.getX()*85+42,pos.getY()*85+42,0);
			if(life!=null)
				life.draw();
			if(ammo!=null)
				ammo.draw();
			if(zed!=null)
				zed.draw();
			for(Player p: players)
				p.draw();
		
		glPopMatrix();
	}
	public void tick(){
		if(life!=null)
			life.tick();
		if(ammo!=null)
			ammo.tick();
		if(zed!=null)
			zed.tick();
	}
}
