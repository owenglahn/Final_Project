import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class US_elections {

	public static int solution(int num_states, int[] delegates, int[] votes_Biden, int[] votes_Trump, int[] votes_Undecided) {
		int[] votesNeededBS = getVotesNeededBS(num_states, votes_Biden, votes_Trump, votes_Undecided);
		int trumpDels = 0; // delegates guaranteed to be won by trump
		int bidenDels = 0; // delegates winnable by biden
		int votesNeeded = 0;
		for (int i = 0; i < num_states; i++) {
			if (votesNeededBS[i] == -1)
				trumpDels += delegates[i];
			else {
				bidenDels += delegates[i];
				votesNeeded += votesNeededBS[i];
			}
		}
		if (bidenDels < trumpDels)
			return -1;
		votesNeeded = clean(num_states, bidenDels, votesNeeded, delegates, votesNeededBS);
		return votesNeeded;
	}

	/*
	 * Helper to get votes needed by state
	 */
	private static int[] getVotesNeededBS(int num_states, int[] votes_Biden, int[] votes_Trump, int[] votes_Undecided) {
		int[] votesNeededBS = new int[num_states]; // votes needed for state i, -1 if it cannot be won

		for (int i = 0; i < num_states; i++) {
			votesNeededBS[i] = -1;
			if (votes_Undecided[i] < votes_Trump[i] - votes_Biden[i])
				continue; // not possible for biden to win that state

			int votesBehind = votes_Trump[i] - votes_Biden[i];
			if (votesBehind > 0) {
				votesNeededBS[i] = (int) (votesBehind + Math.ceil((votes_Undecided[i] - votesBehind) / 2.0));
			} else {
				votesNeededBS[i] = (int) Math.ceil((votes_Undecided[i] + votesBehind) / 2.0);
				if ( votesNeededBS[i] < 0 ) votesNeededBS[i] = 0; // Biden is guaranteed to win state
			}
			if (votesNeededBS[i] + votes_Biden[i] == votes_Trump[i] + votes_Undecided[i] - votesNeededBS[i]) {
				if (votesNeededBS[i] == votes_Undecided[i])
					votesNeededBS[i] = -1; // tie, Trump wins state
				else
					votesNeededBS[i]++; // tie, more votes available, increment votes needed
			}
		}
		return votesNeededBS;
	}
	
	/*
	 * Helper method to remove states unnecessary to winning from calculation.
	 */
	public static int clean(int num_states, int bidenDels, int votesNeeded, int[] delegates, int[] votesNeededBS)
	{
		int totalDels = 0;
		List<State> winnable = new ArrayList<>();
		for ( int i = 0 ; i < num_states ; i++ )
		{
			if (votesNeededBS[i] > 0) winnable.add(new State(delegates[i], votesNeededBS[i]));
			totalDels += delegates[i];
		}
//		Collections.sort(winnable);
		int threshHold = totalDels/2; // dels needed to win
		if (bidenDels == threshHold + 1) return votesNeeded; // save time
//		for (State state : winnable) 
//		{
//			if (bidenDels - state.delegates > threshHold)
//			{
//				votesNeeded -= state.votesNeeded;
//				bidenDels -= state.delegates;
//			}
//		}
		int[] M = new int[num_states];
		if (votesNeededBS[0] != -1 && votesNeeded - votesNeededBS[0] >= threshHold) M[0] = votesNeeded - votesNeededBS[0];
		else M[0] = votesNeeded;
		for (int i = 1; i < num_states; i++)
		{
		}
		
		return votesNeeded;
	}

	public static void main(String[] args) {
		try {
			String path = args[0];
			File myFile = new File(path);
			Scanner sc = new Scanner(myFile);
			int num_states = sc.nextInt();
			int[] delegates = new int[num_states];
			int[] votes_Biden = new int[num_states];
			int[] votes_Trump = new int[num_states];
			int[] votes_Undecided = new int[num_states];
			for (int state = 0; state < num_states; state++) {
				delegates[state] = sc.nextInt();
				votes_Biden[state] = sc.nextInt();
				votes_Trump[state] = sc.nextInt();
				votes_Undecided[state] = sc.nextInt();
			}
			sc.close();
			int answer = solution(num_states, delegates, votes_Biden, votes_Trump, votes_Undecided);
			System.out.println(answer);
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}

/*
 * Helper class representing a state.
 * 
 * Used in clean() method to be able to sort states in order of the votes needed to win them.
 */
class State implements Comparable<State>
{
	int delegates; // delegates awarded by that state
	int votesNeeded; // votes needed by biden to win state

	State(int pDels, int pVotes)
	{
		delegates = pDels;
		votesNeeded = pVotes;
	}
	
	/*
	 * Sort in order of descending votes needed
	 */
	@Override
	public int compareTo(State o) {
		return o.votesNeeded - votesNeeded;
	}
	
	/*
	 * Used for testing
	 */
	public String toString()
	{
		return "(" + delegates + ":" + votesNeeded + ")";
	}
}