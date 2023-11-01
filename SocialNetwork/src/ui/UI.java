package ui;

import domain.User;
import domain.validators.ValidationException;
import service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;

import static java.lang.System.exit;

public class UI {

    private final UserService userService;

    public UI(UserService userService) {
        this.userService = userService;
    }

    private void PrintMenu() {
        System.out.println("Alegeti o comanda: ");
        System.out.println("[0]Opreste programul");
        System.out.println("\t[1].Adaugauga utilizator\n\t[2].Sterge utilizator");
        System.out.println("\t[3].Adaugauga pritenie\n\t[4].Sterge pritenie");
        System.out.println("\t[5]Afiseaza numarul de comunitati");
        System.out.println("\t[6]Afiseaza cea mai sociabila comunitate\n");
        System.out.println("\t[7]Afiseaza toti utilizatorii");
        System.out.println("\t[8]Afiseaza toate priteniile\n\t[9]Afiseaza utilizartorii ce au minim N priteni");

    }

    private Integer ReadUserInput() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int cmd = -1;
        try {
            String Input = bufferedReader.readLine();
            cmd = Integer.parseInt(Input);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Comanda trebuie sa fie unul din numerele aferente optiunilor");
        }
        return cmd;
    }

    private void addUser() {
        System.out.println("Introduceti numele:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name = bufferedReader.readLine();
            System.out.println("prenumele:");
            String Lname = bufferedReader.readLine();
            User user = new User(name, Lname);
//            var name2 = userService.addUser(user);
            var name2 = userService.addUser(user);
            if (!name2.isPresent()) {
                System.out.println("Utilizator deja existent");
            } else {
                System.out.println("utilizatorul a fost adaugat cu succes");
            }
        } catch (IOException | IllegalArgumentException | ValidationException e) {
            System.out.println("datele introduse nu sunt valide");
        }
    }

    private void deleteUser() {
        System.out.println("Introduceti id-ul utilizatorului ce va fi sters");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = bufferedReader.readLine();
            Long id = Long.parseLong(input);
            Optional<User> name = userService.removeEntity(id);
            if (!name.isPresent()) {
                System.out.println("Utilizator inexistent" + id);
            } else {
                System.out.println("utilizatorul" + name.toString() + "A fost sters");
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Id-ul trebuie sa fie un numar natural");
        }
    }

    private void addFriendship() {
        System.out.println(userService.getEntities());
        System.out.println("Introduceti id-urile celor doi utilizatori\nid1=");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = bufferedReader.readLine();
            Long id1 = Long.parseLong(input);
            System.out.println("id2=");
            input = bufferedReader.readLine();
            Long id2 = Long.parseLong(input);
            userService.addFriendship(id1, id2);
            System.out.println("Pritenie adaugata cu succes");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("id-urile trebuie sa fie numere naturale");
        } catch (ValidationException e) {
            System.out.println("Prietenie exista deja");
        }
    }

    private void removeFriendship() {
        System.out.println(userService.getFriendships() + "\n id1=");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = bufferedReader.readLine();
            Long id1 = Long.parseLong(input);
            System.out.println("id2=");
            input = bufferedReader.readLine();
            Long id2 = Long.parseLong(input);
            userService.removeFriendship(id1, id2);
            System.out.println("Prietenia a fost stearsa cu succes");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Trebuie introduse id-uri existente");
        }
    }

    private void NumberOfCommunities() {
        System.out.println("Numarul de comunitati = " + userService.NOCommunities() + "\n");
    }

    private void BiggestCommunity() {
        System.out.println("Cea mai mare comunitate este formata din:" + userService.MostSociableCommunity());
    }

    private void UserMoreNFriends() {
        System.out.println("Introduceti numarul minim de prieteni:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String s = "";
        try {
            String input = bufferedReader.readLine();
            Integer nr = Integer.parseInt(input);
            s = userService.MinNFriends(nr);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Trebuie introduse id-uri existente");
        } finally {
            System.out.println("Utilizatorii ce au peste N priteni sunt:");
            System.out.println(s);
        }

    }

    private void execute(Integer cmd) {
        switch (cmd) {
            case 0:
                exit(1);
            case 1:
                addUser();
                break;
            case 2:
                deleteUser();
                break;
            case 3:
                addFriendship();
                break;
            case 4:
                removeFriendship();
                break;
            case 5:
                NumberOfCommunities();
                break;
            case 6:
                BiggestCommunity();
                break;
            case 7:
                System.out.println(userService.getEntities());
                break;
            case 8:
                String result = userService.getFriendships();
                System.out.println(!result.isEmpty() ? result : "Nu exista utilizatori cu prieteni");
                return;
            case 9:
                UserMoreNFriends();
                break;
            default:
                System.out.println("Comanda inexistenta");
        }
    }

    public void run() {
        boolean stop = false;
        while (!stop) {
            PrintMenu();
            Integer cmd = ReadUserInput();
            execute(cmd);
        }
    }

}
