import java.util.Objects;

/**Task: This is the mean class that provides encapsulation for all the teams being read
 *       in from the text file. it also proved functiona lties to organize and manupulate
 *       that data being read in.*/
public class Team{

    /**Data unique to User*/
    private String name;
    private String nickname;
    private String info;

    /**Data used to determines who wins*/
    private int ranking;

    /**These are public because they can be changed by someone in the future to update team stats*/
    public double offensePPG;
    public double defensePPG;

    /**Task: This is class teams paramitzed constructor. It will encapulate a team and
     *       their data so they can be used to pare or compete in the simulation
     * @param name The name of the team
     * @param nickname the nickname of the team
     * @param info A short description of the team
     * @param ranking The ranking in the team region from 1 to 16
     * @param oPPG offensive points per game
     * @param dPPG Defencsive points per game*/
    public Team(String name, String nickname, String info, int ranking, double oPPG, double dPPG){
        this.name = name;
        this.nickname = nickname;
        this.info = info;
        this.ranking = ranking;
        offensePPG = oPPG;
        defensePPG = dPPG;
    }

    /**Task: Will return a teams name
     *@return name the name of the team*/
    public String getName(){
        return name;
    }

    /**Task: Will get a teams mascot decription
     * @return nickname the mascot of the team*/
    public String getNickname(){
        return nickname;
    }

    /**Task: Gets deatailed info about specific team
     * @return info a short description of the team*/
    public String getInfo(){
        return info;
    }

    /**Task: Return the teams in question ranking
     * @return ranking the ranking from 1 - 16*/
    public int getRanking(){
        return ranking;
    }

    /**Task: Gets a team offense rating
     * @return offensePPG the average points per game for offense*/
    public double getOffensePPG(){
        return offensePPG;
    }

    /**Task: gets a teams defence rating
     * @return defensePPG*/
    public double getDefensePPG(){
        return defensePPG;
    }

    /**Task: Sets a teams info to what being passed in parameter field
     *@param info The short description of the team*/
    public void setInfo(String info){
        this.info = info;
    }

   /**Task: Sets a new Nickname for a team
     * @param newNickname the new nickname for a team*/
    public void setNickname(String newNickname){
        nickname = newNickname;
    }

    /**Task: Set a teams ranking and makes sure that whenever the mutator is
     *       called that the paramter entered doesnt exceed aour ranking limit
     *       of 1-16
     * @param ranking The ranking from 1 to 16*/
    public void setRanking(int ranking){
        this.ranking = ranking;
    }

    /**Task: Reset a teams defence to whatever double valuie is in paramter field.
     *@param newDefense The new points per game for defense*/
    public void setDefense(double newDefense){
        defensePPG = newDefense;
    }

    /**Task: Reset a teams defence to whatever double valuie is in paramter field.
     * @param newOffense the new points per game for offense.*/
    public void setOffense(double newOffense){
        offensePPG =  newOffense;
    }

    //Josh Start
    @Override
    /**Task: Formats a teams class data into a string
     * @return a formatted String with a team information*/
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", info='" + info + '\'' +
                ", ranking=" + ranking +
                ", offensePPG=" + offensePPG +
                ", defensePPG=" + defensePPG +
                '}';
    }

    @Override
    /**Task: Check to see if two team are exactly alike
     * @param obj the team to be compared
     * @return true or false based on weather the team are the same*/
    public boolean equals(Object obj) {

        boolean flag;

        if (obj == null || getClass() != obj.getClass())
                flag = false;

        Team team = (Team) obj;

        flag = ranking == team.ranking &&
                Double.compare(team.offensePPG, offensePPG) == 0 &&
                Double.compare(team.defensePPG, defensePPG) == 0 &&
                name.equals(team.name) &&
                nickname.equals(team.nickname) &&
                info.equals(team.info) &&
                this.hashCode()== obj.hashCode();

        return flag;
    }

    @Override
    /**Task: Give a hashcode value of a specific item
     * @return a unique hashcode for this team*/
    public int hashCode() {
        return Objects.hash(name, nickname, info, ranking, offensePPG, defensePPG);
    }
    //Josh End
}// End Team Class
