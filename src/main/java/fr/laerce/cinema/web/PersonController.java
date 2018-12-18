package fr.laerce.cinema.web;

import fr.laerce.cinema.dao.PersonneDao;
import fr.laerce.cinema.model.Person;
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

@Controller
@RequestMapping(value = "/person")
public class PersonController {
    @Autowired
    PersonneDao personneDao;

    @Autowired
    ImageManager imm;

    //Manière courte (c.f. FilmController.java)
    @GetMapping("/list")
    public String list(Model model){
        model.addAttribute("persons", personneDao.findAll());
        return "person/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") long id, Model model){
        model.addAttribute("person", personneDao.findById(id).get());
        return "person/detail";
    }

    @GetMapping("/mod/{id}")
    public String mod(@PathVariable("id")long id, Model model){
        model.addAttribute("person", personneDao.findById(id).get());
        return "person/form";
    }

    @GetMapping("/add")
    public String add(Model model){
        int maxid = (int)personneDao.count() + 1; //pour générer l'id du film a ajouter on recupère le nombre de films dans le filmDao et on y ajoute 1.
        Person personAge = new Person();
        personAge.setId(maxid);
        model.addAttribute("person", personAge);
        return "person/form";
    }

    @PostMapping("/add")
    public String submit(@RequestParam("photo") MultipartFile file, @ModelAttribute Person person){
        if(file.getContentType().equalsIgnoreCase("image/jpeg")){
            try {
                imm.savePhoto(person, file.getInputStream());
            } catch (IOException ioe){
                System.out.println("Erreur lecture : "+ioe.getMessage());
            }
        }
// TODO gérer le cas où on n'entre pas correctement les données de la personne à ajouter
        personneDao.save(person);
        return "redirect:/person/list";
    }

    @GetMapping("/del/{id}")
    public String remove(@PathVariable("id")long id, Model model){
        model.addAttribute("personnedel", personneDao.findById(id).get());
        personneDao.deleteById(id);
        return "person/deleted";
    }

    @Value("${cinema.img.path}")
    String path;
    @GetMapping("/affiche/{id}")
    public void affiche (HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) throws IOException {

//merci patrick
        /////////////////////////////////////////////////////
        //on copie colle le code du prof et on adapte
        // Chemin absolu de l'image
//        String url="C:\\Users\\CDI\\Desktop\\images\\personnesS\\";
        String url="images/personnesZ/"; //Locale
//        String url=path+"/personnesZ/";   //Locale
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
