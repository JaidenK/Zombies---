import java.util.ArrayList;

import king.jaiden.util.*;
import static org.lwjgl.opengl.GL11.*;


public class Player extends Entity{
	ArrayList<Ammo> ammo;
	ArrayList<Card> cards;
	Color color;
	Zombies game;
	PlopPoint pp;
	int movement;
	ArrayList<Zombie> zeds;
	boolean combating = false;
	int spendBulletLoseLife = 0;
	int roll = 0;
	ArrayList<Life> lives;
	PlopPoint ts;
	public Player(Zombies game,PlopPoint pp){
		super("res/images/player.png",true);
		img.setDimensions(new Coord(65,65));
		this.game = game;
		ts = pp;
		setColor();
		init();
	}
	public void setColor(){
		int i = 0;
		Color tmp = Color.GRAY;;
		while(true){
			i++;
			if(i>1000){
				System.out.println("Cannot color player");
				break;
			}
			switch((int)(Math.random()*6)){
			case 0:
				tmp = Color.RED;
				break;
			case 1:
				tmp = Color.ORANGE;
				break;
			case 2:
				tmp = Color.YELLOW;
				break;
			case 3:
				tmp = Color.GREEN;
				break;
			case 4:
				tmp = Color.BLUE;
				break;
			case 5:
				tmp = Color.VIOLET;
				break;
			}
			boolean bad = false;
			for(Player p: game.players){
				if(p.color==tmp){
					bad = true;
				}
			}
			if(!bad){
				break;
			}
			
		}
		color = tmp;
	}
	public void rollForMovement(){
		movement = d6()+100;
	}
	public void draw(){
		DrawUtil.setColor(color);
		super.draw();
		DrawUtil.setColor(Color.WHITE);
		glPushMatrix();
			glLoadIdentity();
			glTranslated(game.getWindowDimensions().getX()/2-40,-game.getWindowDimensions().getY()/2+40,0);
			glPushMatrix();
				if(lives.size()>0){
					for(int i = 0; i < lives.size(); i++){
						lives.get(i).draw();
						glTranslated(-lives.get(i).img.getDimensions().getX()-10,0,0);
					}
				}
			glPopMatrix();
			glTranslated(0,100,0);
			glPushMatrix();
			if(ammo.size()>0){
				for(int i = 0; i < ammo.size(); i++){
					ammo.get(i).draw();
					glTranslated(-ammo.get(i).img.getDimensions().getX()-10,0,0);
				}
			}
		glPopMatrix();
			
		glPopMatrix();
	}
	public void combat() {
		Zombie z = pp.zed;
		if(!combating){
			combating = true;
			roll = d6();
			System.out.println(roll+" CMOBAT ROLL");
			System.out.println(lives.size()+" lives");
			System.out.println(ammo+" bullets");
			game.moveLabel.setLabel("Combat Roll: "+roll);
			if(roll<4){
				System.out.println("ROLL FAILED");
				spendBulletLoseLife = 0;
				ammoOrLife();
			}else{
				zeds.add(z);
				pp.zed = null;
				combating = false;
			}
		}else{
			ammoOrLife();
		}
	}
	
	public void ammoOrLife(){
		game.moveLabel.setLabel("You failed to kill the zombie with a roll of "+roll+".  Add bullets or lose a heart?");
		int requiredBullets = 4-roll;
		if(ammo.size()<requiredBullets){
			if(loseLife()){
				combat();
			}
		}else if(spendBulletLoseLife!=0){
			if(spendBulletLoseLife<0){//spend
				for(int i = 0; i < requiredBullets; i++){
					ammo.remove(0);
				}
				zeds.add(pp.zed);
				pp.zed = null;
				combating = false;
			}else{
				if(loseLife()){
					combat();
				}
			}
			spendBulletLoseLife = 0;
		}
	}
	
	public boolean loseLife(){
		combating = false;
		if(lives.size()>1){
			lives.remove(0);
			return true;
		}else{
			die();
			return false;
		}
	}
	
	public void init(){
		pp = ts;
		cards = new ArrayList<Card>();
		zeds = new ArrayList<Zombie>();
		lives = new ArrayList<Life>();
		lives.add(new Life(false));
		lives.add(new Life(false));
		lives.add(new Life(false));
		ammo = new ArrayList<Ammo>();
		ammo.add(new Ammo(false));
		ammo.add(new Ammo(false));
		ammo.add(new Ammo(false));
	}
	
	public void die(){
		pp.players.remove(this);
		init();
		pp.players.add(this);
	}
	
	public void pickUpCards() {
		// TODO Auto-generated method stub
		
	}
	
	public void updateLabel(){
		game.moveLabel.setLabel("Moves remaining: "+movement);
	}
	
	public boolean moveUp() {
		if(combating){
			return false;
		}
		if(pp.top!=null){
			movement--;
			PlopPoint old = pp;
			pp = pp.top;
			old.players.remove(this);
			pp.players.add(this);
			if(pp.zed!=null)
				combat();
		}
		updateLabel();
		if(movement==0)
			return true;
		return false;
	}
	public boolean moveDown() {
		if(combating){
			return false;
		}
		if(pp.bottom!=null){
			movement--;
			PlopPoint old = pp;
			pp = pp.bottom;
			old.players.remove(this);
			pp.players.add(this);
			if(pp.zed!=null)
				combat();
		}
		updateLabel();
		if(movement==0)
			return true;
		return false;
	}
	public boolean moveRight() {
		if(combating){
			return false;
		}
		if(pp.right!=null){
			movement--;
			PlopPoint old = pp;
			pp = pp.right;
			old.players.remove(this);
			pp.players.add(this);
			if(pp.zed!=null)
				combat();
		}
		updateLabel();
		if(movement==0)
			return true;
		return false;
	}
	public boolean moveLeft() {
		if(combating){
			return false;
		}
		if(pp.left!=null){
			movement--;
			PlopPoint old = pp;
			pp = pp.left;
			old.players.remove(this);
			pp.players.add(this);
			if(pp.zed!=null)
				combat();
		}
		updateLabel();
		if(movement==0)
			return true;
		return false;
	}
}
