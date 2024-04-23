package edoc.usuarios.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDataDTO {
  
  @ApiModelProperty(position = 0)
  private String token;
  @ApiModelProperty(position = 1)
  private String message;
  @ApiModelProperty(position = 2)
  private String token_type;
  @ApiModelProperty(position = 3)
  private String expires_in;
  @ApiModelProperty(position = 4)
  private String scope;
}
