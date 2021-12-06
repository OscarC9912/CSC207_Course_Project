package InterfaceAdapters;

import ApplicationBusinessRule.SimpleScheduler;
import ApplicationBusinessRule.Timetable;
import ApplicationBusinessRule.filter.InstructorFilter;
import ApplicationBusinessRule.filter.MaximumHourFilter;
import ApplicationBusinessRule.filter.TimeslotFilter;
import EnterpriseBusinessRules.NewCourse;
import FrameworksDrivers.MakeCSV;
import FrameworksDrivers.UserData;
import FrameworksDrivers.WebParse;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

import static FrameworksDrivers.WebParse.courseParse;

public class Controller implements Initializable {

    ArrayList<Timetable> filteredTimetables = new ArrayList<>();

    Timetable currTimetable = new Timetable();

    // top of the anchor pane
    @FXML
    BorderPane borderPane;

    @FXML
    TextField courseSearchField;

    @FXML
    Button searchButton;

    @FXML
    Button knitBtn;

    @FXML
    Button choice1;

    @FXML
    Button choice2;

    @FXML
    Button choice3;

    @FXML
    Button choice4;

    @FXML
    Button choice5;

    // left of the pane
    @FXML
    ListView<String> courseListView;

    @FXML
    Button confirmButton;

    @FXML
    Button clearButton;

    // middle of the pane
    @FXML
    TableView<TimeSlotForGUI> tableView;

    @FXML
    TableColumn<TimeSlotForGUI, String> TimeSlotColumn;

    @FXML
    TableColumn<TimeSlotForGUI, String> MondayColumn;

    @FXML
    TableColumn<TimeSlotForGUI, String> TuesdayColumn;

    @FXML
    TableColumn<TimeSlotForGUI, String> WednesdayColumn;

    @FXML
    TableColumn<TimeSlotForGUI, String> ThursdayColumn;

    @FXML
    TableColumn<TimeSlotForGUI, String> FridayColumn;

    @FXML
    Label botLabel;


    // click events
    @FXML
    public void searchButtonClicked() {
        String course = courseSearchField.getText();
        try {
            NewCourse foundedCourse = courseParse(course);
            String foundedString = foundedCourse.getCourseCode();
            if (!courseListView.getItems().contains(foundedString)) {
                if (!foundedString.equals("")) {
                    courseListView.getItems().add(foundedString);
                    UserData.inputCourse(foundedString);
                }
            }
        } catch (IOException c) {
            c.printStackTrace();
            AlertBox.display("Course not found error", "The course is not found");
        }
    }

    @FXML
    public void knitClicked() {
        MakeCSV.makeCSV(filteredTimetables);
    }

    @FXML
    public void timeTable1Clicked() {
        currTimetable = filteredTimetables.get(0);

        ObservableList<TimeSlotForGUI> data = Controller.getObservableList(currTimetable);
        tableView.setItems(data);
    }

    @FXML
    public void timeTable2Clicked() {
        currTimetable = filteredTimetables.get(1);

        ObservableList<TimeSlotForGUI> data = Controller.getObservableList(currTimetable);
        tableView.setItems(data);
    }

    @FXML
    public void timeTable3Clicked() {
        currTimetable = filteredTimetables.get(2);

        ObservableList<TimeSlotForGUI> data = Controller.getObservableList(currTimetable);
        tableView.setItems(data);
    }

    @FXML
    public void timeTable4Clicked() {
        currTimetable = filteredTimetables.get(3);

        ObservableList<TimeSlotForGUI> data = Controller.getObservableList(currTimetable);
        tableView.setItems(data);
    }

    @FXML
    public void timeTable5Clicked() {
        currTimetable = filteredTimetables.get(4);

        ObservableList<TimeSlotForGUI> data = Controller.getObservableList(currTimetable);
        tableView.setItems(data);
    }

    public static ObservableList<TimeSlotForGUI> getObservableList(Timetable currTimetable) {
        final ObservableList<TimeSlotForGUI> data = FXCollections.observableArrayList();

        ArrayList<ArrayList<String>> listOfListOfCourseName = ConvertToUI.timetableToUI(currTimetable.getTimeTable());
        for (int i = 0; i < 12; i++) {
            String timeSlot = i + 9 + ":00";
            ArrayList<String> listOfCourseName = listOfListOfCourseName.get(i);
            listOfCourseName.add(0, timeSlot);
            TimeSlotForGUI tsf = new TimeSlotForGUI(listOfCourseName.get(0),
                                                    listOfCourseName.get(1),
                                                    listOfCourseName.get(2),
                                                    listOfCourseName.get(3),
                                                    listOfCourseName.get(4),
                                                    listOfCourseName.get(5));
            data.add(tsf);
        }

        return data;
    }

    @FXML
    public void confirmButtonClicked() throws IOException {
        ArrayList<String> courseNameList = new ArrayList<>(courseListView.getItems());
        ArrayList<NewCourse> newCourseList = new ArrayList<>();
        for (String courseName: courseNameList) {
            NewCourse parsedCourse = WebParse.courseParse(courseName);
            newCourseList.add(parsedCourse);
        }
        SimpleScheduler Ss = new SimpleScheduler(newCourseList);
        ArrayList<Timetable> arranged = Ss.arrange(new Timetable(), new HashSet<>());
        ArrayList<Timetable> filtered;
        String filter = UserData.getFilterType();
        if (filter.equals("Instructors")) {
            ArrayList<String> insPreference = UserData.readPreference();
            InstructorFilter IF = new InstructorFilter(arranged, insPreference);
            filtered = IF.sort();
        } else if (filter.equals("Max Duration")) {
            ArrayList<String> maxPreference = UserData.readPreference();
            MaximumHourFilter MHF = new MaximumHourFilter(arranged, maxPreference);
            filtered = MHF.sort();
        } else if (filter.equals("Timeslots")) { // timeslots
            ArrayList<String> timeSlotPreference = UserData.readPreference();
            TimeslotFilter TSF = new TimeslotFilter(arranged, timeSlotPreference);
            filtered = TSF.sort();
        } else { // no filter
            filtered = arranged;
        }
        if (filtered.size() > 5) {
            filteredTimetables = new ArrayList<Timetable>();
            filteredTimetables.add(filtered.get(0));
            filteredTimetables.add(filtered.get(1));
            filteredTimetables.add(filtered.get(2));
            filteredTimetables.add(filtered.get(3));
            filteredTimetables.add(filtered.get(4));
        } else {
            filteredTimetables = filtered;
            int space = 5 - filtered.size();
            for (int i = 0; i < space; i++) {
                filteredTimetables.add(new Timetable());
            }
        }
    }

    @FXML
    public void clearButtonClicked() throws IOException {
        courseListView.getItems().clear();
        UserData.clearCourses();
    }

    @FXML
    public void highContrastBtnClicked() {
        borderPane.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");

        courseSearchField.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        searchButton.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");

        courseListView.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        courseListView.getStylesheets().add("style.css");

        tableView.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        tableView.getStylesheets().add("style.css");

        TimeSlotColumn.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        MondayColumn.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        TuesdayColumn.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        WednesdayColumn.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        ThursdayColumn.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        FridayColumn.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");

        choice1.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        choice2.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        choice3.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        choice4.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        choice5.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");

        confirmButton.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");
        clearButton.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");

        knitBtn.setStyle("-fx-background-color: #0027FF; -fx-text-fill: #FBFF00");

        botLabel.setStyle("-fx-text-fill: #FBFF00");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // read in the previous courses
        try {
            ArrayList<String> loadedCoursesName = UserData.readCourses();
            for (String courseName: loadedCoursesName) {
                courseListView.getItems().add(courseName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        final ObservableList<TimeSlotForGUI> data = getObservableList(currTimetable);

        TimeSlotColumn.setCellValueFactory(new PropertyValueFactory<TimeSlotForGUI, String>("TimeSlot"));
        MondayColumn.setCellValueFactory(new PropertyValueFactory<TimeSlotForGUI, String>("MondaySession"));
        TuesdayColumn.setCellValueFactory(new PropertyValueFactory<TimeSlotForGUI, String>("TuesdaySession"));
        WednesdayColumn.setCellValueFactory(new PropertyValueFactory<TimeSlotForGUI, String>("WednesdaySession"));
        ThursdayColumn.setCellValueFactory(new PropertyValueFactory<TimeSlotForGUI, String>("ThursdaySession"));
        FridayColumn.setCellValueFactory(new PropertyValueFactory<TimeSlotForGUI, String>("FridaySession"));

        // add data inside table
        tableView.setItems(data);
    }

    public static void main(String[] args) {
        Application.launch(UserInterface.class, args);
    }
}