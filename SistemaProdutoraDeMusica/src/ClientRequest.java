import java.io.Serializable;

public class ClientRequest implements Serializable {

  private static final long serialVersionUID = 1L;
  
  public enum Tipo {
    LISTAR,
    PLAY,
    LISTAR_COMPRADOS,
    COMPRAR,
    BAIXAR,
    CADASTRAR,
    LOGAR,
    SAIR
  }

  private String[] parameters;
  private Tipo tipo;
  private String login;
  
  public String[] getParameters() {
    return parameters;
  }
  
  public void setParameters(String[] parameters) {
    this.parameters = parameters;
  }
  
  public Tipo getTipo() {
    return tipo;
  }
  
  public void setTipo(Tipo request) {
    this.tipo = request;
  }
  
  public String getLogin() {
    return login;
  }
  
  public void setLogin(String login) {
    this.login = login;
  } 
  
  
}

