
public class Main {
  
  public static void main(String[] args) {
    Cliente clienteTest = new Cliente("a", "a", "1111");
    
    Sistema.getInstance().getClientes().add(clienteTest);
    
    Sistema.getInstance().iniciar();
  }

}
