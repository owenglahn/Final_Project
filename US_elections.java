import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class US_elections {

	public static int solution(int num_states, int[] delegates, int[] votes_Biden, int[] votes_Trump,
			int[] votes_Undecided) {
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
				if (votesNeededBS[i] < 0)
					votesNeededBS[i] = 0; // Biden is guaranteed to win state
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
	public static int clean(int num_states, int bidenDels, int votesNeeded, int[] delegates, int[] votesNeededBS) {
		int totalDels = 0;
		for (int i = 0; i < num_states; i++) {
			totalDels += delegates[i];
		}
		int threshHold = totalDels / 2 + 1; // dels needed to win
		if (bidenDels == threshHold)
			return votesNeeded; // save time
		int capacity = bidenDels - threshHold; // max weight for the knapsack problem
		
		/* 
		 * Usage of the knapsack problem where the number of delegates is the weight 
		 * and the number of votes needed to win is the value. This calculates the
		 * maximum number of votes that can be removed from Biden's tally while still
		 * allowing him to win. Returns the total possible votes needed minus the 
		 * maximum that can be removed.
		 */
		int[][] M = new int[num_states + 1][capacity + 1];
		for (int w = 0 ; w <= capacity; w++)
		{
			M[0][w] = 0;
		}
		for (int i = 1; i <= num_states; i++)
		{
			M[i][0] = 0;
			for (int w = 1; w <= capacity; w++)
			{
				if (delegates[i-1] > w) M[i][w] = M[i-1][w];
				else M[i][w] = Math.max(M[i-1][w], votesNeededBS[i-1] + M[i - 1][w - delegates[i-1]]);
			}
		}
		return votesNeeded - M[num_states][bidenDels - threshHold];
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