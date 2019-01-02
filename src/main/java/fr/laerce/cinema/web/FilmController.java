package fr.laerce.cinema.web;

import fr.laerce.cinema.dao.FilmDao;
import fr.laerce.cinema.model.Film;
import fr.laerce.cinema.service.ImageManager;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/film")
public class FilmController {
    @Autowired
    FilmDao filmDao;

    @Autowired
    ImageManager imm;

    //Manière longue (c.f. PersonController.java)
    @GetMapping("/list")
    public String list(Model model){
        Iterable<Film> films = filmDao.findAll();
        model.addAttribute("films", films);
        model.addAttribute("titrepage", "Liste des films");//de la page
//        System.out.println(films);
        return "film/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id")long id, Model model){
        Film film = filmDao.findById(id).get();
//        Film film = filmDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Request"));
        model.addAttribute("film", film);
        System.out.println("réalisateur film: " + film.getDirector());
        model.addAttribute("titrepage", "Film: " + film.getTitle());//de la page
        return "film/detail";
    }

    @GetMapping("/mod/{id}")
    public String mod(@PathVariable("id")long id, Model model){
        model.addAttribute("film", filmDao.findById(id).get());
        model.addAttribute("titrepage", "Modifier un film");//de la page
        return "film/form";
    }

    @GetMapping("/add")
    public String add(Model model){
        int maxid = (int)filmDao.count() + 1; //pour générer l'id du film a ajouter on recupère le nombre de films dans le filmDao et on y ajoute 1.
        Film film = new Film();
        film.setId(maxid);
        model.addAttribute("film", film);
        System.out.println("filmDao.count() = " + filmDao.count());
        System.out.println("filmDao.count() = " + filmDao.count());//###DEBUG
        System.out.println("filmDao.count() + 1 = " + maxid);//###DEBUG
        System.out.println("films : " + filmDao.findAll());//###DEBUG
        System.out.println();//###DEBUG
        System.out.println();//###DEBUG
        model.addAttribute("titrepage", "Ajouter un film");//de la page

        return "film/add";
    }

/* TODO gérer le cas où on n'entre pas correctement les données du film à ajouter
 * On ne devrait pouvoir ajouter un film que si toutes ses informations ont été saisies
 * C'est à dire:
 * - son ID
 * - son Titre
 * - sa  Notation
 * - son ImagePath
 * - son Résumé
 **/

    @PostMapping("/add")
//    public String submit(@RequestParam("poster")MultipartFile file, @ModelAttribute Film film){
    public String submit(@RequestParam("poster")MultipartFile file, @ModelAttribute Film film, Model model){

        Film lefilm = film;
        String messageerreur = "";
        //TEST DE L'EXISTENCE ET DE LA COHÉRENCE DES CHAMPS DU FORMULAIRE
        if(lefilm.getId() == 0){
            messageerreur += "Aucun Id n'a été saisi pour le film.";
        }
        if(lefilm.getTitle() == null || lefilm.getTitle() == ""){
            messageerreur += "\nAucun Titre n'a été saisi pour le film.";
        }
        if(lefilm.getRating() == null || lefilm.getRating().toString() == ""){
            messageerreur += "\nAucune Note n'a été saisie pour le film.";
        } else {
            if(lefilm.getRating().intValue() > 5 || lefilm.getRating().intValue() < 0){
                messageerreur += "\nLa note saisie est incorrecte.";
            }
        }
        if(lefilm.getImagePath() == null || lefilm.getImagePath() == ""){
            messageerreur += "\nAucun Path saisi pour l'image du film.";
        }
        if(lefilm.getSummary() == null || lefilm.getSummary() == ""){
            messageerreur += "\nAucun Résumé saisi pour le film.";
        }

        if(file.getContentType().equalsIgnoreCase("image/jpeg")){
            try {
                imm.savePoster(film, file.getInputStream());
            } catch (IOException ioe){
                System.out.println("Erreur lecture : "+ioe.getMessage());
            }
        }
        if (messageerreur.length()>0) {
            model.addAttribute("message", messageerreur);
            System.out.println("message: " + messageerreur);
            return "redirect:/film/form";
        } else {
            filmDao.save(film);
            return "redirect:/film/list";
        }
    }

    @GetMapping("/del/{id}")
    public String remove(@PathVariable("id")long id, Model model){
        model.addAttribute("filmdel", filmDao.findById(id).get());
        filmDao.deleteById(id);
        model.addAttribute("titrepage", "Supprimer un film");//de la page
        return "film/deleted";
    }

/*    @Value("${cinema.img.path}")
    String path;*/
/*    @GetMapping("/affiche/{id}")
    public void affiche (HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) throws IOException {

//merci patrick
        /////////////////////////////////////////////////////
        //on copie colle le code du prof et on adapte
        // Chemin absolu de l'image
        System.out.println("ON VEUT AFFICHER UN FILM OU PAS ?");
//        String url="C:\\Users\\CDI\\Desktop\\images\\affichesZ\\";
//        String url="images/affichesZ/"; //Locale
        String url="\\images\\affichesZ\\";//Locale
//        String url=path+"/affichesZ/";    //Locale
        //chemin relatif
        String filename =url+id;
        // Type mime associé à l'image d'après le nom de fichier
        //on a besoin de request d'ou request et response dans les parametre de la methode
        //on recupere a partir de la request le context du servlet et la methode getmine
        String mime = request.getServletContext ().getMimeType (filename);
        //gestion du null
        if (mime == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        //1on defini le type dans response
        response.setContentType(mime);
        //on créée lefichier voulu
        File file = new File(filename);
        //2 on definit la Longeur de la réponse
        response.setContentLength((int)file.length());
        //on converti le fichier en fileinputstream
        FileInputStream in = new FileInputStream(file);
        //on recupere l'objet outputstream de response qui est bien configurer grace a 1 et 2
        OutputStream out = response.getOutputStream();

        // Copie le contenu du fichier vers le flux de sortie(demander rien je comprend pas a partir de là)
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        out.close();
        in.close();
    }*/

    @Value( "${url1}" )
    private String url1;
    //deuxieme methode pour affichezr  image
    @GetMapping("/poster/{id}")
    public ResponseEntity<byte[]> getImageAsResponseEntity (HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) {
        try {
            HttpHeaders headers = new HttpHeaders ();
            String filename=url1+id;
            File i = new File (filename);
            FileInputStream in = new FileInputStream(i);
            byte[] media = IOUtils.toByteArray (in);
            headers.setCacheControl (CacheControl.noCache ().getHeaderValue ());

            ResponseEntity<byte[]> responseEntity = new ResponseEntity<> (media, headers, HttpStatus.OK);
            return responseEntity;
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return null;
    }

}
