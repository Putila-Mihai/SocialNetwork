package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.ValidationException;
import repository.InMemoryRepository;
import repository.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService implements Service {
    private final Repository<Long, User> userRepository;

    public UserService(InMemoryRepository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Construieste un  string ce contine toate informatiile entitatilor din repository
     *
     * @return String
     */
    public String getEntities() {

        Iterable<User> iterable = userRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder();

        for (User user : iterable) {
            stringBuilder.append(user.toString()).append(("id: ")).append(user.getId()).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Adauga un nou utilizator in repo
     *
     * @param user userul ce urmeaza sa fie acceptat
     * @return utilizatorul daca acesta exista deja sau nimic in caza contrar
     */
    public Optional<User> addUser(User user) {
        user.setId(UUID.nameUUIDFromBytes((user.getFirstName() + user.getLastName()).getBytes()).getMostSignificantBits());
        Optional<User> u = userRepository.save(user);
//        return u.orElse(null);
        return u;
    }

    /**
     * Adauga o noua prietenie
     *
     * @param id1 userul ce urmeaza sa fie adaugat in acea prietenie
     * @param id2 celalat user ce va fi adaugat in prietenie
     */
    public void addFriendship(Long id1, Long id2) {
        Optional<User> user1 = userRepository.findOne(id1);
        Optional<User> user2 = userRepository.findOne(id2);
        if (user1.isPresent() && user2.isPresent()) {
            if (!(user1.get().getFriends().contains(user2.get()))) {
                user1.get().addFriend(user2.get());
                user2.get().addFriend(user1.get());
            } else
                throw new ValidationException();
        }
    }

    /**
     * sterge o prietenie
     *
     * @param id1 id-ul primului utilizator
     * @param id2 id-ul al doilea utilizator
     */
    public void removeFriendship(Long id1, Long id2) {
        Optional<User> user1 = userRepository.findOne(id1);
        Optional<User> user2 = userRepository.findOne(id2);
        if (user1.isPresent() && user2.isPresent()) {
            user2.get().removeFriend(user1.get());
            user1.get().removeFriend(user2.get());
        }
    }

    /**
     * formeaza un string cu toate prieteniile
     *
     * @return string-ul format
     */
    public String getFriendships() {
        StringBuilder string = new StringBuilder();
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            string.append(user.getFirstName() + " " + user.getLastName()).append(" are pritenii: ");
            for (User x : user.getFriends())
                string.append(x.getFirstName() + " " + x.getLastName() + " " + x.getId().toString() + "\n\t\t\t\t");
            string.append("\n");
        }
        return string.toString();
    }

    /**
     * sterge o entitate din repository
     *
     * @param id id-ul entitatii
     * @return entitatea stearsa sau nimic daca nu exista aceasta
     */
    public Optional<User> removeEntity(Long id) {
        Optional<User> u = userRepository.delete(id);
//        return u.orElse(null);
        return u;
    }

    /**
     * calculeaza numarul de comunitati
     *
     * @return numarul comunitatilor
     */
    public int NOCommunities() {
        ArrayList<Friendship> friendships = getFriendshipList();
        return noConnectedComponents(friendships);
    }

    /**
     * formeaza lista de prietenii
     *
     * @return lista formata
     */
    private ArrayList<Friendship> getFriendshipList() {
        ArrayList<Friendship> friendships = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            for (User friend : user.getFriends()) {
                Friendship friendship = new Friendship();
                friendship.setId(new Tuple<Long, Long>(user.getId(), friend.getId()));
                friendships.add(friendship);
            }
        }

        return friendships;
    }

    /**
     * calculeaza componetele conexe
     *
     * @param friendships lista de prietenii
     * @return numarul componentelor conexe
     */
    private static int noConnectedComponents(ArrayList<Friendship> friendships) {
        Map<Long, ArrayList<Long>> graph = new HashMap<>();
        createGraph(friendships, graph);
        Set<Long> visited = new HashSet<>();
        int components = 0;
        // Perform DFS to count connected components.
        for (Long node : graph.keySet()) {
            if (!visited.contains(node)) {
                components++;
                dfs(node, visited, graph, false, new ArrayList<>());
            }
        }

        return components;
    }

    /**
     * creaza graful respectiv prieteniilor
     *
     * @param friendships lista de prietenii
     * @param graph       graful ce va rezulta
     */
    private static void createGraph(ArrayList<Friendship> friendships, Map<Long, ArrayList<Long>> graph) {
        if (friendships == null || friendships.isEmpty()) {
        }
        for (Friendship friendship : friendships) {
            Long u1 = friendship.getId().getLeft();
            Long u2 = friendship.getId().getRight();
            addEdge(u1, u2, graph);
            addEdge(u2, u2, graph);
        }
    }

    private static void addEdge(Long source, Long destination, Map<Long, ArrayList<Long>> graph) {
        graph.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
    }

    private static void dfs(Long node, Set<Long> visited, Map<Long, ArrayList<Long>> graph, boolean saveComponent, ArrayList<Long> currentComponent) {
        visited.add(node);

        if (graph.containsKey(node)) {
            for (Long neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfs(neighbor, visited, graph, saveComponent, currentComponent);
                }
            }
        }
        if (saveComponent)
            currentComponent.add(node);
    }

    /**
     * formeaza un string din cea mai sociabila comunitate
     *
     * @return string-ul format
     */
    public String MostSociableCommunity() {
        ArrayList<Friendship> friendships = getFriendshipList();
        List<Long> community = mostSocialComunnity(friendships);
        StringBuilder ret = new StringBuilder();
        for (Long x : community) {
            Optional u = userRepository.findOne(x);
            u.ifPresent(k -> ret.append(u.get().toString()).append("\n"));
        }
        return ret.toString();
    }

    private List<Long> mostSocialComunnity(ArrayList<Friendship> friendships) {
        Map<Long, ArrayList<Long>> graph = new HashMap<>();
        createGraph(friendships, graph);
        ArrayList<Long> largestComponent = new ArrayList<>();
        ArrayList<Long> currentComponent = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for (Long node : graph.keySet()) {
            if (!visited.contains(node)) {
                currentComponent.clear();
                dfs(node, visited, graph, true, currentComponent);
                if (currentComponent.size() > largestComponent.size()) {
                    largestComponent = new ArrayList<>(currentComponent);
                }
            }
        }

        return largestComponent;
    }

    /**
     * formeaza un string pentru utilizatorii ce au minimul de prieteni
     *
     * @param nrPrieteni minimul de prieteni
     * @return string-ul
     */
    public String MinNFriends(int nrPrieteni) {
        Iterable<User> iterable = userRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder();
        Predicate<User> predicate = user -> user.getFriends().size() >= nrPrieteni;
        List<User> list = StreamSupport.stream(iterable.spliterator(), false).filter(predicate).collect(Collectors.toList());
        list.forEach(user -> stringBuilder.append(user.toString()).append(" Prieteni").append(user.getFriends().size()).append("\n"));
        return stringBuilder.toString();
    }

}
