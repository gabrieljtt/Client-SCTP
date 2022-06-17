import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String args[]){

        Scanner input = new Scanner(System.in);

        System.out.println("Entre com endereço:");
        String s1 = input.nextLine();
        System.out.println("Entre com a porta:");
        String s2 = input.nextLine();

        Client client = new Client(s1, s2);

        try {
            client.connection();
            System.out.println("Conexão criada!");
        } catch (IOException e ){
            System.out.println("Erro de conexão!");
            e.printStackTrace();
        }

        while(true){
            String s3 = input.nextLine();

            try {
                client.send(s3);
                if(s3.equalsIgnoreCase("exit")){
                    break;
                }
            } catch (IOException e){
                System.out.println("Erro no envio de dados!");
                e.printStackTrace();
            }
        }
    }
}