import java.util.ArrayList;

import king.jaiden.util.*;


public class Player extends Entity{
	int life;
	int ammo;
	ArrayList<Card> cards;
	Color color;
	Zombies game;
	PlopPoint pp;
	int movement;
	ArrayList<Zombie> zeds;
	boolean combating = false;
	int spendBulletLoseLife = 0;
	int roll = 0;
	public Player(Zombies game,PlopPoint pp){
		super("res/images/player.png");
		img.setDimensions(new Coord(65,65));
		this.game = game;
		this.pp = pp;
		cards = new ArrayList<Card>();
		zeds = new ArrayList<Zombie>();
		setColor();
		life = 3;
		ammo = 3;
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
	}
	public void combat() {
		Zombie z = pp.zed;
		if(!combating){
			combating = true;
			roll = d6();
			System.out.println(roll+" CMOBAT ROLL");
			System.out.println(life+" lives");
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
		game.moveLabel.setLabel("You failed to kill the zombie with a roll of "+roll+".  Add bullets or lose a heart? "+ammo+" bullets remaining. "+life+" hearts remaining.");
		int requiredBullets = 4-roll;
		if(ammo<requiredBullets){
			life -= 1;
			combating = false;
			combat();
		}else if(spendBulletLoseLife!=0){
			if(spendBulletLoseLife<0){//spend
				ammo -= requiredBullets;
				zeds.add(pp.zed);
				pp.zed = null;
				combating = false;
			}else{//lose
				life -= 1;
				combating = false;
				combat();
			}
			spendBulletLoseLife = 0;
		}
	}
	
	public void pickUpCards() {
		// TODO Auto-generated method stub
		
	}
	
	public void updateLabel(){
		game.moveLabel.setLabel("Moves remaining: "+movement);
	}
	
	public boolean moveUp() {
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
