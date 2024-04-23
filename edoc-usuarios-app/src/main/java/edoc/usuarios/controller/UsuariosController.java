package edoc.usuarios.controller;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edoc.usuarios.dto.UsuarioDataDTO;
import edoc.usuarios.dto.UsuarioResponseDTO;
import edoc.usuarios.model.AppUser;
import edoc.usuarios.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/v1/usuarios")
@Api(tags = "usuarios", description = "Dados do usuario")
@RequiredArgsConstructor
public class UsuariosController {

  private final UserService usuarioService;
  private final ModelMapper modelMapper;

  @PostMapping(value ="/token", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "${UsuariosController.token} Obter token ")
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Algo deu errado"), //
      @ApiResponse(code = 422, message = "Nome de usuário/senha inválidos fornecidos")})
  public String login(//
      @ApiParam("usuario") @RequestParam String usuario, //
      @ApiParam("senha") @RequestParam String senha) {
    return usuarioService.login(usuario, senha);
  }

  @PostMapping(value ="/cadastro", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "${UsuariosController.cadastro} Cadastro usuarios ")
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Algo deu errado"), //
      @ApiResponse(code = 403, message = "Acesso negado"), //
      @ApiResponse(code = 422, message = "O nome de usuário já está em uso")})
  public String signup(@ApiParam("Cadastro usuarios") @RequestBody UsuarioDataDTO user) {
    return usuarioService.signup(modelMapper.map(user, AppUser.class));
  }

  @DeleteMapping(value ="/{usuario}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "${UsuariosController.exclusao} Excluir usuario", authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Algo deu errado"), //
      @ApiResponse(code = 403, message = "Acesso negado"), //
      @ApiResponse(code = 404, message = "O usuário não existe"), //
      @ApiResponse(code = 500, message = "Token JWT expirado ou inválido")})
  public String delete(@ApiParam("usuario") @PathVariable String usuario) {
    usuarioService.delete(usuario);
    return usuario;
  }

  @GetMapping(value = "/{usuario}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "${UsuariosController.pesquisar} Pesquisar usuario", response = UsuarioResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Algo deu errado"), //
      @ApiResponse(code = 403, message = "Acesso negado"), //
      @ApiResponse(code = 404, message = "O usuário não existe"), //
      @ApiResponse(code = 500, message = "Token JWT expirado ou inválido")})
  public UsuarioResponseDTO search(@ApiParam("Usuario") @PathVariable String usuario) {
    return modelMapper.map(usuarioService.search(usuario), UsuarioResponseDTO.class);
  }

  @GetMapping(value = "/meu")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  @ApiOperation(value = "${UsuariosController.meu} Obter dados do meu usuario", response = UsuarioResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Algo deu errado"), //
      @ApiResponse(code = 403, message = "Acesso negado"), //
      @ApiResponse(code = 500, message = "Token JWT expirado ou inválido")})
  public UsuarioResponseDTO whoami(HttpServletRequest req) {
    return modelMapper.map(usuarioService.whoami(req), UsuarioResponseDTO.class);
  }

  @GetMapping(value = "/atualiza/token", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  @ApiOperation(value = "Atualizar Token")
  public String atualiza(HttpServletRequest req) {
    return usuarioService.refresh(req.getRemoteUser());
  }

}
