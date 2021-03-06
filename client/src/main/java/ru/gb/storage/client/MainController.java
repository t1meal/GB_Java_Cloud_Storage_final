package ru.gb.storage.client;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.gb.storage.commons.FilesInfo;
import ru.gb.storage.commons.messages.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;

public class MainController {
    @FXML
    private AnchorPane AuthorizationPane;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private AnchorPane WorkingPane;
    @FXML
    private TableView<FilesInfo> clientTable;
    @FXML
    private TableView<FilesInfo> cloudTable;
    @FXML
    private TextField pathFieldClient;
    @FXML
    private TextField pathFieldCloud;

    private static BooleanProperty isAuthorized;
    private final Path clientPath = FileSystems.getDefault().getRootDirectories().iterator().next();
    private final InitTable initTable = new InitTable();
    @FXML
    void initialize() {
        isAuthorized = new SimpleBooleanProperty(false);
        isAuthorized.addListener((observableValue, aBoolean, t1) -> MainController.this.changeScene());
        initTable.constructTable(clientTable);
        initTable.constructTable(cloudTable);
        pathFieldClient.setText(clientPath.toString());
        clientTable.getItems().addAll(initTable.fillingList(clientPath));
        cloudTable.getItems().addAll(initTable.fillingList(clientPath));
        ClientHandler.getInstance().setMainController(this);
        mouseListener();
    }
    @FXML
    private void tryToAuth() {
        System.out.println("Запрос авторизации к серверу!");
        NetworkController.send(new AuthorizationMessage(loginField.getText(), passwordField.getText()));
        NetworkController.send(new StorageMessage(null));
        loginField.clear();
        passwordField.clear();
    }
    @FXML
    private void sendToCloud() {
        String clientFilePath = clientTable.getSelectionModel().getSelectedItem().getPath().toString();
        String clientFileName = clientTable.getSelectionModel().getSelectedItem().getName();
        String currentCloudPath = pathFieldCloud.getText();
        NetworkController.send(new FileSendMessage(clientFilePath,clientFileName, currentCloudPath));
    }
    @FXML
    private void downloadFromCloud() {
        String fileName = cloudTable.getSelectionModel().getSelectedItem().getName();
        String downloadFilePath = pathFieldCloud.getText();
//        String reqPath = Paths.get(pathFieldServer.getText(), fileName).toString();
        NetworkController.send(new FileRequestMessage(downloadFilePath, fileName));
    }
    @FXML
    private void deleteClientFile(){
        FilesInfo file = clientTable.getSelectionModel().getSelectedItem();
        if (file != null){
            Path path = Paths.get(pathFieldClient.getText()).resolve(file.getName());
            deleteFile(path);
            refreshClientTable(Paths.get(pathFieldClient.getText()));
        }else {
            new Alert(Alert.AlertType.ERROR,  "Выберите файл!").showAndWait();
        }
    }
    @FXML
    private void deleteCloudFile (){
        FilesInfo file = cloudTable.getSelectionModel().getSelectedItem();
        Path deletePath = Paths.get(pathFieldCloud.getText()).resolve(file.getName());
        NetworkController.send(new FileDeleteMessage(deletePath));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void upToClientDirectory() {
        Path path = Paths.get(pathFieldClient.getText()).getParent();
        if (Objects.nonNull(path)) {
            clientTable.getItems().clear();
            clientTable.getItems().addAll(initTable.fillingList(path));
            pathFieldClient.setText(path.toString());
        }
    }
    public void upToCloudDirectory (){
        String upPath = Paths.get(pathFieldCloud.getText()).getParent().toString();
        NetworkController.send(new StorageMessage(upPath));
    }
    public void refreshCloudTable(List<FilesInfo> storageList) {
        Platform.runLater(() -> {
            cloudTable.getItems().clear();
            cloudTable.getItems().addAll(initTable.fillingList(storageList));
        });
    }
    public void refreshClientTable(Path path) {
        Platform.runLater(() -> {
            ObservableList<FilesInfo> storageList = initTable.fillingList(path);
            clientTable.getItems().clear();
            clientTable.getItems().addAll(initTable.fillingList(storageList));
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void deleteFile (Path path){
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Файл не удален!").showAndWait();
        }
    }
    public void changeScene() {
        AuthorizationPane.setVisible(false);
        AuthorizationPane.setManaged(false);
        WorkingPane.setVisible(true);
        WorkingPane.setManaged(true);
    }
    private void mouseListener() {
        clientTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathFieldClient.getText()).resolve(clientTable.getSelectionModel().getSelectedItem().getName());
                if (clientTable.getSelectionModel().getSelectedItem().getSize() == -1) {
                    clientTable.getItems().clear();
                    clientTable.getItems().addAll(initTable.fillingList(path));
                    pathFieldClient.setText(path.toString());
                }
            }
        });
        cloudTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2){
                String updatePath = Paths.get(pathFieldCloud.getText()).resolve(cloudTable.getSelectionModel().getSelectedItem().getName()).toString();
                if (cloudTable.getSelectionModel().getSelectedItem().getSize() == -1) {
                    cloudTable.getItems().clear();
                    StorageMessage updateMessage = new StorageMessage(pathFieldCloud.getText());
                    updateMessage.setPath(updatePath);
                    NetworkController.send(updateMessage);
                }
            }
        });
    }
    public static void setIsAuthorized(boolean authorized) {
        MainController.isAuthorized.set(authorized);
    }
    public String getCurrentClientPath() {return pathFieldClient.getText();}
    public void setCloudPath(String path) {
        pathFieldCloud.setText(path);
    }
}
