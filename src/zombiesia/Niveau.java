/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiesia;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import jlibs.core.lang.Ansi;

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
	private int difficulte;
	private boolean alive;

	public Niveau(int numero) throws InvalidValueException {
		this.zombies = new ArrayList<>();
		this.loadLevel(numero);
		this.alive = true;
	}

	private void loadLevel(int numero) throws InvalidValueException {
		InputStream level = Niveau.class.getResourceAsStream("/niveaux/niveau" + numero + ".txt");
		if (level == null) {
			throw new InvalidValueException("Niveau inexistant.");
		}

		Scanner sc = new Scanner(level);
		this.hauteur = sc.nextInt();
		this.largeur = sc.nextInt();
		this.difficulte = sc.nextInt();

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
				if (isCaseFree(xJoueur, yJoueur - 1)) 
					yJoueur--;
				break;
			case 's':
				if (isCaseFree(xJoueur, yJoueur + 1)) 
					yJoueur++;
				break;
			case 'q':
				if (isCaseFree(xJoueur - 1, yJoueur)) 
					xJoueur--;
				break;
			case 'd':
				if (isCaseFree(xJoueur + 1, yJoueur)) 
					xJoueur++;
				break;
		}
	}

	public void moveZombies() {
		for(Zombie z:this.zombies) {
			Noeud next = this.nextMove(z.x, z.y, xJoueur, yJoueur);
			if(next != null)
				z.move(next.x, next.y);
		}
	}

	public boolean isZombieHere(int x, int y) {
		for (Zombie z : this.zombies) {
			if (z.x == x && z.y == y) {
				return true;
			}
		}

		return false;
	}

	public boolean isCaseFree(int x, int y) {
		if (x >= 0 && y >= 0 && x < largeur && y < hauteur) {
			return (!isZombieHere(x, y) && plateau[y][x] == ' ');
		}
		return false;
	}

	public boolean isArrived() {
		return (alive && xJoueur == xArrivee && yJoueur == yArrivee);
	}

	public boolean isDead() {
		return !alive;
	}
	
	public void levelActions() {
		if(isZombieHere(xJoueur, yJoueur)) {
			alive = false;
		}
	}

	public String toString() {
		String retour = "";
		Ansi joueur, zombie, arrivee, normal;
		joueur = new Ansi(Ansi.Attribute.NORMAL, Ansi.Color.BLACK, Ansi.Color.WHITE);
		zombie = new Ansi(Ansi.Attribute.BRIGHT, Ansi.Color.GREEN, Ansi.Color.WHITE);
		arrivee = new Ansi(Ansi.Attribute.BRIGHT, Ansi.Color.WHITE, Ansi.Color.RED);
		normal = new Ansi(Ansi.Attribute.BRIGHT, Ansi.Color.BLACK, Ansi.Color.CYAN);

		for (int y = 0; y < hauteur; y++) {
			for (int x = 0; x < largeur; x++) {
				retour += "+---";
			}
			retour += "+\n";
			for (int x = 0; x < largeur; x++) {
				retour += "|";
				if (this.xJoueur == x && this.yJoueur == y) {
					retour += joueur.colorize(" J ");
				} else if (this.xArrivee == x && this.yArrivee == y) {
					retour += arrivee.colorize(" A ");
				} else if (isZombieHere(x, y)) {
					retour += zombie.colorize(" Z ");
				} else {
					retour += normal.colorize(" " + plateau[y][x] + " ");
				}
			}
			retour += "|\n";
		}

		return retour;
	}
	
	// ------------------------
	// Partie réservée au calcul du chemin
	// ------------------------

	public Noeud chemin(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		ArrayList<Noeud> ferme = new ArrayList<>();
		ArrayList<Noeud> ouvert = new ArrayList<>();
		ArrayList<Noeud> voisins;
		
		ouvert.add(new Noeud(xOrigine, yOrigine, null));

		while (!ouvert.isEmpty()) {
			Noeud n = plusPetitNoeud(ouvert);
			ouvert.remove(n);
			ferme.add(n);
			
			voisins = getVoisins(n, (this.difficulte % 2 == 1));
			
			for(Noeud v:voisins) {
				if(v.x == xGoal && v.y == yGoal)
					return v;
				else {
					if(getNoeudAt(ouvert, v.x, v.y) == null && getNoeudAt(ferme, v.x, v.y) == null)
						ouvert.add(v);
					else {
						Noeud actuel;
						if((actuel = getNoeudAt(ferme, v.x, v.y)) != null) {
							if (actuel.f() > v.f()) {
								ferme.remove(actuel);
								ouvert.add(v);
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public Noeud nextMove(int xOrigine, int yOrigine, int xGoal, int yGoal) {
		Noeud n = chemin(xOrigine, yOrigine, xGoal, yGoal);
		
		if(n == null) return null;
		
		while(n.parent != null) {
			if(n.parent.parent == null)
				return n;
			
			n = n.parent;
		}
		
		return null;
	}
	
	public Noeud getNoeudAt(ArrayList<Noeud> al, int x, int y) {
		for(Noeud n : al) if(n.x == x && n.y == y) return n;
		
		return null;
	}

	public Noeud plusPetitNoeud(ArrayList<Noeud> ouverts) {
		Noeud noeudMin = new Noeud(10000, 10000, null);

		for (Noeud n : ouverts) 
			if (n.f() < noeudMin.f()) 
				noeudMin = n;
			
		return noeudMin;
	}
	
	public ArrayList<Noeud> getVoisins(Noeud n, boolean diagonales) {
		ArrayList<Noeud> retour = new ArrayList<>();
		int[] xs = {n.x-1, n.x, n.x, n.x+1, n.x-1, n.x+1, n.x-1, n.x+1};
		int[] ys = {n.y, n.y-1, n.y+1, n.y, n.y-1, n.y-1, n.y+1, n.y+1};
		
		for(int i = 0; i < (diagonales ? 8 : 4); i++)
			if(isCaseFree(xs[i], ys[i])) retour.add(new Noeud(xs[i], ys[i], n));

		return retour;
	}

	// ---
	// Classes internes
	// ---
	public class Zombie {

		public int x, y, tour;

		public Zombie(int x, int y) {
			this.x = x;
			this.y = y;
			this.tour = 0;
		}
		
		public void move(int x, int y) {
			this.tour++;
			this.tour %= (difficulte <= 2 ? 1 : 2);
			if(tour == 0) {
				this.x = x;
				this.y = y;
			}
		}
	}

	public class Noeud {

		public int x;
		public int y;
		public int cout;
		public int distance;
		public Noeud parent;

		
		public Noeud(int x, int y, Noeud parent, boolean diagonale) {
			this.x = x;
			this.y = y;
			this.distance = Math.abs(x - xJoueur) + Math.abs(y - yJoueur);
			this.cout = (diagonale ? 14 : 10);
			this.parent = parent;
		}
		
		public Noeud(int x, int y, Noeud parent) {
			this(x, y, parent, false);
		}

		public int f() {
			return cout + distance;
		}
		
		public String toString() {
			return "Noeud(" + x + ", " + y + ")\n";
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
