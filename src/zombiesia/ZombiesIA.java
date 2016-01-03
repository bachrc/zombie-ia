/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiesia;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
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
		
		intro(40, "JEU DE ZOMBIE SUPER LUDIQUE AVEC UNE INTELLIGENCE ARTIFICIELLE QUI DEPASSE L'ENTENDEMENT.",	"Vraiment, elle est révolutionnaire.", "L'algo A* pourrait limite faire le café, et faire du path-finding droit vers votre coeur.");
		
		System.out.println(jeu);
	}
	
	private static void intro(int longueur, String... messages) {
		/*final String[] messages = {"JEU DE ZOMBIE SUPER LUDIQUE AVEC UNE INTELLIGENCE ARTIFICIELLE QUI DEPASSE L'ENTENDEMENT.",
			"Vraiment, elle est révolutionnaire.",
			"L'algo A* pourrait limite faire le café, et faire du path-finding droit vers votre coeur."
		};
		int longueur = 40; */
		try {
			for(String mess:messages) {
				String[] lines = WordUtils.wrap(mess, longueur, "\n", true).split("\n");
				System.out.println("--" + StringUtils.repeat(' ', longueur) + "--");
				for(String l:lines) 
					System.out.println("--" + StringUtils.center(l, longueur) + "--");

				System.out.println(StringUtils.repeat('-', longueur + 4));

				Thread.sleep(mess.length() * 45);
			}
		}
		catch(Exception e) {
			
		}
	}
}
