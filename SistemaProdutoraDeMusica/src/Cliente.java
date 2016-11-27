import java.util.ArrayList;
import java.util.List;

public class Cliente {
  private String login;
  private String senha;
  private String numeroCartao;
  
  private List<Integer> compras; // Lista dos codigos das musicas que ele ja comprou
 
  public Cliente(String login, String senha, String numeroCartao) {
    this.login = login;
    this.senha = senha;
    this.numeroCartao = numeroCartao;
    this.compras = new ArrayList<>();
  }

  public String getLogin() {
    return login;
  }
  
  public void setLogin(String login) {
    this.login = login;
  }
  
  public String getSenha() {
    return senha;
  }
  
  public void setSenha(String senha) {
    this.senha = senha;
  }
  
  public String getNumeroCartao() {
    return numeroCartao;
  }
  
  public void setNumeroCartao(String numeroCartao) {
    this.numeroCartao = numeroCartao;
  }
  
  public List<Integer> getCompras() {
    return compras;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((login == null) ? 0 : login.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Cliente other = (Cliente) obj;
    if (login == null) {
      if (other.login != null)
        return false;
    } else if (!login.equals(other.login))
      return false;
    return true;
  }
 
  
}
