package ru.gb.storage.client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import ru.gb.storage.commons.FilesInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class InitTable {

    public void constructTable(TableView<FilesInfo> table) {
        TableColumn<FilesInfo, String> columnName = new TableColumn<>("Имя");
        columnName.setCellValueFactory(filesInfoStringCellDataFeatures -> new SimpleStringProperty(filesInfoStringCellDataFeatures.getValue().getName()));
        columnName.setPrefWidth(200);

        TableColumn<FilesInfo, Long> columnSize = new TableColumn<>("Размер");
        columnSize.setCellValueFactory(filesInfoLongCellDataFeatures -> new SimpleObjectProperty<>(filesInfoLongCellDataFeatures.getValue().getSize()));
        columnSize.setCellFactory(new Callback<>() {
            @Override
            public TableCell<FilesInfo, Long> call(TableColumn<FilesInfo, Long> filesInfoLongTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty || item == -1L) {
                            setText(null);
                            setStyle("");
                        } else {

                            setText(String.format("%,d kBts", item / 1000));
                        }
                    }
                };
            }
        });
        columnSize.setPrefWidth(80);
        table.getColumns().addAll(columnName, columnSize);
    }
    public ObservableList<FilesInfo> fillingList(Path path) {
        ObservableList<FilesInfo> listFiles = FXCollections.observableArrayList();
        List<File> list = Arrays.asList(path.toFile().listFiles());
        for (File file : list) {
            listFiles.add(new FilesInfo(file.toPath()));
        }
        return listFiles;
    }
    public ObservableList<FilesInfo> fillingList(List<FilesInfo> storageList) {
        ObservableList<FilesInfo> listFiles = FXCollections.observableArrayList();
        listFiles.addAll(storageList);
        return listFiles;
    }
}
