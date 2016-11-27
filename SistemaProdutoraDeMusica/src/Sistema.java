import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Sistema {
  //////////////////////////////////////////////////////////////
  private static final String DIRETORIO_MUSICAS = "C:\\musicas";
  private static final String NUMERO_CARTAO_PRODUTORA = "2222";
  private static final Sistema INSTANCE = new Sistema();
  public static final int PORTA = 50000;
  public static final String IP_SISTEMA_CARTAO = "127.0.0.1";
  public static final int PORTA_SISTEMA_CARTAO = 55555;
  //////////////////////////////////////////////////////////////

  
  private List<Musica> musicas;
  private List<Cliente> clientes;

  private Sistema() {
    musicas = new ArrayList<>();
    clientes = new ArrayList<>();
  }

  public static Sistema getInstance() {
    return INSTANCE;
  }
   
  public void iniciar() {
    musicas = gerarListaMusicas(DIRETORIO_MUSICAS);
    
    ServerSocket escuta = null;
    try {
      escuta = new ServerSocket(PORTA);
      Utils.printlnAndFlush("*** Sistema da produtora musical iniciado na porta " + PORTA + " ***");
      
      while (true) {
        Socket cliente = escuta.accept();
        Utils.printlnAndFlush("*** Conexao aceita de " + cliente.getRemoteSocketAddress());
        ConexaoHandler conexao = new ConexaoHandler(cliente);
        conexao.start();
      }
    } catch (IOException e) {
      System.err.println("Erro na escuta: " + e.getMessage());
    } finally {
      if (escuta != null) {
        try {
          escuta.close();
        } catch (IOException e) {
          System.err.println("Erro ao fechar socket: " + e.getMessage());
        }
      }
    }
    
  }
  
  public void addMusicas(List<Musica> musicas) {
    this.musicas.addAll(musicas);
  }
  
  public List<Musica> getMusicas() {
    return musicas;
  }
  
  public Musica getMusicaByCodigo(int codigo) {
    for (Musica musica : musicas) {
      if (musica.getCodigo() == codigo) {
        return musica;
      }
    }
    
    return null;
  } 

  public String efetivarCompraMusica(Musica musica, Cliente cliente) {
    if (clienteComprouMusica(cliente.getLogin(), musica.getCodigo())) {
      return "Você já comprou a música!";
    }
    
    String errorMsg = fazerPagamentoCartao(cliente.getNumeroCartao(), musica.getPreco());
    if (errorMsg != null) {
      return errorMsg;
    }
    cliente.getCompras().add(musica.getCodigo());
    return null;
  }
   
  public String fazerPagamentoCartao(String numeroCartao, double quantia) {
    Socket socket = null;
    
    try {
      Utils.printlnAndFlush("Conectando ao sistema de cartão de crédito...");
      socket = new Socket(IP_SISTEMA_CARTAO, PORTA_SISTEMA_CARTAO);
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      Utils.printlnAndFlush("Conectado a " + socket.getRemoteSocketAddress());
      
      TransacaoCartaoData transacaoCartaoData = 
          new TransacaoCartaoData(numeroCartao, NUMERO_CARTAO_PRODUTORA, quantia);
      
      Utils.printlnAndFlush("Solicitando transação...");
  
      out.writeObject(transacaoCartaoData);
      TransacaoCartaoData resultado = null;
      try {
        resultado = (TransacaoCartaoData) in.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      
      if (resultado.getErrorMessage() == null) {
        Utils.printlnAndFlush("Transação realizada com sucesso!");
      } else {
        return resultado.getErrorMessage();
      }
      
    } catch (UnknownHostException e) {
      System.err.println("Erro UnknownHost: " + e.getMessage());
      return "Erro ao contactar a operadora de cartão!c";
    } catch (IOException e) {
      System.err.println("Erro IO: " + e.getMessage());
      return "Erro ao contactar a operadora de cartão!";
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
          System.err.println("Erro ao fechar socket: " + e.getMessage());
        }
      }
    }
    
    return null;
  }
  
  public static List<Musica> gerarListaMusicas(String diretorio) {
    List<Musica> musicas = new ArrayList<>();
    
    try { 
      File dirFile = new File(diretorio);
      File[] listOfFiles = dirFile.listFiles();
      
      if (listOfFiles == null) {
        return musicas;
      }
      
      Utils.printlnAndFlush("Gerando lista de músicas...");

      for (File file : listOfFiles) {
        if (!file.getName().endsWith(".mp3")) {
          continue;
        }
        
        InputStream input = new FileInputStream(file);
        ContentHandler handler = new DefaultHandler();
        Metadata metadata = new Metadata();
        Parser parser = new Mp3Parser();
        ParseContext parseCtx = new ParseContext();
        parser.parse(input, handler, metadata, parseCtx);
        input.close();
   
        Musica musica = new Musica();
        musica.setNome(metadata.get("title"));
        musica.setArtista(metadata.get("xmpDM:artist"));
        musica.setGenero(metadata.get("xmpDM:genre"));
        musica.setAlbum(metadata.get("xmpDM:album"));
        musica.setAno(metadata.get("xmpDM:releaseDate"));
        musica.setArquivo(file.getAbsolutePath());
        musica.setPreco(Math.round(Utils.random(1.0, 30.0)*100) / 100.0);
        musicas.add(musica);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (TikaException e) {
      e.printStackTrace();
    }
    
    Utils.printlnAndFlush(musicas.size() + " músicas encontradas.");

    return musicas;
  }

  public List<Cliente> getClientes() {
    return clientes;
  }
   


  public String cadastrarCliente(String login, String senha, String numeroCartao) {
    login = login.trim();
    
    if (getClienteByLogin(login) != null) {
      return "Já existe um usuário com o login informado.";
    }
    
    if (login.isEmpty()) {
      return "Login não informado.";
    } 
    
    if (senha.isEmpty()) {
      return "Senha não informada.";
    } 
    
    if (!numeroCartao.matches("^\\d\\d\\d\\d$")) {
      return "Número do cartão inválido (Tem que ter 4 dígitos).";
    }
    
    clientes.add(new Cliente(login, senha, numeroCartao));
    
    return null;
  }
  
  public Cliente getClienteByLogin(String login) {
    for (Cliente cliente : clientes) {
      if (cliente.getLogin().equals(login)) {
        return cliente;
      }
    }
    return null;
  }

  public List<Musica> getMusicasNaoCompradas(Cliente cliente) {
    List<Musica> resultado = new ArrayList<>(musicas);
    
    for (Musica musica : musicas) {
      for (int codigo : cliente.getCompras()) {
        if (musica.getCodigo() == codigo) {
          resultado.remove(musica);
          break;
        }
      }
    }
    
    return resultado;
    
  }
 
  public boolean clienteComprouMusica(String loginCliente, int codigoMusica) {
    Cliente cliente = getClienteByLogin(loginCliente);
    if (cliente != null) {
      return cliente.getCompras().contains(codigoMusica);  
    }
    
    return false;
  }
  
}
