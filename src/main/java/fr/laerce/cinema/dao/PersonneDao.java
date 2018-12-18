package fr.laerce.cinema.dao;

import fr.laerce.cinema.model.Person;
import org.springframework.data.repository.CrudRepository;


public interface PersonneDao extends CrudRepository<Person, Long> {

}
