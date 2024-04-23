package edoc.usuarios.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edoc.usuarios.constantes.UsuariosConstantes;
import edoc.usuarios.dto.TokenDataDTO;
import edoc.usuarios.exception.CustomException;
import edoc.usuarios.model.AppUser;
import edoc.usuarios.model.AppUserRole;
import edoc.usuarios.repository.UserRepository;
import edoc.usuarios.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  public String login(String username, String password) {
	    try {
	        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	        TokenDataDTO response = new TokenDataDTO();
	        response.setToken(jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getAppUserRoles()));
	        addParametrosUsuario(response);	        
	        // Converter o objeto para JSON usando o Jackson
	        ObjectMapper objectMapper = new ObjectMapper();
	        return objectMapper.writeValueAsString(response);
	    } catch (AuthenticationException e) {
	        throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
	    } catch (JsonProcessingException e) {
	        throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
	}

private void addParametrosUsuario(TokenDataDTO response) {
    response.setExpires_in(String.valueOf(jwtTokenProvider.getValidityInMilliseconds()));
	response.setMessage(UsuariosConstantes.MENSAGEM);	   
	response.setScope(UsuariosConstantes.EDOC);
	response.setToken_type(UsuariosConstantes.Bearer);
}

  public String signup(AppUser appUser) {
   try {
	   
	   for (AppUserRole inputRole : appUser.getAppUserRoles()) {
           if (!AppUserRole.isValidRole(inputRole.toString())) {
           	  throw new CustomException("A string '" + inputRole + "' não corresponde a nenhum valor do enum.", HttpStatus.UNPROCESSABLE_ENTITY);
           }
       }
	    if (!userRepository.existsByUsername(appUser.getUsername())) {
	        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
	        userRepository.save(appUser);	      
	        TokenDataDTO response = new TokenDataDTO();
	        response.setMessage("Autenticação bem-sucedida");
	        response.setToken(jwtTokenProvider.createToken(appUser.getUsername(), appUser.getAppUserRoles()));
	        addParametrosUsuario(response);
	        // Converter o objeto para JSON usando o Jackson	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        return objectMapper.writeValueAsString(response);
	    } else {
	      throw new CustomException("O nome de usuario ja esta em uso", HttpStatus.UNPROCESSABLE_ENTITY);
	    }    
	} catch (AuthenticationException e) {
		throw new CustomException("O nome de usuario ja esta em uso", HttpStatus.UNPROCESSABLE_ENTITY);
	} catch (JsonProcessingException e) {
		throw new CustomException("O nome de usuario ja esta em uso", HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
  }

  public void delete(String username) {
    userRepository.deleteByUsername(username);
  }

  public AppUser search(String username) {
    AppUser appUser = userRepository.findByUsername(username);
    if (appUser == null) {
      throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
    }
    return appUser;
  }

  public AppUser whoami(HttpServletRequest req) {
    return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
  }

	public String refresh(String username) {
		try {
			TokenDataDTO response = new TokenDataDTO();
			response.setToken(
					jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getAppUserRoles()));
			response.setMessage("Autenticação bem-sucedida");
			 addParametrosUsuario(response);
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(response);
			
		} catch (AuthenticationException e) {
			throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (JsonProcessingException e) {
			throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

}