
package test;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.NodoDistribuido;

/**
 *
 * @author David Morales
 */
public class MainProyecto {
    public static void main(String[] args) {
        try {
            Scanner sc=new Scanner(System.in);
            System.out.print("IP server: ");
            String ip=sc.nextLine();
            System.out.print("Puerto: ");
            int puerto = sc.nextInt();
            
            NodoDistribuido n=new NodoDistribuido(puerto, ip);
            
            while(sc.nextLine().equals("")){
                 System.out.println(n.getIps());
                 if(!n.getConexiones().isEmpty()){
                     n.getConexiones().get(0).enviarMensaje("getFile");
                 }
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
