
package logic;

import java.io.*;
import java.net.Socket;

/**
 *
 * @author David Morales
 */
public class Conexion extends Thread{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean conectado=true;
    private boolean recibiendo=false;
    private NodoDistribuido pertenece;

    public Conexion(Socket socket,NodoDistribuido nd) throws IOException {
        pertenece = nd;
        this.socket=socket;
        in =new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }
    
    public void enviarMensaje(String mensaje){
        try {
            out.writeUTF(mensaje);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
//    public void enviarObjeto(Object o){
//        try {
//            out.writeObject(o);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//    
//    public Object leerObjeto(){
//        try {
//            return in.readObject();
//        } catch (IOException | ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
    public byte[] leerArchivo(){
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
            while(!recibiendo){
                 pertenece.procesar(leerMensaje(),this);
            }
           
        }
    }
    

//    public ObjectInputStream getIn() {
//        return in;
//    }
//
//    public void setIn(ObjectInputStream in) {
//        this.in = in;
//    }
//
//    public ObjectOutputStream getOut() {
//        return out;
//    }
//
//    public void setOut(ObjectOutputStream out) {
//        this.out = out;
//    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public NodoDistribuido getPertenece() {
        return pertenece;
    }

    public void setPertenece(NodoDistribuido pertenece) {
        this.pertenece = pertenece;
    }

    public boolean isRecibiendo() {
        return recibiendo;
    }

    public void setRecibiendo(boolean recibiendo) {
        this.recibiendo = recibiendo;
    }

    
    
}
