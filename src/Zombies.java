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
	Label moveLabel;
	
	ArrayList<Player> players;
	
	int currentPlayer;
	int currentStage;
	
	public void init() {
		availableTiles = new ArrayList<Tile>();
		
		initTiles();
		
		map = new ArrayList<Tile>();
		map.add(availableTiles.remove(0));
		
		shuffleTiles();
		
		players = new ArrayList<Player>();
		Player aPlayer = new Player(this,map.get(0).pp.get(1));
		players.add(aPlayer);
//		aPlayer = new Player(this,map.get(0).pp.get(1));
//		players.add(aPlayer);
//		aPlayer = new Player(this,map.get(0).pp.get(1));
//		players.add(aPlayer);
//		aPlayer = new Player(this,map.get(0).pp.get(1));
//		players.add(aPlayer);
//		aPlayer = new Player(this,map.get(0).pp.get(1));
//		players.add(aPlayer);
//		aPlayer = new Player(this,map.get(0).pp.get(1));
//		players.add(aPlayer);
		
		map.get(0).pp.get(1).players.addAll(players);

		currentPlayer = 0;
		currentStage = -1;
		nextStage();
		
		debug = false;
		
		circle = new Image("res/images/circle.png");
		circle.setDimensions(new Coord(42,42));
		
		edgeScrollMajor = 100;
		edgeScrollMinor = 10;
		
		panSpeedMajor = 5;
		panSpeedMinor = 10;
		
		
		
		
		createMenu();
	}
	
	public void shuffleTiles(){
		Tile helipad = availableTiles.remove(availableTiles.size()-1);
		ArrayList<Tile> newTiles = new ArrayList<Tile>();
		while(availableTiles.size()>0){
			newTiles.add(availableTiles.remove((int)(Math.random()*availableTiles.size())));
		}
		newTiles.add((int)(Math.random()*(newTiles.size()/2))+(newTiles.size()/2), helipad);
		for(Tile t: newTiles){
			System.out.println(t.name);
		}
		availableTiles = newTiles;
	}
	
	public void nextTurn(){
		System.out.println("Next turn");
		currentPlayer++;
		if(currentPlayer>=players.size()){
			currentPlayer = 0;
		}
	}
	
	public void nextStage(){
		currentStage++;
		if(currentStage==7){
			nextTurn();
			currentStage=0;
		}
		System.out.println(currentStage+" current stage");
		switch(currentStage){
		case 0:
			if(availableTiles.size()>0){
				placingTile = true;
				inHand = availableTiles.remove(0);
				break;
			}else{
				placingTile = false;
				nextStage();
				break;
			}
		case 1:
			if(players.get(currentPlayer).pp.zed!=null){
				break;
			}
			nextStage();
			break;
		case 2:
			if(players.get(currentPlayer).cards.size()<3){
				players.get(currentPlayer).pickUpCards();
				nextStage();
				break;
			}
			nextStage();
			break;
		case 3:
			players.get(currentPlayer).rollForMovement();
			nextStage();
			break;
		case 4:
			players.get(currentPlayer).updateLabel();
			break;
		case 5:
			nextStage();
			break;
		case 6:
			nextStage();
			break;
		}
	}
	
	public void createMenu(){
		footerMenu = new Menu();
		footerMenu.setColor(Color.BLACK);
		footerMenu.setDimensions(new Coord(windowDimensions.getX(),40));
		footerMenu.setVisible(true);
		
		tileLabel = new Label("Awaiting input.",new Coord(1,2));
		tileLabel.setDimensions(new Coord(1,20));
		tileLabel.setSize(Size.MATCH_PARENT_WIDTH);
		tileLabel.setVisible(true);
		
		moveLabel = new Label("Awaiting input.",new Coord(1,2));
		moveLabel.setDimensions(new Coord(1,20));
		moveLabel.setSize(Size.MATCH_PARENT_WIDTH);
		moveLabel.setVisible(true);
		
		footerMenu.add(moveLabel);
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
		switch(currentStage){
		case 0:
			break;
		case 1:
			if(players.get(currentPlayer).pp.zed!=null){
				players.get(currentPlayer).combat();
			}else{
				nextStage();
			}
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		}
		
	}

	public boolean addTile(Tile t, boolean actuallyPlace){
		//Must check each edge.  Every edge must match or be empty
		Tile top = getTileFromPos(new IntCoord((int)t.pos.getX(),(int)t.pos.getY()+1));
		Tile right = getTileFromPos(new IntCoord((int)t.pos.getX()+1,(int)t.pos.getY()));
		Tile bottom = getTileFromPos(new IntCoord((int)t.pos.getX(),(int)t.pos.getY()-1));
		Tile left = getTileFromPos(new IntCoord((int)t.pos.getX()-1,(int)t.pos.getY()));

	
		
		if(top==null&&right==null&&bottom==null&&left==null){ // Adjacent
//			System.out.println("New tile must be adjacent to an existing tile! @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
			return false;
		}else if(getTileFromPos(t.pos)!=null){	// not overlapping
//			System.out.println("Tile cannot overlap existing tile! @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
			return false;
		}else{	// Roads line up
			boolean valid = false;
			if(top!=null){
				if(t.top!=top.bottom){
//					System.out.println("Cannot connect to top tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
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
//					System.out.println("Cannot connect to right tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
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
//					System.out.println("Cannot connect to bottom tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
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
//					System.out.println("Cannot connect to left tile. @ x="+(int)t.pos.getX()+" y="+(int)t.pos.getY());
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
			glTranslated(0,windowDimensions.getY()/2-20,0);
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
			case KEY_F2:
				nextStage();
				break;
			case KEY_LSHIFT:
				players.get(currentPlayer).spendBulletLoseLife = -1;
				break;
			case KEY_RSHIFT:
				players.get(currentPlayer).spendBulletLoseLife = 1;
				break;
			case KEY_UP:
				System.out.println(currentStage);
				if(currentStage==4){
					if(players.get(currentPlayer).moveUp())
						nextStage();
				}
				break;
			case KEY_RIGHT:
				System.out.println(currentStage);
				if(currentStage==4){
					if(players.get(currentPlayer).moveRight())
						nextStage();
				}
				break;
			case KEY_DOWN:
				System.out.println(currentStage);
				if(currentStage==4){
					if(players.get(currentPlayer).moveDown())
						nextStage();
				}
				break;
			case KEY_LEFT:
				System.out.println(currentStage);
				if(currentStage==4){
					if(players.get(currentPlayer).moveLeft())
						nextStage();
				}
				break;
			}
		}
		while(Mouse.next()){
			if(Mouse.getEventButton()==0&&Mouse.isButtonDown(0)){
				if(placingTile){
					if(addTile(inHand,addTile(inHand,false))){
						placingTile = false;
						nextStage();
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
