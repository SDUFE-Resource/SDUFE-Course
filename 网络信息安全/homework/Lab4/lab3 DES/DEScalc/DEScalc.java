import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.*;

/** DEScalc - main program for idea calculator
 *
 *  @author Lawrie Brown <Lawrie.Brown@adfa.edu.au> - Oct 2001, mod Jan 2005
 */
public class DEScalc extends Applet 
			implements ActionListener, ItemListener {

    /** DES Cipher object used for calculations. */
    private DES myDES;

    /** GUI Components for the applet. */
    private Label	lTitle;
    private Label	lData;
    private Label	lKey;
    private Label	lResult;
    private Label	lTrace;
    private TextField	tData;
    private TextField	tKey;
    private TextField	tResult;
    private Button	bEncrypt;
    private Button	bDecrypt;
    private Button	bAbout;
    private Button	bQuit;

    /** checkboxes to specify desired level of trace information. */
    private CheckboxGroup	cbTrace;
    private Checkbox		cTrace0, cTrace1, cTrace2, cTrace3, cTrace4;

    /** area to display  diagnostic trace information. */
    private TextArea	taTrace;

    /** level of trace information desired. */
    private int traceLev = 2;

    /** Flag saying whether we're running as application or applet. */
    boolean		application = false;

    /** Usage and copyright message - displayed when applet first starts. */
    private static final String about = "DES Calculator Applet v2.0 Feb 2005.\n\n" +
    	"The DES Calculator applet is used to encrypt or decrypt test data values\n" +
	"using DES block cipher.  It takes a 64-bit (16 hex digit) data value and a\n" +
	"64-bit (16 hex digit) key.  It can optionally provide a trace\n" +
	"of the calculations performed, with varying degrees of detail.\n\n" +
	"DEScalc was written and is Copyright 2005 by Lawrie Brown.\n" +
	"Permission is granted to copy, distribute, and use this applet\n" +
	"provided the author is acknowledged and this copyright notice remains intact.\n\n" +
	"See http://www.unsw.adfa.edu.au/~lpb/ for authors website.\n";

    /** Init method creates the GUI. */
    public void init() {
	// build the GUI
	setLayout(new BorderLayout());

	// nice plain white background
	setBackground(Color.white);

	// title
	Font fTitle = new Font("Times", Font.BOLD, 24);
	lTitle = new Label("DES Block Cipher Calculator");
	lTitle.setAlignment(Label.CENTER);
	lTitle.setFont(fTitle);
	lTitle.setForeground(Color.blue);
	add("North", lTitle);
	
	// various inputs and controls
	Panel pControls = new Panel();
	GridBagLayout gbl = new GridBagLayout();
	pControls.setLayout(gbl);
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.anchor = GridBagConstraints.WEST;
	Font fLabels = new Font("Courier", Font.BOLD, 14);

	lData  = new Label("Input Data (in hex)");
	lData.setAlignment(Label.LEFT);
	lData.setFont(fLabels);
	lData.setForeground(Color.blue);
	gbc.gridwidth = GridBagConstraints.RELATIVE;
	gbl.setConstraints(lData, gbc);
	pControls.add(lData);

	tData  = new TextField(2*DES.BLOCK_SIZE);
	tData.setText("");
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gbl.setConstraints(tData, gbc);
	pControls.add(tData);

	lKey   = new Label("DES Key (in hex)");
	lKey.setAlignment(Label.LEFT);
	lKey.setFont(fLabels);
	lKey.setForeground(Color.blue);
	gbc.gridwidth = 1;
	gbl.setConstraints(lKey, gbc);
	pControls.add(lKey);

	tKey    = new TextField(2*DES.KEY_LENGTH);
	tKey.setText("");
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gbl.setConstraints(tKey, gbc);
	pControls.add(tKey);

	lResult = new Label("Result will be displayed here.");
	lResult.setAlignment(Label.LEFT);
	lResult.setFont(fLabels);
	lResult.setForeground(Color.blue);
	gbc.gridwidth = 1;
	gbl.setConstraints(lResult, gbc);
	pControls.add(lResult);

	tResult = new TextField(2*DES.BLOCK_SIZE);
	tResult.setEditable(false);
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gbl.setConstraints(tResult, gbc);
	pControls.add(tResult);

	// buttons to request various actions
	Panel pButtons = new Panel();
	bEncrypt = new Button("Encrypt");
	bEncrypt.setForeground(Color.blue);
	pButtons.add(bEncrypt);
	bEncrypt.addActionListener(this);
	bDecrypt = new Button("Decrypt");
	bDecrypt.setForeground(Color.blue);
	pButtons.add(bDecrypt);
	bDecrypt.addActionListener(this);
	bAbout = new Button("About");
	bAbout.setForeground(Color.blue);
	pButtons.add(bAbout);
	bAbout.addActionListener(this);
	bQuit = new Button("Quit");
	bQuit.setForeground(Color.blue);
	if (application) {	// only place Quit button if an application
	    pButtons.add(bQuit);
	    bQuit.addActionListener(this);
	}
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gbl.setConstraints(pButtons, gbc);
	pControls.add(pButtons);

	// and place panels into main GUI
	add("Center", pControls);

	// lastly build the trace info text area
	Panel pTrace = new Panel();
	pTrace.setLayout(new BorderLayout());
	lTrace  = new Label("Trace of DES Calculations or Errors");
	lTrace.setAlignment(Label.CENTER);
	lTrace.setFont(fLabels);
	lTrace.setForeground(Color.blue);
	pTrace.add("North", lTrace);

	cbTrace = new CheckboxGroup();
	Panel pCB = new Panel();
	Label lCB = new Label("Trace Level: ");
	lCB.setFont(fLabels);
	lCB.setForeground(Color.blue);
	pCB.add(lCB);
	cTrace0 = new Checkbox("0: none", cbTrace, (traceLev == 0 ? true : false));
	pCB.add(cTrace0);
	cTrace0.addItemListener(this);
	cTrace1 = new Checkbox("1: calls", cbTrace, (traceLev == 1 ? true : false));
	if (traceLev >= 1)	pCB.add(cTrace1);
	cTrace1.addItemListener(this);
	cTrace2 = new Checkbox("2: +rounds", cbTrace, (traceLev == 2 ? true : false));
	if (traceLev >= 2)	pCB.add(cTrace2);
	cTrace2.addItemListener(this);
	cTrace3 = new Checkbox("3: +steps", cbTrace, (traceLev == 3 ? true : false));
	if (traceLev >= 3)	 pCB.add(cTrace3);
	cTrace3.addItemListener(this);
	cTrace4 = new Checkbox("4: +subkeys", cbTrace, (traceLev >= 4 ? true : false));
	if (traceLev >= 4)	 pCB.add(cTrace4);
	cTrace4.addItemListener(this);
	pTrace.add("Center", pCB);

	Font fTrace = new Font("Courier", Font.PLAIN, 12);
	taTrace = new TextArea(about, 20, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
	taTrace.setEditable(false);
	taTrace.setFont(fTrace);
	taTrace.setBackground(Color.white);
	pTrace.add("South", taTrace);

	add("South", pTrace);
    
        // create the DES cipher object to use
        myDES = new DES();
	// and set level of trace info 
	myDES.traceLevel = traceLev;
    }
    
    /**
     *  respond to Action Event: pressing one of the buttons.
     */
    public void actionPerformed (ActionEvent e) {
	byte[]  data;				/* 64-bit DES data */
	byte[]  key;				/* 128-bit DES key */
	byte[]  result;				/* 64-bit DES result */
	String	hexdata;			/* hex string to use as data */
	String	hexkey;				/* hex string to use as key */
	String	info = "";			/* trace info to display */
	Object	source = e.getSource();		/* identify action source */

	if (source == bEncrypt) {		// want to encrypt the data
	    // now extract key and data
	    hexkey  = tKey.getText();
	    if ((hexkey.length() != (2*DES.KEY_LENGTH)) ||
	        (!Util.isHex(hexkey))) {
		lResult.setForeground(Color.red);
		lResult.setText("Error in Key!!!");
		taTrace.setForeground(Color.red);
		taTrace.setText("DES key [" + hexkey +
		    "] must be strictly " + (2*DES.KEY_LENGTH) +
                    " hex digits long.");
		tResult.setText("");
		return;
	    }
	    key = Util.hex2byte(hexkey);

	    hexdata = tData.getText();
	    if ((hexdata.length() != (2*DES.BLOCK_SIZE)) ||
	        (!Util.isHex(hexdata))) {
		lResult.setForeground(Color.red);
		lResult.setText("Error in Data!!!");
		taTrace.setForeground(Color.red);
		taTrace.setText("DES data [" + hexdata +
		    "] must be strictly " + (2*DES.BLOCK_SIZE) +
                    " hex digits long.");
		tResult.setText("");
		return;
	    }
	    data = Util.hex2byte(hexdata);
    
	    myDES.setKey(key);
	    if (traceLev>0) info += myDES.traceInfo;
    
	    result = myDES.encrypt(data);
	    if (traceLev>0) info += myDES.traceInfo;

	    lResult.setForeground(Color.blue);
	    lResult.setText("Encrypted value is:");
	    tResult.setText(Util.toHEX1(result));
	    taTrace.setForeground(Color.black);
	    taTrace.setText(info);

	} else if (source == bDecrypt) {	// want to decrypt the data
	    // now extract key and data
	    hexkey  = tKey.getText();
	    if ((hexkey.length() != (2*DES.KEY_LENGTH)) ||
	        (!Util.isHex(hexkey))) {
		lResult.setForeground(Color.red);
		lResult.setText("Error in Key!!!");
		taTrace.setForeground(Color.red);
		taTrace.setText("DES key [" + hexkey +
		    "] must be strictly " + (2*DES.KEY_LENGTH) +
                    " hex digits long.");
		tResult.setText("");
		return;
	    }
	    key = Util.hex2byte(hexkey);

	    hexdata = tData.getText();
	    if ((hexdata.length() != (2*DES.BLOCK_SIZE)) ||
	        (!Util.isHex(hexdata))) {
		lResult.setForeground(Color.red);
		lResult.setText("Error in Data!!!");
		taTrace.setForeground(Color.red);
		taTrace.setText("DES data [" + hexdata +
		    "] must be strictly " + (2*DES.BLOCK_SIZE) +
                    " hex digits long.");
		tResult.setText("");
		return;
	    }
	    data = Util.hex2byte(hexdata);
    
	    myDES.setKey(key);
	    if (traceLev>0) info += myDES.traceInfo;
    
	    result = myDES.decrypt(data);
	    if (traceLev>0) info += myDES.traceInfo;

	    lResult.setForeground(Color.blue);
	    lResult.setText("Decrypted value is:");
	    tResult.setText(Util.toHEX1(result));
	    taTrace.setForeground(Color.black);
	    taTrace.setText(info);

	} else if (source == bAbout) {
	    taTrace.setForeground(Color.black);	// display about message
	    taTrace.setText(about);
	} else if ((source == bQuit) && application) {
	    System.exit(0);	// can only quit applications
	}
    }
    
    
    /**
     *  respond to ItemEvent: pressing one of the checkboxes.
     */
    public void itemStateChanged (ItemEvent e) {
	Object	source = e.getSource();		/* identify action source */

	if (source == cTrace0) traceLev = 0;
	else if (source == cTrace1) traceLev = 1;
	else if (source == cTrace2) traceLev = 2;
	else if (source == cTrace3) traceLev = 3;
	else if (source == cTrace4) traceLev = 4;
	
  	myDES.traceLevel = traceLev;

	taTrace.setForeground(Color.black);
	taTrace.setText("DES trace level set to " + traceLev);
    }


    /** usage description for command-line invocation of the DES calculator. */
    private static final String usage = "Usage:\n" +
	"DEScalc [-tlevel]\n" +
	"	- run DES calculator as GUI applet (with specified trace-level)\n" +
	"DEScalc [-e|-d] [-tlevel] hexkey hexdata\n" +
	"	- DES en/decrypt data using key (both in hex)\n" +
	"	- with trace details at specified level (0-4)\n";

    //......................................................................
    /** ideacalc program main routine.
	@param args command line arguments
     */
    public static void main (String[] args) {
    
	byte[]  data;				/* 64-bit DES data */
	byte[]  key;				/* 128-bit DES key */
	byte[]  result;				/* 64-bit DES result */
	String	hexdata;			/* hex string to use as data */
	String	hexkey;				/* hex string to use as key */
	boolean	encrypting = true;		/* encrypt/decrypt flag */
	int	traceLev = 2;			/* level to trace at */
	int	curr = 0;			/* current argument indeex */
	int	argc = args.length;		/* total number of arguments */
    
	// parse command-line flags
	while ((argc > 0) && (args[curr].startsWith("-"))) {
	    if (args[curr].equals("-e"))
	        encrypting = true;
	    else if (args[curr].equals("-d"))
	        encrypting = false;
	    else if (args[curr].startsWith("-t"))
	        traceLev = Integer.parseInt(args[curr].substring(2));
	    else {
	        System.err.println("Unknown flag: "+args[curr]+"\n"+usage+"\n"+about);
	        System.exit(1);
	    }
	    curr++;
	    argc--;
	}

	// if no other args run as GUI
	if (argc == 0) {
	    // GUI interface - setup some System properties (esp for optimum MacOSX use)
	    System.setProperty("apple.laf.useScreenMenuBar", "true");      // use Mac-style menus
	    // create frame to display graphics in
	    Frame	fr = new Frame ("DES Block Cipher Calculator");
	    // create a new instance of this class to run
	    DEScalc	app = new DEScalc();
	    app.application = true; // running as application
	    app.traceLev = traceLev; // set desired trace level
	    // and set the applet running displayed in the frame
	    app.init();             // run init method
	    fr.add( "Center", app); // insert into frame
	    fr.setSize(800,800);    // specify size of frame
	    fr.pack();              // pack components in
	    fr.show();              // display on screen
	    app.start();            // then run applet code
	// check still have 2 more values for key & data
	} else if (argc < 2) {
		System.err.println(usage);
		System.exit(1);
	// run in command-line mode
	} else {

	    // now extract key and data
	    hexkey  = args[curr++];
	    if ((hexkey.length() != (2*DES.KEY_LENGTH)) ||
	        (!Util.isHex(hexkey))) {
		System.err.println("DES key [" + hexkey +
		    "] must be strictly " + (2*DES.KEY_LENGTH) +
                    " hex digits long.\n" + usage);
		System.exit(1);
	    }
	    key = Util.hex2byte(hexkey);

	    hexdata = args[curr++];
	    if ((hexdata.length() != (2*DES.BLOCK_SIZE)) ||
	        (!Util.isHex(hexdata))) {
		System.err.println("DES data [" + hexdata +
		    "] must be strictly " + (2*DES.BLOCK_SIZE) +
                    " hex digits long.\n" + usage);
		System.exit(1);
	    }
	    data = Util.hex2byte(hexdata);

	    // now do actual en/decryption and display trace data
	    // icreate DES cipher object
	    DES testDES = new DES();
    
	    // set level of trace info 
	    testDES.traceLevel = traceLev;
    
	    testDES.setKey(key);
	    if (traceLev>0) System.out.println(testDES.traceInfo);
    
	    if (encrypting) {
		result = testDES.encrypt(data);
		if (traceLev>0) System.out.println(testDES.traceInfo);
		System.out.println(Util.toHEX1(result));
	    } else {
		result = testDES.decrypt(data);
		if (traceLev>0) System.out.println(testDES.traceInfo);
		System.out.println(Util.toHEX1(result));
	    }
	}
    }
}
