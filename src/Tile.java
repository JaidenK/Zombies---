import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import king.jaiden.util.*;
import static org.lwjgl.opengl.GL11.*;


public class Tile {
	Image img;
	int tileID,zeds,life,ammo;
	Zombies game;
	ArrayList<PlopPoint> pp;
	boolean top, right, bottom, left;
	IntCoord pos;
	int rotation = 0;
	String name;
	public Tile(Zombies game,String fileName){

		pp = new ArrayList<PlopPoint>();
		BufferedReader br = null;
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader("res/tiles/"+fileName));
			while((sCurrentLine=br.readLine())!=null){
				String[] x = sCurrentLine.split(":");
				if(x[0].equals("id")){
					tileID = Integer.parseInt(x[1]);
				}else if(x[0].equals("image")){
					img = new Image("res/images/"+x[1]);
					img.setDimensions(new Coord(256,256));
				}else if(x[0].equals("top")){
					if(Integer.parseInt(x[1])==1){
						top = true;
					}else{
						top = false;
					}
				}else if(x[0].equals("right")){
					if(Integer.parseInt(x[1])==1){
						right = true;
					}else{
						right = false;
					}
				}else if(x[0].equals("bottom")){
					if(Integer.parseInt(x[1])==1){
						bottom = true;
					}else{
						bottom = false;
					}
				}else if(x[0].equals("left")){
					if(Integer.parseInt(x[1])==1){
						left = true;
					}else{
						left = false;
					}
				}else if(x[0].equals("pp")){
					String[] pData = x[1].split(",");
					int[] pd = new int[8];
					for(int i = 0; i < pd.length; i++){
						pd[i] = Integer.parseInt(pData[i]);
					}
					PlopPoint p = new PlopPoint(new IntCoord(pd[1],pd[2]),pd[0],(pd[7]==1)?true:false);
					p.setConnectionIds(pd[3], pd[4], pd[5], pd[6]);
					pp.add(p);
				}else if(x[0].equals("zombies")){
					zeds = Integer.parseInt(x[1]);
				}else if(x[0].equals("life")){
					life = Integer.parseInt(x[1]);
				}else if(x[0].equals("ammo")){
					ammo = Integer.parseInt(x[1]);
				}else if(x[0].equals("name")){
					name = x[1];
				}
			}
		} catch (Exception e) {
			System.out.println("Can't make tile");
			e.printStackTrace();
		}
		this.game = game;
		this.pos = new IntCoord(0,0);
		
		for(PlopPoint p: pp){
			p.setConnections(
					(p.ti!=0)?pp.get(p.ti-1):null,
					(p.ri!=0)?pp.get(p.ri-1):null,
					(p.bi!=0)?pp.get(p.bi-1):null,
					(p.li!=0)?pp.get(p.li-1):null);
		}
		
	}
	
	public void spawnAmmo(){
		for(int i = 0; i < ammo; i++){
			int n = 0;
			while(true){
				int index = (int)(Math.random()*pp.size());
				if(!pp.get(index).street&&pp.get(index).ammo==null&&pp.get(index).life==null){
					pp.get(index).ammo = new Ammo(true);
					break;
				}
				n++;
				if(n>1000){
					System.out.println("Can't find spot for ammo.");
					break;
				}
			}
		}
	}

	public void spawnLife(){
		for(int i = 0; i < life; i++){
			int n = 0;
			while(true){
				int index = (int)(Math.random()*pp.size());
				if(!pp.get(index).street&&pp.get(index).ammo==null&&pp.get(index).life==null){
					pp.get(index).life = new Life(true);
					break;
				}
				n++;
				if(n>1000){
					System.out.println("Can't find spot for life.");
					break;
				}
			}
		}
	}
	
	public void spawnZeds(){
		for(int i = 0; i < zeds; i++){
			int n = 0;
			while(true){
				int index = (int)(Math.random()*pp.size());
				if(name.equals(" ")){
					if(pp.get(index).street&&pp.get(index).zed==null){
						pp.get(index).zed = new Zombie();
						break;
					}
				}else{
					if(!pp.get(index).street&&pp.get(index).zed==null){
						pp.get(index).zed = new Zombie();
						break;
					}
				}
				n++;
				if(n>1000){
					System.out.println("Can't find spot for zombie.");
					break;
				}
			}
		}
	}
	
	
	public void rotate(){
		rotation-=90;
		
		boolean fence = right;
		right = top;
		top = left;
		left = bottom;
		bottom = fence;
		
		for(PlopPoint p: pp){
			PlopPoint f = null;
			switch((int)p.pos.getX()){
			case 0:
				switch((int)p.pos.getY()){
				case 0:
					p.pos = new IntCoord(0,2);
					break;
				case 1:
					p.pos = new IntCoord(1,2);
					break;
				case 2:
					p.pos = new IntCoord(2,2);
					break;
				}
				break;
			case 1:
				switch((int)p.pos.getY()){
				case 0:
					p.pos = new IntCoord(0,1);
					break;
				case 1:
					p.pos = new IntCoord(1,1);
					break;
				case 2:
					p.pos = new IntCoord(2,1);
					break;
				}
				break;
			case 2:
				switch((int)p.pos.getY()){
				case 0:
					p.pos = new IntCoord(0,0);
					break;
				case 1:
					p.pos = new IntCoord(1,0);
					break;
				case 2:
					p.pos = new IntCoord(2,0);
					break;
				}
				break;
			}

			System.out.println(p.top);
			System.out.println(p.right);
			System.out.println(p.bottom);
			System.out.println(p.left);
			System.out.println();
			f = p.top;
			p.top = p.left; 
			p.left = p.bottom;
			p.bottom = p.right; 
			p.right = f; 
			System.out.println(p.top);
			System.out.println(p.right);
			System.out.println(p.bottom);
			System.out.println(p.left);
			System.out.println("---------------------");
		}
	}
	public void connect(Tile t, int direction){
		PlopPoint p0 = null, p1 = null;
		switch(direction){
		case 0: // top
			top = false;
			t.bottom = false;
			for(PlopPoint x: pp){
				if(x.pos.equals(new IntCoord(1,2)))
					p0 = x;
			}
			for(PlopPoint x: t.pp){
				if(x.pos.equals(new IntCoord(1,0)))
					p1 = x;
			}
			p0.top=p1;
			p1.bottom=p0;
			break;
		case 1: // right
			right = false;
			t.left = false;
			for(PlopPoint x: pp){
				if(x.pos.equals(new IntCoord(2,1)))
					p0 = x;
			}
			for(PlopPoint x: t.pp){
				if(x.pos.equals(new IntCoord(0,1)))
					p1 = x;
			}
			p0.right=p1;
			p1.left=p0;
			break;
		case 2: // bottom
			bottom = false;
			t.top = false;
			for(PlopPoint x: pp){
				if(x.pos.equals(new IntCoord(1,0)))
					p0 = x;
			}
			for(PlopPoint x: t.pp){
				if(x.pos.equals(new IntCoord(1,2)))
					p1 = x;
			}
			p0.bottom=p1;
			p1.top=p0;
			break;
		case 3: // left
			left = false;
			t.right = false;
			for(PlopPoint x: pp){
				if(x.pos.equals(new IntCoord(0,1)))
					p0 = x;
			}
			for(PlopPoint x: t.pp){
				if(x.pos.equals(new IntCoord(2,1)))
					p1 = x;
			}
			p0.left=p1;
			p1.right=p0;
			break;
		}
	}
	public void draw(){
		glPushMatrix();
			glTranslated(pos.getX()*256,pos.getY()*256,0);
			glRotated(rotation,0,0,1);
			img.draw();
			glRotated(-rotation,0,0,1);
			glTranslated(-128,-128,-1);
			for(PlopPoint p: pp){
				p.draw();
			}
		glPopMatrix();
	}
	public void drawDebug(){
		glPushMatrix();
			glTranslated(pos.getX()*256,pos.getY()*256,0);
			drawPP();
		glPopMatrix();
	}
	public void drawPP(){
		glTranslated(-128,-128,-1);
		for(PlopPoint p: pp){
			DrawUtil.setColor(Color.GREEN);
			glBegin(GL_QUADS);
			if(p.top != null){
				glVertex2d(p.pos.getX()*85+35,p.pos.getY()*85+42);
				glVertex2d(p.pos.getX()*85+49,p.pos.getY()*85+42);
				glVertex2d(p.pos.getX()*85+49,(p.pos.getY()+1)*85+42);
				glVertex2d(p.pos.getX()*85+35,(p.pos.getY()+1)*85+42);
			}
			if(p.bottom != null){
				glVertex2d(p.pos.getX()*85+49,p.pos.getY()*85+42);
				glVertex2d(p.pos.getX()*85+35,p.pos.getY()*85+42);
				glVertex2d(p.pos.getX()*85+35,(p.pos.getY()-1)*85+42);
				glVertex2d(p.pos.getX()*85+49,(p.pos.getY()-1)*85+42);
			}
			if(p.right != null){
				glVertex2d(p.pos.getX()*85+42,p.pos.getY()*85+49);
				glVertex2d(p.pos.getX()*85+42,p.pos.getY()*85+35);
				glVertex2d((p.pos.getX()+1)*85+42,p.pos.getY()*85+35);
				glVertex2d((p.pos.getX()+1)*85+42,p.pos.getY()*85+49);
			}
			if(p.left != null){
				glVertex2d((p.pos.getX()-1)*85+42,p.pos.getY()*85+49);
				glVertex2d((p.pos.getX()-1)*85+42,p.pos.getY()*85+35);
				glVertex2d(p.pos.getX()*85+42,p.pos.getY()*85+35);
				glVertex2d(p.pos.getX()*85+42,p.pos.getY()*85+49);
			}
			glEnd();

			glPushMatrix();
				if(!p.street){
					DrawUtil.setColor(Color.BLUE);
				}
				glTranslated(p.pos.getX()*85.333+42.6666667,p.pos.getY()*85.333+42.6666667,0);
				game.circle.draw();
			glPopMatrix();
		}
		DrawUtil.setColor(Color.YELLOW);
		if(top){
			glPushMatrix();
				glTranslated(1*85.333+42.6666667,3*85.333+42.6666667,0);
				game.circle.draw();
			glPopMatrix();
		}
		if(right){
			glPushMatrix();
				glTranslated(3*85.333+42.6666667,1*85.333+42.6666667,0);
				game.circle.draw();
			glPopMatrix();
		}
		if(bottom){
			glPushMatrix();
				glTranslated(1*85.333+42.6666667,-1*85.333+42.6666667,0);
				game.circle.draw();
			glPopMatrix();
		}
		if(left){
			glPushMatrix();
				glTranslated(-1*85.333+42.6666667,1*85.333+42.6666667,0);
				game.circle.draw();
			glPopMatrix();
		}
		DrawUtil.setColor(Color.WHITE);
	}
	public void tick(){
		for(PlopPoint p: pp){
			p.tick();
		}
	}
}
