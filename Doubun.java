
import java.awt.*;
import java.awt.event.*;

class Doubun extends Component {

//  Constructors    \\  //  \\  //  \\  //  \\

Doubun(String text, int framesPerSecond) {
	if (text == null) throw new IllegalArgumentException(
		"Text must be non-null!"
	);
	if (framesPerSecond <= 0) throw new IllegalArgumentException(
		"Frames per second must be positive!"
	);
	
	this.text = text;
	updateThread = new Thread() {
		public void run() {
			while (true) try {
				Doubun.this.advance();
				Thread.sleep(1000 / framesPerSecond);
			}
			catch (InterruptedException eIt) { break; }
		};
	};
}



//  Main    \\  //  \\  //  \\  //  \\  //  \\

public static void main(String... args) {
	// Get input text
	InputDialog inputDialog = new InputDialog(
		null, 
		"Please enter the text you'd like to scroll:", 
		"Marquee text"
	);
	inputDialog.setVisible(true);  // Modal dialog, will block
	inputDialog.dispose();
	String text = inputDialog.getResponse();
	if (text == null) return;

	// Create instance
	Doubun instance = new Doubun(text, 24);
	instance.setPreferredSize(new Dimension(160, 40));
	instance.setFont(new Font("Dialog", Font.ITALIC, 24));
	instance.setForeground(Color.BLUE);
	
	// Create frame to hold instance
	Frame frame = new Frame("Doubun");
	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent eW) {
			// Dispose on close
			frame.dispose();
			instance.getUpdateThread().interrupt();
		}
	});
	
	// Okay, stitch everything together and start
	frame.add(instance, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);	
	instance.getUpdateThread().start();
}



//  Members \\  //  \\  //  \\  //  \\  //  \\

private String text;
private int xOffset;
private Thread updateThread;



//  Methods \\  //  \\  //  \\  //  \\  //  \\

public void paint(Graphics g) {
	FontMetrics fm = g.getFontMetrics();
	
	int startingX = getWidth() + xOffset;
	int endingX = startingX + fm.stringWidth(text);
	
	if (endingX <= 0) xOffset = 0;	
	int baselineForVCenter = (getHeight() / 2) + (fm.getAscent() / 2);
	
	g.clearRect(0, 0, getWidth(), getHeight());
	
	g.setColor(getForeground());
	g.drawString(text, startingX, baselineForVCenter);
}

public void advance() {
	xOffset -= 1;
	repaint();
	Toolkit.getDefaultToolkit().sync();
}

public Thread getUpdateThread() {
	return updateThread;
}



//  Classes \\  //  \\  //  \\  //  \\  //  \\

static class InputDialog extends Dialog {
	// I guess AWT being in the applet era meant
	// they expected input from the browser?
	private String response = null;
	
	String getResponse() { return response; }
	
	InputDialog(Frame owner, String message, String title) {
		super(owner, title, true);
		
		Panel inputPart = new Panel();
		inputPart.setLayout(new BorderLayout());		
		TextField inputField = new TextField();
		inputPart.add(new Label(message), BorderLayout.NORTH);
		inputPart.add(inputField, BorderLayout.CENTER);
		
		Panel buttonPart = new Panel();
		buttonPart.setLayout(new FlowLayout(FlowLayout.CENTER));
		Button okButton = new Button("OK");
		Button cancelButton = new Button("Cancel");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eA) {
				response = inputField.getText();
				if (response.trim().isEmpty()) response = null;
				setVisible(false);
			};
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eA) {
				setVisible(false);	
			}
		});
		buttonPart.add(okButton);
		buttonPart.add(cancelButton);
		
		add(inputPart, BorderLayout.CENTER);
		add(buttonPart, BorderLayout.SOUTH);
		pack();
	}
}

}
