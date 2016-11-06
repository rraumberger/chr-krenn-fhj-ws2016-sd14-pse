package at.fhj.swd14.pse.person;

import javax.ejb.EJB;

import at.fhj.swd14.pse.converter.PersonConverter;
import at.fhj.swd14.pse.repository.PersonRepository;
import at.fhj.swd14.pse.user.UserDto;

/**
 * Implementation of the service for the frontend
 * @author Patrick Kainz
 *
 */
public class PersonServiceImpl implements PersonService {

	@EJB
	PersonRepository repository;
	
	@Override
	public PersonDto find(long id) {
		return PersonConverter.convert(repository.find(id));
	}

	@Override
	public PersonDto findByUser(UserDto user) {
		return PersonConverter.convert(repository.findByUserId(user.getId()));
	}

}