import king.jaiden.util.*;


public class Zombie extends Entity{
	public Zombie(){
		super("res/images/zombie0"+((int)(Math.random()*3)+1)+".png");
		img.setDimensions(new Coord(65,65));
	}
}
