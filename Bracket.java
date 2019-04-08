import java.util.ArrayList;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author jshilts
 * Created by Matt and Dan on 5/1/2017.
 * Contributor: Hillary Ssemakula 5/1
 * Contributor: Joshua Shilts 4/?/19
 * @description This is the bracket class it hold information pertain to a specific user or
 *        player and the choice they have made and the core they have received. This class also implements the
 *        serializable interface to help keep this data encapsulated.*/
public class Bracket implements Serializable {

    //User Info
    private String playerName;
    private String password;
    private int score = 0;

    private ArrayList<String> bracket;
    private transient int[] teamScores = new int[127];

    static final int EAST_BRACKET = 3;
    static final int WEST_BRACKET = 4;
    static final int MIDWEST_BRACKET = 5;
    static final int SOUTH_BRACKET = 6;

    public static final long serialVersionUID = 5609181678399742983L;
    private boolean isSim=false;//Value that determines whether or not this is a simulated bracket


    /**Task: This is brackets classes default contructor that take
     *       no data feilds and a hardcoded user name and password*/
    public Bracket(){//Default constructor added by Elizabeth 4/1/19
        bracket = new ArrayList<String>();
        playerName = "default";
        password = "1234";
    }

    /**Task: This is a paramitized contrustor that takes an array list contain the names
     *       for the teams to choose from in this bracket
     @param starting, and arraylist containing the 64 teams participating in the tournament*/
    public Bracket(ArrayList<String> starting){
        bracket = new ArrayList<String>(starting);
        while(bracket.size()<127){
            bracket.add(0,"");
        }
    }

    /**Task: This is another paramitzed contructor that takes a bracket and
     *       creates a new bracket based of the parametiter
     * @param starting, master bracket pre-simulation*/
    public Bracket(Bracket starting){ bracket = new ArrayList<String>(starting.getBracket()); }


    /**Task: Constructor that creates a new bracket with a users name
     * @param starting, master bracket pre-simulation
     * @param user, name of the new bracket owner*/
    public Bracket(Bracket starting, String user){
        bracket = new ArrayList<String>(starting.getBracket());
        playerName = user;
    }

    /**Task: Returns an arrayList that contain all the names of the team
     *       for this selected bracket
     * @return an arraylist of team names*/
    public ArrayList<String> getBracket(){
        return bracket;
}

    /**Task: This method handles advance teams in the brackets. Meaning as
     *       user selects that selection goes to the corresponding bracket
     *       updated by matt(whoever you are) 5/7, now removesAbove anytime
     *       the above position is not equal to the clicked one
     * @param position, the starting position of the team to be moved*/
    public void moveTeamUp(int position){

        int newPos = (int)((position-1)/2);

        if(!bracket.get(position).equals(bracket.get(newPos))) {

            bracket.set(newPos, bracket.get(position));
        }
    }

    /**Task: Resets all children of root location except for initail teams at
     *       final children special behavior if root = 0; just resets the final 4
     *  @param root, everything below and including this is reset*/
    public void resetSubtree(int root) {

        if (root == 3) {

            resetFullTree(3);

        } else {

            if (root == 7 || root == 0) {
                resetFullTree(3);//resets top left bracket
                resetFullTree(4);//resets bottom left bracket
                resetFullTree(5);//resets top right bracket
                resetFullTree(6);//resets bottom right bracket
                resetFullTree(0);//resets the final 4  choices

            } else {

                if (root == 0) {//special behavior to reset final 4
                    for (int i = 0; i < 7; i++) {
                        bracket.set(i, "");
                    }
                } else {

                    int child1 = 2 * root + 1;
                    int child2 = 2 * root + 2;

                    if (child1 < 64) {//child is above round 1

                        resetSubtree(child1);
                    }

                    if (child2 < 64) {

                        resetSubtree(child2);
                    }
                }

                if (root < 63)

                    bracket.set(root, "");
            }
        }
    }
  
    /**Task: Removes all future wins of a team, including spot that this is called from
     * @param child, index of the first place that the team gets deselected*/
    public void removeAbove(int child){//renamed by matt 5/1
        if (child==0)

            bracket.set(child,"");
        else {

            int parent = (int) ((child - 1) / 2);

            if (bracket.get(parent).equals(bracket.get(child))) {
                removeAbove(parent);
            }

            bracket.set(child, "");
        }
    }

    /**Task: This method is used to compare parent name to child name, it allows
     *       the program to delete the nodes if parent name  is the same as child
     *       name amd if the current position has already been set to and empty String
     *
     * @param child the node where to start
     * @param name varable that is going to be check against childs*/
    public void removeAboveCurrent(int child, String name) {//added by zion 4/3
    	 if (child==0)

    	     bracket.set(child,"");
         else {														

             int parent = (int) ((child - 1) / 2);
             
            if (bracket.get(parent).equals(name)) {
                 removeAboveCurrent(parent,name);
             }

            bracket.set(child, "");
         }
    }
    
    /**Task: This is how the user can reset the entire tree or the entire selection
     *@param root location that is going to be set*/
     public void resetFullTree(int root) {

    	if (root ==0){//special behavior to reset final 4

    	    for (int i = 0; i < 7; i++) {
                bracket.set(i,"");
            }

    	} else {

            int child1 = 2 * root + 1;
            int child2 = 2 * root + 2;

            if (child1 < 64) {//child is above round 1
                resetFullTree(child1);
            }

            if (child2 < 64) {
                resetFullTree(child2);
            }

            if(root<63)
            bracket.set(root, "");
        }
    }

    /**Task: Adds a new value to the bracket arrayList and is used for creating new brackets
     * @param position, index to add new value
     * @param s, string added to bracket*/
    private void add(int position, String s){ bracket.add(position, s); }

    /** Task: Set player's password to string parameter
     * @param password, a String that will be user password*/
    public void setPassword(String password) { this.password = password; }

    /** Task: Returns the name of the player
     * @return name of player*/
    public String getPlayerName() { return playerName; }

    /** Task: Returns the player's password
     * @return String that is users password*/
    public String getPassword() { return password; }

    /** Task: Returns true or false depending on whether there are any empty slots on the bracket.
     *        If a position has an empty string then the advancing team has not been chosen for
     *        that spot and the whole bracket is not complete.
     * @return true or false depending on weather the user has filled out all brackets.*/
    public boolean isComplete() {

        for(String team: bracket){

            if(team.equals("")){ return false; }
        }
        return true;
    }
    
    /** Task: Returns true or false depending on whether there are any empty slots in te bracket from
     *        a given point all the way to the starting 64 teams. If the root itself is empty return false.
     *        Otherwise the method is recursively applied to the left and right subtrees of the root.
     * @param root, the int index of the root
     * @return true or false based on weather a bracket is done*/
    public boolean isSubtreeComplete(int root) {
        if(bracket.get(root).equals(""))
            return false;

        int rightChild = 2 * root + 2;
        int leftChild = 2 * root + 1;

        if(leftChild< bracket.size() && rightChild<bracket.size())

            return isSubtreeComplete(leftChild) && isSubtreeComplete(rightChild);

        return true;
    }

    /**Task: Scores the bracket by assigning points of each correct winner number of
     *       points is based on round
     * @param master, the master bracket of true winners to which all brackets are compared*/
    public int scoreBracket(Bracket master){

        int score = 0;

        if (bracket.get(0).equals(master.getBracket().get(0)))//finals
            score+=32;

        for (int i = 1; i < 3; i++) {
            if (bracket.get(i).equals(master.getBracket().get(i)))//semi
                score+=16;
        }

        for (int i = 3; i < 7; i++) {
            if (bracket.get(i).equals(master.getBracket().get(i)))//quarters
                score+=8;
        }

        for (int i = 7; i < 15; i++) {
            if (bracket.get(i).equals(master.getBracket().get(i)))//sweet 16
            score+=4;
        }

        for (int i = 15; i < 31; i++) {
            if (bracket.get(i).equals(master.getBracket().get(i)))//round of 32
            score+=2;
        }

        for (int i = 31; i < 63; i++) {
            if (bracket.get(i).equals(master.getBracket().get(i)))//round of 64
            score+=1;
        }

        return score;
    }

    /**Task: Sets the playerName for the bracket
     * @param user, name of the player*/
    public void setPlayerName(String user){ playerName = user; }

   /**Task:Set teamScore for a game
     * @param game, index of the place that will be scored
     * @param score, the amount of points that the team scores*/
    public void setTeamScore(int game, int score){ teamScores[game] = score; }

    /**Task: Gets the score at a particular index
     * @param index, the place in the bracket that you retrieve the score from
     * @return the score at that index*/
    public int getTeamScore(int index){ return teamScores[index]; }

    /**Task: return true or flase if the sim has run
     *@return true or false weather it has been run*/
    public boolean getSim() { return isSim; }

    /**Task: will set boolean value of sim
     *@param b the new value sim will be set to*/
    public void setSim(boolean b) { isSim=b; }

    /**Task: will set score value o
     *@param x the new value score will be set to*/
    public void setScore(int x){ score = x; }

    @Override
    /**Task: return a formatted datafeild of this brackets data
     * @return a foramteed String with a brackets data*/
    public String toString() {
        return "Bracket{" +
                "bracket=" + bracket +
                ", teamScores=" + Arrays.toString(teamScores) +
                ", playerName='" + playerName + '\'' +
                ", password='" + password + '\'' +
                ", score=" + score +
                ", isSim=" + isSim +
                '}';
    }

    /**Task: will return a score that is part of a bracket
     * @return the score for the bracket*/
    public int getScore(){ return score; }
}


