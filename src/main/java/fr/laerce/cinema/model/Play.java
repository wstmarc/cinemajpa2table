package fr.laerce.cinema.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="play")

public class Play {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;
    @Basic
    @Column(name = "rank", nullable = false)
    private Integer rank;
    @Basic
    @Column(name = "name", nullable = false, length = 90)
    private String name;
    @ManyToOne
    @JoinColumn(name="person_id")
    private Person actor;
    @ManyToOne
    @JoinColumn(name="film_id")
    private Film film;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Person getActor() {
        return actor;
    }

    public void setActor(Person acteur) {
        this.actor = acteur;
    }

   public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Play play = (Play) o;

        if (!id.equals(play.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        return result;
    }
}
