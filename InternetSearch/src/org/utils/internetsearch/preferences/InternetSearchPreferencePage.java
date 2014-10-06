package org.utils.internetsearch.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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
import org.utils.internetsearch.Activator;

public class InternetSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private Button fDefaultBrowserButton;
	private Button fDefaultExtBrowserButton;
	private Button fCustomBrowserButton;
	private Label fCustomBrowserLabel;
	private List<Button> fSEngineButtons;
	
	private List<String> fTmpEngines = new ArrayList<String>(); //local model - must be in sync with ISPreferenceManager
	private List<String> fTmpSelectedEngines = new ArrayList<String>();
	private Group sEnginesGroup;
	private Group sEnginesListGroup;
	private Button fEditCustomBrowser;

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		ISPrefenceManager.refreshPrefences();
		
		fTmpEngines.addAll(ISPrefenceManager.getAllEngines());
		fTmpSelectedEngines.addAll(ISPrefenceManager.getSelectedEngines());
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
		fDefaultBrowserButton.setSelection(ISPrefenceManager.getSelectedBrowser().equals(PreferenceConstants.INTERNAL_BROWSER));
		
		fDefaultExtBrowserButton = new Button(browsersGroup, SWT.RADIO);
		fDefaultExtBrowserButton.setText("Default External Browser");
		fDefaultExtBrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		fDefaultExtBrowserButton.setSelection(ISPrefenceManager.getSelectedBrowser().equals(PreferenceConstants.EXTERNAL_BROWSER));
		
		fCustomBrowserButton = new Button(browsersGroup, SWT.RADIO);
		fCustomBrowserButton.setText("Custom Browser");
		fCustomBrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		fCustomBrowserButton.setSelection(ISPrefenceManager.getSelectedBrowser().equals(PreferenceConstants.CUSTOM_BROWSER));
		fCustomBrowserButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				fEditCustomBrowser.setEnabled(fCustomBrowserButton.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		fEditCustomBrowser = new Button(browsersGroup, SWT.NONE);
		fEditCustomBrowser.setText("Edit...");
		fEditCustomBrowser.addSelectionListener(new SelectionListener() {
			
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
		fEditCustomBrowser.setEnabled(fCustomBrowserButton.getSelection());
		
		fCustomBrowserLabel = new Label(browsersGroup, SWT.WRAP);
		fCustomBrowserLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		fCustomBrowserLabel.setText(ISPrefenceManager.getCustomBrowser());
		
		///search engines 
		
		sEnginesGroup = new Group(parent, SWT.NONE);
		sEnginesGroup.setLayout(new GridLayout(1, false));
		sEnginesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sEnginesGroup.setText("Select Search Engines");
		
		sEnginesListGroup = new Group(sEnginesGroup, SWT.NONE);
		sEnginesListGroup.setLayout(new GridLayout(1, false));
		sEnginesListGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		drawSEngineButtons();
		
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
						layoutData.heightHint = 500;
						layoutData.widthHint = 500;
						parent.setLayoutData(layoutData);
						
						Label message = new Label(parent, SWT.NONE);
						message.setText("Edit Search Engines\n\nSearch_Engine_URL (the selected text is appended by default)\n\nOR\n\n" +
								"Search_{}_Engine_URL (where {} is replaced by the selected text)\n");
						message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
						
						fEngineNames = new Text(parent, SWT.WRAP | SWT.MULTI | SWT.BORDER);
						fEngineNames.setLayoutData(layoutData);

						StringBuffer sb = new StringBuffer();
						for (String string : fTmpEngines)
							sb.append(string).append("\n");
						fEngineNames.setText(sb.toString().trim());
						
						return parent;
					}
					
					@Override
					protected void okPressed() {
						String tmpText = fEngineNames.getText().trim();
						
						fTmpEngines.clear();
						
						// refresh the current engines list
						if (tmpText.isEmpty()) {
							fTmpEngines.addAll(PreferenceConstants.fDefaultEngines);
						} else if (!tmpText.contains("\n")) {
							fTmpEngines.add(tmpText);
						} else {
							String[] strParts = tmpText.split("\n");
							for (String part : strParts) {
								part = part.trim();
								if (part.isEmpty())
									continue;
								fTmpEngines.add(part);
							}
						}
						
						close();
						
					}
				}.open();
				
				//redraw the buttons based on the new changes
				drawSEngineButtons();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		return parent;
	}
	
	private void drawSEngineButtons() {
		if (fSEngineButtons != null) {
			for (Button button : fSEngineButtons)
				button.dispose();
		}
		
		//if no engines - set default
		if (fTmpEngines.isEmpty())
			fTmpEngines.addAll(PreferenceConstants.fDefaultEngines);
		
		fSEngineButtons = new ArrayList<Button>();
		
		for (String engine : fTmpEngines) {
			Button tmpButton = new Button(sEnginesListGroup, SWT.CHECK);
			tmpButton.setText(engine);
			tmpButton.setSelection(fTmpSelectedEngines.contains(engine));
			tmpButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			fSEngineButtons.add(tmpButton);
		}
		
		sEnginesListGroup.layout();
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
		
		if (fDefaultBrowserButton.getSelection()) {
			ISPrefenceManager.setSelectedBrowser(PreferenceConstants.INTERNAL_BROWSER);
		} else if (fDefaultExtBrowserButton.getSelection()) {
			ISPrefenceManager.setSelectedBrowser(PreferenceConstants.EXTERNAL_BROWSER);
		} else if (fCustomBrowserButton.getSelection()) {
			ISPrefenceManager.setSelectedBrowser(PreferenceConstants.CUSTOM_BROWSER);
			ISPrefenceManager.setCustomBrowser(fCustomBrowserLabel.getText().trim());
		}
		
		fTmpSelectedEngines.clear();
		
		// there must be at least one engine at this point (you cannot remove all of them)
		for (Button button : fSEngineButtons){
			if (button.getSelection())
				fTmpSelectedEngines.add(button.getText());
		}
		
		//if no engine is selected
		if (fTmpSelectedEngines.isEmpty())
			fTmpSelectedEngines.add(PreferenceConstants.DEFAULT_SEARCH_ENGINE);

		ISPrefenceManager.setAllEngines(fTmpEngines);
		ISPrefenceManager.setSelectedEngines(fTmpSelectedEngines);
		
		ISPrefenceManager.savePreferences();
	}
	
	@Override
	protected void performDefaults() {
		if (PreferenceConstants.DEFAULT_BROWSER.equals(PreferenceConstants.INTERNAL_BROWSER)) {
			fDefaultBrowserButton.setSelection(true);
		} else if (PreferenceConstants.DEFAULT_BROWSER.equals(PreferenceConstants.EXTERNAL_BROWSER)) {
			fDefaultExtBrowserButton.setSelection(true);
		} else if (PreferenceConstants.DEFAULT_BROWSER.equals(PreferenceConstants.CUSTOM_BROWSER)) {
			fCustomBrowserButton.setSelection(true);
			fCustomBrowserLabel.setText("");
		}
		
		fTmpEngines.clear();
		fTmpEngines.addAll(PreferenceConstants.fDefaultEngines);
		fTmpSelectedEngines.clear();
		fTmpSelectedEngines.add(PreferenceConstants.DEFAULT_SEARCH_ENGINE);
		
		drawSEngineButtons();
		
		super.performDefaults();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		fDefaultBrowserButton.dispose();
		fDefaultExtBrowserButton.dispose();
		fCustomBrowserButton.dispose();
		
		for (Button button : fSEngineButtons)
			button.dispose();
		
		fTmpEngines.clear();
		fTmpSelectedEngines.clear();
	}
	
}