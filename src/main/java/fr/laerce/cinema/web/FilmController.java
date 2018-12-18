package fr.laerce.cinema.web;

import fr.laerce.cinema.dao.FilmDao;
import fr.laerce.cinema.model.Film;
import fr.laerce.cinema.service.ImageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
//        System.out.println(films);
        return "film/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id")long id, Model model){
        model.addAttribute("film", filmDao.findById(id).get());
        return "film/detail";
    }

    @GetMapping("/mod/{id}")
    public String mod(@PathVariable("id")long id, Model model){
        model.addAttribute("film", filmDao.findById(id).get());
        return "film/form";
    }

    @GetMapping("/add")
    public String add(Model model){
        int maxid = (int)filmDao.count() + 1; //pour générer l'id du film a ajouter on recupère le nombre de films dans le filmDao et on y ajoute 1.
        Film film = new Film();
        film.setId(maxid);
        model.addAttribute("film", film);
        System.out.println("filmDao.count() = " + filmDao.count());//###DEBUG
        System.out.println("filmDao.count() + 1 = " + maxid);//###DEBUG
        System.out.println("films : " + filmDao.findAll());//###DEBUG
        System.out.println();//###DEBUG
        System.out.println();//###DEBUG
        return "film/add";
    }

    @PostMapping("/add")
    public String submit(@RequestParam("poster")MultipartFile file, @ModelAttribute Film film){
        if(file.getContentType().equalsIgnoreCase("image/jpeg")){
            try {
                imm.savePoster(film, file.getInputStream());
            } catch (IOException ioe){
                System.out.println("Erreur lecture : "+ioe.getMessage());
            }
        }
// TODO gérer le cas où on n'entre pas correctement les données du film à ajouter
        filmDao.save(film);

        return "redirect:/film/list";
    }

    @GetMapping("/del/{id}")
    public String remove(@PathVariable("id")long id, Model model){
        model.addAttribute("filmdel", filmDao.findById(id).get());
        filmDao.deleteById(id);
        return "film/deleted";
    }

/*    @Value("${cinema.img.path}")
    String path;*/
    @GetMapping("/affiche/{id}")
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
    }

}
