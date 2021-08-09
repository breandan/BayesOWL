/**
 * Modified on Dec. 18, 2008
 * @author Shenyong Zhang
 */
package umbc.ebiquity.BayesOWL.GUI;

import norsys.netica.NeticaException;
import norsys.netica.Net;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.parser.*;
import umbc.ebiquity.BayesOWL.constructor.*;

import com.swtdesigner.SWTResourceManager;
import java.awt.Desktop;
import java.io.File;

import norsys.netica.Environ;

/**
 * BayesOWL's Graphical User Interface.
 *
 */
public class MainUI {
	private Display display;
	private Shell shell;
	private Tree taxonomyTree;
	private Label showResultText;
	private Label logoLabel;
	private Label umbcLabel;
	private Menu mainMenu;
	private TreeItem taxonomyItem;
	private String inputFilePath;
	private ExNode[] nodes;
	private String[][] parents;
	private Net net;
	private String NeticaLicense;
	
	/**
	 * Constructor.
	 */
	public MainUI(){
		Initialize();
	}
	
	/**
	 * Initialize method.
	 */
	private void Initialize(){
		display = new Display();
		shell = new Shell();
		taxonomyTree = new Tree(shell, SWT.BORDER);
		logoLabel = new Label(shell, SWT.NONE);
		umbcLabel = new Label(shell, SWT.NONE);
		showResultText = new Label(shell, SWT.READ_ONLY | SWT.BORDER);

		shell.setMinimumSize(new Point(640, 480));
		shell.setSize(640, 480);
		shell.setText("BayesOWL  version 1.0");
		shell.setImage(new Image(display, ".//img//bn.gif"));
		final Image ebiquityImage = new Image(display, ".//img//ebiquitylogo.gif");
		logoLabel.setImage(ebiquityImage);
		logoLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final Image umbcImage = new Image(display, ".//img//umbclogo.gif");
		umbcLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		umbcLabel.setImage(umbcImage);

		shell.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				taxonomyTree.setBounds(0, 0, 150, shell.getBounds().height - 50);
				showResultText.setBounds(150, 0, shell.getBounds().width-150, 
						(shell.getBounds().height - ebiquityImage.getBounds().height));
				logoLabel.setBounds(151, showResultText.getBounds().height - 50, 
						ebiquityImage.getBounds().width, ebiquityImage.getBounds().height);
				umbcLabel.setBounds(shell.getBounds().width - umbcImage.getBounds().width - 10, 
						showResultText.getBounds().height - 50, umbcImage.getBounds().width, 
						umbcImage.getBounds().height);
			}
		});

		mainMenu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mainMenu);
		taxonomyTree.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		taxonomyTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectNodeResponse(e);
			}
		});
		taxonomyTree.setToolTipText("Taxonomies in Ontology");
		showResultText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		showResultText.setFont(SWTResourceManager.getFont("Arial", 12, SWT.ITALIC));
		showResultText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		showResultText.setText(Message.INIT_SHOW_TEXT);
		taxonomyItem = new TreeItem(taxonomyTree, SWT.MULTI);
		taxonomyItem.setText("Taxonomy");
		taxonomyItem.setImage(new Image(display, ".//img/tree.jpg"));
		taxonomyItem.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NONE));
		taxonomyItem.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));

		/************************Start Menu*****************************/
		//'file' menu
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		//item 'file'
		MenuItem fileItem = new MenuItem(mainMenu, SWT.CASCADE);
		fileItem.setText("File");
		fileItem.setAccelerator('F');
		fileItem.setMenu(fileMenu);
		{
			//New
			MenuItem newFileItem = new MenuItem(fileMenu, SWT.CASCADE);
			newFileItem.setText("input ontology file");
			newFileItem.setAccelerator('N');
			newFileItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					newFileItemSelected();
				}
			});
			
			//URL
			MenuItem urlFileItem = new MenuItem(fileMenu, SWT.CASCADE);
			urlFileItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					urlFileItemSelected();
				}
			});
			urlFileItem.setText("input ontology URL");
			urlFileItem.setAccelerator('U');
			
			//probability
			MenuItem probFileItem = new MenuItem(fileMenu, SWT.CASCADE);
			probFileItem.setAccelerator('P');
			probFileItem.setText("input probability file");
			probFileItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					probFileItemSelected();
				}
			});
			
			//Exit
			MenuItem exitItem = new MenuItem(fileMenu, SWT.CASCADE);
			exitItem.setText("Exit");
			exitItem.setAccelerator('E');
			exitItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					close();
				}
			});
		}
		
		//'Bayesian' menu
		Menu BNMenu = new Menu(shell, SWT.DROP_DOWN);
		//'Bayesian' item
		MenuItem BNItem = new MenuItem(mainMenu, SWT.CASCADE);
		BNItem.setText("BNet");
		BNItem.setAccelerator('B');
		BNItem.setMenu(BNMenu);
		{
			//license
			MenuItem licenseItem = new MenuItem(BNMenu, SWT.CASCADE);
			licenseItem.setText("input Netica license");
			licenseItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					licenseItemSelected();
				}
			});
			
			//Open
			MenuItem openBNItem = new MenuItem(BNMenu, SWT.CASCADE);
			openBNItem.setText("open Bayesian Net");
			openBNItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					openBNItemSelected();
				}
			});
		}
		
		//'evidence' menu
		Menu evidenceMenu = new Menu(shell, SWT.DROP_DOWN);
		//'evidence' item
		MenuItem evidenceItem = new MenuItem(mainMenu, SWT.CASCADE);
		evidenceItem.setText("Evidence");
		evidenceItem.setAccelerator('E');
		evidenceItem.setMenu(evidenceMenu);
		{
			MenuItem inputEvidence = new MenuItem(evidenceMenu, SWT.CASCADE);
			inputEvidence.setText("input evidence");
			inputEvidence.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					inputEvidenceItemSelected();
				}
			});
		}
		
		//'help' menu
		Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		//'help' item
		MenuItem helpItem = new MenuItem(mainMenu, SWT.CASCADE);
		helpItem.setText("Help");
		helpItem.setAccelerator('H');
		helpItem.setMenu(helpMenu);
		{
			//context
			MenuItem contextItem = new MenuItem(helpMenu, SWT.CASCADE);
			contextItem.setText("context");
			contextItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					contextItemSelected();
				}
			});
			//separator
			MenuItem separator = new MenuItem(helpMenu, SWT.SEPARATOR);
			
			//about
			MenuItem aboutItem = new MenuItem(helpMenu, SWT.CASCADE);
			aboutItem.setText("about");
			aboutItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					aboutItemSelected();
				}
			});
		}
	}
	
	/**
	 * Start BayesOWL GUI.
	 */
	public void run(){
		try{
			Environ env = new Environ(NeticaLicense);
			shell.open();
			shell.layout();
			while(!shell.isDisposed()){
				if(!display.readAndDispatch())
					display.sleep();
			}
			shell.dispose();
			env.finalize();
		}catch(NeticaException ne){
			new MessageDialog(null, "BayesOWL", null, ne.getMessage(), 
					MessageDialog.ERROR, new String[]{"OK"}, MessageDialog.DIALOG_PERSISTLOCATION).open();
		}
		
	}
	
	/**
	 * About item selected action.
	 */
	private void aboutItemSelected(){
		showResultText.setFont(SWTResourceManager.getFont("", 24, SWT.NONE));
		showResultText.setText("BayesOWL\nVersion:1.0");
		//JOptionPane.showMessageDialog(null, "BayesOWL\nVersion:1.0");
		String s = "BayesOWL\nVersion: 1.0";
		MessageDialog md = new MessageDialog(null, "BayesOWL", null, s, 
				MessageDialog.INFORMATION, new String[]{"OK"}, MessageDialog.NONE);
		md.open();
	}
	
	private void newFileItemSelected(){
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterPath("./");
		fileDialog.setFilterExtensions(new String[]{"*.owl; *.rdf"});
		fileDialog.open();
		if(fileDialog.getFileName().length() > 0){
			String fileName = fileDialog.getFileName();
			inputFilePath = fileDialog.getFilterPath() + "\\" + fileName;
			if(new File(inputFilePath).exists()){
				taxonomyItem.setText(fileName);
				startParseFile(inputFilePath);
				validParseResult(nodes, parents);
				showResultText.setText("Ontology: " + taxonomyItem.getText());				
			}else{
				new MessageDialog(null, "BayesOWL", null, "No such file. Plase try again!", 
						MessageDialog.ERROR, new String[]{"OK"}, MessageDialog.DIALOG_PERSISTLOCATION).open();
			}
		}
	}
	
	/**
	 * Start parsing ontology file.
	 * @param filePathOrURL
	 */
	private void startParseFile(String filePathOrURL){
		TaxoParser taxoParser = new TaxoParser(filePathOrURL);
		//taxoParser.startParsing(filePathOrURL);
		BNConstructor constructBN = new BNConstructor();
		constructBN.constructBN(taxoParser.getNames(), taxoParser.getTags(), taxoParser.getParents());
		net = constructBN.getNet();
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setFilterPath("./");
		fd.setFilterExtensions(new String[]{".dne"});
		fd.open();
		if(fd.getFileName().endsWith(".dne")){
			constructBN.saveBNNet(fd.getFileName());
		}else{
			fd.setFileName(fd.getFileName() + ".dne");
		}		
		nodes = constructBN.getNodes();
		parents = taxoParser.getParents();
	}
	
	/**
	 * Input ontology URL.
	 */
	private void urlFileItemSelected(){
		InputDialog id = new InputDialog(null, "input Ontology URL", 
				"Please input Ontoloty URL\nBayesOWL version 1.0", "input URL here", null);
		id.open();		
		String url = id.getValue();
		try{
			startParseFile(url);
			validParseResult(nodes, parents);
			showResultText.setText("Ontology: " + taxonomyItem.getText());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Input probability file.
	 */
	private void probFileItemSelected() {
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterPath("./");
		fileDialog.setFilterExtensions(new String[]{"*.owl; *.rdf"});
		fileDialog.open();
		if(fileDialog.getFileName().length() > 0){
			String fileName = fileDialog.getFileName();
			inputFilePath = fileDialog.getFilterPath() + "\\" + fileName;
			if(new File(inputFilePath).exists()){
				//TODO: add code here
				//ProbParser probParser = new ProParser();
				//probParser.startParsing(filePath);				
			}else{
				new MessageDialog(null, "BayeOWL", null, "No such file. Please try again!", 
						MessageDialog.ERROR, new String[]{"OK"}, 
						MessageDialog.DIALOG_DEFAULT_BOUNDS).open();
			}
		}
	}
	
	/**
	 * Input evidence file.
	 * NOT valid for version 1.0
	 */
	private void inputEvidenceItemSelected(){
		/**
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterPath("./");
		fileDialog.setFilterExtensions(new String[]{"*.txt"});
		fileDialog.open();
		if(fileDialog.getFileName().length() > 0){
			String fileName = fileDialog.getFileName();
			inputFilePath = fileDialog.getFilterPath() + "\\" + fileName;
			if(new File(inputFilePath).exists()){
				try{
					Soft2Virtual2Ex soft2virtual = new Soft2Virtual2Ex();
					soft2virtual.run(new File(inputFilePath));
					showResultText.setText("Virtual Evidence Inference Completed.\n\n" + 
							"Original file: \n   " + inputFilePath.substring(0, inputFilePath.length()-4) + ".dne" + "\n" + 
							"Result file: \n   " + inputFilePath.substring(0, inputFilePath.length()-4) + "result.dne");
				}catch(Exception ne){
					ne.printStackTrace();
				}
			}else{
				new MessageDialog(null, "BayesOWL", null, "No such file. Plase try again!", 
						MessageDialog.ERROR, new String[]{"OK"}, MessageDialog.DIALOG_DEFAULT_BOUNDS).open();
			}
		}
		*/
	}
	
	/**
	 * To open a Bayesian Net.
	 */
	private void openBNItemSelected(){
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterPath("./");
		fileDialog.setFilterExtensions(new String[]{"*.dne"});
		fileDialog.open();
		if(fileDialog.getFileName().length() > 0){
			String fileName = fileDialog.getFileName();
			inputFilePath = fileDialog.getFilterPath() + "\\" + fileName;
			if(new File(inputFilePath).exists()){
				try{
					Desktop desktop = Desktop.getDesktop();
					desktop.open(new File(inputFilePath));
				}catch(Exception r){
					r.printStackTrace();
				}
			}else{
				new MessageDialog(null, "BayesOWL", null, "No such file. Plase try again!", 
						MessageDialog.ERROR, new String[]{"OK"}, 
						MessageDialog.DIALOG_DEFAULT_BOUNDS).open();
			}
		}
	}
	
	/**
	 * BayesOWL context.
	 */
	private void contextItemSelected(){
		try{
			File file = new File("help.chm");
			Desktop desktop = Desktop.getDesktop();
			desktop.open(file);
		}catch(Exception r){
			r.printStackTrace();
		}
	}
	
	/**
	 * Input Netica License.
	 */
	private void licenseItemSelected(){
		InputDialog id = new InputDialog(null, "BayesOWL", 
				"Please input your Netica license.", "input license here", null);
		id.open();
		if(id.getValue().equalsIgnoreCase("input license here")){
			//System.out.println(NeticaLicense + "**");
		}else{
			NeticaLicense = id.getValue();
			//System.out.println(NeticaLicense + "**");
		}
	}
	
	/**
	 * Show extracted taxonomies in a tree.
	 * @param nodes
	 * @param parents
	 */
	private void validParseResult(ExNode[] nodes, String[][] parents){
		if(nodes == null || parents == null || nodes.length != parents.length){
			return;
		}else{
			taxonomyItem.setItemCount(2);
			taxonomyItem.getItem(0).setFont(SWTResourceManager.getFont("Arial", 12, SWT.NONE));
			taxonomyItem.getItem(0).setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
			taxonomyItem.getItem(1).setFont(SWTResourceManager.getFont("Arial", 12, SWT.NONE));
			taxonomyItem.getItem(1).setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_MAGENTA));
			taxonomyItem.getItem(0).setText("Concept Nodes");
			taxonomyItem.getItem(1).setText("Logic Nodes");
			taxonomyItem.getItem(0).setImage(new Image(display, ".//img//cNode.gif"));
			taxonomyItem.getItem(1).setImage(new Image(display, ".//img//lNode.gif"));
			int count = 0;
			for(int i = 0; i < nodes.length; i++){
				if(nodes[i].getNodeTag().equals(ExNode.TAG.NORMALNODE))
					count++;
			}
			ExNode[] normalNodes = new ExNode[count];
			ExNode[] logicNodes = new ExNode[nodes.length - count];
			int cNodeCounter = 0, lNodeCounter = 0;
			int[] cParents = new int[count], lParents = new int[nodes.length - count];
			for(int i = 0; i < nodes.length; i++){
				if(nodes[i].getNodeTag().equals(ExNode.TAG.NORMALNODE)){
					normalNodes[cNodeCounter] = nodes[i];
					cParents[cNodeCounter] = i;
					cNodeCounter++;
				}else{
					logicNodes[lNodeCounter] = nodes[i];
					lParents[lNodeCounter] = i;
					lNodeCounter++;
				}
			}
			taxonomyItem.getItem(0).setItemCount(count);
			taxonomyItem.getItem(1).setItemCount(nodes.length - count);
			
			//
			for(int i = 0; i < count; i++){
				taxonomyItem.getItem(0).getItem(i).setFont(SWTResourceManager.getFont("Arial", 10, SWT.NONE));
				taxonomyItem.getItem(0).getItem(i).setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				taxonomyItem.getItem(0).getItem(i).setText(normalNodes[i].getName());
				taxonomyItem.getItem(0).getItem(i).setImage(new Image(display, ".//img/node.gif"));
			}
			for(int i = 0; i < nodes.length - count; i++){
				taxonomyItem.getItem(1).getItem(i).setFont(SWTResourceManager.getFont("Arial", 10, SWT.NONE));
				taxonomyItem.getItem(1).getItem(i).setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
				taxonomyItem.getItem(1).getItem(i).setText(logicNodes[i].getName());
				taxonomyItem.getItem(1).getItem(i).setImage(new Image(display, ".//img/lNode1.gif"));
			}
		}
	}
	
	/**
	 * When node is selected. 
	 * @param e
	 */
	private void selectNodeResponse(SelectionEvent e){
		showResultText.setFont(SWTResourceManager.getFont("", 16, SWT.NONE));
		if(e.item.equals(taxonomyItem)){
			String s;
			if(taxonomyItem.getText() != "Taxonomy"){
				s = "Ontology: " + taxonomyItem.getText();
				showResultText.setText(s);
			}
		}else if(e.item.equals(taxonomyItem.getItem(0))){
			String s = taxonomyItem.getItem(0).getText();
			s += "\n" + "concept node number: " + taxonomyItem.getItem(0).getItemCount() + ".";
			showResultText.setText(s);
		}else if(e.item.equals(taxonomyItem.getItem(1))){
			String s = taxonomyItem.getItem(1).getText();
			s += "\n" + "Logic node number: " + taxonomyItem.getItem(1).getItemCount() + ".";
			showResultText.setText(s);
		}else{
			for(int i = 0; i < taxonomyItem.getItemCount(); i++){
				for(int j = 0; j < taxonomyItem.getItem(i).getItemCount(); j++){
					if(e.item.equals(taxonomyItem.getItem(i).getItem(j))){
						String s = "Node Name:                     " + "\n   ";
						String name = taxonomyItem.getItem(i).getItem(j).getText();
						s = s + name + "\n\n";	
						s += "Believes: \n";
						try {
							s += "   True: " + net.getNode(name).getBelief("True") + "\n";
							s += "   False: " + net.getNode(name).getBelief("False") + "\n";
						}catch(NeticaException ne) {
							ne.printStackTrace();
						}
						
						for(int k = 0; k < nodes.length; k++){
							if(nodes[k].getName().equals(taxonomyItem.getItem(i).getItem(j).getText())){
								if(parents[k].length > 0){
									s = s + "\nNode Parents:                  " + "\n   ";
									for(int r = 0; r < parents[k].length; r++){
										if(parents[k][r] != null){
											s = s + parents[k][r] + " ";
										}
									}
								}
							}
						}
						showResultText.setText(s);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Close GUI.
	 */
	private void close(){
		shell.dispose();
	}
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		MainUI mainUI = new MainUI();
		mainUI.run();
	}
}
