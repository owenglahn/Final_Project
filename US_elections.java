import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/*
 * Conditions to check:
 * - Either of two states could go to trump and biden would still win, but not both
 * - no votes have been cast yet (currently passes test)
 * 
 * Find more consistent algorithm, don't want to have to check every edge case with a conditional
 */

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
		for ( int i = 0 ; i < num_states ; i++ ) // eliminates states not need to win
		{
			if ( bidenDels - trumpDels > 2 * delegates[i] && votesNeededBS[i] > 0 )
			{
				bidenDels -= delegates[i];
				trumpDels += delegates[i];
				votesNeeded -= votesNeededBS[i];
			}
		}
		System.out.println(votesNeeded);
		clean(num_states, bidenDels, votesNeeded, delegates, votesNeededBS);
		return votesNeeded;
	}

	/*
	 * Helper to get votes needed by state
	 * 
	 * Maybe more efficient to combine delegates and votes needed per state using
	 * hashmap
	 * 
	 * Fix condition where Biden is guaranteed to win
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
	public static void clean(int num_states, int bidenDels, int votesNeeded, int[] delegates, int[] votesNeededBS)
	{
		int totalDels = 0;
		for ( int i = 0 ; i < num_states ; i++ )
		{
			totalDels += delegates[i];
		}
		int threshHold = totalDels/2; // dels needed to win
		System.out.println(threshHold);
		System.out.println(bidenDels);
		if (bidenDels == threshHold + 1) return ; // save time
		for ( int i = 0 ; i < num_states ; i++ )
		{
			if (bidenDels - delegates[i] > threshHold && votesNeededBS[i] > 0)
			{
				bidenDels -= delegates[i];
				votesNeeded -= votesNeededBS[i];
			}
		}
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