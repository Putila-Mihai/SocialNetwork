import domain.User;
import domain.validators.UtilizatorValidator;
import repository.InMemoryRepository;
import service.UserService;
import ui.UI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        InMemoryRepository<Long, User> repo = new InMemoryRepository<>(new UtilizatorValidator());
        UserService userService = new UserService(repo);
        UI ui = new UI(userService);

        userService.addUser(new User("Mihai", "Nemteam"));
        userService.addUser(new User("Alin", "BaiatFin"));
        userService.addUser(new User("Stefan", "Brailean"));
        userService.addUser(new User("Decebal", "Aluion"));
        userService.addUser(new User("Andreeea", "Eleena"));
        userService.addUser(new User("Ana", "Amaria"));
        userService.addUser(new User("Ana", "MariaIoana"));


        ui.run();
    }

}
