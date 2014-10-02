package org.eclipse.ui.internal.console;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.handlers.HandlerUtil;

public class InternetSearchHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection sel = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();

		if (sel instanceof TextSelection && !sel.isEmpty()) {
			TextSelection selection = (TextSelection) sel; 

			if (selection.getLength() == 0) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Internet Search", "No text selected!"); 
				return null;
			}
			
			String selText = selection.getText().trim();

			if (selText.isEmpty()) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Internet Search", "No text selected!"); 
				return null;
			}
			
			try {
				IWorkbench workbench = PlatformUI.getWorkbench();

				if (workbench == null)
					return null;

				IWorkbenchBrowserSupport browserSupport = workbench.getBrowserSupport();

				if (browserSupport == null)
					return null;

				List<String> engines = InternetSearchPreferencePage.getSelectedSearchEngines();
				
				if (InternetSearchPreferencePage.getBrowserType().equals(InternetSearchPreferencePage.INTERNAL_BROWSER)) {

					for (String engine : engines) {
						IWebBrowser browser =browserSupport.createBrowser(
								IWorkbenchBrowserSupport.AS_EDITOR | 
								IWorkbenchBrowserSupport.LOCATION_BAR | 
								IWorkbenchBrowserSupport.NAVIGATION_BAR | 
								IWorkbenchBrowserSupport.STATUS, 
								engine + selText,
								selText, 
								null);
						browser.openURL(new URL(engine + URLEncoder.encode(selText, "UTF-8")));
					}
				} else if (InternetSearchPreferencePage.getBrowserType().equals(InternetSearchPreferencePage.EXTERNAL_BROWSER)) {
					IWebBrowser webBrowser = browserSupport.getExternalBrowser();
					
					for (String engine : engines)
						webBrowser.openURL(new URL(engine + URLEncoder.encode(selText, "UTF-8")));
				} else if (InternetSearchPreferencePage.getBrowserType().equals(InternetSearchPreferencePage.CUSTOM_BROWSER)) {
					for (String engine : engines)
						Runtime.getRuntime().exec(InternetSearchPreferencePage.getCustomBrowser() + " " + engine + URLEncoder.encode(selText, "UTF-8"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

}
