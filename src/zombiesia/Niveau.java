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
	private ArrayList<Zombie> al;
	
	public Niveau(int numero) throws InvalidValueException{
		this.al = new ArrayList<>();
		this.loadLevel(numero);
	}
	
	private void loadLevel(int numero) throws InvalidValueException {
		InputStream level = Niveau.class.getResourceAsStream("/niveaux/niveau" + numero + ".txt");
		if (level == null)
			throw new InvalidValueException("Niveau inexistant.");
		
		Scanner sc = new Scanner(level);
		this.hauteur = sc.nextInt();
		this.largeur = sc.nextInt();
		
		this.plateau = new char[largeur][hauteur];
		sc.nextLine();
		int lineCount;
		for(lineCount = 0; lineCount < this.plateau.length && sc.hasNextLine(); lineCount++) {
			char[] line = sc.nextLine().toCharArray();
			if(line.length != largeur)
				throw new InvalidValueException("Largeur de la ligne " + (lineCount + 1) + " du niveau " + numero + " est de " + line.length + " au lieu de " + largeur);
			
			for(int i = 0; i < line.length; i++)
				switch(line[i]) {
					case '-':
						this.plateau[lineCount][i] = ' '; break;
					case '|':
						this.plateau[lineCount][i] = '|'; break;
					case '_':
						this.plateau[lineCount][i] = '-'; break;
					case 'Z':
						this.al.add(new Zombie(i, lineCount));
						break;
					case 'A':
						this.xArrivee = i;
						this.yArrivee = lineCount;
						break;
					case 'J':
						this.xJoueur = i;
						this.yJoueur = lineCount;
						break;
					default:
						throw new InvalidValueException("Caractère invalide.");
				}
			
			
		}
		
		if(hauteur != lineCount) {
			throw new InvalidValueException("Nombre de lignes invalide.");
		}
	}
	
	public boolean isZombieHere(int x, int y) {
		for(Zombie z:this.al)
			if(z.x == x && z.y == y)
				return true;
		
		return false;
	}
	
	public String toString() {
		String retour = "";
		Ansi joueur, zombie, arrivee, normal;
		joueur = new Ansi(Ansi.Attribute.NORMAL, Ansi.Color.BLACK, Ansi.Color.WHITE);
		zombie = new Ansi(Ansi.Attribute.BRIGHT, Ansi.Color.GREEN, Ansi.Color.WHITE);
		arrivee = new Ansi(Ansi.Attribute.BRIGHT, Ansi.Color.WHITE, Ansi.Color.RED);
		normal = new Ansi(Ansi.Attribute.BRIGHT, Ansi.Color.BLACK, Ansi.Color.CYAN);
		
		for (int y = 0; y < hauteur; y++) {
			for (int x = 0; x < largeur; x++) retour += "+---"; retour += "+\n";
			for (int x = 0; x < largeur; x++) {
				retour += "|";
				if(this.xJoueur == x && this.yJoueur == y) retour += joueur.colorize(" J ");
				else if(this.xArrivee == x && this.yArrivee == y) retour += arrivee.colorize(" A ");
				else if(isZombieHere(x, y)) retour += zombie.colorize(" Z ");
				else retour += normal.colorize(" " + plateau[y][x] + " ");
			}
			retour += "|\n";
		}
		
		return retour;
	}
	
	// ---
	// Classes internes
	// ---
	
	public class Zombie {
		public int x, y;
		
		public Zombie(int x, int y) {
			this.x = x;
			this.y = y;	
		}
	}
	
	public class InvalidValueException extends Exception {
		private final String message;
		
		public InvalidValueException(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
	}
}
