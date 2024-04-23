package edoc.usuarios.model;

import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {
  ROLE_ADMIN, ROLE_CLIENT;

  public String getAuthority() {
    return name();
  }
  public static boolean isValidRole(String inputRole) {
      return Arrays.stream(values())
              .anyMatch(role -> role.name().equals(inputRole));
  }
}
