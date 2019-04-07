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
import java.util.Optional;
/**
 *  MarchMadnessGUI
 * 
 * this class contains the buttons the user interacts
 * with and controls the actions of other objects 
 *
 * @author Grant Osborn
 */
public class MarchMadnessGUI extends Application {
    
    
    //all the gui ellements
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
    private Button viewPlayerBracketButton;

    //allows you to navigate back to division selection screen
    private Button back;
  
    
    private  Bracket startingBracket; 
    //reference to currently logged in bracket
    private Bracket selectedBracket;
    private Bracket simResultBracket;
    private Bracket createdBracket;//the bracket that you created


    private ArrayList<Bracket> playerBrackets;
    private HashMap<String, Bracket> playerMap;

    

    private ScoreBoardTable scoreBoard;
    private TableView table;
    private BracketPane bracketPane;
    private BorderPane loginP;//edited by josh
    private TournamentInfo teamInfo;
    private int last=0;//chris

    private String userName,password;//chris
    
    @Override
    public void start(Stage primaryStage) {
        //try to load all the files, if there is an error display it
        try{
            teamInfo=new TournamentInfo();
            startingBracket= new Bracket(TournamentInfo.loadStartingBracket());
            simResultBracket=new Bracket(TournamentInfo.loadStartingBracket());
            simResultBracket.setSim(true);
            createdBracket=new Bracket(TournamentInfo.loadStartingBracket());

        } catch (IOException ex) {
            showError(new Exception("Can't find "+ex.getMessage(),ex),true);
        }
        //deserialize stored brackets
        playerBrackets = loadBrackets();
        
        playerMap = new HashMap<>();
        addAllToMap();
        


        //the main layout container
        root = new BorderPane();
        scoreBoard= new ScoreBoardTable();
        table=scoreBoard.start();
        loginP=createLogin();
        CreateToolBars();
        
        //display login screen
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    //chris
    private void serializeBracket(Bracket b)
    {
    	String filename= b.getPlayerName()+".ser";
    	try
        {    
            //Saving of object in a file 
            FileOutputStream file = new FileOutputStream(filename); 
            ObjectOutputStream out = new ObjectOutputStream(file); 
              
            // Method for serialization of object 
            out.writeObject(b); 
              
            out.close(); 
            file.close(); 
              
            System.out.println("Object has been serialized"); 
  
        } 
          
        catch(IOException ex) 
        { 
            System.out.println("IOException is caught"); 
        } 
    }
    
    /**
     * simulates the tournament  
     * simulation happens only once and
     * after the simulation no more users can login
     */
    private void simulate(){
        //cant login and restart prog after simulate
        login.setDisable(true);
        simulate.setDisable(true);
        
       scoreBoardButton.setDisable(false);
       viewBracketButton.setDisable(false);
       viewPlayerBracketButton.setDisable(false);
        yourBracket.setDisable(false);
       
      //chris
        //We wait until the user has chosen to simulate the game before we save the player bracket. 
        //this is to prevent an error where the score the user gets is always 0
        Bracket tmpPlayerBracket = new Bracket(startingBracket, userName);
        playerBrackets.add(tmpPlayerBracket);
        tmpPlayerBracket.setPassword(password);
        
        playerMap.put(userName, tmpPlayerBracket);
        serializeBracket(tmpPlayerBracket);
        selectedBracket = tmpPlayerBracket;

       teamInfo.simulate(simResultBracket);
       yourBracket.setDisable(false);
       for(Bracket b:playerBrackets){
           scoreBoard.addPlayer(b,b.scoreBracket(simResultBracket));
       }
        
        displayPane(table);
    }
    
    /**
     * Displays the login screen
     * 
     */
    private void login(){            
        login.setDisable(false);
        simulate.setDisable(true);
        scoreBoardButton.setDisable(true);//Chris and Josh
        viewPlayerBracketButton.setDisable(true);
  
      

        yourBracket.setDisable(true);
        randomize.setDisable(true);
        //chris
        viewBracketButton.setDisable(true);//changed to true. This allows you to view the scoreboard before logging in. 
        //However, since the brackets havent been simulated yet there are not scores

        btoolBar.setDisable(true);
	        displayPane(loginP);
    }
    
     /**
     * Displays the score board
     * 
     */
    private void scoreBoard(){
        displayPane(table);
    }
    
     /**
      * Displays Simulated Bracket
      * 
      */

    public void runUserSelection(){
        String s;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Bracket Selection");
        dialog.setHeaderText("View a User's Bracket");
        dialog.setContentText("Please enter the username:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
        	s = result.get();
        	viewBracket(s);
        	}	
    	}
    
    /*
    * Task:Displays Simulated Bracket of a user with a specific username
    * Added by Elizabeth 4/7/2019, adapted from above viewBracket method
    */
   private void viewBracket(String s) {

       Bracket someBracket = playerMap.get(s);
       if(someBracket != null){
       bracketPane = new BracketPane(simResultBracket, someBracket);

       //The following lines of code were modified to allow the Brackets to be viewed in the center of the screen
       GridPane full = new GridPane();
       full.add(new ScrollPane(bracketPane.getFullPane()), 0, 0);
       full.setAlignment(Pos.CENTER);

       full.setMouseTransparent(true);

       displayPane(full);
       }
       else{
           Alert alert = new Alert(AlertType.WARNING);
           alert.setTitle("Warning");
           alert.setHeaderText("Bracket not found");
           alert.setContentText("There is no bracket for a user with this name");

           alert.showAndWait();
       }
   }
    
    //modified by chris
    private void viewBracket()
    {
       //selectedBracket=simResultBracket;
       createdBracket=selectedBracket;//saves your bracket
       bracketPane=new BracketPane(simResultBracket,selectedBracket);
       //bracketPane = new BracketPane(simResultBracket, selectedBracket);
       //selectedBracket=simResultBracket;
       //The following lines of code were modified to allow the Brackets to be viewed in the center of the screen
       GridPane full =new GridPane();
   	   full.add(new ScrollPane(bracketPane.getFullPane()),0, 0);
   	   full.setAlignment(Pos.CENTER);
   	   //full.setDisable(true);
   	   full.setMouseTransparent(true);
   	
   	   displayPane(full); 
    }
    //Chris
    private void randomSelection()
    {
    	selectedBracket=startingBracket;//reset selected bracket to starting bracket -> fixed an error where a non-new user couldnt use the randomize button
    	teamInfo.simulate(selectedBracket);

    	bracketPane=new BracketPane(selectedBracket,this);
        //selectedBracket=simResultBracket;
        GridPane full = new GridPane();
        full.add(new ScrollPane(bracketPane.getFullPane()), 0, 0);
        full.setAlignment(Pos.CENTER);
        full.setDisable(false);
        displayPane(full); 
    }
    //Chris
    private void yourBracket()
    {
    	bracketPane = new BracketPane(createdBracket,simResultBracket);//created bracket is the final version of the bracket you created
    	GridPane full =new GridPane();
    	full.add(new ScrollPane(bracketPane.getFullPane()),0, 0);
    	full.setAlignment(Pos.CENTER);
    	//full.setDisable(true);
    	full.setMouseTransparent(true);//similar to disapling the button, but it doesnt grey out the pane
    	displayPane(full);
    }
    //chris
    public int getLast()
    {
    	return last;
    }
    
    /**
     * allows user to choose bracket
     * 
     */
   private void chooseBracket(){
        //login.setDisable(true);
        btoolBar.setDisable(false);
        
        
        
        // if bracketPane has not been initialized,
        // initialize it here
        
        // this might be a temporary fix - we will see
        // Elizabeth 4/4/19
        System.out.println("running");
        if(bracketPane == null)
            bracketPane=new BracketPane(selectedBracket,this);
        displayPane(new BracketPane(selectedBracket,this));

    }
    /**
     * resets current selected sub tree
     * for final4 reset Ro2 and winner
     */
   //chris
    private void clear(){
      
      bracketPane.clear();
      
      //last=bracketPane.getLastPosition();
      int disp=bracketPane.getDisplayedSubTree();
      System.out.println("LAST: "+last+" Displ:"+bracketPane.getDisplayedSubTree());
      //System.out.println("Selected"+this.selectedBracket.);
      //displayPane(new ScrollPane(bracketPane.getPane(bracketPane.getLastPosition())));
      bracketPane=new BracketPane(selectedBracket,this);
     // displayPane(bracketPane);
      bracketPane.updateLast(disp);
      bracketPane.updateDisplayedSubtree(last);
      System.out.println("bracketPane LAst "+bracketPane.getLastPosition());
      GridPane full = new GridPane();
      full.add(new ScrollPane(bracketPane.getPane(last)), 0, 0);
      full.setAlignment(Pos.CENTER);
      displayPane(full);
        
    }
    
    /**
     * resets entire bracket
     */
    //updated by chris
    private void reset(){
        if(confirmReset()){
            //horrible hack to reset
            //selectedBracket=startingBracket;
        	bracketPane.updateLast(7);
            bracketPane.updateDisplayedSubtree(7);
            this.clear();
            //bracketPane=new BracketPane(selectedBracket);
            //displayPane(new BracketPane(selectedBracket,this));
        }
    }
    
    private void finalizeBracket(){
        if(bracketPane != null){
       if(bracketPane.isComplete()){
           btoolBar.setDisable(true);
           bracketPane.setDisable(true);
           simulate.setDisable(false);
           login.setDisable(true);
           createdBracket=selectedBracket;//saves your bracket
           //save the bracket along with account info
          
            seralizeBracket(selectedBracket);
           
           
       }else{
            infoAlert("You can only finalize a bracket once it has been completed.");
            //go back to bracket section selection screen
            // bracketPane=new BracketPane(selectedBracket);
            displayPane(bracketPane);
        
       }
        }
        else{
            System.out.println("Null bracket");
        }
       //bracketPane=new BracketPane(selectedBracket);
      
      
        
    }
    
    
    /**
     * displays element in the center of the screen
     * 
     * @param p must use a subclass of Pane for layout. 
     * to be properly center aligned in  the parent node
     */
    private void displayPane(Node p){
        root.setCenter(p);
        if(bracketPane!=null)
        bracketPane.setGUI(this);//chris
        BorderPane.setAlignment(p,Pos.CENTER);
    }
    //chris
    public void updateLast(int l)
    {
    	this.last=l;
    	System.out.println("last="+last);
    }
    /**
     * Creates toolBar and buttons.
     * adds buttons to the toolbar and saves global references to them
     */
    private void CreateToolBars(){
        toolBar  = new ToolBar();
        btoolBar  = new ToolBar();
        login=new Button("Login");
        simulate=new Button("Simulate");
        viewPlayerBracketButton = new Button("View a Player's Bracket");
        scoreBoardButton=new Button("ScoreBoard");
        viewBracketButton= new Button("View Simulated Bracket");
        yourBracket=new Button("View Your Bracket");//chris
        randomize=new Button("Randomize Bracket");//chris
        clearButton=new Button("Clear");
        resetButton=new Button("Reset");
        finalizeButton=new Button("Finalize");
        toolBar.getItems().addAll(
                createSpacer(),
                login,
                simulate,
                scoreBoardButton,
                viewBracketButton,
                yourBracket,//chris
                viewPlayerBracketButton,
                createSpacer()
        );
        btoolBar.getItems().addAll(
                createSpacer(),
                clearButton,
                resetButton,
                finalizeButton,
                randomize,//chris
                back=new Button("Home"),
                createSpacer()
        );
    }
    
   /**
    * sets the actions for each button
    */
    private void setActions(){
        login.setOnAction(e->login());
        simulate.setOnAction(e->simulate());
        scoreBoardButton.setOnAction(e->scoreBoard());
        viewBracketButton.setOnAction(e->viewBracket());
        clearButton.setOnAction(e->clear());
        resetButton.setOnAction(e->reset());
        viewPlayerBracketButton.setOnAction(e -> runUserSelection());
        yourBracket.setOnAction(e->this.yourBracket());
        finalizeButton.setOnAction(e->finalizeBracket());
        this.randomize.setOnAction(e->this.randomSelection());//chris
        back.setOnAction(e->{
            bracketPane=new BracketPane(selectedBracket,this);
            displayPane(bracketPane);
        });
    }
    
    /**
     * Creates a spacer for centering buttons in a ToolBar
     */
    private Pane createSpacer(){
        Pane spacer = new Pane();
        HBox.setHgrow(
                spacer,
                Priority.SOMETIMES
        );
        return spacer;
    }
    
    
    private BorderPane createLogin(){ //edited by josh
        
        
        /*
        LoginPane
        Sergio and Joao
         */
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


        TextArea directions= new TextArea();//Josh

        //Josh Start
        //Right panel
        Image image = new Image(new File("/home/jshilts/IdeaProjects/Project 4 v2/src/march2").toURI().toString());
        ImageView i = new ImageView();
        i.setImage(image);
        i.setFitHeight(500);
        i.setFitWidth(350);
        gridRight.setPadding(new Insets(35,80,10,10));
        gridRight.getChildren().addAll(i);

        //Top
        //Style Title
        Text welcomeMessage = new Text("March Madness Login");
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        welcomeMessage.setEffect(ds);
        welcomeMessage.setCache(true);
        welcomeMessage.setFont(Font.font("Arial", FontWeight.BOLD,32));
        loginPage.setAlignment(welcomeMessage,Pos.TOP_CENTER);
        loginPage.setTop(welcomeMessage);
        //Josh End

        Label userName = new Label("User Name: ");

        userName.setFont(Font.font("Arial", FontWeight.BOLD,14));//Josh
        loginPane.add(userName, 0, 1);

        TextField enterUser = new TextField();
        loginPane.add(enterUser, 1, 1);


        Label password = new Label("Password: ");
        password.setFont(Font.font("Arial", FontWeight.BOLD,14));//Josh
        loginPane.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        loginPane.add(passwordField, 1, 2);

        //Josh start
        //Left
        Button instruction = new Button("Instruction");

        instruction.setFont(Font.font("Arial", FontWeight.BOLD,14));
        instruction.setStyle("-fx-base: ADD8E6 ;");
        instructionPane.setPadding(new Insets(10,0,10,65));
        instructionPane.setAlignment(Pos.CENTER);
        instructionPane.setStyle("-fx-background-color: #6495ED;");
        directions.setPrefSize(250,400);

        instructionPane.getChildren().addAll(instruction,directions);
        directions.setWrapText(true);
        directions.setEditable(false);

        //Josh end

        Button signButton = new Button("Sign in");
        signButton.setFont(Font.font("Arial", FontWeight.BOLD,14));//Josh
        loginPane.add(signButton, 1, 4);
        signButton.setDefaultButton(true);//added by matt 5/7, lets you use sign in button by pressing enter

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

           // playerMap.put(name,startingBracket);

            //Josh Start
            if(enterUser.getText().equals(""))
                infoAlert("Please enter a valid user name");
            else
                if(passwordField.getText().equals(""))
                    infoAlert("Please enter a valid password");


            //Clears text feilds for user security
            enterUser.setText("");
            passwordField.setText("");
            if(directions!=null)
                directions.setText("");
                instruction.setDisable(false);

            //Josh End

            if(playerMap.get(name)==null)
            	playerMap.remove(name);
            System.out.println(playerMap.get(name));
            if (playerMap.get(name) != null) {
                //check password of user
                 
                Bracket tmpBracket = this.playerMap.get(name);
               System.out.println(playerMap.get(name));
                String password1 = tmpBracket.getPassword();

                if (Objects.equals(password1, playerPass)) {

                    // load bracket
                    selectedBracket=playerMap.get(name);
                    this.userName=name;
                    this.password=playerPass;
                    chooseBracket();
                    bracketPane.setGUI(this);

                    randomize.setDisable(false);

                }else{
                   infoAlert("The password you have entered is incorrect!");
                    randomize.setDisable(false);
                }

            } else {
                //check for empty fields
                if(!name.equals("")&&!playerPass.equals("")){

                    //create new bracket
                    Bracket tmpPlayerBracket = new Bracket(startingBracket, name);
                    //playerBrackets.add(tmpPlayerBracket);
                    tmpPlayerBracket.setPassword(playerPass);

                    //playerMap.put(name, tmpPlayerBracket);
                    selectedBracket = tmpPlayerBracket;
                    //alert user that an account has been created
                    infoAlert("No user with the Username \""  + name + "\" exists. A new account has been created.");
                    randomize.setDisable(false);
                    this.userName=name;
                    this.password=playerPass;
                    chooseBracket();
                    bracketPane.setGUI(this);
                }
            }
        });

        //Josh Start
        instruction.setOnAction(event -> {

            try {
                Scanner scan = new Scanner(new File("C:\\Users\\Christopher\\Project4 Prototype\\src\\backup\\Instructions.txt"));
                int x = 0;
                while (scan.hasNext() ) {

                    directions.appendText(" " + scan.next());
                    directions.setFont(Font.font("Arial", FontPosture.ITALIC,14));//Josh
                  //  t.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));

                }
                instruction.setDisable(true);
                scan.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        });
        //Josh End
        
        return loginPage;
    }
    
    /**
     * addAllToMap
     * adds all the brackets to the map for login
     */
    private void addAllToMap(){
        for(Bracket b:playerBrackets){
            playerMap.put(b.getPlayerName(), b);   
        }
    }
    
    /**
     * The Exception handler
     * Displays a error message to the user
     * and if the error is bad enough closes the program
     *
     * @param fatal true if the program should exit. false otherwise 
     */
    private void showError(Exception e,boolean fatal){
        String msg=e.getMessage();
        if(fatal){
            msg=msg+" \n\nthe program will now close";
            //e.printStackTrace();
        }
        Alert alert = new Alert(AlertType.ERROR,msg);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(420);   
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.showAndWait();
        if(fatal){ 
            System.exit(666);
        }   
    }
    
    /**
     * alerts user to the result of their actions in the login pane 
     * @param msg the message to be displayed to the user
     */
    private void infoAlert(String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    /**
     * Prompts the user to confirm that they want
     * to clear all predictions from their bracket
     * @return true if the yes button clicked, false otherwise
     */
    private boolean confirmReset(){
        Alert alert = new Alert(AlertType.CONFIRMATION, 
                "Are you sure you want to reset the ENTIRE bracket?", 
                ButtonType.YES,  ButtonType.CANCEL);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult()==ButtonType.YES;
    }
    
    
    /**
     * Tayon Watson 5/5
     * seralizedBracket
     * @param B The bracket the is going to be seralized
     */
    // need to add finally
    private void seralizeBracket(Bracket B){
        FileOutputStream outStream = null;
        ObjectOutputStream out = null;
    try 
    {
      outStream = new FileOutputStream(B.getPlayerName()+".ser");
      out = new ObjectOutputStream(outStream);
      out.writeObject(B);
      
    } 
    catch(IOException e)
    {
      // Grant osborn 5/6 hopefully this never happens 
      showError(new Exception("Error saving bracket \n"+e.getMessage(),e),false);
    }

    }
    /**
     * Tayon Watson 5/5
     * deseralizedBracket
     * @param filename of the seralized bracket file
     * @return deserialized bracket 
     */
    private Bracket deseralizeBracket(String filename) throws FileNotFoundException,
            IOException{
        
        // worked on by Elizabeth 4/1/19
        
        // no default constructor for bracket - we fixed
        Bracket bracket = new Bracket();
        FileInputStream inStream = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(inStream);
        
    try 
    {
        bracket = (Bracket) in.readObject();
        
        
    }catch (FileNotFoundException | ClassNotFoundException e) {
      // Grant osborn 5/6 hopefully this never happens either
      showError(new Exception("Error loading bracket \n" +e.getMessage(),e),false);
    } 
    finally{
        in.close();
        inStream.close();
    }
    // we should really move in.close() into finally block
    return bracket;
    }
    
      /**
     * Tayon Watson 5/5
     * deseralizedBracket
     *
     * @return deserialized bracket 
     */
    private ArrayList<Bracket> loadBrackets()
    {   
        ArrayList<Bracket> list=new ArrayList<Bracket>();
        File dir = new File(".");
        for (final File fileEntry : dir.listFiles()){
            String fileName = fileEntry.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);
       
            if (extension.equals("ser")){
                try{
                	if(deseralizeBracket(fileName).getPlayerName()!=null)
                list.add(deseralizeBracket(fileName));
                }
                catch (FileNotFoundException e){
                    showError(new Exception("File not found \n" +e.getMessage(),e),false);
                }
                catch (IOException e){
                    showError(new Exception("IO Exception \n" +e.getMessage(),e),false);
                }
                
            }
        }
        return list;
    }
       
}
