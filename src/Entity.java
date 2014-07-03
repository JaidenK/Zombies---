import king.jaiden.util.*;

import static org.lwjgl.opengl.GL11.*;

public class Entity {
	PlopPoint pp;
	Tile t;
	Image img;
	Coord offset;
	double rot;
	int ticksTilTilt = 0;
	boolean tilting = true;
	public Entity(String file){
		img = new Image(file);
		img.setDimensions(new Coord(42,42));
		rot = Math.random() * 90 - 45;
		offset = new Coord((Math.random()-0.5)*40,(Math.random()-0.5)*40);
	}
	public void tick(){
		if(tilting){
			if(ticksTilTilt<10){
				rot+=1;
				ticksTilTilt++;
			}else if(ticksTilTilt<20){
				rot-=1;
				ticksTilTilt++;
			}else{
				tilting = false;
				ticksTilTilt = (int)(Math.random()*360);
			}
		}else if(ticksTilTilt == 0){
			tilting = true;
		}else{
			ticksTilTilt--;
		}
	}
	public void draw(){
		glPushMatrix();
			DrawUtil.translate(offset);
			glRotated(rot,0,0,1);
			img.draw();
		glPopMatrix();
	}
}
