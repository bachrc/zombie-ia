/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiesia;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
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

	public void moveZombies() {
		for (Zombie z : this.zombies) {
			if(z.estBloquant())
				z.block(xJoueur, yJoueur, xArrivee, yArrivee);
			else
				z.move(xJoueur, yJoueur);
		}
	}

	public void removeExplosive(int x, int y) {
		for (int i = 0; i < this.explosifs.size(); i++) 
			if (explosifs.get(i).x == x && explosifs.get(i).y == y) 
				this.explosifs.remove(i);

	}

	public boolean isZombieHere(int x, int y) {
		for (Zombie z : this.zombies) 
			if (z.x == x && z.y == y) 
				return true;

		return false;
	}

	public boolean isExplosiveHere(int x, int y) {
		for (TNT t : this.explosifs) 
			if (t.x == x && t.y == y) 
				return true;

		return false;
	}

	public boolean isCaseFree(int x, int y) {
		return isCaseFree(x, y, true);
	}
	
	public boolean isCaseFree(int x, int y, boolean geneZombie) {
		if (x >= 0 && y >= 0 && x < largeur && y < hauteur) 
			return ((geneZombie ? !isZombieHere(x, y) : true) && plateau[y][x] == ' ');
		
		return false;
	}

	public boolean isArrived() {
		return (alive && xJoueur == xArrivee && yJoueur == yArrivee);
	}

	public boolean isDead() {
		return !alive;
	}

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
		String ANSI_RESET = "\u001B[0m";
		String ANSI_RED = "\u001B[31m";
		String ANSI_GREEN = "\u001B[32m";
		String ANSI_PURPLE = "\u001B[35m";
		String ANSI_BLUE = "\u001B[34m";

		for (int x = 0; x < largeur; x++)
			retour += "+--";
		
		retour += "+\n";

		for (int y = 0; y < hauteur; y++) {
			retour += "|";
			for (int x = 0; x < largeur; x++) {
				if (this.xJoueur == x && this.yJoueur == y) 
					retour += ANSI_BLUE + "J1" + ANSI_RESET;
				else if (this.xArrivee == x && this.yArrivee == y)
					retour += ANSI_PURPLE + "AR" + ANSI_RESET;
				else if (isZombieHere(x, y))
					retour += ANSI_GREEN + "ZO" + ANSI_RESET;
				else if (isExplosiveHere(x, y))
					retour += ANSI_RED + "TN" + ANSI_RESET;
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
	
	public Noeud chemin(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		return chemin(xOrigine, yOrigine, xGoal, yGoal, true);
	}
	
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
	
	public ArrayList<Noeud> alChemin(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		return alChemin(xOrigine, yOrigine, xGoal, yGoal, true);
	}
	
	public ArrayList<Noeud> alChemin(int xOrigine, int yOrigine, int xGoal, int yGoal, boolean obstacles) {
		ArrayList<Noeud> chemin = new ArrayList<>();
		Noeud n = chemin(xOrigine, yOrigine, xGoal, yGoal, obstacles);
		
		while(n != null) {
			chemin.add(0, n);
			n = n.parent;
		}
		
		return chemin;
	}

	public Noeud nextMove(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		ArrayList<Noeud> chemin = alChemin(xOrigine, yOrigine, xGoal, yGoal);

		if (chemin.size() >= 2) 
			return chemin.get(1);
		else 
			return null;
	}

	public Noeud getNoeudAt(ArrayList<Noeud> al, int x, int y) {
		for (Noeud n : al) 
			if (n.x == x && n.y == y) 
				return n;

		return null;
	}

	public Noeud plusPetitNoeud(ArrayList<Noeud> ouverts) {
		Noeud noeudMin = new Noeud(10000, 10000, null);

		for (Noeud n : ouverts) 
			if (n.f() < noeudMin.f()) 
				noeudMin = n;

		return noeudMin;
	}

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
	public class Zombie {

		public int x, y, tour, champVision;
		public boolean blocage;

		public Zombie(int x, int y) {
			this(x, y, false);
		}
		
		public Zombie(int x, int y, boolean blocking) {
			this.x = x;
			this.y = y;
			this.tour = 0;
			this.champVision = odorat;
			this.blocage = blocking;
		}

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
		
		public boolean estBloquant() {
			return this.blocage;
		}
	}

	public class Noeud {

		public int x;
		public int y;
		public int cout;
		public int distance;
		public Noeud parent;

		public Noeud(int x, int y, Noeud parent, boolean diagonale, int xCible, int yCible) {
			this.x = x;
			this.y = y;
			this.distance = Math.abs(x - xCible) + Math.abs(y - yCible);
			this.cout = (isExplosiveHere(x, y) ? 18 : (diagonale ? 14 : 10));
			this.parent = parent;
		}
		
		public Noeud(int x, int y, Noeud parent, boolean diagonale) {
			this(x, y, parent, diagonale, xJoueur, yJoueur);
		}

		public Noeud(int x, int y, Noeud parent) {
			this(x, y, parent, false);
		}

		public int f() {
			return cout + distance + (parent == null ? 0 : parent.f());
		}

		@Override
		public String toString() {
			return "Noeud(" + x + ", " + y + ")\n";
		}

	}

	public class TNT {

		public int x;
		public int y;

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
