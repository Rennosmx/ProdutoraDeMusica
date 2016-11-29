import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;


public class ConexaoHandler extends Thread {
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private Socket clienteSocket;
  
  public ConexaoHandler(Socket clienteSocket) {
    this.clienteSocket = clienteSocket;
  }
  
  @Override
  public void run() {
    try {
      in = new ObjectInputStream(clienteSocket.getInputStream());
      out = new ObjectOutputStream(clienteSocket.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    ClientRequest clientRequest = null;
    
    try {
      clientRequest = (ClientRequest) in.readObject();
      processarRequest(clientRequest);
    } catch (ClassNotFoundException e) {
      System.err.println("ClassNotFoundException: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("IOException: " + e.getMessage());
    } finally {
      try {
        clienteSocket.close();
      } catch (IOException e) {
        System.err.println("Erro ao fechar socket cliente: " + e.getMessage());
      }
    }  
  }

  private void processarRequest(ClientRequest clientRequest) {
    switch (clientRequest.getTipo()) {
      case LISTAR: {
        Cliente cliente = Sistema.getInstance().getClienteByLogin(clientRequest.getLogin());
        try {
          out.writeObject(Sistema.getInstance().getMusicasNaoCompradas(cliente));
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      break;
      
      case LISTAR_COMPRADOS: {
        Cliente cliente = Sistema.getInstance().getClienteByLogin(clientRequest.getLogin());
        try {
          out.writeObject(Sistema.getInstance().getMusicasCompradas(cliente));
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      break;  
      
      case COMPRAR:{
        String errorMsg = null;
        Response response = new Response();
        
        if (clientRequest.getParameters().length < 1) {
          errorMsg = "Código da música não informado!";
        } else {
          int codigo;
          try {
            codigo = Integer.parseInt(clientRequest.getParameters()[0]);
          } catch (NumberFormatException e) {
            codigo = -1;
          }
          
          Cliente cliente = Sistema.getInstance().getClienteByLogin(clientRequest.getLogin());
          if (cliente == null) {
            errorMsg = "Login inválido!";                       
          }
          
          Musica musica = Sistema.getInstance().getMusicaByCodigo(codigo);
          if (musica == null) {
            errorMsg = "Código da música inválido!";              
          } else {
            errorMsg = Sistema.getInstance().efetivarCompraMusica(musica, cliente);
          } 
        }
        
        if (errorMsg == null) {
          response.setSuccess(true);
        }
        
        try {
          response.setErrorMessage(errorMsg);
          out.writeObject(response);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      break;
      
      case LOGAR: {
        Cliente cliente = autenticarCliente(clientRequest.getParameters()[0], clientRequest.getParameters()[1]);
        Response response = new Response();
        if (cliente == null) {
          response.setSuccess(false);
          response.setErrorMessage("Usuário/senha incorretos.");
        } else {
          response.setSuccess(true);
        }
        
        try {
          out.writeObject(response);
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
      break;  
      
      case CADASTRAR: {
        String login = clientRequest.getParameters()[0];
        String senha = clientRequest.getParameters()[1];
        String numeroCartao = clientRequest.getParameters()[2];
        
        String resultado = Sistema.getInstance().cadastrarCliente(login, senha, numeroCartao);
        Response response = new Response();
        
        if (resultado != null) {
          response.setSuccess(false);
          response.setErrorMessage(resultado);
        } else {
          response.setSuccess(true);
        }
        
        try {
          out.writeObject(response);
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
      break;        
      
      case BAIXAR: {
        String errorMsg = null;
        
        String login = clientRequest.getLogin();
        try {
          int codigo = Integer.parseInt(clientRequest.getParameters()[0]);
          if (!Sistema.getInstance().clienteComprouMusica(login, codigo)) {
            errorMsg = "Acesso negado.";
          } else {
            errorMsg = enviarMusica(codigo);
          }
        } catch (NumberFormatException e) {
          errorMsg = "Código inválido.";
        } finally {
          if (errorMsg != null) {
            Response response = new Response();
            response.setErrorMessage(errorMsg);
            response.setSuccess(false);
            try {
              out.writeObject(response);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }   
      break;

      case PLAY: {
        String errorMsg = null;      
        try {
          int codigo = Integer.parseInt(clientRequest.getParameters()[0]);
          
          Response response = new Response();
          response.setSuccess(true);
          if (Sistema.getInstance().clienteComprouMusica(clientRequest.getLogin(), codigo)) {
        	  response.setData(new byte[] {1});
          } else {
        	  response.setData(new byte[] {0});
          }
          
          try {
			out.writeObject(response);
			out.flush();
		  } catch (IOException e) {
		    e.printStackTrace();
		  }  

          errorMsg = enviarMusica(codigo);
        } catch (NumberFormatException e) {
          errorMsg = "Código inválido.";
        } finally {
          if (errorMsg != null) {
            Response response = new Response();
            response.setErrorMessage(errorMsg);
            response.setSuccess(false);
            try {
              out.writeObject(response);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }   
      break;      
      
      default:
      break;
    }
    
  }

  
  private String enviarMusica(int codigo) {
    Musica musica = Sistema.getInstance().getMusicaByCodigo(codigo);
    Response response = null;
  
    if (musica == null) {
      return "Não foi encontrada nenhuma música com o código fornecido";
    }
    
    InputStream fileIn = null;
    
    try {
      File file = new File(musica.getArquivo());
      fileIn = new FileInputStream(file);
      byte[] buffer = new byte[8192];
      int count;
      while (true) {
        response = new Response();
        response.setSuccess(true);
        count = fileIn.read(buffer);
        if (count == -1) {
          out.writeObject(response);
          break;
        }

        response.setData(Arrays.copyOf(buffer, count));
        out.writeObject(response);
      }
      out.flush();
    } catch (FileNotFoundException  e) {
      return "Arquivo não encontrado no servidor.";
    } catch (IOException e) {
      return "Problema I/O no servidor.";
    } finally {
      if (fileIn != null) {
        try {
          fileIn.close();
          fileIn = null;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }          
    }
    
    return null;
  }

  private Cliente autenticarCliente(String login, String senha) {
    for (Cliente cliente : Sistema.getInstance().getClientes()) {
      if (cliente.getLogin().equalsIgnoreCase(login) && cliente.getSenha().equals(cliente.getSenha())) {
        return cliente;
      }
    }
    
    return null;
  }
  
  
}
