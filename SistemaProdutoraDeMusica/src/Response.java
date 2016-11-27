import java.io.Serializable;

public class Response implements Serializable {

  private static final long serialVersionUID = 2L;
  
  private boolean isSuccess;
  private String errorMessage;
  private byte[] data;
  
  public boolean isSuccess() {
    return isSuccess;
  }
  
  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
  
  public String getErrorMessage() {
    return errorMessage;
  }
  
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
 
}
