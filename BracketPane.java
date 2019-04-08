import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.Region;

/**Task: This is the BracketPane class that combines the structural functionalities of bracket and
 *       the data encapsulated from team and put the associated classes in a bracket pane*/
public class BracketPane extends BorderPane {

    ArrayList<Root> roots;
    private MarchMadnessGUI gui;

    /**Reference to the Simulated Bracket*/
    private Bracket simulatedBracket;

    /**Reference to the graphical representation of the nodes within the bracket.*/
    private static ArrayList<BracketNode> nodes;

    /**Used to initiate the paint of the bracket nodes*/
    private static boolean isTop = true;

    /**Maps the text "buttons" to it's respective grid-pane*/
    private HashMap<StackPane, Pane> panes;

    /**Reference to the current bracket.*/
    private Bracket currentBracket;

    /**Reference to active subtree within current bracket.*/
    private int displayedSubtree=0;

    /**Keeps track of whether or not bracket has been finalized.*/
    private boolean finalized;

    /** Important logical simplification for allowing for code that is easierto maintain.*/
    private HashMap<BracketNode, Integer> bracketMap = new HashMap<>();

    /**Reverse of the above;*/
    private HashMap<Integer, BracketNode> nodeMap = new HashMap<>();

    /** records the last bracket the user viewed*/
    private int lastposition=7;//Chris - records the last bracket the user viewed

    /**Task: removes a team if the user wantrs to change his selection in the BracketPane
     * @param treeNum location to navigate to node where team and data is being displayed*/
    private void clearAbove(int treeNum) {

        int nextTreeNum = (treeNum - 1) / 2;

        if (!nodeMap.get(nextTreeNum).getName().isEmpty()) {
            nodeMap.get(nextTreeNum).setName("");
            clearAbove(nextTreeNum);
        }
    }
        
    /**Task: Clears a subtree and reflect it on dsplay pane*/
    public void clear(){ clearSubtree(displayedSubtree); }

    /**Task: Used to fix a bug with autodefaulting to viewing the full pane when clicking clear twice
     *@param a the node to refernce*/
    public void updateDisplayedSubtree(int a) { this.displayedSubtree=a; }

    /**Task: Handles clicked events for BracketNode objects*/
    private EventHandler<MouseEvent> clicked = mouseEvent -> {

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

            BracketNode n = (BracketNode) mouseEvent.getSource();
            int treeNum = bracketMap.get(n);
            int nextTreeNum = (treeNum - 1) / 2;

            if (!nodeMap.get(nextTreeNum).getName().equals(n.getName())) {

                currentBracket.removeAbove((nextTreeNum));

                clearAbove(treeNum);
                nodeMap.get((bracketMap.get(n) - 1) / 2).setName(n.getName());
                currentBracket.moveTeamUp(treeNum);
            }
        }

        else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {

            String text = "";
            BracketNode n = (BracketNode) mouseEvent.getSource();
            int treeNum = bracketMap.get(n);
            String teamName = currentBracket.getBracket().get(treeNum);

            try {
                TournamentInfo info = new TournamentInfo();
                Team t = info.getTeam(teamName);
                text += "Team: " + teamName + " | Ranking: "
                        + t.getRanking() + "\n" + "Mascot: " + t.getNickname() + "\n"+"Info: " +
                        t.getInfo() + "\n" + "Average Offensive PPG: " + t.getOffensePPG() + "\n"+"Average Defensive PPG: "+ t.getDefensePPG();//Josh

            } catch (IOException e) {//if for some reason TournamentInfo isnt working, it will display info not found
                text += "Info for " + teamName + "not found";

            }
                        //create a popup with the team info
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.CLOSE);
            alert.setTitle("March Madness Bracket Simulator");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    };

    /**Task:Handles mouseEntered events for BracketNode objects
     * @return The event realted to this action*/
    private EventHandler<MouseEvent> enter = mouseEvent -> {
        BracketNode tmp = (BracketNode) mouseEvent.getSource();
        tmp.setStyle("-fx-background-color: lightcyan;");
        tmp.setEffect(new InnerShadow(10, Color.LIGHTCYAN));
        };

    /**Task: Handles mouseExited events for BracketNode objects
     * @return Return event realted to this mouse event*/
    private EventHandler<MouseEvent> exit = mouseEvent -> {

        BracketNode tmp = (BracketNode) mouseEvent.getSource();
        tmp.setStyle(null);
        tmp.setEffect(null);
    };

    /**Task: Included this to fix a bug where a user would click clear twice and if would auto default to
     *      sending them back to the fullpane view*/
    public void updateLast(int l) { this.lastposition=l; }//Chris

    /**Task: Will return a gridPane that hold all of our brackets
     *@return gridpane and all that it contains*/
    public GridPane getFullPane() { return fullPane; }

    /**Where the gridpane goes*/
    private GridPane center;

    /**Fianl gridpane*/
    private GridPane fullPane;

    /**Task: Will get a specific pane and return it
     *
     * @param a index used to find pane
     * @return the pane you were looking for*/

    public Pane getPane(int a){//Chris

        Pane temp = new Pane();

        if(a==7||a==0)
             temp=getFullPane();
        else
            temp = roots.get(a-3);

        return temp;
    }

    /**Task: will return last position of pane
     *@return the last position of pane*/
    public int getLastPosition() { return lastposition; }//Chris

    /**Task: will return a and siplay the data contained in a tree
     *@return the tree and its data*/
    public int getDisplayedSubTree() {return this.displayedSubtree;}//Chris

    /**Task: Will set the GUI to a pane that you want
     *@param g the gui you want to set the pane to*/
    public void setGUI(MarchMadnessGUI g) { this.gui=g; }

    /**Task: This is a BracketPane constructor it initializes the properties
     *       needed to construct a bracket.
     * @param currentBracket the bracket that is to be created*
     * @param g the GUI in use*/
    public BracketPane(Bracket currentBracket,MarchMadnessGUI g) {

        displayedSubtree = 0;
        this.displayedSubtree = g.getLast();
        this.gui = g;

        this.currentBracket = currentBracket;
        this.simulatedBracket = null;

        bracketMap = new HashMap<>();
        nodeMap = new HashMap<>();
        panes = new HashMap<>();
        nodes = new ArrayList<>();
        roots = new ArrayList<>();
        center = new GridPane();

        ArrayList<StackPane> buttons = new ArrayList<>();
        buttons.add(customButton("EAST"));
        buttons.add(customButton("WEST"));
        buttons.add(customButton("MIDWEST"));
        buttons.add(customButton("SOUTH"));
        buttons.add(customButton("FULL"));

        ArrayList<GridPane> gridPanes = new ArrayList<>();

        for (int m = 0; m < buttons.size() - 1; m++) {

            roots.add(new Root(3 + m));
            panes.put(buttons.get(m), roots.get(m));
        }

        Pane finalPane = createFinalFour();

        fullPane = new GridPane();
        GridPane gp1 = new GridPane();
        gp1.add(roots.get(0), 0, 0);
        gp1.add(roots.get(1), 0, 1);
        GridPane gp2 = new GridPane();
        gp2.add(roots.get(2), 0, 0);
        gp2.add(roots.get(3), 0, 1);
        gp2.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        fullPane.add(gp1, 0, 0);
        fullPane.add(finalPane, 1, 0, 1, 2);
        fullPane.add(gp2, 2, 0);
        fullPane.setAlignment(Pos.CENTER);
        panes.put(buttons.get((buttons.size() - 1)), fullPane);
        finalPane.toBack();

        // Initializes the button grid
        GridPane buttonGrid = new GridPane();

        for (int i = 0; i < buttons.size(); i++){
            buttonGrid.add(buttons.get(i), 0, i);
        }

        buttonGrid.setAlignment(Pos.CENTER);

        // set default center to the button grid
        this.setCenter(buttonGrid);

        for (StackPane t : buttons) {
            t.setOnMouseEntered(mouseEvent -> {
                t.setStyle("-fx-background-color: lightblue;");
                t.setEffect(new InnerShadow(10, Color.LIGHTCYAN));
            });

            t.setOnMouseExited(mouseEvent -> {
                t.setStyle("-fx-background-color: orange;");
                t.setEffect(null);
            });

            t.setOnMouseClicked(mouseEvent -> {
                setCenter(null);

                /**panes are added as ScrollPanes to retain center alignment when
                 * moving through full-view and region-view*/
                center.add(new ScrollPane(panes.get(t)), 0, 0);
                center.setAlignment(Pos.CENTER);
                setCenter(center);

                displayedSubtree=buttons.indexOf(t)==7?0:buttons.indexOf(t)+3;
                System.out.println("DisplayedSubTreeNumber: "+displayedSubtree);

                if(gui!=null)
                    gui.updateLast(displayedSubtree);
                else
                    System.out.println("gui null");
            });
        }
    }

    /**Task:Allows you to define a BracketPane with a specified simulated Bracket as well as a current bracket
     * @param currentBracket  the bracket that is to be created
     * @param simulatedBracket the bracket that has been simulated*/
    public BracketPane(Bracket currentBracket, Bracket simulatedBracket) {
        displayedSubtree=0;
        this.currentBracket = currentBracket;
        this.simulatedBracket=simulatedBracket;

        bracketMap = new HashMap<>();
        nodeMap = new HashMap<>();
        panes = new HashMap<>();
        nodes = new ArrayList<>();
        roots = new ArrayList<>();

        center = new GridPane();

        ArrayList<StackPane> buttons = new ArrayList<>();
        buttons.add(customButton("EAST"));
        buttons.add(customButton("WEST"));
        buttons.add(customButton("MIDWEST"));
        buttons.add(customButton("SOUTH"));
        buttons.add(customButton("FULL"));

        ArrayList<GridPane> gridPanes = new ArrayList<>();

        for (int m = 0; m < buttons.size() - 1; m++) {
            roots.add(new Root(3 + m,simulatedBracket));
            panes.put(buttons.get(m), roots.get(m));

        }

        Pane finalPane = createFinalFour();

        fullPane = new GridPane();
        GridPane gp1 = new GridPane();
        gp1.add(roots.get(0), 0, 0);
        gp1.add(roots.get(1), 0, 1);
        GridPane gp2 = new GridPane();
        gp2.add(roots.get(2), 0, 0);
        gp2.add(roots.get(3), 0, 1);
        gp2.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        fullPane.add(gp1, 0, 0);
        fullPane.add(finalPane, 1, 0, 1, 2);
        fullPane.add(gp2, 2, 0);
        fullPane.setAlignment(Pos.CENTER);
        panes.put(buttons.get((buttons.size() - 1)), fullPane);
        finalPane.toBack();

        // Initializes the button grid
        GridPane buttonGrid = new GridPane();

        for (int i = 0; i < buttons.size(); i++) {
            buttonGrid.add(buttons.get(i), 0, i);
        }

        buttonGrid.setAlignment(Pos.CENTER);

        // set default center to the button grid
        this.setCenter(buttonGrid);

        for (StackPane t : buttons) {
            t.setOnMouseEntered(mouseEvent -> {
                t.setStyle("-fx-background-color: lightblue;");
                t.setEffect(new InnerShadow(10, Color.LIGHTCYAN));
            });
            t.setOnMouseExited(mouseEvent -> {
                t.setStyle("-fx-background-color: orange;");
                t.setEffect(null);
            });
            t.setOnMouseClicked(mouseEvent -> {
                setCenter(null);
                /**panes are added as ScrollPanes to retain center alignment when moving through full-view and region-view*/
                center.add(new ScrollPane(panes.get(t)), 0, 0);
                center.setAlignment(Pos.CENTER);
                setCenter(center);

                displayedSubtree=buttons.indexOf(t)==7?0:buttons.indexOf(t)+3;
            });
        }
    }

    /**Task: Helpful method to retrieve our magical numbers
     *
     * @param root the root node (3,4,5,6)
     * @param pos  the position in the tree (8 (16) , 4 (8) , 2 (4) , 1 (2))
     * @return The list representing the valid values.*/
    public ArrayList<Integer> helper(int root, int pos) {

        ArrayList<Integer> positions = new ArrayList<>();

        int base = 0;
        int tmp = (root * 2) + 1;

        if (pos == 8) base = 3;

        else if (pos == 4) base = 2;

        else if (pos == 2) base = 1;

        for (int i = 0; i < base; i++) tmp = (tmp * 2) + 1;

        for (int j = 0; j < pos * 2; j++) positions.add(tmp + j);

        return positions;
    }

    /**Task: Sets the current bracket to bracket being passed in parameter field,
     *@param target The bracket to replace currentBracket*/
    public void setBracket(Bracket target) { currentBracket = target; }

    /**Task:Clears the sub tree from,
     * @param position The position to clear after*/
    public void clearSubtree(int position) {

        String currentName = nodeMap.get(position).getName();

        this.lastposition=position;
        currentBracket.resetSubtree(position);
        currentBracket.removeAboveCurrent(position,currentName);
    }

    /**Task:Resets the bracket-display*/
    public void resetBracket() { currentBracket.resetSubtree(0); }

    /**Task:Requests a message from current bracket to tell if the bracket
     *      has been completed.
     *@return True if completed, false otherwise.*/
    public boolean isComplete() { return currentBracket.isComplete(); }

    /**Task: This methods checks to see if the current-bracket is complete
     * @return true if  and the value of finalized is also true.*/
    public boolean isFinalized() { return currentBracket.isComplete() && finalized; }

    /**Task: Allows somone to change the simulation so make it finalized
     * @param isFinalized The value to set finalized to.*/
    public void setFinalized(boolean isFinalized) { finalized = isFinalized && currentBracket.isComplete(); }

    /**Task: This creates and designs the the StackPane that hold the custom
     *       buttons that guides us to related brackets
     * @param name The name of the button
     * @return pane The stack-pane "button"*/
    private StackPane customButton(String name) {
        StackPane pane = new StackPane();
        Rectangle r = new Rectangle(100, 50, Color.TRANSPARENT);
        Text t = new Text(name);
        t.setTextAlignment(TextAlignment.CENTER);
        pane.getChildren().addAll(r, t);
        pane.setStyle("-fx-background-color: orange;");
        return pane;
    }

    /**Task: This create the Pane final Four which display all four brackets in a region view display
     *@return a pane that shows all four brackets*/
    public Pane createFinalFour() {

        /**Create pane to hold brackets*/
        Pane finalPane = new Pane();

        /**Set locations for brackets*/
        BracketNode nodeFinal0 = new BracketNode("", 162, 300, 70, 0);
        BracketNode nodeFinal1 = new BracketNode("", 75, 400, 70, 0);
        BracketNode nodeFinal2 = new BracketNode("", 250, 400, 70, 0);

        /**Gets names for brackets from current bracket*/
        nodeFinal0.setName(currentBracket.getBracket().get(0));
        nodeFinal1.setName(currentBracket.getBracket().get(1));
        nodeFinal2.setName(currentBracket.getBracket().get(2));

        /**Populates the brackets*/
        finalPane.getChildren().add(nodeFinal0);
        finalPane.getChildren().add(nodeFinal1);
        finalPane.getChildren().add(nodeFinal2);

        /**Adds node for relatating from GUI to brackets*/
        bracketMap.put(nodeFinal1, 1);
        bracketMap.put(nodeFinal2, 2);
        bracketMap.put(nodeFinal0, 0);

        nodeMap.put(1, nodeFinal1);
        nodeMap.put(2, nodeFinal2);
        nodeMap.put(0, nodeFinal0);

        //chris
        if(simulatedBracket!=null) {

            //check to see if the final 3 choices match the simulated choices. If they do color them green, if not red
            if(currentBracket.getBracket().get(0).equalsIgnoreCase(simulatedBracket.getBracket().get(0))) {

                nodeFinal0.setColor(Color.GREEN);

            } else {
                nodeFinal0.setColor(Color.RED);
            }

            if(currentBracket.getBracket().get(1).equalsIgnoreCase(simulatedBracket.getBracket().get(1))) {
                nodeFinal1.setColor(Color.GREEN);

            } else {
                nodeFinal1.setColor(Color.RED);
            }
            if(currentBracket.getBracket().get(2).equalsIgnoreCase(simulatedBracket.getBracket().get(2))) {
                nodeFinal2.setColor(Color.GREEN);
            }
            else
                nodeFinal2.setColor(Color.RED);
        }

        nodeFinal0.setOnMouseClicked(clicked);
        nodeFinal0.setOnMouseDragEntered(enter);
        nodeFinal0.setOnMouseDragExited(exit);

        nodeFinal1.setOnMouseClicked(clicked);
        nodeFinal1.setOnMouseDragEntered(enter);
        nodeFinal1.setOnMouseDragExited(exit);

        nodeFinal2.setOnMouseClicked(clicked);
        nodeFinal2.setOnMouseDragEntered(enter);
        nodeFinal2.setOnMouseDragExited(exit);
        nodeFinal0.setStyle("-fx-border-color: darkblue");
        nodeFinal1.setStyle("-fx-border-color: darkblue");
        nodeFinal2.setStyle("-fx-border-color: darkblue");
        finalPane.setMinWidth(400.0);

        return finalPane;
    }

    /**Task: Draws out the nodes for Bracket
     *
     * Creates the graphical representation of a subtree.
     * Note, this is a vague model. */
    private class Root extends Pane {

        /**Where they will go*/
        private int location;

        /**The simulated bracket reference*/
        private Bracket sim;

        /**Task: This is roots constructor that will create and build our tree
         *       based on values in constructor
         *
         * @param location where the vertices will go
         * @param sim from which bracket the names will get pulled from and stored in the bracket*/
        public Root(int location, Bracket sim) {

            this.location = location;
            this.sim = sim;
            createVertices(420, 200, 100, 20, 0, 0);
            createVertices(320, 119, 100, 200, 1, 0);
            createVertices(220, 60, 100, 100, 2, 200);
            createVertices(120, 35, 100, 50, 4, 100);
            createVertices(20, 25, 100, 25, 8, 50);

            for (BracketNode n : nodes) {
                n.setOnMouseClicked(clicked);
                n.setOnMouseEntered(enter);
                n.setOnMouseExited(exit);
            }
        }
        /**Task:This is another constructor for Root that only takes a location
         *
         * @param location where we start*/
        public Root(int location) {
            this.location = location;

            createVertices(420, 200, 100, 20, 0, 0);
            createVertices(320, 119, 100, 200, 1, 0);
            createVertices(220, 60, 100, 100, 2, 200);
            createVertices(120, 35, 100, 50, 4, 100);
            createVertices(20, 25, 100, 25, 8, 50);

            for (BracketNode n : nodes) {
                n.setOnMouseClicked(clicked);
                n.setOnMouseEntered(enter);
                n.setOnMouseExited(exit);
            }
        }

        /**Task:Creates 3 lines in appropriate location unless it is the last line.
         *      Adds these lines and "BracketNodes" to the Pane of this inner class
         *
         * @param iX    Starting X corrdinate for line
         * @param iY    Starting Y corrdinate for line
         * @param iXO   End X corrdinate for line
         * @param iYO   End Y corrdinate for line
         * @param num   the number of lines we have
         * @param increment keeps track of what the increment is.*/
        private void createVertices(int iX, int iY, int iXO, int iYO, int num, int increment) {

            int y = iY;

            if (num == 0 && increment == 0) {
                BracketNode last = new BracketNode("", iX, y - 20, iXO, 20);
                nodes.add(last);
                getChildren().addAll(new Line(iX, iY, iX + iXO, iY), last);
                last.setName(currentBracket.getBracket().get(location));

                ///chris

                if(sim!=null) {
                    //check to see if the division final choices match thesimulated choices. If they do color them green. If not color them red
                    if(currentBracket.getBracket().get(location).equalsIgnoreCase(sim.getBracket().get(location))) {
                        last.setColor(Color.GREEN);

                    } else {

                        last.setColor(Color.RED);
                    }
                }

                bracketMap.put(last, location);
                nodeMap.put(location, last);

            } else {

                ArrayList<BracketNode> aNodeList = new ArrayList<>();

                for (int i = 0; i < num; i++) {

                    Point2D tl = new Point2D(iX, y);
                    Point2D tr = new Point2D(iX + iXO, y);
                    Point2D bl = new Point2D(iX, y + iYO);
                    Point2D br = new Point2D(iX + iXO, y + iYO);

                    BracketNode nTop = new BracketNode("", iX, y - 20, iXO, 20);

                    aNodeList.add(nTop);
                    nodes.add(nTop);

                    BracketNode nBottom = new BracketNode("", iX, y+(iYO - 20), iXO, 20);

                    aNodeList.add(nBottom);
                    nodes.add(nBottom);

                    Line top = new Line(tl.getX(), tl.getY(), tr.getX(), tr.getY());
                    Line bottom = new Line(bl.getX(), bl.getY(), br.getX(), br.getY());
                    Line right = new Line(tr.getX(), tr.getY(), br.getX(), br.getY());
                    getChildren().addAll(top, bottom, right, nTop, nBottom);

                    isTop = !isTop;
                    y += increment;
                }

                ArrayList<Integer> tmpHelp = helper(location, num);
                             
                ///chris
                if(sim!=null) {
                                	
                    //For each created Node, check and see if it matches the simulated bracket's node. If it does color it green.
                    if(tmpHelp.size()!=16)//only color code the non predefined matches
                        for(int a=0;a<tmpHelp.size();a++) {

                            if(currentBracket.getBracket().get(tmpHelp.get(a)).equalsIgnoreCase(sim.getBracket().get(tmpHelp.get(a)))) {

                                aNodeList.get(a).setColor(Color.GREEN);

                            } else

                                aNodeList.get(a).setColor(Color.RED);
                        }
                }

                for (int j = 0; j < aNodeList.size(); j++) {

                    aNodeList.get(j).setName(currentBracket.getBracket().get(tmpHelp.get(j)));

                    bracketMap.put(aNodeList.get(j), tmpHelp.get(j));
                    nodeMap.put(tmpHelp.get(j), aNodeList.get(j));
                                        
                }
            }
        }
    }

    /**Task: This inner class will creates the rectanlge that hold each team name*/
    private class BracketNode extends Pane {

        /**The Team name**/
        private String teamName;

        /**The rectanle that will hold that name*/
        private Rectangle rect;

        /**The name that gets assigned to a node*/
        private Label name;

        /**Task: This is the bracketNode constructor that takes mutliple parameter
         *       fields to create the pane with team brackets
         *
         * @param teamName The name if any
         * @param x        The starting x location
         * @param y        The starting y location
         * @param rX       The width of the rectangle to fill pane
         * @param rY       The height of the rectangle*/
        public BracketNode(String teamName, int x, int y, int rX, int rY) {

            this.setLayoutX(x);
            this.setLayoutY(y);
            this.setMaxSize(rX, rY);
            this.teamName = teamName;

            rect = new Rectangle(rX, rY);
            rect.setFill(Color.TRANSPARENT);
            name = new Label(teamName);
            name.setTranslateX(5);
            getChildren().addAll(name, rect);
        }

        /**Task: Returns the teams name
         * @return teamName The teams name.*/
        public String getName() { return teamName;}

        /**Task: Allows you to change the color of the text
         * @param c the new color to be set*/
        public void setColor(Color c) { this.name.setTextFill(c); }

        /**Task: Set the name of a team at a node
         * @param teamName The name to assign to the node.*/
        public void setName(String teamName) {

            this.teamName = teamName;
            name.setFont(new Font(10));
            name.setText(teamName);
        }
    }
}//End Class
