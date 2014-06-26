
package logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author David Morales
 */
public class Conexion extends Thread{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean conectado=true;
    private NodoDistribuido pertenece;

    public Conexion(Socket socket,NodoDistribuido nd) throws IOException {
        pertenece = nd;
        this.socket=socket;
        in =new ObjectInputStream(this.socket.getInputStream());
        out = new ObjectOutputStream(this.socket.getOutputStream());
    }
    
    public void enviarMensaje(String mensaje){
        try {
            out.writeUTF(mensaje);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void enviarObjeto(Object o){
        try {
            out.writeObject(o);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Object leerObjeto(){
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public String leerMensaje(){
        try {
            return in.readUTF();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        while(conectado){
            pertenece.procesar(leerMensaje(),this);
        }
    }
    

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    
    
}
