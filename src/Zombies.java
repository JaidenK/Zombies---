import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;

import king.jaiden.util.*;


public class Zombies extends ApplicationWindow {
	
	ArrayList<Tile> availableTiles;
	ArrayList<Tile> map;
	
	Image circle;
	
	boolean debug;
	boolean placingTile;
	
	Tile inHand;
	
	int edgeScrollMajor, edgeScrollMinor;
	double panSpeedMajor, panSpeedMinor;
	
	Menu footerMenu;
	Label tileLabel;
	
	public void init() {
		availableTiles = new ArrayList<Tile>();
		
		initTiles();
		
		map = new ArrayList<Tile>();
		map.add(availableTiles.remove(0));
		
		debug = false;
		
		circle = new Image("res/images/circle.png");
		circle.setDimensions(new Coord(42,42));
		
		edgeScrollMajor = 100;
		edgeScrollMinor = 10;
		
		panSpeedMajor = 5;
		panSpeedMinor = 10;
		
		
		if(availableTiles.size()>0){
			placingTile = true;
			inHand = availableTiles.remove(0);
		}else{
			placingTile = false;
		}
		
		createMenu();
	}
	
	public void createMenu(){
		footerMenu = new Menu();
		footerMenu.setColor(Color.BLACK);
		footerMenu.setDimensions(new Coord(windowDimensions.getX(),20));
		footerMenu.setVisible(true);
		
		tileLabel = new Label("Awaiting input.",new Coord(1,2));
		tileLabel.setSize(Size.MATCH_PARENT);
		tileLabel.setVisible(true);
		
		footerMenu.add(tileLabel);
	}
	
	public void initTiles(){
		DecimalFormat df = new DecimalFormat("00");
		for(int i = 0; i < 18; i++){
			availableTiles.add(new Tile(this,"tile"+df.format(i)+".txt"));
			if(i == 4 || i == 9 || i == 13 || i == 14 ){
				availableTiles.add(new Tile(this,"tile"+df.format(i)+".txt"));
				availableTiles.add(new Tile(this,"tile"+df.format(i)+".txt"));
				availableTiles.add(new Tile(this,"tile"+df.format(i)+".txt"));
			}
		}
	}
	
	public void tick(){
		super.tick();
		for(Tile t: map){
			t.tick();
		}
	}

	public boolean addTile(Tile t, boolean actuallyPlace){
		//Must check each edge.  Every edge must match or be empty
		Tile top = getTileFromPos(new IntCoord((int)t.pos.getX(),(int)t.pos.getY()+1));
		Tile right = getTileFromPos(new IntCoord((int)t.pos.getX()+1,(int)t.pos.getY()));
		Tile bottom = getTileFromPos(new IntCoord((int)t.pos.getX(),(int)t.pos.getY()-1));
		Tile left = getTileFromPos(new IntCoord((int)t.pos.getX()-1,(int)t.pos.getY()));

	
		
		if(top==null&&right==null&&bottom==null&&left==null){ // Adjacent
			System.out.println("New tile must be adjacent to an existing tile! @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
			return false;
		}else if(getTileFromPos(t.pos)!=null){	// not overlapping
			System.out.println("Tile cannot overlap existing tile! @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
			return false;
		}else{	// Roads line up
			boolean valid = false;
			if(top!=null){
				if(t.top!=top.bottom){
					System.out.println("Cannot connect to top tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
					return false;
				}
				if(t.top){
					valid = true;
					if(actuallyPlace)
						t.connect(top,0);
				}
				
			}
			if(right!=null){
				if(t.right!=right.left){
					System.out.println("Cannot connect to right tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
					return false;
				}
				if(t.right){
					valid = true;
					if(actuallyPlace)
						t.connect(right,1);
				}
			}
			if(bottom!=null){
				if(t.bottom!=bottom.top){
					System.out.println("Cannot connect to bottom tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
					return false;
				}
				if(t.bottom){
					valid = true;
					if(actuallyPlace)
						t.connect(bottom,2);
				}
			}
			if(left!=null){
				if(t.left!=left.right){
					System.out.println("Cannot connect to left tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
					return false;
				}
				if(t.left){
					valid = true;
					if(actuallyPlace)
						t.connect(left,3);
				}
			}
			
			if(!valid){
				System.out.println("No connecting roads.");
				return false;
			}
			if(actuallyPlace){
				map.add(t);
				t.spawnAmmo();
				t.spawnLife();
				t.spawnZeds();
			}
			return true;
		}
	}
	
	public Tile getTileFromPos(IntCoord pos){
		for(Tile t: map){
			if(t.pos.equals(pos))
				return t;
		}
		return null;
	}
	
	public void draw(){
		super.draw();
		for(Tile t: map){
			t.draw();
		}
		if(placingTile){
			drawPlacingTile();
		}
		drawGUI();
		if(debug){
			drawDebug();
		}
	}
	
	public void drawGUI(){
		glPushMatrix();
			glLoadIdentity();
			drawFooter();
		glPopMatrix();
	}
	
	public void drawFooter(){
		glPushMatrix();
			glTranslated(0,windowDimensions.getY()/2-10,0);
			footerMenu.draw();
		glPopMatrix();
	}
	
	public void drawPlacingTile(){
		inHand.pos = getPosUnderMouse();
		if(addTile(inHand,false)){
			glColor4d(0,1,0,0.5);
		}else{
			glColor4d(1,0,0,0.5);
		}
		inHand.draw();
		glColor4f(1,1,1,1);
	}
	
	public IntCoord getPosUnderMouse(){
		return new IntCoord((int)(Math.round((Mouse.getX()-windowDimensions.getX()/2-xPan)/256)),(int)(Math.round((Mouse.getY()-windowDimensions.getY()/2-yPan)/256)));
	}
	
	public void drawDebug(){
		for(Tile t: map){
			t.drawDebug();
		}
		if(placingTile){
			inHand.drawDebug();
		}
		glLoadIdentity();
		glTranslated(-windowDimensions.getX()/2,-windowDimensions.getY()/2,0);
		glBegin(GL_LINE_STRIP);
			glVertex2d(edgeScrollMajor,edgeScrollMajor);
			glVertex2d(edgeScrollMajor,windowDimensions.getY()-edgeScrollMajor);
			glVertex2d(windowDimensions.getX()-edgeScrollMajor,windowDimensions.getY()-edgeScrollMajor);
			glVertex2d(windowDimensions.getX()-edgeScrollMajor,edgeScrollMajor);
			glVertex2d(edgeScrollMajor,edgeScrollMajor);
		glEnd();
		glBegin(GL_LINE_STRIP);
			glVertex2d(edgeScrollMinor,edgeScrollMinor);
			glVertex2d(edgeScrollMinor,windowDimensions.getY()-edgeScrollMinor);
			glVertex2d(windowDimensions.getX()-edgeScrollMinor,windowDimensions.getY()-edgeScrollMinor);
			glVertex2d(windowDimensions.getX()-edgeScrollMinor,edgeScrollMinor);
			glVertex2d(edgeScrollMinor,edgeScrollMinor);
		glEnd();
	}
	
	public void input(){
		while(Keyboard.next()&&Keyboard.isKeyDown(Keyboard.getEventKey())){
			switch(Keyboard.getEventKey()){
			case KEY_F1:
				debugMode();
				break;
			}
		}
		while(Mouse.next()){
			if(Mouse.getEventButton()==0&&Mouse.isButtonDown(0)){
				if(placingTile){
					if(addTile(inHand,addTile(inHand,false))){
						if(availableTiles.size()>0){
							placingTile = true;
							inHand = availableTiles.remove(0);
						}else{
							placingTile = false;
						}
					}
				}
			}else if(Mouse.getEventButton()==1&&Mouse.isButtonDown(1)){
				if(placingTile){
					inHand.rotate();
				}
			}
		}
		edgeScroll();
		mouseOver();
	}
	
	public void mouseOver(){
		Tile t = getTileFromPos(getPosUnderMouse());
		if(t!=null){
			tileLabel.setLabel(t.name);
		}else{
			tileLabel.setLabel("");
		}
	}
	
	public void debugMode(){
		if(debug){
			debug = false;
		}else{
			debug = true;
		}
	}
	
	public void edgeScroll(){
		int mx = Mouse.getX();
		if(mx<edgeScrollMajor){
			if(mx<edgeScrollMinor){
				xPan += panSpeedMinor;
			}else{
				xPan += panSpeedMajor;
			}
		}else if(mx>windowDimensions.getX()-edgeScrollMajor){
			if(mx>windowDimensions.getX()-edgeScrollMinor){
				xPan -= panSpeedMinor;
			}else{
				xPan -= panSpeedMajor;
			}
		}
		int my = Mouse.getY();
		if(my<edgeScrollMajor){
			if(my<edgeScrollMinor){
				yPan += panSpeedMinor;
			}else{
				yPan += panSpeedMajor;
			}
		}else if(my>windowDimensions.getY()-edgeScrollMajor){
			if(my>windowDimensions.getY()-edgeScrollMinor){
				yPan -= panSpeedMinor;
			}else{
				yPan -= panSpeedMajor;
			}
		}
	}
	
	public Zombies(IntCoord intCoord, int i, String string, boolean b,
			int twoDimensional) {
		super(intCoord,i,string,b,twoDimensional);
	}
	public static void main(String[] args) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		new Zombies(new IntCoord((int)screenSize.getWidth(),(int)screenSize.getHeight()), 90, "Zombies!!!", true, ApplicationWindow.TWO_DIMENSIONAL);
	}
}
