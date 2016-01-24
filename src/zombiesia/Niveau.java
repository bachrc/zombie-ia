
package zombiesia;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe principale gérant les évènements dans un niveau.
 * @author Yohann Bacha <y.bacha@live.fr>
 */
public class Niveau {

	private char[][] plateau;
	private int hauteur, largeur;
	private int xJoueur, yJoueur;
	private int xArrivee, yArrivee;
	private ArrayList<Zombie> zombies;
	private ArrayList<TNT> explosifs;
	private int difficulte;
	private int odorat;
	private boolean alive;

	/**
	 * Constructeur instanciant l'objet du niveau en chargeant le niveau renseigné
	 * @param numero Le numéro du niveau à charger dans le package niveaux
	 * @throws zombiesia.Niveau.InvalidValueException Renvoyée si le niveau possède une valeur invalide quelque part.
	 */
	public Niveau(int numero) throws InvalidValueException {
		this.zombies = new ArrayList<>();
		this.explosifs = new ArrayList<>();
		this.loadLevel(numero);
		this.alive = true;
	}

	/**
	 * Methode qui instancie l'objet courant avec le niveau passé en paramètre.
	 * Les fichiers texte servant de source pour la génération du niveau comportent toujours une ligne d'entête.
	 * <Hauteur-niveau> <Largeur-niveau> <Difficulte> <Odorat-zombie>
	 * @param numero Numéro du niveau à charger.
	 * @throws zombiesia.Niveau.InvalidValueException Renvoyée si une valeur quelconque est invalide, ou si le niveau n'existe pas.
	 */
	private void loadLevel(int numero) throws InvalidValueException {
		InputStream level = Niveau.class.getResourceAsStream("/niveaux/niveau" + numero + ".txt");
		if (level == null) {
			throw new InvalidValueException("Niveau inexistant.");
		}

		Scanner sc = new Scanner(level);
		this.hauteur = sc.nextInt();
		this.largeur = sc.nextInt();
		this.difficulte = sc.nextInt();
		this.odorat = sc.nextInt();

		this.plateau = new char[hauteur][largeur];
		sc.nextLine();
		int lineCount;
		for (lineCount = 0; lineCount < hauteur && sc.hasNextLine(); lineCount++) {
			char[] line = sc.nextLine().toCharArray();
			if (line.length != largeur) {
				throw new InvalidValueException("Largeur de la ligne " + (lineCount + 1) + " du niveau " + numero + " est de " + line.length + " au lieu de " + largeur);
			}

			for (int i = 0; i < line.length; i++) {
				switch (line[i]) {
					case '-':
						this.plateau[lineCount][i] = ' ';
						break;
					case '|':
						this.plateau[lineCount][i] = '|';
						break;
					case '_':
						this.plateau[lineCount][i] = '-';
						break;
					case 'Z':
						this.zombies.add(new Zombie(i, lineCount));
						this.plateau[lineCount][i] = ' ';
						break;
					case 'B':
						this.zombies.add(new Zombie(i, lineCount, true));
						this.plateau[lineCount][i] = ' ';
						break;
					case 'A':
						this.xArrivee = i;
						this.yArrivee = lineCount;
						this.plateau[lineCount][i] = ' ';
						break;
					case 'J':
						this.xJoueur = i;
						this.yJoueur = lineCount;
						this.plateau[lineCount][i] = ' ';
						break;
					case 'T':
						this.explosifs.add(new TNT(i, lineCount));
						this.plateau[lineCount][i] = ' ';
						break;
					default:
						throw new InvalidValueException("Caractère invalide.");
				}
			}

		}

		if (hauteur != lineCount) {
			throw new InvalidValueException("Nombre de lignes invalide : Hauteur renseignée : " + hauteur + ", hauteur actuelle : " + lineCount);
		}
	}

	/**
	 * Fait bouger le joueur dans la direction suggérée par le caractère en paramètre
	 * @param dir La direction dans laquelle le joueur doit se déplacer.
	 */
	public void movePlayer(char dir) {
		switch (dir) {
			case 'z':
				if (isCaseFree(xJoueur, yJoueur - 1, false)) 
					yJoueur--;

				break;
			case 's':
				if (isCaseFree(xJoueur, yJoueur + 1, false)) 
					yJoueur++;

				break;
			case 'q':
				if (isCaseFree(xJoueur - 1, yJoueur, false)) 
					xJoueur--;

				break;
			case 'd':
				if (isCaseFree(xJoueur + 1, yJoueur, false)) 
					xJoueur++;
				
				break;
		}
	}

	/**
	 * Méthode faisant bouger les zombies.
	 * Les zombies bloquants s'interposent entre le joueur et l'arrivée.
	 * Les autres se dirigent directement entre le joueur 
	 */
	public void moveZombies() {
		for (Zombie z : this.zombies) {
			if(z.estBloquant())
				z.block(xJoueur, yJoueur, xArrivee, yArrivee);
			else
				z.move(xJoueur, yJoueur);
		}
	}

	/**
	 * Supprime les explosifs aux coordonnées renseignées.
	 * @param x L'abscisse de la coordonnée
	 * @param y L'ordonnée de la coordonnée
	 */
	public void removeExplosive(int x, int y) {
		for (int i = 0; i < this.explosifs.size(); i++) 
			if (explosifs.get(i).x == x && explosifs.get(i).y == y) 
				this.explosifs.remove(i);

	}

	/**
	 * Renseigne si un zombie se trouve aux coordonnées renseignées.
	 * @param x L'abscisse des coordonnées
	 * @param y L'ordonnée des coordonnées
	 * @return Si un zombie se trouve ici ou non
	 */
	public boolean isZombieHere(int x, int y) {
		for (Zombie z : this.zombies) 
			if (z.x == x && z.y == y) 
				return true;

		return false;
	}

	/**
	 * Renseigne si un explosif se trouve aux coordonnées renseignées.
	 * @param x L'abscisse des coordonnées
	 * @param y L'ordonnée des coordonnées
	 * @return Si un explosif se trouve ici ou non
	 */
	public boolean isExplosiveHere(int x, int y) {
		for (TNT t : this.explosifs) 
			if (t.x == x && t.y == y) 
				return true;

		return false;
	}

	/**
	 * Renseigne si une case est libre de déplacement, en considérant que les murs et zombies sont un obstacle
	 * @param x L'abscisse de la case
	 * @param y L'ordonnée de la case
	 * @return Si la case est libre ou non
	 */
	public boolean isCaseFree(int x, int y) {
		return isCaseFree(x, y, true);
	}
	
	/**
	 * Renseigne si une case est libre de déplacement, en considérant que les murs sont des obstacles.
	 * @param x L'abcisse de la case
	 * @param y L'ordonnée de la case
	 * @param geneZombie Booléen pour savoir si l'on considère le zombie comme un obstacle ou non
	 * @return Si la case est libre ou non
	 */
	public boolean isCaseFree(int x, int y, boolean geneZombie) {
		if (x >= 0 && y >= 0 && x < largeur && y < hauteur) 
			return ((geneZombie ? !isZombieHere(x, y) : true) && plateau[y][x] == ' ');
		
		return false;
	}

	/**
	 * Renvoie si le joueur est arrivé à bon port, vivant.
	 * @return Si le joueur est arrivé vivant
	 */
	public boolean isArrived() {
		return (alive && xJoueur == xArrivee && yJoueur == yArrivee);
	}
	
	/**
	 * Renvoie si le joueur est encore vivant
	 * @return Si le joueur est vivant ou non
	 */
	public boolean isDead() {
		return !alive;
	}

	/**
	 * Execute toutes les actions du niveau une fois que le joueur et les zombies ont joué.
	 * Tue le joueur s'il est sur la même case qu'un zombie ou qu'un explosif
	 * Et les explosifs explosent sur les zombies, faisant se supprimer l'explosif et le zombie
	 */
	public void levelActions() {
		if (isZombieHere(xJoueur, yJoueur) || isExplosiveHere(xJoueur, yJoueur))
			alive = false;

		for (int i = 0; i < this.zombies.size(); i++) {
			if (isExplosiveHere(zombies.get(i).x, zombies.get(i).y)) {
				removeExplosive(zombies.get(i).x, zombies.get(i).y);
				this.zombies.remove(i--);
			}
		}
	}

	@Override
	public String toString() {
		String retour = "";

		for (int x = 0; x < largeur; x++)
			retour += "+--";
		
		retour += "+\n";

		for (int y = 0; y < hauteur; y++) {
			retour += "|";
			for (int x = 0; x < largeur; x++) {
				if (this.xJoueur == x && this.yJoueur == y) 
					retour += "J1";
				else if (this.xArrivee == x && this.yArrivee == y)
					retour += "AR";
				else if (isZombieHere(x, y))
					retour += "ZO";
				else if (isExplosiveHere(x, y))
					retour += "TN";
				else
					retour += "" + plateau[y][x] + plateau[y][x];
				
				retour += " ";
			}
			retour += "|\n";
		}

		for (int x = 0; x < largeur; x++) {
			retour += "+--";
		}
		retour += "+\n";
		
		return retour;
	}

	// ------------------------
	// Partie réservée au calcul du chemin
	// ------------------------
	
	/**
	 * Renvoie le chemin idéal entre les coordonnées de base et de cible en utilisant l'algorithme A*.
	 * Ici, les zombies sont considérées comme un obstacle
	 * @param xOrigine
	 * @param yOrigine
	 * @param xGoal
	 * @param yGoal
	 * @return Le Noeud cible qui possède récursivement des parents jusqu'à l'origine
	 */
	public Noeud chemin(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		return chemin(xOrigine, yOrigine, xGoal, yGoal, true);
	}
	
	/**
	 * Renvoie le chemin idéal entre les coordonnées de base et de cible en utilisant l'algorithme A*.
	 * @param xOrigine
	 * @param yOrigine
	 * @param xGoal
	 * @param yGoal
	 * @param obstacles Si les zombies doivent être considérés comme des obtacles ou non
	 * @return Le Noeud cible qui possède récursivement des parents jusqu'à l'origine
	 */
	public Noeud chemin(int xOrigine, int yOrigine, int xGoal, int yGoal, boolean obstacles) {
		ArrayList<Noeud> ferme = new ArrayList<>();
		ArrayList<Noeud> ouvert = new ArrayList<>();
		ArrayList<Noeud> voisins;

		ouvert.add(new Noeud(xOrigine, yOrigine, null));

		while (!ouvert.isEmpty()) {
			Noeud n = plusPetitNoeud(ouvert);
			ouvert.remove(n);
			ferme.add(n);

			voisins = getVoisins(n, (this.difficulte % 2 == 1), obstacles);

			for (Noeud v : voisins) {
				if (v.x == xGoal && v.y == yGoal) {
					return v;
				} else {
					if (getNoeudAt(ouvert, v.x, v.y) == null && getNoeudAt(ferme, v.x, v.y) == null) {
						ouvert.add(v);
					} else {
						Noeud actuel;
						if ((actuel = getNoeudAt(ferme, v.x, v.y)) != null) {
							if (actuel.f() > v.f()) {
								ferme.remove(actuel);
								ouvert.add(v);
							}
						} else if ((actuel = getNoeudAt(ouvert, v.x, v.y)) != null) {
							if (actuel.f() > v.f()) {
								ouvert.remove(actuel);
								ouvert.add(v);
							}
						}
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * Retourne l'ArrayList du chemin complet.
	 * Les zombies sont considérés comme un obstacle.
	 * @param xOrigine
	 * @param yOrigine
	 * @param xGoal
	 * @param yGoal
	 * @return Retourne la liste de toutes les étapes du chemin
	 */
	public ArrayList<Noeud> alChemin(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		return alChemin(xOrigine, yOrigine, xGoal, yGoal, true);
	}
	
	/**
	 * Retourne l'ArrayList du chemin complet.
	 * @param xOrigine
	 * @param yOrigine
	 * @param xGoal
	 * @param yGoal
	 * @param obstacles Si les zombies sont considérés comme des obstacles ou non.
	 * @return 
	 */
	public ArrayList<Noeud> alChemin(int xOrigine, int yOrigine, int xGoal, int yGoal, boolean obstacles) {
		ArrayList<Noeud> chemin = new ArrayList<>();
		Noeud n = chemin(xOrigine, yOrigine, xGoal, yGoal, obstacles);
		
		while(n != null) {
			chemin.add(0, n);
			n = n.parent;
		}
		
		return chemin;
	}

	/**
	 * Renvoie le prochain déplacement pour une trajectoire optimale.
	 * @param xOrigine
	 * @param yOrigine
	 * @param xGoal
	 * @param yGoal
	 * @return Le noeud vers lequel se déplacer. Null si aucun chemin possible.
	 */
	public Noeud nextMove(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		ArrayList<Noeud> chemin = alChemin(xOrigine, yOrigine, xGoal, yGoal);

		if (chemin.size() >= 2) 
			return chemin.get(1);
		else 
			return null;
	}

	/**
	 * Renvoie un pointeur vers un objet correspondant à ces coordonnées dans l'ArrayList
	 * @param al L'ArrayList à parcourir
	 * @param x
	 * @param y
	 * @return Le Noeud correspondant aux coordonnées. Null s'il n'y en a pas
	 */
	public Noeud getNoeudAt(ArrayList<Noeud> al, int x, int y) {
		for (Noeud n : al) 
			if (n.x == x && n.y == y) 
				return n;

		return null;
	}

	/**
	 * Renvoie le noeud avec la valeur f la plus petite de l'ArrayList.
	 * @param ouverts L'arrayList à parcourir
	 * @return Le noeud le plus petit sélectionné
	 */
	public Noeud plusPetitNoeud(ArrayList<Noeud> ouverts) {
		Noeud noeudMin = new Noeud(10000, 10000, null);

		for (Noeud n : ouverts) 
			if (n.f() < noeudMin.f()) 
				noeudMin = n;

		return noeudMin;
	}

	/**
	 * Renvoie les Noeuds voisins d'un noeud, à part les cases obstacle.
	 * @param n Le Noeud où l'on doit regarder autout
	 * @param diagonales Si l'on doit compter les cases en diagonales
	 * @param obstacles Si nous comptons les zombies comme des obstacles ou non.
	 * @return ArrayList de Noeuds correspondant aux critères en paramètre
	 */
	public ArrayList<Noeud> getVoisins(Noeud n, boolean diagonales, boolean obstacles) {
		ArrayList<Noeud> retour = new ArrayList<>();
		int[] xs = {n.x - 1, n.x, n.x, n.x + 1, n.x - 1, n.x + 1, n.x - 1, n.x + 1};
		int[] ys = {n.y, n.y - 1, n.y + 1, n.y, n.y - 1, n.y - 1, n.y + 1, n.y + 1};

		for (int i = 0; i < (diagonales ? 8 : 4); i++) {
			if (isCaseFree(xs[i], ys[i], obstacles)) {
				retour.add(new Noeud(xs[i], ys[i], n, i > 4));
			}
		}

		return retour;
	}

	// ---
	// Classes internes
	// ---
	
	/**
	 * Classe interne représentant le zombie.
	 */
	public class Zombie {

		public int x, y, tour, champVision;
		public boolean blocage;
		
		/**
		 * Constructeur d'un zombie non bloquant.
		 * @param x Colonne où est initialement le zombie
		 * @param y Ligne où est initialement le zombie
		 */
		public Zombie(int x, int y) {
			this(x, y, false);
		}
		
		/**
		 * Constructeur d'un zombie.
		 * @param x Colonne où est initialement le zombie
		 * @param y Ligne où est initialement le zombie
		 * @param blocking Si le zombie est bloquant ou non.
		 */
		public Zombie(int x, int y, boolean blocking) {
			this.x = x;
			this.y = y;
			this.tour = 0;
			this.champVision = odorat;
			this.blocage = blocking;
		}

		/**
		 * Méthode bougeant le zombie afin d'atteindre la cible renseignée
		 * @param x Colonne ciblée
		 * @param y Ligne ciblée
		 */
		public void move(int x, int y) {
			this.tour++;
			this.tour %= (difficulte <= 2 ? 1 : 2);
			ArrayList<Noeud> chemin = alChemin(this.x, this.y, x, y);

				// Si c'est le tour de jouer du zombie et qu'il n'est pas trop loin
			if (tour == 0 && chemin.size() < this.champVision && chemin.size() >= 2) {
				Noeud next = chemin.get(1);
				this.x = next.x;
				this.y = next.y;
			}
		}
		
		/**
		 * Engage le zombie dans une procédure bloquante s'il est bloquant.
		 * @param xJoueur Colonne du joueur
		 * @param yJoueur Ligne du joueur
		 * @param xArrivee Colonne du but du joueur
		 * @param yArrivee Ligne du but du joueur
		 */
		public void block(int xJoueur, int yJoueur, int xArrivee, int yArrivee) {
			this.tour++;
			this.tour %= (difficulte <= 2 ? 1 : 2);
			
			if(estBloquant() && alChemin(this.x, this.y, xJoueur, yJoueur, false).size() <= this.champVision && this.tour == 0) { // Si le zombie est bien bloquant, que le joueur est à portée, et que c'est son tour
				ArrayList<Noeud> cheminJoueur = alChemin(xJoueur, yJoueur, xArrivee, yArrivee, false);
				if(cheminJoueur.size() > 0) {
					Noeud cible;
					if(cheminJoueur.size() <= 3)
						cible = new Noeud(xJoueur, yJoueur, null);
					else
						cible = cheminJoueur.get(cheminJoueur.size()/2);
					
					Noeud next = nextMove(this.x, this.y, cible.x, cible.y);
					if(next != null) {
						this.x = next.x;
						this.y = next.y;
					}
				}
			}
		}
		
		/**
		 * Indique si le zombie est bloquant
		 * @return Si le zombie est bloquant
		 */
		public boolean estBloquant() {
			return this.blocage;
		}
	}

	/**
	 * Classe interne modélisant les noeuds.
	 */
	public class Noeud {

		public int x;
		public int y;
		public int cout;
		public int distance;
		public Noeud parent;

		/**
		 * Constructeur d'un objet Noeud s'appuyant sur les informations du niveau afin de se paramétrer.
		 * @param x La colonne du noeud
		 * @param y La ligne du noeud
		 * @param parent Le Noeud parent à partir duquel on accède à ce noeud
		 * @param diagonale Si le parent est en diagonale ou non
		 * @param xCible La colonne ciblée
		 * @param yCible La ligne ciblée
		 */
		public Noeud(int x, int y, Noeud parent, boolean diagonale, int xCible, int yCible) {
			this.x = x;
			this.y = y;
			this.distance = Math.abs(x - xCible) + Math.abs(y - yCible);
			this.cout = (isExplosiveHere(x, y) ? 18 : (diagonale ? 14 : 10));
			this.parent = parent;
		}
		
		/**
		 * Constructeur d'un objet Noeud s'appuyant sur les informations du niveau afin de se paramétrer.
		 * La cible ici est le joueur.
		 * @param x La colonne du noeud
		 * @param y La ligne du noeud
		 * @param parent Le Noeud parent à partir duquel on accède à ce noeud
		 * @param diagonale Si le parent est en diagonale ou non
		 */
		public Noeud(int x, int y, Noeud parent, boolean diagonale) {
			this(x, y, parent, diagonale, xJoueur, yJoueur);
		}

		/**
		 * Constructeur d'un objet Noeud s'appuyant sur les informations du niveau afin de se paramétrer.
		 * La cible ici est le joueur, et le parent n'est pas en diagonale.
		 * @param x La colonne du noeud
		 * @param y La ligne du noeud
		 * @param parent Le Noeud parent à partir duquel on accède à ce noeud
		 */
		public Noeud(int x, int y, Noeud parent) {
			this(x, y, parent, false);
		}

		/**
		 * Renvoie la valeur f du Noeud actuel.
		 * Celle-ci s'appuie sur le cout du noeud, de la distance euclidienne, et sur la valeur f des noeuds parents.
		 * @return La valeur f du noeud.
		 */
		public int f() {
			return cout + distance + (parent == null ? 0 : parent.f());
		}

		@Override
		public String toString() {
			return "Noeud(" + x + ", " + y + ")\n";
		}

	}

	/**
	 * Simple classe de TNT.
	 */
	public class TNT {

		public int x;
		public int y;

		/**
		 * Constructeur par défaut d'un objet TNT
		 * @param x La colonne ou est placée la TNT
		 * @param y La ligne ou est placée la TNT
		 */
		public TNT(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public class InvalidValueException extends Exception {

		private final String message;

		public InvalidValueException(String message) {
			this.message = message;
		}

		@Override
		public String getMessage() {
			return message;
		}
	}
}