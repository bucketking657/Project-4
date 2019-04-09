import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import java.util.HashMap;
import java.util.Map;

/**Task:ScoreBoardPane class is the class the displays the Scoreboard from the Main GUI.
 *      It shows all of the Player's names and their scores*/
public class ScoreBoardTable {

    /**Data structure that will hold a players bracket and their score for it*/
    private Map<Bracket, Integer> scores;

    /**Max amount of players*/
    private static final int MAX_PLAYER_NUMBER = 16;

    /**Task:tables where data gets sent to be displayed*/
    private TableView<Bracket> table;
    private ObservableList<Bracket> data;

    /**Task:ScoreBoardPane constructor*/
    @SuppressWarnings("unchecked")//what the heck is this? (Josh)
    public ScoreBoardTable() {
        table = new TableView<>();
        data = FXCollections.observableArrayList();
        scores = new HashMap<>();

        /**Task: Passes the data to the TableView object, which is
         *       automatically sorted with the TableColumn.SortType.DESCENDING code line.
         *       <p>
         *      TableColumn userNameCol is the column on the left side of the table.
         *      userNameCol.setCellValueFactory()*/
        TableColumn<Bracket, String> userNameCol = new TableColumn<>("Username");

        userNameCol.setMinWidth(140);
        userNameCol.setMaxWidth(140);
        userNameCol.setStyle("-fx-border-width: 3px");
        userNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bracket, String>, ObservableValue<String>>() {

            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bracket, String> b) {
                return new SimpleStringProperty(b.getValue().getPlayerName());
            }
        });

        userNameCol.setSortable(true);
        userNameCol.setSortType(TableColumn.SortType.DESCENDING); //sorts column from highest to lowest

        /**Task: Passes the data to the TableView object, which is automatically sorted
         *       with the TableColumn.SortType.DESCENDING code line.
         *       <p>
         *      TableColumn totalPtsCol is the column on the right side of the table
         *      totalPtsCol.setCellValueFactory()*/
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

        /**TasK: TableView table_view is what the user sees in the GUI. This creates the table.*/
        SortedList<Bracket> sortData = new SortedList<>(data);				//added by zion 4/4 wraps the data
        sortData.comparatorProperty().bind(table.comparatorProperty());		//sortData sorts the data according to columns in the sortOrder list

        table.setItems(sortData);
        table.sort();
        table.getSortOrder().addAll(totalPtsCol, userNameCol);			//added by zion 4/4 table columns passed in are sorted based on column's sort type IF sortable is true
        table.getColumns().setAll(userNameCol,totalPtsCol);
    }

    /**Task: show us the table with points
     *@return the table with a users points*/
    public TableView<Bracket> start() { return table; }

    /**Task: Method addPlayer adds a player to the Bracket
     *
     * @param name the name of whose bracket this is
     * @param score the score of that bracket*/
    public void addPlayer(Bracket name, int score) {

        try {

            if (scores == null) {
                scores = new HashMap<Bracket, Integer>();
            }

            /**Task: Only allow to update the existing player score or add new player if there
            *        is less than 16 players*/
            if (scores.get(name) != null || scores.size() < MAX_PLAYER_NUMBER) {
                scores.put(name, score);
                data.add(name);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**TasK:This will clear the map of any players and their scores*/
    public void clearPlayers() {
        scores = new HashMap<Bracket, Integer>();
        data = FXCollections.observableArrayList();
    }
}//End Class
