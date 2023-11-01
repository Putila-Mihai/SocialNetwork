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

        ui.run();
    }

}
