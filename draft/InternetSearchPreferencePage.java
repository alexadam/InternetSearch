package org.eclipse.ui.internal.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.console.ConsolePlugin;

public class InternetSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public static final String CUSTOM_BROWSER = "custom";
	public static final String EXTERNAL_BROWSER = "external";
	public static final String INTERNAL_BROWSER = "internal";
	private static final String STACKOVERFLOW_COM = "http://stackoverflow.com/search?q=";
	private static final String DUCKDUCKGO_COM = "https://duckduckgo.com/?q=";
	private static final String GOOGLE_COM = "http://www.google.com/search?q=";
	
	private static final String DEFAULT_SEARCH_ENGINE = GOOGLE_COM;
	private static final String DEFAULT_BROWSER = EXTERNAL_BROWSER;
	
	private static final String SELECTED_BROWSER_PREF_NAME = "internetSearch.currentBrowser";
	private static final String CUSTOM_BROWSER_PREF_NAME = "internetSearch.customBrowser";
	private static final String SEARCH_ENGINES_PREF_NAME = "internetSearch.engines";
	private static final String SELECTED_SEARCH_ENGINES_PREF_NAME = "internetSearch.currentEngines";
	
	private static final List<String> fDefaultEngines = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add(GOOGLE_COM);
			add(DUCKDUCKGO_COM);
			add(STACKOVERFLOW_COM);
		}
	};
	
	private static List<String> fEngines = new ArrayList<String>();
	private static List<String> fSelectedEngines = new ArrayList<String>();
	private static String fSelectedBrowser = DEFAULT_BROWSER;
	private static String fCustomBrowser = "";
	
	private Button fDefaultBrowserButton;
	private Button fDefaultExtBrowserButton;
	private Button fCustomBrowserButton;
	private Label fCustomBrowserLabel;
	private List<Button> fSEngineButtons;

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(ConsolePlugin.getDefault().getPreferenceStore());
		initPreferences(); //TODO
	}

	@Override
	protected void createFieldEditors() {
	}
	
	@Override
	protected Control createContents(Composite parent) {
		super.createContents(parent);
		
		//browsers
		Group browsersGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		
		browsersGroup.setLayout(layout);
		browsersGroup.setLayoutData(gridData);
		browsersGroup.setText("Select Browser");
		
		fDefaultBrowserButton = new Button(browsersGroup, SWT.RADIO);
		fDefaultBrowserButton.setText("Default Eclipse Browser (Not Recommended!)");
		fDefaultBrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		fDefaultBrowserButton.setSelection(fSelectedBrowser.equals(INTERNAL_BROWSER));
		
		fDefaultExtBrowserButton = new Button(browsersGroup, SWT.RADIO);
		fDefaultExtBrowserButton.setText("Default External Browser");
		fDefaultExtBrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		fDefaultExtBrowserButton.setSelection(fSelectedBrowser.equals(EXTERNAL_BROWSER));
		
		fCustomBrowserButton = new Button(browsersGroup, SWT.RADIO);
		fCustomBrowserButton.setText("Custom Browser");
		fCustomBrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Button editCustomBrowser = new Button(browsersGroup, SWT.NONE);
		editCustomBrowser.setText("Edit...");
		editCustomBrowser.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog customBrowserDialog = new InputDialog(getShell(), "Internet Search", "Set Custom Browser", fCustomBrowserLabel.getText(), null);
				customBrowserDialog.open();
				String browserName = customBrowserDialog.getValue();
						
				if (browserName == null)
					browserName = "";
				else
					browserName = browserName.trim();

				fCustomBrowserLabel.setText(browserName);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		fCustomBrowserLabel = new Label(browsersGroup, SWT.WRAP);
		fCustomBrowserLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		if (fSelectedBrowser.equals(CUSTOM_BROWSER)) {
			fCustomBrowserLabel.setText(fCustomBrowser);
			fCustomBrowserButton.setSelection(true);
		}
		
		///search engines 
		
		final Group sEnginesGroup = new Group(parent, SWT.NONE);
		sEnginesGroup.setLayout(new GridLayout(1, false));
		sEnginesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sEnginesGroup.setText("Select Search Engines");
		
		final Group sEnginesListGroup = new Group(sEnginesGroup, SWT.NONE);
		sEnginesListGroup.setLayout(new GridLayout(1, false));
		sEnginesListGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		drawSEngineButtons(sEnginesListGroup);
		
		Button editSEnginesButton = new Button(sEnginesGroup, SWT.NONE);
		editSEnginesButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
		editSEnginesButton.setText("Edit...");
		editSEnginesButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Dialog(getShell()) {
					
					private Text fEngineNames = null;
					
					@Override
					protected Control createDialogArea(Composite parent) {
						super.createDialogArea(parent);
						
						setTitle("Internet Search");
						
						parent.setLayout(new GridLayout());
						GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
						layoutData.heightHint = 400;
						layoutData.widthHint = 400;
						parent.setLayoutData(layoutData);
						
						Label message = new Label(parent, SWT.NONE);
						message.setText("Edit Search Engines");
						message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
						
						fEngineNames = new Text(parent, SWT.WRAP | SWT.MULTI | SWT.BORDER);
						fEngineNames.setLayoutData(layoutData);

						StringBuffer sb = new StringBuffer();
						for (String string : fEngines) {
							sb.append(string).append("\n");
						}
						
						fEngineNames.setText(sb.toString().trim());
						
						return parent;
					}
					
					@Override
					protected void okPressed() {
						String tmpText = fEngineNames.getText().trim();
						fEngines.clear();
						
						// refresh the current engines list
						if (tmpText.isEmpty()) {
							fEngines.addAll(fDefaultEngines);
						} else if (!tmpText.contains("\n")) {
							fEngines.add(tmpText);
						} else {
							String[] strParts = tmpText.split("\n");
							
							for (String part : strParts) {
								part = part.trim();
								
								if (part.isEmpty())
									continue;
								
								fEngines.add(part);
							}
						}
						
						//remove obsolete 'selected' search engines
						Iterator<String> iterSelEngines = fSelectedEngines.iterator();
						while (iterSelEngines.hasNext()) {
							if (!fEngines.contains(iterSelEngines.next()))
								iterSelEngines.remove();
						}
						
						// if all the selected engines were removed, set the default
						if (fSelectedEngines.isEmpty())
							fSelectedEngines.add(DEFAULT_SEARCH_ENGINE);
						
						super.okPressed();
					}
				}.open();
				
				//redraw the buttons based on the new changes
				drawSEngineButtons(sEnginesListGroup);
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		return parent;
	}
	
	private void drawSEngineButtons(Group sEnginesGroup) {
		if (fSEngineButtons != null) {
			for (Button button : fSEngineButtons)
				button.dispose();
		}
		
		fSEngineButtons = new ArrayList<Button>();
		
		for (String engine : fEngines) {
			Button tmpButton = new Button(sEnginesGroup, SWT.CHECK);
			tmpButton.setText(engine);
			tmpButton.setSelection(fSelectedEngines.contains(engine));
			tmpButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			fSEngineButtons.add(tmpButton);
		}
		
		sEnginesGroup.layout();
	}
	
	@Override
	public boolean performOk() {
		saveData();
		return super.performOk();
	}
	
	@Override
	protected void performApply() {
		saveData();
		super.performApply();
	}
	
	private void saveData() {
		IPreferenceStore prefStore = ConsolePlugin.getDefault().getPreferenceStore();
		
		if (fDefaultBrowserButton.getSelection()) {
			prefStore.setValue(SELECTED_BROWSER_PREF_NAME, INTERNAL_BROWSER);
			fSelectedBrowser = INTERNAL_BROWSER;
		} else if (fDefaultExtBrowserButton.getSelection()) {
			prefStore.setValue(SELECTED_BROWSER_PREF_NAME, EXTERNAL_BROWSER);
			fSelectedBrowser = EXTERNAL_BROWSER;
		} else if (fCustomBrowserButton.getSelection()) {
			prefStore.setValue(SELECTED_BROWSER_PREF_NAME, CUSTOM_BROWSER);	
			prefStore.setValue(CUSTOM_BROWSER_PREF_NAME, fCustomBrowserLabel.getText().trim());
			fSelectedBrowser = CUSTOM_BROWSER;
			fCustomBrowser = fCustomBrowserLabel.getText().trim();
		} else {
			prefStore.setValue(SELECTED_BROWSER_PREF_NAME, INTERNAL_BROWSER);
			fSelectedBrowser = INTERNAL_BROWSER;
		}
		
		StringBuffer allEngines = new StringBuffer();
		StringBuffer selectedEngines = new StringBuffer();
		
		//refresh the selections
		fSelectedEngines.clear();
		
		// there must be at least one engine at this point (you cannot remove all of them)
		for (Button button : fSEngineButtons){
			allEngines.append(button.getText()).append("\n");
			
			if (button.getSelection()) {
				selectedEngines.append(button.getText()).append("\n");
				//refresh the selection
				fSelectedEngines.add(button.getText());
			}
		}
		
		//if no engine is selected
		if (selectedEngines.length() == 0) {
			selectedEngines.append(DEFAULT_SEARCH_ENGINE);
			fSelectedEngines.add(DEFAULT_SEARCH_ENGINE);
		}
		
		prefStore.setValue(SEARCH_ENGINES_PREF_NAME, allEngines.toString().trim());
		prefStore.setValue(SELECTED_SEARCH_ENGINES_PREF_NAME, selectedEngines.toString().trim());
	}
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		
		//TODO
		
		IPreferenceStore prefStore = ConsolePlugin.getDefault().getPreferenceStore();
		
		prefStore.setValue(SELECTED_BROWSER_PREF_NAME, INTERNAL_BROWSER);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		fDefaultBrowserButton.dispose();
		fDefaultExtBrowserButton.dispose();
		fCustomBrowserButton.dispose();
		
		for (Button button : fSEngineButtons)
			button.dispose();
	}
	
	public static void initPreferences() {
		// init preferences and set defaults
		IPreferenceStore prefStore = ConsolePlugin.getDefault().getPreferenceStore();
		
		fSelectedBrowser = prefStore.getString(SELECTED_BROWSER_PREF_NAME);

		if (fSelectedBrowser.isEmpty()) {
			//first time
			prefStore.setValue(SELECTED_BROWSER_PREF_NAME, DEFAULT_BROWSER);
			fSelectedBrowser = DEFAULT_BROWSER;
		}
		
		if (fSelectedBrowser.equals(CUSTOM_BROWSER)) {
			fCustomBrowser = prefStore.getString(CUSTOM_BROWSER_PREF_NAME);
			
			//if no custom browser specified
			if (fCustomBrowser.isEmpty())
				fSelectedBrowser = DEFAULT_BROWSER;
		}

		if (prefStore.getString(SEARCH_ENGINES_PREF_NAME).isEmpty()) {
			StringBuffer sb = new StringBuffer();

			for (String string : fDefaultEngines)
				sb.append(string).append("\n");

			prefStore.setValue(SEARCH_ENGINES_PREF_NAME, sb.toString());

			fEngines.addAll(fDefaultEngines);
		} else {
			if (fEngines.isEmpty()) {
				String tmpStr = prefStore.getString(SEARCH_ENGINES_PREF_NAME);

				if (tmpStr.contains("\n"))
					fEngines = new ArrayList<String>(Arrays.asList(tmpStr.split("\n")));
				else
					fEngines.add(tmpStr);
			}
		}

		if (prefStore.getString(SELECTED_SEARCH_ENGINES_PREF_NAME).isEmpty()) {
			//first time
			fSelectedEngines.add(DEFAULT_SEARCH_ENGINE);
			prefStore.setValue(SELECTED_SEARCH_ENGINES_PREF_NAME, DEFAULT_SEARCH_ENGINE);
		} else {
			if (fSelectedEngines.isEmpty()) {
				String tmpStr = prefStore.getString(SELECTED_SEARCH_ENGINES_PREF_NAME);

				if (tmpStr.contains("\n"))
					fSelectedEngines = new ArrayList<String>(Arrays.asList(tmpStr.split("\n")));
				else
					fSelectedEngines.add(tmpStr);
			}
		}
	}
	
	public static List<String> getAllSearchEngines() {
		return fEngines;
	}
	
	public static List<String> getSelectedSearchEngines() {
		return fSelectedEngines;
	}
	
	public static String getBrowserType() {
		return fSelectedBrowser;
	}
	
	public static String getCustomBrowser() {
		return fCustomBrowser;
	}
}
