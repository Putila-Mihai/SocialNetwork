package com.example.socialnetworkapp.controlers;

import com.example.socialnetworkapp.Observer.Observer;
import com.example.socialnetworkapp.domain.Friendship;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.Pageable;
import com.example.socialnetworkapp.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import com.example.socialnetworkapp.domain.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Application implements Initializable, Observer {
    @FXML
    private Text username;

    @FXML
    private ListView<String> friendsList;

    @FXML
    private ListView<String> friendsRequestList;

    @FXML
    private ListView<String> userList;
    @FXML
    private ListView<String> messagesFriendList;

    private final ObservableList<String> friendsObs = FXCollections.observableArrayList();

    private final ObservableList<String> friendsReqObs = FXCollections.observableArrayList();


    private final ObservableList<String> userObs = FXCollections.observableArrayList();
    private final ObservableList<String> mesOBS = FXCollections.observableArrayList();
    private User user;
    @FXML
    private int NrOfRecords = 5;
    @FXML
    private int curentPage = 0;
    @FXML
    private int totalNr;
    @FXML
    private Page<User> page;
    @FXML
    private ComboBox<String> comboBox;

    public void initApp(User user) {

        this.user = user;
        username.setText(user.getFirstName() + " " + user.getLastName());
        UserService.getInstance().getUserFriends(user.getId()).forEach(u -> friendsObs.add(u.getFirstName() + " " + u.getLastName() + " " + u.getEmail()));
        UserService.getInstance().getAllCereri().forEach(cerere -> {
            if (cerere.getUser2().equals(user))
                friendsReqObs.add(cerere.getUser1().getfirstName() + " " + cerere.getUser1().getLastName() + " " + cerere.getUser1().getEmail() + " " + cerere.getStatus().toString());
        });
        UserService.getInstance().getAllMesaje().forEach(m -> {
            if (m.getFrom().equals(user)) mesOBS.add(m.getText() + " " + m.getTo().getfirstName());
        });
        List<User> UserList = StreamSupport.stream(page.getContent().spliterator(), false).toList();
        UserList.forEach(u -> {
            userObs.add(u.getfirstName() + " " + u.getlastName() + " " + u.getEmail());
        });
        userList.setItems(userObs);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        page = UserService.getInstance().getUsersOnPage(new Pageable(curentPage, NrOfRecords));
        totalNr = page.getTotalNumberOfElements();
        friendsList.setItems(friendsObs);
        friendsRequestList.setItems(friendsReqObs);
        userList.setItems(userObs);
        messagesFriendList.setItems(mesOBS);
        UserService.getInstance().addObserver(this);

    }

    @Override
    public void update() {
        friendsObs.clear();
        userObs.clear();
        friendsReqObs.clear();
        initApp(this.user);
    }

    public void sendRequest(ActionEvent actionEvent) {
        if (userList.getSelectionModel().getSelectedItems() == null)
            return;
        String user = userList.getSelectionModel().getSelectedItem().toString();
        String email = user.split(" ")[2];
        UserService.getInstance().SendRequest(this.user, UserService.getInstance().getUserByEmail(email));
    }

    public void acceptFriendRequest(ActionEvent actionEvent) {
        if (friendsRequestList.getSelectionModel().getSelectedItem() == null)
            return;
        String user = friendsRequestList.getSelectionModel().getSelectedItem().toString();
        String email = user.split(" ")[2];
        String status = user.split(" ")[3];
        if (!status.equals("PENDING"))
            return;
        UserService.getInstance().acceptFriendship(email, this.user.getEmail());
    }

    public void declineFriendRequest(ActionEvent actionEvent) {
//        if(friendsRequestList.getSelectionModel().getSelectedItem() == null)
//            return;
//
//        String userInfo = friendsRequestList.getSelectionModel().getSelectedItem().toString();
//        String email = userInfo.split(" ")[2];
//        String status = userInfo.split(" ")[4];
//
//        if(!status.equals("PENDING"))
//            return;
//
//        service.declineFriendRequest(user.getEmail(), email);
//
//        friendsObs.removeAll(friendsObs.stream().toList());
//        friendsReqObs.removeAll(friendsReqObs.stream().toList());
//        initApp(this.user);
    }

    @FXML
    private void prevAction(ActionEvent event) {
        if(curentPage == 0)
            return;
        curentPage= curentPage-1;
        page = UserService.getInstance().getUsersOnPage(new Pageable(curentPage, NrOfRecords));
        totalNr = page.getTotalNumberOfElements();
        update();
    }

    @FXML
    private void nextAction(ActionEvent event) {
        curentPage = curentPage+1;
        page = UserService.getInstance().getUsersOnPage(new Pageable(curentPage, NrOfRecords));
        totalNr = page.getTotalNumberOfElements();
        update();
    }

    @FXML
    private void comboBoxAction(ActionEvent event) {
        String selectedValue = comboBox.getSelectionModel().getSelectedItem();
        NrOfRecords = Integer.parseInt(selectedValue);
        page = UserService.getInstance().getUsersOnPage(new Pageable(curentPage, NrOfRecords));
        totalNr = page.getTotalNumberOfElements();
        update();

    }
}
