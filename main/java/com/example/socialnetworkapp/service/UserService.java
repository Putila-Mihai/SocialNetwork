package com.example.socialnetworkapp.service;

import com.example.socialnetworkapp.Observer.Observable;
import com.example.socialnetworkapp.domain.*;
import com.example.socialnetworkapp.domain.exceptions.AlreadyFriendsException;
import com.example.socialnetworkapp.domain.exceptions.RejectedRequestException;
import com.example.socialnetworkapp.domain.validators.ValidationException;
import com.example.socialnetworkapp.repository.*;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.Pageable;
import javafx.event.ActionEvent;

import java.security.InvalidParameterException;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService implements Observable {

    //    private final UserDBRepository userRepository;
    private final UserDBPageRepository userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;

    private final MesajDBRepository mesajDBRepository;
    private final CerereDBRepository cerereDBRepository;

    private static UserService service;
    private final String dbUrl = "jdbc:postgresql://localhost:5432/socialnetwork";
    private final String dbUser = "postgres";
    private final String dbPassword = "postgres";

    public static UserService getInstance() {
        if (service == null)
            service = new UserService();
        return service;
    }

    private UserService() {
//        userRepository = new UserDBRepository(this.dbUrl, this.dbUser, this.dbPassword);
        userRepository = new UserDBPageRepository(this.dbUrl, this.dbUser, this.dbPassword);
        friendshipRepository = new FriendsDBRepository(this.dbUrl, this.dbUser, this.dbPassword);
        cerereDBRepository = new CerereDBRepository(dbUrl, dbUser, dbPassword, userRepository);
        mesajDBRepository = new MesajDBRepository(dbUrl, dbUser, dbPassword, userRepository);
    }

    /**
     * Construieste un  string ce contine toate informatiile entitatilor din repository
     *
     * @return String
     */
    public boolean checkPassword(String email,String dbPassword){
        return userRepository.checkpassword(email,dbPassword);
    }
    public Iterable<User> getAllUser() {
        return userRepository.findAll();
    }

    public Iterable<Friendship> getAllFrienships() {
        return friendshipRepository.findAll();
    }

    public String getEntities() {

        Iterable<User> iterable = userRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder();

        for (User user : iterable) {
            stringBuilder.append(user.toString()).append(("id: ")).append(user.getId()).append("\n");
        }
        return stringBuilder.toString();
    }

    public Optional<User> editUser(User user) {
        Optional<User> u = userRepository.update(user);
        return u;
    }

    /**
     * Adauga un nou utilizator in repo
     *
     * @param user userul ce urmeaza sa fie acceptat
     * @return utilizatorul daca acesta exista deja sau nimic in caza contrar
     */
    public Optional<User> addUser(User user) {
        user.setId(UUID.nameUUIDFromBytes((user.getfirstName() + user.getLastName()).getBytes()).getMostSignificantBits());
        Optional<User> u = userRepository.save(user);
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
            Friendship friendship = new Friendship();
            friendship.setId(new Tuple<Long, Long>(user1.get().getId(), user2.get().getId()));
            Iterable<Friendship> friends = friendshipRepository.findAll();
            for (Friendship f : friends) {
                if (f == friendship)
                    throw new RuntimeException();
            }
            friendshipRepository.save(friendship);
        }
        notifyAllObservers();
    }

    /**
     * sterge o prietenie
     *
     * @param id1 id-ul primului utilizator
     * @param id2 id-ul al doilea utilizator
     */
    public void removeFriendship(Long id1, Long id2) throws Exception {
        Optional<User> user1 = userRepository.findOne(id1);
        Optional<User> user2 = userRepository.findOne(id2);
        if (user1.isPresent() && user2.isPresent()) {
            friendshipRepository.delete(new Tuple<Long, Long>(id1, id2));
        } else
            throw new Exception("Nu exista acesti utilizatori");
        notifyAllObservers();
    }


    /**
     * formeaza un string cu toate prieteniile
     *
     * @return string-ul format
     */
    public String getFriendships() {
        StringBuilder string = new StringBuilder();
        Iterable<Friendship> friends = friendshipRepository.findAll();
        for (Friendship f : friends) {
            Optional<User> s1 = userRepository.findOne(f.getId().getLeft());
            Optional<User> s2 = userRepository.findOne(f.getId().getRight());
            string.append(s1.get().getfirstName() + " " + s2.get().getfirstName() + "  " + f.getSDate());
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
        System.out.println(friendships);
        return noConnectedComponents(friendships);
    }

    /**
     * formeaza lista de prietenii
     *
     * @return lista formata
     */
    private ArrayList<Friendship> getFriendshipList() {
        ArrayList<Friendship> friendships = new ArrayList<>();
//
//        for (User user : userRepository.findAll()) {
//            for (User friend : user.getFriends()) {
//                Friendship friendship = new Friendship();
//                friendship.setId(new Tuple<Long, Long>(user.getId(), friend.getId()));
//                friendships.add(friendship);
//            }
//        }
        for (Friendship f : friendshipRepository.findAll()) {
            friendships.add(f);
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
            Long u1 = friendship.getId().getLeft().longValue();
            Long u2 = friendship.getId().getRight().longValue();
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
        if (saveComponent) currentComponent.add(node);
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
     * @param id minimul de prieteni
     * @return string-ul
     */
    public String MinNFriends(Long id, int luna) {
        Iterable<Friendship> friends = friendshipRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder();
        List<Friendship> list = StreamSupport.stream(friends.spliterator(), false).filter(friendship -> {
            return friendship.getId().getLeft().longValue() == id || friendship.getId().getRight().longValue() == id;
        }).collect(Collectors.toList());
        List<Friendship> friendsof = list.stream().filter(friendship -> friendship.getDate().getMonth() == Month.of(luna)).collect(Collectors.toList());
        stringBuilder.append(userRepository.findOne(id).get().getfirstName() + "|");
        friendsof.forEach(friendship -> {
            if (friendship.getId().getLeft().longValue() == id)
                stringBuilder.append(userRepository.findOne(friendship.getId().getRight()).get().getfirstName()).append("|").append(friendship.getSDate()).append("\n\t\t");
            else
                stringBuilder.append(userRepository.findOne(friendship.getId().getLeft()).get().getfirstName()).append("|").append(friendship.getSDate()).append("\n\t\t");
        });
        return stringBuilder.toString();
    }

    public String usersByName(String nume) {
        Set<User> users = userRepository.getByName(nume);
        StringBuilder stringBuilder = new StringBuilder();
        users.forEach(user -> stringBuilder.append(user.getfirstName() + " " + user.getLastName() + "\n"));
        return stringBuilder.toString();
    }

    public User removeUser(long userId) {
        Optional<User> usr = userRepository.delete(userId);
        if (usr.isEmpty())
            throw new InvalidParameterException();
        notifyAllObservers();
        return usr.get();
    }

    public void updateUser(User user) throws ValidationException {
        userRepository.update(user);
        notifyAllObservers();
    }

    public Iterable<Mesaj> getAllMesaje() {
        return mesajDBRepository.findAll();
    }

    public Iterable<Cerere> getAllCereri() {
        return cerereDBRepository.findAll();
    }

    public void SendMesaj(Long id1, Long id2, String text, Long reply) throws Exception {
        Optional<User> u1 = userRepository.findOne(id1);
        Optional<User> u2 = userRepository.findOne(id2);
        if (!u1.isPresent() || !u2.isPresent()) {
            throw new Exception("Utilizatorii nu exista!");
        }

        if (Objects.equals(u1.get().getId(), u2.get().getId())) {
            throw new Exception("Utilizatori trebuie sa fie diferiti");
        }
        Mesaj m = new Mesaj(text, u1.get(), u2.get(), reply);
        mesajDBRepository.save(m);
        notifyAllObservers();
    }

    public User findUser(long id) throws InvalidParameterException {
        Optional<User> user = userRepository.findOne(id);
        if (user.isEmpty())
            throw new InvalidParameterException("Nu exista useri cu acest id");
        return user.get();
    }

    public List<User> getUserFriends(long id) {
        ArrayList<Friendship> friendships = new ArrayList<Friendship>();
        friendshipRepository.findAll().forEach(friendships::add);
        List<Friendship> filtered = friendships.stream().filter(f -> f.getUser1() == id || f.getUser2() == id).toList();
        ArrayList<User> friends = new ArrayList<>();
        filtered.forEach(x -> friends.add(findUser(x.getFriendOf(id))));
        return friends;
    }


    public void SendRequest(User u1, User u2) throws AlreadyFriendsException {
        if (!getUserFriends(u1.getId()).contains(u2)) {
            cerereDBRepository.save(new Cerere(u1, u2, Status.PENDING));
        } else throw new AlreadyFriendsException();
        notifyAllObservers();
    }

    public void acceptFriendRequest(Cerere cerere) throws RejectedRequestException {
        if (cerere.getStatus() == Status.PENDING) {
//            updateCerere(cerere);
            if (!UserService.getInstance().getUserFriends(cerere.getUser1().getId()).contains(UserService.getInstance().findUser(cerere.getUser2().getId()))) {
                UserService.getInstance().addFriendship(cerere.getUser1().getId(), cerere.getUser2().getId());
            } else throw new RejectedRequestException("Deja exista prietenia");
        } else throw new RejectedRequestException("cerere respinsa deja");
    }

    public void updateCerere(Cerere cerere) {
        cerereDBRepository.update(cerere);
        notifyAllObservers();
    }

    public void updateFriendRequest(Cerere cerere) {
        cerereDBRepository.update(cerere);
        notifyAllObservers();
    }

    public Page<User> getUsersOnPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserByEmail(String email) {
        Iterable<User> users = userRepository.findAll();
        for (User u : users)
            if (u.getEmail().equals(email))
                return u;
        return null;
    }
    public void acceptFriendship(String email1, String email2) {
        try {
            removeFriendship(getUserByEmail(email1).getId(),getUserByEmail(email2).getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addFriendship(getUserByEmail(email1).getId(),getUserByEmail(email2).getId());
        for (Cerere cerere:cerereDBRepository.findAll()) {
            if ((cerere.getUser1().getEmail().equals(email1) && cerere.getUser2().getEmail().equals(email2))
                    || (cerere.getUser2().getEmail().equals(email1) && cerere.getUser1().getEmail().equals(email2))) {
                cerere.setStatus(Status.ACCEPTED);
                updateCerere(cerere);
            }
        }
        notifyAllObservers();
    }

}
