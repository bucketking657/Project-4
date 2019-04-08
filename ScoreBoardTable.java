import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**Task: ScoreBoardPane class is the class the displays the Scoreboard
 *       from the Main GUI. It shows all of the Player's names and their scores.*/
public class ScoreBoardTable {

    /**The data structure that will hold a users scores*/
    private Map<Bracket, Integer> scores;

    /**The max amount of players our table can hold*/
    private static final int MAX_PLAYER_NUMBER = 16;

    /**The table where we will view them**/
    private TableView<Bracket> table;

    /**Where the data will be**/
    private ObservableList<Bracket> data;

    /**Task: This is the scoreboard constructor that will display the users
     *       score and sort the users if more than one by their score*/
    public ScoreBoardTable() {
        table = new TableView<>();
        data = FXCollections.observableArrayList();
        scores =  new HashMap<Bracket, Integer>();

        /**TableColumn userNameCol is the column on the left side of the table.
         * userNameCol.setCellValueFactory() passes the data to the TableView object, which is
         * automatically sorted with the TableColumn.SortType.DESCENDING code line.*/
        TableColumn<Bracket, String> userNameCol = new TableColumn<>("Username");

        userNameCol.setMinWidth(140);
        userNameCol.setMaxWidth(140);
        userNameCol.setStyle("-fx-border-width: 3px");
        userNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bracket, String>, ObservableValue<String>>() {

            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bracket, String> b) {

               // if(userNameCol.)
                return new SimpleStringProperty(b.getValue().getPlayerName());
            }
        });

        userNameCol.setSortable(true);

        /**TableColumn totalPtsCol is the column on the right side of the table
         * totalPtsCol.setCellValueFactory() passes the data to the TableView object, which is
         * automatically sorted with the TableColumn.SortType.DESCENDING code line.*/
        TableColumn<Bracket, Number> totalPtsCol = new TableColumn<>("Total Points");

        totalPtsCol.setMinWidth(140);
        totalPtsCol.setMaxWidth(140);
        totalPtsCol.setStyle("-fx-border-width: 3px");
        totalPtsCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bracket, Number>, ObservableValue<Number>>() {

            public ObservableValue<Number> call(TableColumn.CellDataFeatures<Bracket, Number> b) {
                return new SimpleIntegerProperty(scores.get(b.getValue()));
            }
        });

        totalPtsCol.setSortable(true);


        totalPtsCol.setSortType(TableColumn.SortType.DESCENDING); //sorts column from highest to lowest


        /**
         * TableView table_view is what the user sees in the GUI. This creates the table.
         *
         */

        table.setItems(data);
        table.setEditable(false);

        //table.getSelectionModel().setCellSelectionEnabled(true
        table.sort();
        table.getColumns().setAll(userNameCol, totalPtsCol);
        /*SortedList<Bracket> sortData = new SortedList<>(data);				//added by zion 4/4 wraps the data
        sortData.comparatorProperty().bind(table.comparatorProperty());		//sortData sorts the data according to columns in the sortOrder list

        // scores = sortJosh(scores);
        table.setItems(sortData);

        table.getSelectionModel().setCellSelectionEnabled(true);
        table.sort();
        table.getSortOrder().addAll(totalPtsCol, userNameCol);			//added by zion 4/4 table columns passed in are sorted based on column's sort type IF sortable is true

        table.getColumns().setAll(userNameCol,totalPtsCol);*/

    }

    /**Task: Will return a tableView object that uses a bracket
     *@return table that hold a brackets data*/
    public TableView<Bracket> start() { return table; }

    /**Task: Method addPlayer adds a player to the Bracket
     *
     * @param name player name to be added as key*/
    public void addPlayer(Bracket name, int score){
        try {
            if (scores == null) {
                //scores = new HashMap<Bracket, Integer>();
            }

            /**only allow to update the existing player score or add new player if there
             is less than 16 players*/
            if (scores.get(name) != null || scores.size() < MAX_PLAYER_NUMBER) {
                //score= name.scoreBracket(name);
                scores.put(name, score);
                System.out.println(name.getScore()+"score teset");
                data.add(name);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**Task: method clears the players from the Bracket*/
    public void clearPlayers() {
        scores = new HashMap<Bracket, Integer>();
        data = FXCollections.observableArrayList();
    }

}
