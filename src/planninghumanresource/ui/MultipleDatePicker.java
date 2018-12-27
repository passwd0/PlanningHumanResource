package planninghumanresource.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MultipleDatePicker {
	 private Parent parent;
	 ObservableList<LocalDate> selectedDates;
	 private CheckBox checkBox;
	 private HBox buttonsLeft;
	
	public MultipleDatePicker(CheckBox checkBox, Set<LocalDate> dates) {
		selectedDates = FXCollections.observableArrayList(dates);
		this.checkBox = checkBox;

		parent = createScene();
	}
	
	public MultipleDatePicker(CheckBox checkBox) {
		this(checkBox, new HashSet<>());
	}
	
	public Parent getParent() {
		return parent;
	}
	
	public Set<LocalDate> getDate(){
		return selectedDates.stream().collect(Collectors.toSet());
	}
	
	public HBox getButtonsLeft() {
		return buttonsLeft;
	}
	
	private Parent createScene() {
        Button                    addButton     = new Button("+");
        Button                    removeButton  = new Button("-");
        addButton.setPrefWidth(40);
        removeButton.setPrefWidth(40);
        addButton.setPrefHeight(40);
        removeButton.setPrefHeight(40);
        ListView<LocalDate>       dateList      = new ListView<>(selectedDates);
        DateTimeFormatter         dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DatePicker                datePicker    = new DatePicker();
        datePicker.setShowWeekNumbers(false);
        
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date == null) ? "" : dateFormatter.format(date);
            }

            @Override
            public LocalDate fromString(String string) {
                return ((string == null) || string.isEmpty()) ? null : LocalDate.parse(string, dateFormatter);
            }
        });
        datePicker.setOnAction(event -> selectedDates.add(datePicker.getValue()));

        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        boolean alreadySelected = selectedDates.contains(item);
                        setDisable(alreadySelected);
                        setStyle(alreadySelected ? "-fx-background-color: rgba(0, 205, 0, 0.5);" : "");
                    }
                };
            }
        });

        dateList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeSelectedDates(selectedDates, dateList);
            }
        });
        dateList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        removeButton.disableProperty().bind(dateList.getSelectionModel().selectedItemProperty().isNull());
        addButton.setOnAction(event -> {
            Popup popup = new Popup();
            popup.getContent().add(datePicker);
            popup.setAutoHide(true);
            Window window = addButton.getScene().getWindow();
            Bounds bounds = addButton.localToScene(addButton.getBoundsInLocal());
            double x      = window.getX() + bounds.getMinX();
            double y      = window.getY() + bounds.getMinY() + bounds.getHeight() + 5;
            popup.show(addButton, x, y);
            datePicker.show();
        });
        removeButton.setOnAction(event -> removeSelectedDates(selectedDates, dateList));

        buttonsLeft = new HBox(addButton, removeButton);
        
        HBox buttonRight = new HBox(checkBox);
        buttonRight.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonRight, Priority.ALWAYS);
        HBox buttons = new HBox(buttonsLeft, buttonRight);
        VBox panel = new VBox(buttons, dateList);
        return panel;
    }

    private static boolean removeSelectedDates(ObservableList<LocalDate> selectedDates, ListView<LocalDate> dateList) {
        return selectedDates.removeAll(dateList.getSelectionModel().getSelectedItems());
    }
}
