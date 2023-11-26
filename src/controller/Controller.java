package controller;

import java.io.IOException;
import java.util.Scanner;

import model.logic.Modelo;
import view.View;

public class Controller {

    /* Instancia del Modelo */
    private Modelo modelo;

    /* Instancia de la Vista */
    private View view;

    /**
     * Crear la vista y el modelo del proyecto
     */
    public Controller() {
        view = new View();
    }

    public void run() {
        Scanner lector = new Scanner(System.in).useDelimiter("\n");
        boolean fin = false;

        while (!fin) {
            view.printMenu();

            int option = lector.nextInt();
            lector.nextLine(); // Consumir el salto de línea después de leer la opción

            switch (option) {
                case 1:
                    cargarDatos(lector);
                    break;

                case 2:
                    req1(lector);
                    break;

                case 3:
                    req2();
                    break;

                case 4:
                    req3(lector);
                    break;

                case 5:
                    req4();
                    break;

                case 6:
                    req5(lector);
                    break;

                case 7:
                    salir(lector);
                    fin = true;
                    break;

                default:
                    view.printMessage("--------- \n Opcion Invalida !! \n---------");
                    break;
            }
        }
    }

    private void cargarDatos(Scanner lector) {
        view.printMessage("--------- \nCargar datos");
        modelo = new Modelo(1);
        try {
            modelo.cargar();
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.printModelo(modelo);
    }

    private void req1(Scanner lector) {
        view.printMessage("--------- \nIngrese el nombre del primer punto de conexión");
        String punto1 = lector.next();
        lector.nextLine();

        view.printMessage("--------- \nIngrese el nombre del segundo punto de conexión");
        String punto2 = lector.next();
        lector.nextLine();

        String res1 = modelo.req1String(punto1, punto2);
        view.printMessage(res1);
    }

    private void req2() {
        String res2 = modelo.req2String();
        view.printMessage(res2);
    }

    private void req3(Scanner lector) {
        view.printMessage("--------- \nIngrese el nombre del primer país");
        String pais1 = lector.next();
        lector.nextLine();

        view.printMessage("--------- \nIngrese el nombre del segundo país");
        String pais2 = lector.next();
        lector.nextLine();

        String res3 = modelo.req3String(pais1, pais2);
        view.printMessage(res3);
    }

    private void req4() {
        String res4 = modelo.req4String();
        view.printMessage(res4);
    }

    private void req5(Scanner lector) {
        view.printMessage("--------- \nIngrese el nombre del punto de conexión");
        String landing = lector.next();
        lector.nextLine();
        String res5 = modelo.req5String(landing);
        view.printMessage(res5);
    }

    private void salir(Scanner lector) {
        view.printMessage("--------- \n Hasta pronto !! \n---------");
        lector.close();
    }
}
