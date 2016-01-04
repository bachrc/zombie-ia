/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiesia;

import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author Yohann Bacha <y.bacha@live.fr>
 */
public class Messages {
	private static final int longueur = 40;
	
	public enum GroupeMessages {
		Intro("JEU DE ZOMBIE SUPER LUDIQUE AVEC UNE INTELLIGENCE ARTIFICIELLE QUI DEPASSE L'ENTENDEMENT.",	"Vraiment, elle est révolutionnaire.", "L'algo A* pourrait limite faire le café, et faire du path-finding droit vers votre coeur.", "Mais vous avez un but bien précis, n'est-ce pas ? En effet, face à un jeu qui vous a déjà tant amusé, codé avec tant d'amour, vous n'avez qu'un seul choix...", "Qu'allez-vous donc faire ?", "(La bonne réponse est \"mettre un 20 d'office\")"),
		Suite("Chevauchons le démon du jeu, l'amusement extrême est à portée de clic.", "Ne nous relâchons pas, et débutons le niveau 1 ! Vous en êtes capable, j'en suis sûr.", "Votre parfum ne semble vraiment pas plaire aux zombies... Débarassez-vous en, et je vous paie le café.","... Et un nouveau parfum.", "Ca vous semble sympa ?", "Alors ne vous relâchez pas, en avant !");
		
		public String[] messages;
		
		GroupeMessages(String... mess) {
			this.messages = mess;
		}
		
		public void message() {
			introArray(this.messages);
		}
	}
	
	public enum TypeReponse {
		Note,
		Volonte
	}
	
	public static String input(String intitule) {
		Scanner sc = new Scanner(System.in);
		String retour = "";
		System.out.print("-- " + intitule + " : ");
		retour = sc.nextLine();
		System.out.println(StringUtils.repeat('-', longueur + 4));
		
		return retour;
	}
	
	public static boolean reponse(TypeReponse type, String reponse) {
		switch(type) {
			case Note : 
				if(reponse.equals("mettre un 0 d'office")) intro("... Bon, nous avons juste démarré du mauvais pied hein ! Nul besoin de s'énerver, n'est-ce pas.. ?", "Nous avons démarré du mauvais pied. Prenons un café, et oublions toute forme de corruption. Tout se passera bien. Un nuage sur votre café ?");
				else if(reponse.equalsIgnoreCase("mettre un 20 d'office")) intro("Oh voyons, ce n'est pas très fair-play de mettre un 20 d'office alors que vous n'avez pas même pas joué au jeu, haha !", "Jouez d'abord quelques niveaux, et après nous en reparlerons. Ohlala, mais que ces zombies sont intelligents..");
				else { intro("Hahaha, ce n'est pas la réponse attendue, allons ! Reprenez depuis le début : "); return false; }
				break;
		}
		return true;
	}
	
	public static void introArray(String[] messages) {
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
			System.err.print("Erreur : Opération interrompue.");
		}
	}
	
	public static void intro(String... messages) {
		introArray(messages);
	}
}
