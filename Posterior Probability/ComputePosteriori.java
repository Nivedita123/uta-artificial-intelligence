import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ComputePosteriori {

	public static void main(String[] args) {

		// prior probability for the five possible hypotheses for our bag
		double ph1 = 0.1;
		double ph2 = 0.2;
		double ph3 = 0.4;
		double ph4 = 0.2;
		double ph5 = 0.1;

		// probability of cherry candy being picked in each of the five possible
		// hypotheses
		double ph1C = 1.0;
		double ph2C = 0.75;
		double ph3C = 0.50;
		double ph4C = 0.25;
		double ph5C = 0.0;

		// probability of lime candy being picked in each of the five possible
		// hypotheses
		double ph1L = 1 - ph1C;
		double ph2L = 1 - ph2C;
		double ph3L = 1 - ph3C;
		double ph4L = 1 - ph4C;
		double ph5L = 1 - ph5C;

		// prior probability of cherry candy being picked (no observations yet)
		double pNextCherry = (ph1 * ph1C) + (ph2 * ph2C) + (ph3 * ph3C) + (ph4 * ph4C) + (ph5 * ph5C);

		// prior probability of lime candy being picked (no observations yet)
		double pNextLime = 1 - pNextCherry;

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter("result.txt"));

			if (args.length == 1) {

				String input = args[0];

				bw.write("Observation sequence Q: " + input);
				bw.write("\nLength of Q: " + input.length());
				bw.write("\n");

				for (int i = 0; i < input.length(); i++) {

					bw.write("\nAfter Observation " + (i + 1) + " = " + input.charAt(i) + ":");

					bw.write("\n");

					// given current observation is C (Cherry Candy)
					if (input.charAt(i) == 'C') {

						// Posterior probability of all hypotheses after i observations
						ph1 = (ph1C * ph1) / pNextCherry;
						ph2 = (ph2C * ph2) / pNextCherry;
						ph3 = (ph3C * ph3) / pNextCherry;
						ph4 = (ph4C * ph4) / pNextCherry;
						ph5 = (ph5C * ph5) / pNextCherry;

						// Posterior probability of next observation being cherry after i observations.
						pNextCherry = (ph1 * ph1C) + (ph2 * ph2C) + (ph3 * ph3C) + (ph4 * ph4C) + (ph5 * ph5C);

						// Posterior probability of next observation being lime after i observations.
						pNextLime = 1 - pNextCherry;

					}
					// given current observation is L (Lime Candy)
					else if (input.charAt(i) == 'L') {

						// Posterior probability of all hypotheses after i observations
						ph1 = (ph1L * ph1) / pNextLime;
						ph2 = (ph2L * ph2) / pNextLime;
						ph3 = (ph3L * ph3) / pNextLime;
						ph4 = (ph4L * ph4) / pNextLime;
						ph5 = (ph5L * ph5) / pNextLime;

						// Posterior probability of next observation being cherry after i observations.
						pNextCherry = (ph1 * ph1C) + (ph2 * ph2C) + (ph3 * ph3C) + (ph4 * ph4C) + (ph5 * ph5C);

						// Posterior probability of next observation being lime after i observations.
						pNextLime = 1 - pNextCherry;

					}

					bw.write("\nP(h1 | Q) = " + ph1);
					bw.write("\nP(h2 | Q) = " + ph2);
					bw.write("\nP(h3 | Q) = " + ph3);
					bw.write("\nP(h4 | Q) = " + ph4);
					bw.write("\nP(h5 | Q) = " + ph5);

					bw.write("\n");

					bw.write("\nProbability that the next candy we pick will be C, given Q: " + pNextCherry);
					bw.write("\nProbability that the next candy we pick will be L, given Q: " + pNextLime);

					bw.write("\n");

				}

			}
			// no command line arguments
			else {

				bw.write("No observations yet!");

				bw.write("\n");

				bw.write("\nP(h1) = " + ph1);
				bw.write("\nP(h2) = " + ph2);
				bw.write("\nP(h3) = " + ph3);
				bw.write("\nP(h4) = " + ph4);
				bw.write("\nP(h5) = " + ph5);

				bw.write("\n");

				bw.write("\nProbability that the first candy we pick will be C: " + pNextCherry);
				bw.write("\nProbability that the first candy we pick will be L: " + pNextLime);

			}
			
			bw.flush();
			bw.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}