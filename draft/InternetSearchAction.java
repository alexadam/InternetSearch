package org.eclipse.ui.internal.console;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.internal.preferences.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class InternetSearchAction extends Action {

	private IConsoleView fConsoleView;
	private IWebBrowser fWebBrowser;

	public InternetSearchAction(IConsoleView consoleView) {
		super("Google it");
		fConsoleView = consoleView;

		setToolTipText("Google the selected text");
		
		setBrowser();
	}
	
	private void setBrowser() {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();

			if (workbench == null)
				return;

			IWorkbenchBrowserSupport browserSupport = workbench.getBrowserSupport();

			if (browserSupport == null)
				return;

			fWebBrowser = browserSupport.getExternalBrowser();//.createBrowser("");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			IViewSite viewSite = fConsoleView.getViewSite();

			if (viewSite == null)
				return;

			ISelectionProvider selectionProvider = viewSite
					.getSelectionProvider();

			if (selectionProvider == null)
				return;

			TextSelection selection = (TextSelection) selectionProvider
					.getSelection();

			if (selection == null)
				return;

			if (selection.getLength() == 0)
				return;

			String selText = selection.getText().trim();

			if (selText.isEmpty())
				return;

			selText = selText.replaceAll("[^A-Za-z0-9]+", " ");
			selText = selText.replaceAll("\\s+", "+");

			if (fWebBrowser == null)
				return;
			
			IPreferenceStore prefStore = ConsolePlugin.getDefault().getPreferenceStore();
			
			
			fWebBrowser.openURL(new URL("https://www.google.com/#q=" + selText));

		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}
	
	//https://duckduckgo.com/?q=
	
	public void dispose() {
        fConsoleView = null;
	}

}
