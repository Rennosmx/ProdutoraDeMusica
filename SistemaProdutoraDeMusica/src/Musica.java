import java.io.Serializable;

public class Musica implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private static int contador;
  private double preco;
  private int codigo;  
  private String nome;
  private String artista;
  private String album;
  private String ano;
  private String genero;
  private String arquivo;
 
   
  public Musica() {
    codigo = contador;
    contador++;
  }

  public String getArtista() {
    return artista;
  }
  
  public void setArtista(String artista) {
    this.artista = artista;
  }
  
  public String getAlbum() {
    return album;
  }
  
  public void setAlbum(String album) {
    this.album = album;
  }
  
  public String getAno() {
    return ano;
  }
  
  public void setAno(String ano) {
    this.ano = ano;
  }
  
  public String getGenero() {
    return genero;
  }
  
  public void setGenero(String genero) {
    this.genero = genero;
  }
  
  public String getArquivo() {
    return arquivo;
  }
  
  public void setArquivo(String arquivo) {
    this.arquivo = arquivo;
  }
  
  public double getPreco() {
    return preco;
  }
  
  public void setPreco(double preco) {
    this.preco = preco;
  }
  
  public String getNome() {
    return nome;
  }
  
  public void setNome(String nome) {
    this.nome = nome;
  }

  public int getCodigo() {
    return codigo;
  }

}
