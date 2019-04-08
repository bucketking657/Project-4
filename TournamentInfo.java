import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**Task: This is the tournament class that deals with reading in the team
 *       info and serializing it to make each user tournament infor unique*/
public class TournamentInfo{//renamed from teamInfo by matt 5/4

    /**The data structure that will allow us to accesss a teams data by using that teams names as a key*/
    HashMap<String, Team> teams;

    /**Task: This is the tournament info constructor. It throws an IO exception because
     *       all the info for the tournament is read from a file
     *@throws IOException makes sure team files are not null*/
    public TournamentInfo() throws IOException{

        teams = new HashMap<>();

        /**Method that intiates the reading teams from file*/
        loadFromFile();
    }

    /**Task: This private method will load all the team information from the teamInfo.txt
     *       file via a BufferedReader and load each team into the teams HashMap using
     *       their name as the key and the actual Team object as the data.
     *
     * @throws IOException Throws this exception to make sure file being read is not null*/
    private void loadFromFile() throws IOException{

        String name;
        String nickname;
        String info;
        int ranking;
        double offensivePPG;
        double defensivePPG;


        BufferedReader br = null;//Josh

        try{

            /**File for team info*/
            br = new BufferedReader(new FileReader("C:\\Users\\Christopher\\please work\\src\\teamInfo.txt"));

            while((name = br.readLine()) != null){
                nickname = br.readLine();
                info = br.readLine();
                ranking = Integer.parseInt(br.readLine());
                offensivePPG = Double.parseDouble(br.readLine());
                defensivePPG = Double.parseDouble(br.readLine());

                Team newTeam = new Team(name, nickname, info, ranking, offensivePPG, defensivePPG); //creates team with info

                br.readLine();   //gets rid of empty line between team infos

                teams.put(newTeam.getName(), newTeam);   //map team name with respective team object
            }
        }

        catch(IOException ioe) {
            throw ioe;
        }//Josh Start

        finally {
            br.close();
        }//Josh End
    }

    /**Task: This method will take a parameter of a team name and return the
     *       Team object corresponding to it.If it is unsuccessful, meaning
     *       the team does not exist, it will throw an exception.
     *
     * @param teamName the team whose name I want
     * @return A team name*/
    public Team getTeam(String teamName){
        return teams.get(teamName);
    }

    /**Task: This will be the method that actually does the work of determining the outcome
     *       of the games. It will use the seed/ranking from each team on the bracket and put
     *       it into an algorithm to somewhat randomly generate a winner
     *
     * @param startingBracket -- the bracket to be simulated upon. The master bracket*/
    public void simulate(Bracket startingBracket){

        for (int i = 62; i >= 0; i--) {

            int index1 = 2*i+1;
            int index2 = 2*i+2;

            Team team1 = teams.get(startingBracket.getBracket().get(index1));
            Team team2 = teams.get(startingBracket.getBracket().get(index2));

            int score1 = 0;
            int score2 = 0;
            while(score1==score2) {
                score1 = (int) (((Math.random() * 136) + 75) * (1 - (team1.getRanking() * 0.02)));
                score2 = (int) (((Math.random() * 136) + 75) * (1 - (team2.getRanking() * 0.02)));
            }

            startingBracket.setTeamScore(index1, score1);
            startingBracket.setTeamScore(index2, score2);

            if(score1>score2)
                startingBracket.moveTeamUp(index1);
            else
                startingBracket.moveTeamUp(index2);
        }
    }

    /**Task: Reads Strings from initialMatches.txt into an ArrayList in order to construct the starting bracket
     * @return ArrayList of Strings*/
    public static ArrayList<String> loadStartingBracket() throws IOException{

        String name;
        ArrayList<String> starting = new ArrayList<String>();
        BufferedReader br= null;//Josh;//Josh

        try{

            /**This is where intial matches are read in*/
            br =new BufferedReader( new FileReader("C:\\Users\\Christopher\\please work\\src\\initialMatches.txt"));

            while((name = br.readLine()) != null){
                starting.add(name);
            }
        }

        catch(IOException ioe){
            ioe.printStackTrace();
            System.out.print("blah");
            
            //Josh Start
        }finally {
            if(br!=null)

                br.close();

        }//Josh End

        return starting;
    }

}//End Class
