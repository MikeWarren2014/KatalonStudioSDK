package me.mikewarren.katalonstudiosdk.pages

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

public abstract class BasePage {
    protected final String url;

    public BasePage() {
    }

    public BasePage(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void go() {
        if (!WebUI.getUrl().equals(url)) {
            WebUI.navigateToUrl(url);

            WebUI.waitForPageLoad(5);
            
            this.waitOnLoader();
        }
    }
    
    public void waitOnLoader() {
    	WebUI.waitForElementNotPresent(this.getLoader(), 5)
    }
	
	public abstract TestObject getLoader();
}