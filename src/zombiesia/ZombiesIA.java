/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiesia;

import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import zombiesia.Messages.GroupeMessages;
import static zombiesia.Messages.intro;
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
		} catch (InvalidValueException e) {
			System.err.printf(e.getMessage());
		}
	}	
	
	public String toString() {
		return niveau.toString();
	}
	
	public static void main(String[] args) {
		ZombiesIA jeu = new ZombiesIA(1, 1);
		String reponse;
		
		//intro("JEU DE ZOMBIE SUPER LUDIQUE AVEC UNE INTELLIGENCE ARTIFICIELLE QUI DEPASSE L'ENTENDEMENT.",	"Vraiment, elle est révolutionnaire.", "L'algo A* pourrait limite faire le café, et faire du path-finding droit vers votre coeur.", "Mais vous avez un but bien précis, n'est-ce pas ? En effet, face à un jeu qui vous a déjà tant amusé, codé avec tant d'amour, vous n'avez qu'un seul choix...", "Qu'allez-vous donc faire ?", "(La bonne réponse est \"mettre un 20 d'office\")");
		GroupeMessages.Intro.message();
		do {
			reponse = Messages.input("Que faire ?");
		}while(!Messages.reponse(Messages.TypeReponse.Note, reponse));
		
		GroupeMessages.Suite.message();
		System.out.println(jeu);
	}
}
