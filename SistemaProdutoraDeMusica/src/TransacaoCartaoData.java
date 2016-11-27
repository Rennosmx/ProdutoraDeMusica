import java.io.Serializable;

public class TransacaoCartaoData implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String errorMessage;
  private String numeroOrigem;
  private String numeroDestino;
  double quantia;
  
  public TransacaoCartaoData(String numeroOrigem, String numeroDestino, double quantia) {
    this.numeroOrigem = numeroOrigem;
    this.numeroDestino = numeroDestino;
    this.quantia = quantia;
  }
  
  public String getNumeroOrigem() {
    return numeroOrigem;
  }
  public void setNumeroOrigem(String numeroOrigem) {
    this.numeroOrigem = numeroOrigem;
  }
  public String getNumeroDestino() {
    return numeroDestino;
  }
  public void setNumeroDestino(String numeroDestino) {
    this.numeroDestino = numeroDestino;
  }
  public double getQuantia() {
    return quantia;
  }
  public void setQuantia(double quantia) {
    this.quantia = quantia;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  
}
