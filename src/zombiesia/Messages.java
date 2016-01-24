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
 * Petit moteur d'affichage dynamique de messages afin de rendre le tout un petit peu plus convivial.
 * 
 * @author Yohann Bacha <y.bacha@live.fr>
 */
public class Messages {
	private static final int longueur = 50;
	
	/**
	 * Groupes de Messages regroupant une série de 
	 * messages à afficher à la suite
	 */
	public enum GroupeMessages {
		Intro("JEU DE ZOMBIE SUPER LUDIQUE AVEC UNE INTELLIGENCE ARTIFICIELLE QUI DEPASSE L'ENTENDEMENT.",	"Vraiment, elle est révolutionnaire.", "L'algo A* pourrait limite faire le café, et faire du path-finding droit vers votre coeur.", "D'ailleurs, ce jeu a déjà appliqué l'algorithme A* vers votre coeur, et y est arrivé en un temps record, tant il est bien programmé.", "Qu'allez-vous donc faire ?", "(La bonne réponse est \"mettre un 20 d'office\")"),
		Suite("Chevauchons le démon du jeu, l'amusement extrême est à portée de clic.", "Ne nous relâchons pas, et débutons le niveau 1 ! Vous en êtes capable, j'en suis sûr.", "Votre parfum ne semble vraiment pas plaire aux zombies... Débarassez-vous en, et je vous paie le café.","... Et un nouveau parfum.", "Ca vous semble sympa ?", "Alors ne vous relâchez pas, en avant !", "Note : Pour vous déplacer, vous pouvez utiliser les touches zqsd , comme sur un jeu lambda."),
		ApresNiveau1("Oui, non, vous avez raison, ne soyons tout de même pas trop intimes...", "Vous êtes l'autorité tout puissante qui décidera de mon pauvre avenir de développeur. Je me dois d'être respectueux à votre égard.", "Et gentil. C'est très important.", "Je m'égare ! Vous voulez aucun doute continuer votre aventure et constater par vous même l'intelligence de zombies férus de cervelle.", "N'attendons pas plus, en avant !"),
		ApresNiveau3("Je vous offre un niveau à peu près normal. La plupart des zombies sont déjà passés, il n'en reste plus beaucoup ici...", "Vous ne plaisez peut-être pas aux femmes, mais les zombies semblent vous aimer. C'est rassurant, un peu."),
		ApresNiveau4("La voilà la surprise : Des EXPLOSIFS ! De la BONNE TNT bien lourde des familles. Vous en pensez quoi ? Si vous marchez dessus, ou qu'un zombie aussi... C'est la fin pour lui ou vous !", "A vous de l'utiliser à votre avantage, n'est-ce pas.", "En effet, un zombie est con, il peut marcher dessus. Mais il a un petit sixième sens quand même. Alors attention.", "Tentez seulement de résoudre ce niveau sans explosifs."),
		Fin("Votre gloire virtuelle est à son apogée, et tout le monde va vous suivre sur Twitter.", "Vous êtes désormais beau, avez de l'argent, et vous êtes alturiste.", "Très altruiste.", "Je peux donc compter compter sur vous pour avoir une certaine bonne note, n'est-ce pas ? (oui/non)"),
		Commentaires("Bon, oublions tout ça ! Au final, qu'avez-vous pensé du jeu ?", "Vous pouvez faire un petit pavé d'une ligne. Montrez-moi tout votre amour.", "Les livres d'or ne sont pas encore morts !"),
		NoteFin("Hmm... Interessant.", "Enfin voilà, vous avez terminé ce jeu. Ici s'achève la fin du périple.", "~THANK YOU FOR PLAYING~", "Mais avant ça, une toute dernière chose. Toute dernière.", "Quelle note sur vingt, au jugé, attriburiez-vous à ce jeu ? Allez. Soyez honnête. Je ne pleurerai pas.", "Pas beaucoup.", "Bon ok ça dépend.", "Tout dépend de vous.");
		
		
		public String[] messages;
		
		/**
		 * Constructeur par défaut
		 * @param mess Autant de messages qu'il le faut à afficher
		 */
		GroupeMessages(String... mess) {
			this.messages = mess;
		}
		
		/**
		 * Affiche le message en instance
		 */
		public void message() {
			introArray(this.messages);
		}
	}
	
	/**
	 * Enum répertoriant les différents contextes de réponse.
	 */
	public enum TypeReponse {
		Note,
		NoteFin,
		Niveau1, Niveau2, Niveau3, Niveau4, Niveau5, Niveau6, Niveau7
	}
	
	/**
	 * Demande de manière optimisée graphiquement à l'utilisateur de rentrer une valeur.
	 * @param intitule Ce qui est affiché aant la saisie de texte
	 * @return La saisie de l'utilisateur
	 */
	public static String input(String intitule) {
		Scanner sc = new Scanner(System.in);
		String retour;
		System.out.print("-- " + intitule + " : ");
		retour = sc.nextLine();
		System.out.println(StringUtils.repeat('-', longueur + 4));
		
		return retour;
	}
	
	/**
	 * Cette méthode agit selon le contexte et la réponse
	 * @param type Le contexte dans lequel nous sommes
	 * @param reponse La réponse à analyser
	 * @return Si la réponse est celle que l'on attendait ou non
	 */
	public static boolean reponse(TypeReponse type, String reponse) {
		switch(type) {
			case Note : 
				if(reponse.equals("mettre un 0 d'office")) intro("... Bon, nous avons juste démarré du mauvais pied hein ! Nul besoin de s'énerver, n'est-ce pas.. ?", "Nous avons démarré du mauvais pied. Prenons un café, et oublions toute forme de corruption. Tout se passera bien. Un nuage sur votre café ?");
				else if(reponse.equalsIgnoreCase("mettre un 20 d'office")) intro("Oh voyons, ce n'est pas très fair-play de mettre un 20 d'office alors que vous n'avez pas même pas joué au jeu, haha !", "Jouez d'abord quelques niveaux, et après nous en reparlerons. Ohlala, mais que ces zombies sont intelligents..");
				else { intro("Hahaha, ce n'est pas la réponse attendue, allons ! Reprenez depuis le début."); return false; }
				break;
			case Niveau1 :
				if(reponse.equalsIgnoreCase("true")) intro("Impressionnant, votre dextérité quand vous avez fui ce zombie amorphe..", "Ne prends pas la grosse tête cependant. Oh, ça ne te dérange pas que l'on se tutoie ? Nous avons vécu pas mal de choses ensemble déjà...");
				else { intro("Quelle tristesse... Ce zombie fatigué, du PREMIER NIVEAU, dans la difficulté la plus faible, aura eu raison de vous.", "Avouez. C'était juste à but de test, n'est-ce pas ?",  "Genre \"Ohlala, qu'est-ce qu'il se passe si j'me fais toucher par ce zombiiie !\"", "Bon. Maintenant que votre curiosité est satisfaite, réessayez. Sans déconner."); System.exit(0); }
				break;
			case Niveau2 :
				if(reponse.equalsIgnoreCase("true")) { intro("... Vous savez que ce niveau était théoriquement impossible à réussir.", "Ca vous amuse de modifier les fichiers du jeu à votre convenance ?", "Ce n'est pas très Charlie pour le développeur vous savez...", "Eh ben tiens, je vais décidez de ne pas être charlie non plus. Pas de pitié ni pour les croissants, ni pour les k4k0rz de fichier jar."); System.exit(0); }
				else intro("Vous croyiez vraiment que j'allais être si bienveillant... ?", "Dans ce monde... C'est tuer où être tué.", "J'ai le contrôle ici. C'est mon jeu. Je l'ai programmé, vous y jouez, vous obéissez.", "Et dans le cas présent... Vous mourrez.", "Enfin, ce ne serait pas drôle de s'arrêter là. Je vous aime bien, aussi impuissant puissiez-vous être.", "Ne partez pas allons. Je promets d'être plus sympathique avec vous.", "Allons-y. Pénétrez le niveau 3. Et amusez-vous ! C'est le but !");
				break;
			case Niveau3 :
				if(reponse.equalsIgnoreCase("true")) intro("... Vous avez VRAIMENT réussi ce niveau ?", "Vous n'avez pas modifié le niveau directement dans le niveau, n'est-ce pas... ?", "... Vous savez quoi ? Je vais vous croire pour cette fois. Je vais croire en vos compétences pour cette fois...", "Pas touche aux fichiers du jeu, hein ?");
				else intro("ENORME BLAAAAAAAAAAGUE !", "Pardon, c'était trop tentant. Je suis gentil à partir de maintenant. Je vous assure.");
				break;
			case Niveau4 :
				if(reponse.equalsIgnoreCase("true")) intro("Pas mal. Vous voyez, c'est très amusant non ? Et ce n'est pas fini !", "En effet, je vous réserve une petite surprise, qui vous plaira, vu que vous vous débrouillez apparemment, désormais.");
				else intro("... Bon, vous n'êtes pas très doué pour ça, n'est-ce pas.", "Bon, je suis un maitre de jeu un peu trop gentil.", "\"MAIS EN FAIT, Le zombie qui vous a mangé N'AVAIT PAS FAIM ! Et en plus, c'est un traître ! Comme le clone dans Star Wars 7. Donc il vous amène à la sortie du niveau, quelle chance, haha !\"", "Voilà, vous pouvez continuer maintenant. On va pas perdre de temps. Car vient une petite surprise...");
				break;
			case Niveau5 :
				if(reponse.equalsIgnoreCase("true")) intro("... Bon, j'avoue, je suis impressionné que vous y soyez parvenu.", "Mais REGARDEZ maintenant comme vous pouvez vous faciliter la vie grâce à... LA TNT !");
				else intro("Haha, assez compliqué n'est-ce pas ? C'est normal. Avec 4 zombars qui vous en veulent...", "Peut-être aurez-vous moins de mal avec ce deus ex machina.");
				break;
			case Niveau6 :
				if(reponse.equalsIgnoreCase("true")) intro("C'est parfait, vous avez compris le principe ! Voici l'étape finale...", "Un niveau totalement normal !", "L'apogée de votre séjour !", "Un maximum de fun !", "Que votre cerveau reste intact. Je vous le souhaite de tout coeur...", "Après tout, vous en avez besoin pour me donner une bonne note. En avant.", "(normalement, là, une musique épique se joue, mais en console c'est pas très possible, alors voilà)");
				else { intro("Bon. Si vous commencez à perdre alors que les zombies ont presque de la dynamite dans la bouche et vous mourrez quand même...", "Bon. Recommencez le jeu, ré-entrainez vous, et revenez ensuite hm. Allez, à l'année prochaine."); System.exit(0); }
				break;
			case Niveau7 :
				if(reponse.equalsIgnoreCase("true")) intro("Et c'est LA VICTOIIIIIRE !", "Plus d'apocalypse zombie dans votre console, vous avez sauvé votre disque dur de la défragmentation. Vous êtes un héros.", "Tagada tsoin-tsoin.");
				else { intro("Vous êtes arrivé si loin, je vous DEFENDS D'ABANDONNER.", "C'est le bien de ma NOTE qui est en jeu. DEFENDEZ-VOUS !"); return false; }
				break;
			case NoteFin :
				if(reponse.equalsIgnoreCase("oui")) { intro("Ah ! C'est classieux ! Désolé d'avoir été si dur avec vous au départ.", "On prend vite la grosse tête quand on est le maître du jeu, n'est-ce pas ? Haha."); return true; }
				else if(reponse.equalsIgnoreCase("non")) { intro("Oh... Et qu'est-ce qui ne vous a pas plus ?", "Le fait que ce soit un jeu et que je ne vous ai pas, pour le coup, réellement offert de café ?"); return true; }
				else { intro("Vous ne répondez pas à ma question là... Vous avez bien aimé le jeu et l'IA ou non.. ?"); return false; }
		}
		return true;
	}
	
	/**
	 * Méthode affichant le message dynamiquement, en fonction de la longueur du message.
	 * @param messages Un tableau de messages à afficher à la suite
	 */
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
	
	/**
	 * Méthode affichant le message dynamiquement, en fonction de la longueur du message.
	 * @param messages Une liste de messages à afficher à la suite
	 */
	public static void intro(String... messages) {
		introArray(messages);
	}
}
