package edoc.usuarios.dto;

import java.util.List;

import edoc.usuarios.model.AppUserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDataDTO {
  
  @ApiModelProperty(position = 0)
  private String username;
  @ApiModelProperty(position = 1)
  private String email;
  @ApiModelProperty(position = 2)
  private String password;
  @ApiModelProperty(position = 3)
  List<AppUserRole> appUserRoles;

}
