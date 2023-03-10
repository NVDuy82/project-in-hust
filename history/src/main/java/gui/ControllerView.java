package gui;

import database.dao.*;
import database.models.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerView implements Initializable {
    @FXML
    private ListView<Models> listView;

    @FXML
    private ListView<Models> searchSuggestions;

    @FXML
    private TextField searchTextField;

    private ArrayList<Models> resultSuggestionsModels;

    private ObservableList<Models> modelsObservableList = FXCollections.observableArrayList();
    private ObservableList<Models> listSuggestions = FXCollections.observableArrayList();

    private ArrayList<CharacterModels> listCharacter = CharacterDAO.getInstance().selectAll();
    private ArrayList<DynastyModels> listDynasty = DynastyDAO.getInstance().selectAll();
    private ArrayList<EventModels> listEvent = EventDAO.getInstance().selectAll();
    private ArrayList<FestivalModels> listFestival = FestivalDAO.getInstance().selectAll();
    private ArrayList<PlaceModels> listPlace = PlaceDAO.getInstance().selectAll();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setItems(modelsObservableList);

        searchSuggestions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        searchSuggestions.setItems(listSuggestions);

        searchTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldb, Boolean newb) {
                if (newb) {
                    // show suggestions for search
                    searchSuggestions.setVisible(true);
                    if (resultSuggestionsModels != null) {
                        listSuggestions.removeAll(listSuggestions);
                        listSuggestions.addAll(resultSuggestionsModels);
                    }
                    searchSuggestions.getSelectionModel().clearSelection();
                    searchSuggestions.setPrefHeight(listSuggestions.size() * 24);
                    searchSuggestions.setMaxHeight(200);
                } else {
                    // hide suggestions
                    if (!searchSuggestions.isFocused()) {
                        listSuggestions.removeAll(listSuggestions);
                        searchSuggestions.setVisible(false);
                    }
                }
            }
        });

        searchSuggestions.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldb, Boolean newb) {
                if (oldb) {
                    // hide suggestions
                    listSuggestions.removeAll(listSuggestions);
                    searchSuggestions.setVisible(false);
                }
            }
        });
    }

    /**
     * Hi???n th??? t???t c??? nh??n v???t l???ch s???
     * @param event
     */
    public void searchCharacter(ActionEvent event) {
        modelsObservableList.removeAll(modelsObservableList);
        modelsObservableList.addAll(listCharacter);
        listView.scrollTo(0);
    }

    /**
     * Hi???n th??? t???t c??? tri???u ?????i l???ch s???
     * @param event
     */
    public void searchDynasty(ActionEvent event) {
        modelsObservableList.removeAll(modelsObservableList);
        modelsObservableList.addAll(listDynasty);
        listView.scrollTo(0);
    }

    /**
     * Hi???n th??? t???t c??? s??? ki???n l???ch s???
     * @param event
     */
    public void searchEvent(ActionEvent event) {
        modelsObservableList.removeAll(modelsObservableList);
        modelsObservableList.addAll(listEvent);
        listView.scrollTo(0);
    }

    /**
     * Hi???n th??? t???t c??? l??? h???i
     * @param event
     */
    public void searchFestival(ActionEvent event) {
        modelsObservableList.removeAll(modelsObservableList);
        modelsObservableList.addAll(listFestival);
        listView.scrollTo(0);
    }

    /**
     * Hi???n th??? t???t c??? di t??ch l???ch s???
     * @param event
     */
    public void searchPlace(ActionEvent event) {
        modelsObservableList.removeAll(modelsObservableList);
        modelsObservableList.addAll(listPlace);
        listView.scrollTo(0);
    }

    /**
     * Hi???n th??? th??ng tin ?????i t?????ng ???????c ch???n
     * Trong tr?????ng h???p kh??ng c?? ?????i t?????ng ???????c ch???n, hi???n th??? ra th??ng b??o
     * @param event
     * @throws IOException
     */
    public void detailModel(Event event) throws IOException {
        // get stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // create scene
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("detail.fxml"));
        Parent detailParent = fxmlLoader.load();
        Scene scene = new Scene(detailParent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

        // change scene
        ControllerDetail controllerDetail = fxmlLoader.getController();
        Models selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            controllerDetail.setModel(selected);

            stage.setTitle("Detail");
            stage.setScene(scene);
        } else {
            // object has not been selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("XEM TH??NG TIN");
            alert.setHeaderText("Notification");
            alert.setContentText("B???n ch??a ch???n ?????i t?????ng n??o\nH??y ch???n 1 ?????i t?????ng ????? xem th??ng tin");
            alert.show();
        }
    }

    /**
     * T??m c??c ?????i t?????ng c?? t??? kh??a t????ng ???ng theo c???t
     * @param key t??? kh??a
     * @param columns c???t ch???a t??? kh??a c???n t??m
     * @return c??c ?????i t?????ng c?? ch???a t??? kh??a
     */
    public ArrayList<Models> listSearchResult(String key, String... columns) {
        ArrayList<Models> result = new ArrayList<>();
        StringBuilder avoidRepeating = new StringBuilder();
        for (String column : columns) {
            String condition = column + " LIKE " + "\"%" + key + "%\" " + avoidRepeating;

            result.addAll(CharacterDAO.getInstance().selectByCondition(condition));
            result.addAll(DynastyDAO.getInstance().selectByCondition(condition));
            result.addAll(EventDAO.getInstance().selectByCondition(condition));
            result.addAll(FestivalDAO.getInstance().selectByCondition(condition));
            result.addAll(PlaceDAO.getInstance().selectByCondition(condition));

            avoidRepeating.append("AND " + column + " NOT LIKE " + "\"%" + key + "%\" ");
        }

        return result;
    }

    /**
     * T??m c??c ?????i t?????ng c?? th??ng tin ch???a t??? kh??a v?? hi???n th??? ra listView
     * @param key t??? kh??a
     */
    public void search(String key) {
        ArrayList<Models> result = listSearchResult(key, "id", "name", "information");

        modelsObservableList.removeAll(modelsObservableList);
        modelsObservableList.addAll(result);
        listView.scrollTo(0);
    }

    /**
     * T??m ki???m v???i thanh t??m ki???m
     * @param event
     */
    public void searchButton(ActionEvent event) {
        String key = searchTextField.getText();

        listSuggestions.removeAll(listSuggestions);
        searchSuggestions.setVisible(false);

        listView.requestFocus();
        search(key.trim());

        if (modelsObservableList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("T??M KI???M");
            alert.setHeaderText("Notification");
            alert.setContentText("Kh??ng t??m th???y " + "\"" + key + "\"" + " trong d??? li???u");
            alert.show();
        }
    }

    /**
     * G???i ?? t??m ki???m
     * @param event
     */
    public void pressKeyTextField(KeyEvent event) {
        String key = searchTextField.getText().trim();
        if (key.length() > 0) {
            resultSuggestionsModels = listSearchResult(key,  "name");

            listSuggestions.removeAll(listSuggestions);
            listSuggestions.addAll(resultSuggestionsModels);
        } else {
            listSuggestions.removeAll(listSuggestions);
        }
        searchSuggestions.setPrefHeight(listSuggestions.size() * 24);
    }

    /**
     * L???a ch???n g???i ?? t??m ki???m v???i ph??m m??i t??n
     * @param event
     */
    public void textFieldUpDown(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            if (searchSuggestions.getSelectionModel().getSelectedItem() != null) {
                searchSuggestions.getSelectionModel().selectPrevious();
            } else {
                searchSuggestions.getSelectionModel().selectLast();
            }
            searchTextField.setText(searchSuggestions.getSelectionModel().getSelectedItem().getName());
        } else if (event.getCode() == KeyCode.DOWN) {
            if (searchSuggestions.getSelectionModel().getSelectedItem() != null) {
                searchSuggestions.getSelectionModel().selectNext();
            } else {
                searchSuggestions.getSelectionModel().selectFirst();
            }
            searchTextField.setText(searchSuggestions.getSelectionModel().getSelectedItem().getName());
        }
    }

    /**
     * T??m ki???m v???i t??? kh??a g???i ??
     */
    public void clickSuggestion() {
        Models selected = searchSuggestions.getSelectionModel().getSelectedItem();

        listSuggestions.removeAll(listSuggestions);
        searchSuggestions.setVisible(false);

        String key = selected.getName();
        searchTextField.setText(key);

        listView.requestFocus();
        search(key.trim());
    }

    /**
     * L??m m???i thanh t??m ki???m khi ???n n??t tr??n g???i ?? t??m ki???m
     * N???u ???n ENTER, t??m ki???m v???i g???i ?? ???? ch???n
     * @param event
     */
    public void refreshSearchTextField(KeyEvent event) {
        Models selected = searchSuggestions.getSelectionModel().getSelectedItem();
        searchTextField.setText(selected.getName());
        if (event.getCode() == KeyCode.ENTER) {
            search(selected.getName());

            listSuggestions.removeAll(listSuggestions);
            searchSuggestions.setVisible(false);
            listView.requestFocus();
        }
    }

    /**
     * Hi???n th??? th??ng tin ?????i t?????ng khi ???n ENTER tr??n danh s??ch t??m ki???m
     * @param event
     * @throws IOException
     */
    public void checkEnterListView(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            detailModel(event);
        }
    }

    /**
     * Ch???n k???t qu??? t??m k???m ?????u ti??n
     * @param event
     */
    public void selectFirst(ActionEvent event) {
        listView.getSelectionModel().selectFirst();
    }

    /**
     * Ch???n k???t qu??? t??m k???m cu???i c??ng
     * @param event
     */
    public void selectLast(ActionEvent event) {
        listView.getSelectionModel().selectLast();
    }

    /**
     * Ch???n k???t qu??? t??m k???m ph??a tr??n
     * @param event
     */
    public void selectPrevious(ActionEvent event) {
        listView.getSelectionModel().selectPrevious();
    }

    /**
     * Ch???n k???t qu??? t??m k???m ph??a d?????i
     * @param event
     */
    public void selectNext(ActionEvent event) {
        listView.getSelectionModel().selectNext();
    }

    /**
     * B??? ch???n ?????i t?????ng t???i danh s??ch t??m ki???m
     * @param event
     */
    public void clearSelection(ActionEvent event) {
        listView.scrollTo(0);
        listView.getSelectionModel().clearSelection();
    }
}
