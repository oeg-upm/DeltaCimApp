package cim.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cim.model.User;
import cim.repository.UserRepository;

@Service
public class UserService {


	@Autowired
	public UserRepository userRepository;
	private PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
	private static final String USER_DEFAULT_TOKEN = "root";
	
	public Boolean existUsername(String username) {
		Boolean exist = false;
		try {
			exist =  userRepository.existsById(username);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("------------>>>>>>> looking for username: "+username);
		}
		return exist;
	}
	
	public User getUser(String username) {
		return userRepository.findByUsername(username);
	}

	public List<User> getAllUsers(){
		List<User> users = userRepository.findAll();
		users.forEach(user -> user.setPassword(""));
		return users;
	}

	public void createDefaultUser() {
		if(userRepository.findAll().isEmpty()) {
			User root = new User();
			root.setUsername(USER_DEFAULT_TOKEN);
			root.setPassword(bcryptEncoder.encode(USER_DEFAULT_TOKEN));
			userRepository.save(root);
		}
	}
	
	public void createUser(User newUser) {
		Boolean isNewUser = userRepository.existsById(newUser.getUsername());
		if(!isNewUser) {
			newUser.setPassword(bcryptEncoder.encode(newUser.getPassword()));
			userRepository.save(newUser);
		}
	}
	
	public void updateUser(User user) {
		Boolean userExists = userRepository.existsById(user.getUsername());
		if(userExists) {
			userRepository.delete(user);
		}
		userRepository.save(user);
	}

	
	public Boolean checkLogin(User user) {
		Boolean loginCorrect = false;
		Optional<User> optionalUser = userRepository.findById(user.getUsername());
		if (optionalUser.isPresent()) {
			User storedUser = optionalUser.get();
			loginCorrect = storedUser != null && user.getUsername().equals(storedUser.getUsername())
					&& bcryptEncoder.matches(user.getPassword(), storedUser.getPassword());
		}
		return loginCorrect;
	}


	public void remove(String userId) {
		User user = userRepository.findByUsername(userId);
		if(user!=null) 
			userRepository.delete(user);
	}

	


}
