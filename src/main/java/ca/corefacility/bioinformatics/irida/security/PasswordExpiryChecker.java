package ca.corefacility.bioinformatics.irida.security;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PasswordExpiryChecker implements UserDetailsChecker {
	private static final Logger logger = LoggerFactory.getLogger(PasswordExpiryChecker.class);

	private UserRepository userRepository;

	private static final int PASSWORD_AGE_IN_DAYS = 30;

	public PasswordExpiryChecker(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void check(UserDetails toCheck) {
		String username = toCheck.getUsername();
		User user = userRepository.loadUserByUsername(toCheck.getUsername());

		Revisions<Integer, User> revisions = userRepository.findRevisions(user.getId());

		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);

		cal.add(Calendar.DAY_OF_MONTH, -PASSWORD_AGE_IN_DAYS);
		Date monthAgo = cal.getTime();

		User oldUser = null;

		for (Revision<Integer, User> rev : revisions) {

			logger.trace("Checking old user with date of " + rev.getRevisionDate());

			if (rev.getRevisionDate().toDate().before(monthAgo)) {
				oldUser = rev.getEntity();
				break;
			}
		}

		if (oldUser != null && oldUser.getPassword().equals(user.getPassword())) {
			logger.warn("Credentials for user " + user.getUsername() + " have expired.");
			throw new CredentialsExpiredException("Credentials have expired.");
		}

	}
}
