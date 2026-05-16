package com.example.data_processing_be.service.Auth;

import com.example.data_processing_be.entity.User;
import com.example.data_processing_be.exception.EmailAlreadyExistsException;
import com.example.data_processing_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void register(String email, String password, String fullName) {
    if(userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistsException("Email " + email + " đã được sử dụng. Vui lòng sử dụng email khác!");
    }
    String passwordHash = passwordEncoder.encode(password);
    User user = User.builder().fullname(fullName).email(email).passwordHash(passwordHash).build();
    userRepository.save(user);
  }
}
