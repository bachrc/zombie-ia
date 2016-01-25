# Ne vous faites pas manger par les zombies

"Ne vous faites pas manger par les zombies" est un jeu révolutionnaire en console où le but est de combattre une invasion zombie qui se déroule dans votre ordinateur ! Luttez contre la fragmentation de votre disque dur en incarnant votre disque dur faces aux MECHANTES INTERFERENCES.

Voilà le synopsis global. Plus précisément, il s'agit d'un projet d'Intelligence Artificielle sous la tutelle de M. DUTOT, M. DEMARE et de M. OLIVIER, tous trois professeurs de cette même matière. Le sujet est simple : il faut créer l'Intelligence Artificielle contre lesquels vous devez vous confronter.

## Sommaire

<!-- MarkdownTOC -->

- Jouer
    - Compiler à partir des sources
    - Téléchargement
- Le jeu
    - Présentation
    - La lecture de niveaux
    - Le moteur de messages et de choix
    - Intelligence des Zombies
        - Path-finding
        - Zombies affamés
        - Zombies bloquants
    - Cases de TNT
- Conclusion

<!-- /MarkdownTOC -->

## Jouer

Il a deux manières de jouer au jeu en question. L'une pour les gens à l'aise avec la ligne de commande, l'autre pour les feignants, comme la majorité d'entre nous.

Il est important de préciser que deux versions du jeu sont disponibles, selon la compatibilité de votre terminal :

- Une version avec coloration ANSI pour rajouter un peu de couleur lors du jeu
- Une version sans couleur pour les terminaux ne supportant pas l'ANSI

### Compiler à partir des sources

Pour télécharger le jeu, il vous suffit de posséder, déjà, Java avec un JRE >= 6, et git. Il vous suffit de rentrer les commandes suivantes afin de le télécharger :

    git clone https://github.com/totorolepacha/zombie-ia.git
    cd zombie-ia

Et d'ensuite choisir la version qui vous convient le mieux :
- "v1.2" pour la version avec coloration ANSI
- "v1.2-noansi" pour la version sans coloration ANSI

Et d'ensuite exécuter la commande suivante, en remplaçant `<version>` par la version choisie juste avant : 

    git checkout tags/<version>

Il vous suffit ensuite de compiler les sources en oubliant pas d'inclure le jar de ressources d'Oracle, présent dans le dossier.

### Téléchargement

Vous pouvez également télécharger les sources pré-compilées, prêtes à jouer ci-dessous :

- Version 1.2 : [lien](https://github.com/totorolepacha/zombie-ia/releases/download/v1.2/v1.2.zip)
- Version 1.2 (no ansi) : [lien](https://github.com/totorolepacha/zombie-ia/releases/download/v1.2-noansi/v1.2-noansi.zip)

Vous n'avez ainsi qu'à extraire l'archive dans un dossier, et lancer le fichier correspondant à votre OS. Exécutez `launch.bat` si vous êtes sur Windows, et `launch.sh` depuis une distribution Linux.

Ces deux fichiers possèdent le même contenu, soit la ligne de commande suivante : 

    java -jar ZombiesIA.jar

Alors vérifiez que le dossier contenant vos binaires de Java est bien compris dans les variables d'environnement de votre système.

## Le jeu

### Présentation

Le jeu, afin de rester concentré sur l'objectif principal qui est l'intelligence artificielle des zombies, ne possède qu'un petit but. Vous devez vous déplacer d'un point A jusqu'à un point B, en évitant de mourir sur le chemin.

Avant d'aller plus loin, je vous suggère de tout d'abord jouer au jeu, car ce qui suit risque de vous enlever toute la fabuleuse intrigue scénaristique du jeu.

Je vous aurais prévenu.

Ce jeu dévoile petit à petit de nouveau éléments de gameplay au fur et à mesure des 7 niveaux proposés par le jeu.
Bien sûr, en modifiant les sources, vous pouvez ajouter de nouveaux niveaux facilement. Il suffit d'un simple fichier texte, et d'un appel dans la fonction main.

### La lecture de niveaux 

La lecture de niveau se fait actuellement par simple parcours de fichier texte. Dans le package "niveaux", il suffit de créer un niveau du type `niveau*.txt`, en respectant quelques consignes, dont une ligne d'entête respectant la syntaxe : 

    <hauteur> <largeur> <difficulté> <odorat>

Ces valeurs correspondent à :
- Hauteur : La hauteur en cases du niveau
- Largeur : La largeur du niveau en cases
- Difficulté : Le degré de difficulté des zombies, peut varier entre 1 et 4. Fonctionnement détaillé dans la partie de l'Intelligence des zombies.
- Odorat : L'odorat des zombies. Cette valeur sera aussi détaillée plus tard.

De plus, les valeurs détaillant le niveaux se doivent de respecter certaines contraintes. Il y a plusieurs types de cases :

- La case de départ du Joueur, représentée par un `J`
- Une case où se situe un simple zombie affamé, représentée par un `Z`
- Une case où se situe un zombie bloquant, représentée par un `B`
- Une case de mur, représentée soit par `|` ou `_`
- Une case d'arrivée est symbolisée par la lettre `A`
- Une case remplie d'explosifs est représentée comme ceci : `T`
- Et les cases vides de la sorte : `-`

Par exemple, dans le jeu, voici le `niveau1.txt` renseigné :

    15 15 4 20
    ---------A-----
    ---------------
    ---------------
    ---------------
    ---------------
    ---------------
    ---------------
    ---------------
    ------_______--
    ------|--------
    ------|--------
    ------|Z-------
    ------|--------
    ----J-|------Z-
    ------|--------

### Le moteur de messages et de choix

Afin de dynamiser un peu le tout, car après tout, un jeu en console reste un peu austère, j'ai créé un moteur d'affichage dynamique de messages, afin aussi d'intéragir avec le joueur. Même si dans le cas présent, les interactions n'y sont que minimes, vu que nous devons nous concentrer après tout sur l'intelligence des zombies.

L'intégralité du contenu de ce moteur est disponible dans le fichier [Messages.java](src/zombiesia/Messages.java). 

L'essentiel de la classe se situe à la classe [introArray](src/zombiesia/Messages.java#L130). En effet, cette méthode affiche dynamiquement tous les messages qui lui sont donnés en paramètre, la durée variant selon la longueur du texte.

Les différents groupes de messages sont stockés dans un enum nommé [GroupeMessages](src/zombiesia/Messages.java#L24). Ce sont des groupes de messages qui sont de toutes manières affichés dans le jeu à un moment ou à un autre.

Cependant, certains messages ne peuvent être affichées que dans certains contextes. Nous avons donc un enum stockant tous les types de contextes, c'est [TypeReponse](src/zombiesia/Messages.java#L56). Et à partir de ce contexte, la méthode [reponse()](src/zombiesia/Messages.java#L83) réagira différemment en fonction de la variable renseignée. Par exemple, si lors du contexte `TypeReponse.Niveau2`, la partie du joueur renvoie "false" (donc qu'il a perdu), le jeu réagira différemment que si le joueur avait gagné.

Le jeu peut également réagir différemment en fonction d'une saisie utilisateur.

### Intelligence des Zombies

#### Path-finding

Afin que les zombies puissent traquer les joueurs, il faut que les zombies puissent traquer le joueur. Pour cela, le choix a été axé sur l'algorithme A*. Qui offre de très bonnes performances en terme de rapidité, et en termes de qualité.

Je suppose que l'on vous a décrit plus d'une dizaine de fois le fonctionnement de l'algorithme A*. Donc permettez-moi de vous la décrire encore une fois, assez rapidement.

Le traitement du calcul du chemin se trouve dans le fichier [Niveau.java](src/zombiesia/Niveau.java#L316). En effet, pour calculer le chemin optimal, nous n'avons besoin que de trois choses :

- Les coordonnées de départ
- Les coordonnées cible
- Le plateau, ses obstacles, ses spécificités

C'est donc à partir de ces éléments que nous pouvons écrire l'algorithme principal, contenu dans la méthode [chemin()](src/zombiesia/Niveau.java#L316) :

```java
public Noeud chemin(int xOrigine, int yOrigine, int xGoal, int yGoal, boolean obstacles) {
    ArrayList<Noeud> ferme = new ArrayList<>();
    ArrayList<Noeud> ouvert = new ArrayList<>();
    ArrayList<Noeud> voisins;
    ouvert.add(new Noeud(xOrigine, yOrigine, null));
```

Nous allons tout d'abord définir les quelques listes qui vont contenir les noeuds ouverts et les noeuds fermés.

Les noeuds ouverts sont les noeuds potentiellement ouvrants vers un chemin optimal, qui n'ont pas encore été examinés. Les noeuds fermés sont ceux qui ont déjà été examinés.

Nous ajoutons donc la position de départ aux noeuds ouverts.

```java
    while (!ouvert.isEmpty()) {
        Noeud n = plusPetitNoeud(ouvert);
        ouvert.remove(n);
        ferme.add(n);

        voisins = getVoisins(n, (this.difficulte % 2 == 1), obstacles);
```

Tant que nous avons des noeuds ouverts, nous allons à chaque fois sélectionner le noeud qui possède le chemin le plus court non examiné jusqu'à présent, afin de l'examiner, lui, et ses voisins. Vu que nous avons examiné ce noeud, nous le mettons dans la liste des noeuds fermés.

Si la difficulté courante est soit de 1, soit de 3, les cases en diagonale sont aussi prises en compte en tant que voisines. Cependant, le coût des cases en diagonale est légèrement plus élevé que celui des déplacements latéraux.

```java
        for (Noeud v : voisins) {
            if (v.x == xGoal && v.y == yGoal) {
                return v;
```

Nous parcourons ensuite tous les voisins de ce noeud. Tous ces noeuds voisins sont, en mémoire, recréés avec le noeud courant comme parent. Si l'un d'entre eux correspond au noeud cible, c'est bon, le chemin est accompli ! Nous retournons donc le noeud trouvé à l'utilisateur. Ce noeud possède pour parent le noeud par lequel il a été accédé, et ce récursivement jusqu'au noeud d'origine. Nous avons donc le trajet dans sa totalité.

```java
            } else {
                if (getNoeudAt(ouvert, v.x, v.y) == null && getNoeudAt(ferme, v.x, v.y) == null) {
                    ouvert.add(v);
```

Si le voisin n'existe dans aucune des listes, c'est qu'il n'a jamais été découvert. On l'ajoute à la liste des noeuds ouverts.

```java
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
```

S'il existe cependant, c'est une autre paire de manches. Si un noeud avec les mêmes coordonnées existe dans la liste des Noeuds déjà examinés, et que le nouveau Noeud a un trajet plus avantageux que celui déjà examiné, on supprime le noeud déjà examiné des noeuds examinés, et on ajoute le nouveau dans la liste des noeuds à examiner !

Nous continuons la procédure jusqu'à épuisement des noeuds ouverts. Si, un jour, il n'y a plus de noeuds à examiner, c'est que le trajet est impossible. Nous retournons donc null.

#### Zombies affamés

Nous avons donc deux types de zombies. Les zombies les plus basiques comme on les connaît partout, sont les zombies férus de cervelle. Leur seule stratégie sera ici de vous dévorer le cerveau. Ils ne feront que prendre le joueur pour cible, mais ce avec quelques subtilités. Principalement deux :

- La difficulté du jeu : Il existe quatre difficultés différentes dans ce jeu, toutes renseignables via l'entête d'un niveau.
    + Difficulté 4 : le zombie ne se déplacera que tous les deux tours, et seulement dans les 4 directions latérales haut/bas/gauche/droite.
    + Difficulté 3 : le zombie ne se déplacera que tous les deux tours, mais cette fois-ci dans les 8 directions l'entourant.
    + Difficulté 2 : Le zombie se déplace tous les tours, mais seulement dans les 4 directions l'entourant.
    + Difficulté 1 : Difficulté la plus élevée : les zombies se déplacent dans tous les sens, et ce tous les tours.
- L'odorat du zombie : Un indice d'odorat est spécifié dans l'entête de chaque niveau. Cet indice représente la distance maximum à la laquelle le zombie peut sentir le Joueur 1. Si le nombre d'étapes du trajet via A* est supérieur à l'odorat du zombie, le zombie ne bougera pas.
 
#### Zombies bloquants

Les zombies bloquants sont un peu différents des zombies normaux. En effet, leur première cible ne sera pas le joueur. Leur but est de s'interposer entre le joueur et l'arrivée.

En effet, dès que le zombie détectera l'humain, il cherchera à se mettre exactement au milieu du chemin entre l'humain et l'arrivée. Et dès que l'humain sera dangereusement près de l'arrivée, le zombie prendra pour cible l'humain.

Le zombie bloquant est soumis aux mêmes règles de difficulté et d'odorat que les zombies affamés. Même s'il est un peu plus intelligent que la masse, cela reste un zombie.

Un seul zombie bloquant a été placé dans le jeu : le dernier zombie du dernier niveau : le niveau 7.

### Cases de TNT

Les cases d'explosifs sont un petit élément de gameplay rajouté afin de permettre au joueur de se sortir de situations complexes, en prédisant l'intelligence des zombies afin de les faire s'exterminer dessus. Une case d'explosifs aura un cout plus élevé qu'une case normale. Ce qui veut dire qu'un zombie ayant le choix entre une case de TNT et une case normale pour une même distance, tendra à aller vers la case normale. Un petit sixième sens, mais pas trop. Cela reste un zombie.


## Conclusion

En espérant que le jeu vous ait plu, et que l'intelligence de ces mêmes zombies vous ait convenus. N'hésitez pas à remplir le petit questionnaire en fin de jeu.