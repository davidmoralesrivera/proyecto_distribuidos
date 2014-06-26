/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reloj;

import java.io.IOException;
import java.util.Scanner;
import logic.Nodo;


public class Reloj {

    
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("ingrese la ip del servidor o vacio para ser el servidor: ");
        String ip=sc.nextLine();
        System.out.print("Ingrese el puerto: ");
        int port=sc.nextInt();
        Nodo n = new Nodo(ip, port);
    }
}
