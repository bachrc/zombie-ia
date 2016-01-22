/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiesia;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *
 * @author Yohann Bacha <y.bacha@live.fr>
 */
public class Skynet {
	
	/**
	 * Bon, d'accord. Si vous êtes tombés la dessus, vous vous doutez déjà de ce que ça doit être.
	 * Les appréciations que vous renseignez sont envoyées chez moi. Comme ça, je sais ce que vous
	 * pensez du jeu, tout ça.
	 * 
	 * @param appreciation Si vous avez aimé le jeu ou non
	 * @param commentaire Ce que vous avez pensé du jeu
	 * @param note La note au jugé que vous me mettriez
	 */
	public static void send(String appreciation, String commentaire, String note) {
		String url = "http://pachattiere.net/getTrucs.php";
		String charset = "UTF-8";
		try {
			String query = String.format("appreciation=%s&commentaire=%s&note=%s",
					URLEncoder.encode(appreciation, charset),
					URLEncoder.encode(commentaire, charset),
					URLEncoder.encode(note, charset));

			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

			try (OutputStream output = connection.getOutputStream()) {
				output.write(query.getBytes(charset));
			}

			connection.getInputStream();
		} catch (Exception e) {
			// On reste cachés quand même
		}

	}
}
