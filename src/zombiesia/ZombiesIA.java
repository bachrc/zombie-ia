/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiesia;

import java.util.Scanner;
import zombiesia.Messages.GroupeMessages;
import zombiesia.Niveau.InvalidValueException;

/**
 *
 * @author Yohann Bacha <y.bacha@live.fr>
 */
public class ZombiesIA {
	
	private Niveau niveau;
	private int difficulte;

	public ZombiesIA(int difficulte, int niveau) {
		try {
			this.niveau = new Niveau(niveau);
			this.difficulte = difficulte;
		} catch (InvalidValueException e) {
			System.err.printf(e.getMessage());
		}
	}	
	
	public boolean play() {
		Scanner sc = new Scanner(System.in);
		String rep;
		char dir;
		while(true) {
			if(niveau.isArrived()) return true;
			else if(niveau.isDead()) return false;
			else {
				System.out.println(niveau);
				do {
					System.out.print("Direction : ");
					rep = sc.nextLine();
					dir = (rep.length() > 0 ? rep.charAt(0) : ' ');
				}while(dir != 'z' && dir != 's' && dir != 'q' && dir != 'd' && dir != ' ');
				
				this.niveau.movePlayer(dir);
				this.niveau.moveZombies();
				this.niveau.levelActions();
			}
		}
	}
	
	public String toString() {
		return niveau.toString();
	}
	
	public static void main(String[] args) {
		ZombiesIA jeu = new ZombiesIA(1, 1);
		String reponse;
/*		
		GroupeMessages.Intro.message();
		do {
			reponse = Messages.input("Que faire ?");
		}while(!Messages.reponse(Messages.TypeReponse.Note, reponse));
		GroupeMessages.Suite.message();
*/		
		Messages.reponse(Messages.TypeReponse.Niveau1, Boolean.toString(jeu.play()));
		
	}
}
