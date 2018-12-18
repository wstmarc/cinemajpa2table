package fr.laerce.cinema.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "persons")
public class Person {
    private long id;
    private String surname;
    private String givenname;
    private Integer birthYear;
    private String imagePath;
    private List<Film> directedFilms;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "surname", nullable = false, length = 60)
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Basic
    @Column(name = "givenname", nullable = true, length = 40)
    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    @Basic
    @Column(name = "birth_year", nullable = true)
    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    @Basic
    @Column(name = "image_path", nullable = true, length = 80)
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @OneToMany(mappedBy = "realisateur")
    public List<Film> getDirectedFilms() {
        return directedFilms;
    }

    public void setDirectedFilms(List<Film> films) {
        this.directedFilms = films;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person persons = (Person) o;

        if (id != persons.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", nom='" + surname + '\'' +
                ", prenom='" + givenname + '\'' +
                ", naissance=" + birthYear +
                ", photoPath='" + imagePath + '\'' +
                '}';
    }
}
