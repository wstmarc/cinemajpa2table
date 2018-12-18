package fr.laerce.cinema.service;

import fr.laerce.cinema.model.Film;
import fr.laerce.cinema.model.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageManager {
    @Value("${cinema.img.path}")
    String path;

    public int savePhoto(Person p, InputStream fi){
        p.setImagePath(save("p", "personnesS", fi));
        return 0;
    }

    public int savePoster(Film f, InputStream fi) {
        f.setImagePath(save("f","affichesS", fi));
        return 0;
    }

    private String getLastFile(String prefix, String dirPath){
        String max = "";
        try(DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(dirPath),prefix+"*")){
            for (Path fic: dir
                 ) {
                if(max.compareTo(fic.getFileName().toString())<0){
                    max = fic.getFileName().toString();
                }
                
            }
        }catch (IOException ioe){
            
        }
        return max;
    }

    private String save(String prefix, String subPath, InputStream fi){
        String fileName = "";
        try(DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(path+"/"+subPath),prefix+"*")){

            System.out.println("dossier: "+ dir);
            for (Path file: dir
//            for (val file: dir
            ) {
                if(fileName.compareTo(file.getFileName().toString())<0){
                    fileName = file.getFileName().toString();
//                    fileName = file.fileName.toString();
                    System.out.println("nomfichier: "+ fileName);
                }
            }
            String numStr = fileName.substring(1, fileName.indexOf(".jpg"));
//            var numStr = fileName.substring(1, fileName.indexOf(".jpg"));
//            System.out.println(numStr);//###DEBUG
            Integer num = Integer.parseInt(numStr);

//            num = Integer.parseInt(numStr);
            numStr = String.format("%04d",num+1);
//            System.out.println(numStr);//###DEBUG
            fileName = prefix+numStr+".jpg";

            String filePath = path+"/"+subPath+"/"+fileName;
//            val filePath = path+"/"+subPath+"/"+fileName;
            System.out.println("numfichierStr: "+ numStr);//###DEBUG
            System.out.println("numfichier: "+ num);//###DEBUG
            System.out.println("numfichierStr: "+ numStr);//###DEBUG
            System.out.println("nomfichier: "+ fileName);//###DEBUG
            System.out.println("pathfichier: "+ filePath);//###DEBUG

            Files.copy(fi, new File(filePath).toPath());
        }catch (IOException ioe){
            System.out.println("Erreur sur nom d'image : "+ioe.getMessage());
        }


        return fileName;
    }
}
