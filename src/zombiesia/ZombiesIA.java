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
	public String appreciation, commentaire, note;

	/**
	 * Construit un niveau de ZombiesIA
	 * @param niveau Le numéro du niveau à charger. Le niveau.txt correspondant doit exister.
	 */
	public ZombiesIA(int niveau) {
		try {
			this.niveau = new Niveau(niveau);
		} catch (InvalidValueException e) {
			System.err.printf(e.getMessage());
		}
	}	
	
	/**
	 * La méthode lançant la procédure de jeu.
	 * @return true si le joueur a gagné, false s'il a perdu
	 */
	public boolean play() {
		Scanner sc = new Scanner(System.in);
		String rep;
		char dir;
		while(true) {
			if(niveau.isDead()) return false;
			else if(niveau.isArrived()) return true;
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
	
	/**
	 * Représentation graphique du jeu en cours
	 * @return Chaine exposant le jeu
	 */
	public String toString() {
		return niveau.toString();
	}
	
	@SuppressWarnings("empty-statement")
	public static void main(String[] args) {
		ZombiesIA jeu = new ZombiesIA(1);
		String reponse;
/*		
		GroupeMessages.Intro.message();
		do {
			reponse = Messages.input("Que faire ?");
		}while(!Messages.reponse(Messages.TypeReponse.Note, reponse));
		GroupeMessages.Suite.message();
		
		Messages.reponse(Messages.TypeReponse.Niveau1, Boolean.toString(jeu.play()));
		GroupeMessages.ApresNiveau1.message();
		jeu = new ZombiesIA(2);
		Messages.reponse(Messages.TypeReponse.Niveau2, Boolean.toString(jeu.play()));
		jeu = new ZombiesIA(3);
		Messages.reponse(Messages.TypeReponse.Niveau3, Boolean.toString(jeu.play()));
		GroupeMessages.ApresNiveau3.message();
		jeu = new ZombiesIA(4);
		Messages.reponse(Messages.TypeReponse.Niveau4, Boolean.toString(jeu.play()));
		GroupeMessages.ApresNiveau4.message();
		jeu = new ZombiesIA(5);
		Messages.reponse(Messages.TypeReponse.Niveau5, Boolean.toString(jeu.play()));
		jeu = new ZombiesIA(6);
		Messages.reponse(Messages.TypeReponse.Niveau6, Boolean.toString(jeu.play()));
*/		jeu = new ZombiesIA(7);
		while(!Messages.reponse(Messages.TypeReponse.Niveau7, Boolean.toString(jeu.play()))) {
			jeu = new ZombiesIA(7);
		}
		GroupeMessages.Fin.message();
		do {
			reponse = Messages.input("Avez vous aimé ?");
		}while(!Messages.reponse(Messages.TypeReponse.NoteFin, reponse));
		jeu.appreciation = reponse;
		GroupeMessages.Commentaires.message();
		jeu.commentaire = Messages.input("Qu'en avez-vous pensé ?");
		GroupeMessages.NoteFin.message();
		jeu.note = Messages.input("Quelle gentille note pour ce projet ?");
		Skynet.send(jeu.appreciation, jeu.commentaire, jeu.note);
		if(jeu.note.equals("20"))
			Messages.intro("TROP D'HONNEUR ! Ne changez surtout pas de mentalité, c'est l'esprit !", "Merci d'avoir joué. Et merci de remonter ma moyenne.");
		else {
			Messages.intro("Interessant. Merci pour votre note ! Mais si jamais ça peut influer votre jugement...", "Je vous donne ce fromage !");
			System.out.println("         _--\"-.\n" +
"      .-\"      \"-.\n" +
"     |\"\"--..      '-.\n" +
"     |      \"\"--..   '-.\n" +
"     |.-. .-\".    \"\"--..\".\n" +
"     |'./  -_'  .-.      |\n" +
"     |      .-. '.-'   .-'\n" +
"     '--..  '.'    .-  -.\n" +
"          \"\"--..   '_'   :\n" +
"                \"\"--..   |\n" +
"                      \"\"-'");
			Messages.intro("Pourquoi un fromage me demanderez-vous... ?", "Parce qu'un fromage est toujours accompagné...", "D'un BON VINGT !");
			System.out.println("        _\n" +
"       |-|\n" +
"       |~|\n" +
"       |:|\n" +
"      .'.'.\n" +
"     /   ::\\\n" +
"     |_____|     __          _\n" +
"     |:.:;.|   <:__:>     .-'o\\\n" +
"     |_____|   \\  ::/  .o' O. o\\\n" +
"     |   ::|    '..'  |--o.--o--|\n" +
"     |   ;:|     ||   |._._o_._.|\n" +
"     \\_____/    .''.\n" +
"               '----'     ");
			Messages.intro("UN BON VIN ! AVEC LE FROMAGE ! LE VINGT, LA NOTE !", "...", "Excusez-moi. Me mettez pas de malus. S'il vous plaît.", "En espérant que vous vous êtes amusé. Merci d'avoir joué, et à bientôt !");
		}
	}
}