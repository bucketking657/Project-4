import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

import com.sun.javafx.font.FontStrike;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 *  MarchMadnessGUI
 * 
 * this class contains the buttons the user interacts
 * with and controls the actions of other objects 
 *
 * @author Grant Osborn
 */
public class MarchMadnessGUI extends Application {

    /**
     * all the gui ellements
     */
    private BorderPane root;
    private ToolBar toolBar;
    private ToolBar btoolBar;
    private Button simulate;
    private Button login;
    private Button scoreBoardButton;
    private Button viewBracketButton;
    private Button clearButton;
    private Button resetButton;
    private Button finalizeButton;
    private Button yourBracket;
    private Button randomize;

    /**
     * Allows you to navigate back to division selection screen
     */
    private Button back;

    private Bracket startingBracket;

    /**
     * Reference to currently logged in bracket
     */
    private Bracket selectedBracket;
    private Bracket simResultBracket;
    private Bracket createdBracket;//the bracket that you created

    /**
     * Data Strutire for dealing with user data
     */
    private ArrayList<Bracket> playerBrackets;
    private HashMap<String, Bracket> playerMap;

    private ScoreBoardTable scoreBoard;
    private TableView table;
    private BracketPane bracketPane;
    private BorderPane loginP;//edited by josh
    private TournamentInfo teamInfo;

    /**
     * Used for scoreboard
     */
    private String userName, password;//chris

    @Override
    public void start(Stage primaryStage) {

        //try to load all the files, if there is an error display it
        try {

            teamInfo = new TournamentInfo();
            startingBracket = new Bracket(TournamentInfo.loadStartingBracket());
            simResultBracket = new Bracket(TournamentInfo.loadStartingBracket());
            simResultBracket.setSim(true);
            createdBracket = new Bracket(TournamentInfo.loadStartingBracket());

        } catch (IOException ex) {
            System.out.println("Exception in start");//makes it easier to know which exception is being thrown.
            showError(new Exception("Can't find " + ex.getMessage(), ex), true);
        }

        /**deserialize stored brackets*/
        playerBrackets = loadBrackets();

        playerMap = new HashMap<>();
        addAllToMap();

        /**the main layout container*/
        root = new BorderPane();
        scoreBoard = new ScoreBoardTable();
        table = scoreBoard.start();
        loginP = createLogin();
        CreateToolBars();

        /**display login screen*/
        login();

        setActions();
        root.setTop(toolBar);
        root.setBottom(btoolBar);
        Scene scene = new Scene(root);
        primaryStage.setMaximized(true);

        primaryStage.setTitle("March Madness Bracket Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Task: runs this whole thing
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Task: Simulates the tournament
     * </>simulation happens only once and
     * </>after the simulation no more users can login
     */
    private void simulate() {
        //cant login and restart prog after simulate
        login.setDisable(true);
        simulate.setDisable(true);

        scoreBoardButton.setDisable(false);
        viewBracketButton.setDisable(false);
        yourBracket.setDisable(false);

        //chris
        /**We wait until the user has chosen to simulate the game before we save the player bracket.
         this is to prevent an error where the score the user gets is always 0*/
        Bracket tmpPlayerBracket = new Bracket(startingBracket, userName);

        playerBrackets.add(tmpPlayerBracket);
        tmpPlayerBracket.setPassword(password);

        playerMap.put(userName, tmpPlayerBracket);
        selectedBracket = tmpPlayerBracket;

        teamInfo.simulate(simResultBracket);
        yourBracket.setDisable(false);
        for (Bracket b : playerBrackets) {
            scoreBoard.addPlayer(b, b.scoreBracket(simResultBracket));
        }

        displayPane(table);
    }

    /**
     * Task:Displays the login screen
     */
    private void login() {

        simulate.setDisable(true);
        scoreBoardButton.setDisable(true);//Chris and Josh
        btoolBar.setDisable(true);
        yourBracket.setDisable(true);
        randomize.setDisable(true);
        viewBracketButton.setDisable(true);//changed to true. This allows you to view the scoreboard before logging in. 
        //However, since the brackets havent been simulated yet there are not scores

        login.setDisable(false);
        displayPane(loginP);
    }

    /**
     * Task:Displays the score board
     */
    private void scoreBoard() {
        displayPane(table);
    }

    /**
     * Task:Displays Simulated Bracket
     */
    private void viewBracket() {

        createdBracket = selectedBracket;//saves your bracket
        bracketPane = new BracketPane(simResultBracket, selectedBracket);

        //The following lines of code were modified to allow the Brackets to be viewed in the center of the screen
        GridPane full = new GridPane();
        full.add(new ScrollPane(bracketPane.getFullPane()), 0, 0);
        full.setAlignment(Pos.CENTER);

        full.setMouseTransparent(true);

        displayPane(full);
    }

    //Chris
    /**Task: Randomizes the a user bracket*/
    private void randomSelection() {

        selectedBracket = startingBracket;//reset selected bracket to starting bracket -> fixed an error where a non-new user couldnt use the randomize button
        teamInfo.simulate(selectedBracket);

        bracketPane = new BracketPane(selectedBracket);

        GridPane full = new GridPane();
        full.add(new ScrollPane(bracketPane.getFullPane()), 0, 0);
        full.setAlignment(Pos.CENTER);
        full.setDisable(false);
        displayPane(full);
    }

    //Chris

    /**Task: Creates afinal bracket before simulation and displays it*/
    private void yourBracket() {

        bracketPane = new BracketPane(createdBracket, simResultBracket);//created bracket is the final version of the bracket you created
        GridPane full = new GridPane();

        full.add(new ScrollPane(bracketPane.getFullPane()), 0, 0);
        full.setAlignment(Pos.CENTER);
        full.setMouseTransparent(true);//similar to disapling the button, but it doesnt grey out the pane
        displayPane(full);
    }

    /**
     * Task: Allows user to choose bracket
     */
    private void chooseBracket() {
        btoolBar.setDisable(false);

        /** If bracketPane has not been initialized,
         *   initialize it here this might be a
         *   temporary fix - we will see
         *   Elizabeth 4/4/19*/

        if (bracketPane == null)
            bracketPane = new BracketPane(selectedBracket);
        displayPane(new BracketPane(selectedBracket));

    }

    /**
     * Task: Resets current selected sub tree
     * for final4 reset Ro2 and winner
     */
    private void clear() {

        bracketPane.clear();
        bracketPane = new BracketPane(selectedBracket);
        displayPane(bracketPane);
    }

    /**
     * Task:resets entire bracket
     */
    private void reset() {
        if (confirmReset()) {
            selectedBracket = startingBracket;
            displayPane(new BracketPane(selectedBracket));
        }
    }

    /**Task:
     *
     */
    private void finalizeBracket() {
        if (bracketPane != null) {

            if (bracketPane.isComplete()) {

                btoolBar.setDisable(true);
                bracketPane.setDisable(true);
                simulate.setDisable(false);
                login.setDisable(true);
                createdBracket = selectedBracket;//saves your bracket

                //save the bracket along with account info
                seralizeBracket(selectedBracket);

            } else {

                infoAlert("You can only finalize a bracket once it has been completed.");
                displayPane(bracketPane);

            }
        } else {
            System.out.println("Null bracket");
        }
    }

    /**
     * Task: Displays element in the center of the screen
     *
     * @param p must use a subclass of Pane for layout.
     *          to be properly center aligned in  the parent node
     */
    private void displayPane(Node p) {
        root.setCenter(p);
        BorderPane.setAlignment(p, Pos.CENTER);
    }

    /**
     * Task:Creates toolBar and buttons.Adds buttons to the
     * toolbar and saves global references to them
     */
    private void CreateToolBars() {

        /**Creating tool bars*/
        toolBar = new ToolBar();
        btoolBar = new ToolBar();

        /**Declaring buttons*/
        login = new Button("Login");
        simulate = new Button("Simulate");
        scoreBoardButton = new Button("ScoreBoard");
        viewBracketButton = new Button("View Simulated Bracket");
        yourBracket = new Button("View Your Bracket");
        randomize = new Button("Randomize Bracket");
        clearButton = new Button("Clear");
        resetButton = new Button("Reset");
        finalizeButton = new Button("Finalize");

        /**Adding buttons to their respected tool bars*/
        toolBar.getItems().addAll(
                createSpacer(),
                login,
                simulate,
                scoreBoardButton,
                viewBracketButton,
                yourBracket,
                createSpacer()
        );

        btoolBar.getItems().addAll(
                createSpacer(),
                clearButton,
                resetButton,
                finalizeButton,
                randomize,
                back = new Button("Home"),
                createSpacer()
        );
    }

    /**
     * Task: Sets the actions for each button
     */
    private void setActions() {

        login.setOnAction(e -> login());
        simulate.setOnAction(e -> simulate());
        scoreBoardButton.setOnAction(e -> scoreBoard());
        viewBracketButton.setOnAction(e -> viewBracket());
        clearButton.setOnAction(e -> clear());
        resetButton.setOnAction(e -> reset());
        yourBracket.setOnAction(e -> this.yourBracket());
        finalizeButton.setOnAction(e -> finalizeBracket());
        this.randomize.setOnAction(e -> this.randomSelection());

        back.setOnAction(e -> {
            bracketPane = new BracketPane(selectedBracket);
            displayPane(bracketPane);
        });
    }

    /**
     * Task:Creates a spacer for centering buttons in a ToolBar
     */
    private Pane createSpacer() {

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.SOMETIMES);

        return spacer;
    }

    /**
     * Task: This create the Pane where the user logs in
     *
     * @return a loginpane where user logs in
     */
    private BorderPane createLogin() { //edited by josh

        //Josh start
        BorderPane loginPage = new BorderPane();
        VBox instructionPane = new VBox();
        VBox gridRight = new VBox();
        instructionPane.setSpacing(10);
        //Josh End

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(5, 5, 5, 5));


        TextArea directions = new TextArea();//Josh Start


        /**Right panel Components and files*/
        Image image = new Image(new File("/home/jshilts/IdeaProjects/Project 4 Prototypev2/src/march2").toURI().toString());
        ImageView i = new ImageView();
        i.setImage(image);
        i.setFitHeight(500);
        i.setFitWidth(350);
        gridRight.setPadding(new Insets(35, 80, 10, 10));
        gridRight.getChildren().addAll(i);

        //Top
        //Style Title
        Text welcomeMessage = new Text("March Madness Login");
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        welcomeMessage.setEffect(ds);
        welcomeMessage.setCache(true);
        welcomeMessage.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        loginPage.setAlignment(welcomeMessage, Pos.TOP_CENTER);
        loginPage.setTop(welcomeMessage);
        //Josh End

        Label userName = new Label("User Name: ");

        userName.setFont(Font.font("Arial", FontWeight.BOLD, 14));//Josh
        loginPane.add(userName, 0, 1);

        TextField enterUser = new TextField();
        loginPane.add(enterUser, 1, 1);


        Label password = new Label("Password: ");
        password.setFont(Font.font("Arial", FontWeight.BOLD, 14));//Josh
        loginPane.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        loginPane.add(passwordField, 1, 2);

        //Josh start
        //Left
        Button instruction = new Button("Instruction");

        instruction.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        instruction.setStyle("-fx-base: ADD8E6 ;");
        instructionPane.setPadding(new Insets(10, 0, 10, 65));
        instructionPane.setAlignment(Pos.CENTER);
        instructionPane.setStyle("-fx-background-color: #6495ED;");
        directions.setPrefSize(250, 400);

        instructionPane.getChildren().addAll(instruction, directions);
        directions.setWrapText(true);
        directions.setEditable(false);

        //Josh end

        Button signButton = new Button("Sign in");
        signButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));//Josh
        loginPane.add(signButton, 1, 4);
        signButton.setDefaultButton(true);

        Label message = new Label();
        loginPane.add(message, 1, 5);

        //Josh Start
        loginPage.setCenter(loginPane);
        loginPage.setLeft(instructionPane);
        loginPage.setRight(gridRight);
        loginPage.setStyle("-fx-background-color: #6495ED;");
        //Josh End

        signButton.setOnAction(event -> {

            // the name user enter
            String name = enterUser.getText().toLowerCase();//edited by Josh

            // the password user enter
            String playerPass = passwordField.getText();

            //Josh Start
            if (enterUser.getText().equals(""))
                infoAlert("Please enter a valid user name");
            else if (passwordField.getText().equals("")) {
                infoAlert("Please enter a valid password");
            }

            enterUser.setText("");
            passwordField.setText("");
            if (directions != null)
                directions.setText("");
            instruction.setDisable(false);

            //Josh End

            System.out.println(playerMap.get(name) == null);
            System.out.println(playerMap.get(name));
            if (playerMap.get(name) != null) {
                //check password of user

                Bracket tmpBracket = this.playerMap.get(name);
                System.out.println(playerMap.get(name));
                String password1 = tmpBracket.getPassword();

                if (Objects.equals(password1, playerPass)) {

                    // load bracket
                    selectedBracket = playerMap.get(name);
                    this.userName = name;
                    this.password = playerPass;
                    chooseBracket();


                    randomize.setDisable(false);

                } else {

                    infoAlert("The password you have entered is incorrect!");
                    randomize.setDisable(false);
                }

            } else {

                //check for empty fields
                if (!name.equals("") && !playerPass.equals("")) {

                    //create new bracket
                    Bracket tmpPlayerBracket = new Bracket(startingBracket, name);
                    tmpPlayerBracket.setPassword(playerPass);


                    selectedBracket = tmpPlayerBracket;

                    //alert user that an account has been created
                    infoAlert("No user with the Username \"" + name + "\" exists. A new account has been created.");
                    randomize.setDisable(false);
                    this.userName = name;
                    this.password = playerPass;
                    chooseBracket();
                }
            }
        });

        //Josh Start
        /**Task: Functionality for iunstruction button*/
        instruction.setOnAction(event -> {

            Scanner scan= null;
            try {

                scan = new Scanner(new File("/home/jshilts/IdeaProjects/Project 4 Prototypev2/src/Instructions.txt"));

                while (scan.hasNext()) {

                    directions.appendText(" " + scan.next());
                    directions.setFont(Font.font("Arial", FontPosture.ITALIC, 14));//Josh

                }

                instruction.setDisable(true);

            } catch (Exception e) {

                e.printStackTrace();

            }finally{

                scan.close();
            }
        });
        //Josh End

        return loginPage;
    }

    /**Task:adds all the brackets to the map for login*/
    private void addAllToMap() {
        for (Bracket b : playerBrackets) {
            playerMap.put(b.getPlayerName(), b);
        }
    }

    /**Task:Displays a error message to the user and if the error
     *      is bad enough closes the program
     *@param fatal true if the program should exit. false otherwise*/
    private void showError(Exception e, boolean fatal) {

        String msg = e.getMessage();

        if (fatal) {
            msg = msg + " \n\nthe program will now close";

        }

        Alert alert = new Alert(AlertType.ERROR, msg);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(420);

        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.showAndWait();

        if (fatal) {
            System.exit(666);
        }
    }

    /**
     * Task: Alerts user to the result of their actions in the login pane
     *
     * @param msg the message to be displayed to the user*/
    private void infoAlert(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**Task:Prompts the user to confirm that they want
     * to clear all predictions from their bracket
     *@return true if the yes button clicked, false otherwise*/
    private boolean confirmReset() {
        Alert alert = new Alert(AlertType.CONFIRMATION,
                "Are you sure you want to reset the ENTIRE bracket?",
                ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    /**Task: This will serialize or encapsulate the users bracket
     *@param B The bracket the is going to be seralized*/
    private void seralizeBracket(Bracket B) {

        FileOutputStream outStream = null;//Josh edit
        ObjectOutputStream out= null;//Josh edit

        try {

            outStream = new FileOutputStream(B.getPlayerName() + ".ser");

            out = new ObjectOutputStream(outStream);

            out.writeObject(B);

        } catch (IOException e) {

            showError(new Exception("Error saving bracket \n" + e.getMessage(), e), false);
        } finally {//Josh Start
            try {
                if (outStream != null)
                    outStream.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//Josh End

    /**Task: This method will deserialize a bracket where the file the contains that bracket is found
     * @param filename of the seralized bracket file
     * @return deserialized bracket*/
    private Bracket deseralizeBracket(String filename) throws FileNotFoundException,
            IOException{//Elizabeth 4/1/19

        /** no default constructor for bracket - we fixed*/
        Bracket bracket = new Bracket();
        FileInputStream inStream = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(inStream);
        
        try {

            bracket = (Bracket) in.readObject();
        
        }catch (FileNotFoundException | ClassNotFoundException e) {

            showError(new Exception("Error loading bracket \n" +e.getMessage(),e),false);

        } finally{//Josh Start

            in.close();
            inStream.close();
        }//Josh End

        return bracket;
    }

    /**Task: Here this method will load the brackerts from the files
     *       and if they ar serialized it will deserialize them
     *@return An arraylist that contains deserialized brackets*/
    private ArrayList<Bracket> loadBrackets() {

        ArrayList<Bracket> list=new ArrayList<Bracket>();
        File dir = new File(".");

        for (final File fileEntry : dir.listFiles()){

            String fileName = fileEntry.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);
       
            if (extension.equals("ser")){

                try{

                    list.add(deseralizeBracket(fileName));

                } catch (FileNotFoundException e){
                    showError(new Exception("File not found \n" +e.getMessage(),e),false);

                } catch (IOException e){
                    showError(new Exception("IO Exception \n" +e.getMessage(),e),false);
                }
            }
        }
        return list;
    }
}//End Class
