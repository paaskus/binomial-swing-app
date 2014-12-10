import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Container;

public class ThreadedBinomialSwingApp {
	private Thread t = null;

	private final JFrame frame;

	// Input
	private final JTextField numbers;
	private final JTextField coefficient;
	private final JLabel numbersLabel;
	private final JLabel coefficientLabel;
	private final JPanel mainInputPanel;
	private final JPanel inputPanelLeft;
	private final JPanel inputPanelRight;

	//Result
	private final JLabel resultLabel;
	private final JLabel showResult;
	private final JPanel resultPanel;

	// Buttons
	private final JButton binomial1;
	private final JButton binomial2;
	private final JPanel buttonPanel;

	public ThreadedBinomialSwingApp() {
		frame = new JFrame();
		numbers = new JTextField(5);
		coefficient = new JTextField(5);
		numbersLabel = new JLabel("n:");
		coefficientLabel = new JLabel("k:");
		resultLabel = new JLabel("    " + "Result:");
		showResult = new JLabel("Nothing to show yet." + "    ");
		binomial1 = new JButton("Binomial 1");
		binomial2 = new JButton("Binomial 2");
		mainInputPanel = new JPanel();
		inputPanelLeft = new JPanel();
		inputPanelRight = new JPanel();
		resultPanel = new JPanel();
		buttonPanel = new JPanel();
	}

	public static void main(String[] args) {
		ThreadedBinomialSwingApp tbsa = new ThreadedBinomialSwingApp();
		tbsa.makeFrame();
	}

	public void makeFrame() {
		binomial1.addActionListener(createButtonListener(1));
		binomial2.addActionListener(createButtonListener(2));
		inputPanelLeft.add(numbersLabel, "West");
		inputPanelLeft.add(numbers, "East");
		inputPanelRight.add(coefficientLabel, "West");
		inputPanelRight.add(coefficient, "East");
		mainInputPanel.add(inputPanelLeft, "West");
		mainInputPanel.add(inputPanelRight, "East");
		resultPanel.add(resultLabel, "West");
		resultPanel.add(showResult, "East");
		buttonPanel.add(binomial1);
		buttonPanel.add(binomial2);
		frame.add(mainInputPanel, "North");
		frame.add(resultPanel, "Center");
		frame.add(buttonPanel, "South");
		numbersLabel.setLabelFor(numbers);
		coefficientLabel.setLabelFor(coefficient);
		frame.setTitle("BinomialApp");
		frame.setDefaultCloseOperation(3);

		Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(localDimension.width / 2 - this.frame.getSize().width / 2, localDimension.height / 2 - this.frame.getSize().height / 2);
		frame.setVisible(true);
		customFramePack();
	}

	private ActionListener createButtonListener(final int binomialID) {
		return new ActionListener() {
			public long binomial1(int n, int k) throws InterruptedException {
				if (Thread.currentThread().isInterrupted())
					throw new InterruptedException();

				long result = 0;

				if ((n == k) || (k == 0)) {
					result = 1;
				} else if ((n > k) && (k > 0)) {
					result = binomial1(n, k - 1);
					result *= (n - k + 1);
					result /= k;
				}

				return result;
			}

			public long binomial2(int n, int k) throws InterruptedException {
				if (Thread.currentThread().isInterrupted())
					throw new InterruptedException();

				long result = 0;

				if ((n == k) || (k == 0)) {
					result = 1;
				} else if ((n > k) && (k > 0)) {
					result = binomial2(n - 1, k);
					result += binomial2(n - 1, k - 1);
				}

				return result;
			}

			public void actionPerformed(ActionEvent event) {
				if (t != null)
					t.interrupt();
				Runnable r = new Runnable() {
					public void run() {
						try {
							int n = Integer.parseInt(numbers.getText());
							int k = Integer.parseInt(coefficient.getText());
							long result;

							if (binomialID == 1) {
								resultLabel.setText("    " + "Result with input C(" + n + ", " + k + ") is");
								showResult.setText("calculating..." + "    ");
								result = binomial1(n, k);
							} else if (binomialID == 2) {
								resultLabel.setText("    " + "Result with input C(" + n + ", " + k + ") is");
								showResult.setText("calculating..." + "    ");
								result = binomial2(n, k);
							} else {
								return;
							}

							if (!Thread.currentThread().isInterrupted()) {
								if ((n < 0) || (k < 0)) {
									resultLabel.setText("    " + "ERROR:");
									showResult.setText("both n and k must be nonnegative integers" + "    ");
								} else if ((n <= 60) && (k <= n)) {
									resultLabel.setText("    " + "Result with input C(" + n + ", " + k + ") is");
									showResult.setText(result + "    ");
								} else if ((n > 60) && (k <= n)) {
									resultLabel.setText("    " + "ERROR:");
									showResult.setText("n must be less than 60" + "    ");
								} else if ((n <= 60) && (k > n)) {
									resultLabel.setText("    " + "ERROR:");
									showResult.setText("k must be less than n" + "    ");
								} else {
									resultLabel.setText("    " + "ERROR:");
									showResult.setText("n must be less than 60. k must be less than n" + "    ");
								}
							}

							customFramePack();

						} catch (InterruptedException ie) {
							customFramePack();
						} catch (NumberFormatException localNumberFormatException) {
							if ((numbers.getText().length() >= 1) && (coefficient.getText().length() >= 1)) {
								resultLabel.setText("    " + "ERROR:");
								showResult.setText("Invalid input." + "    ");
							} else {
								resultLabel.setText("    " + "ERROR:");
								showResult.setText("Please fill out both input fields." + "    ");
							}
							customFramePack();

							return;
						}
					}
				};
				t = new Thread(r);
				t.start();
			}
		};
	}

	private void customFramePack() {
		frame.setMinimumSize(new Dimension(0, 0));
		frame.pack();
		frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
	}
}