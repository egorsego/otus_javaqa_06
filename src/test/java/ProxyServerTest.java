import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.client.ClientUtil;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ProxyServerTest {
    private static final Logger logger = LogManager.getLogger(ProxyServerTest.class);
    private static WebDriver driver;
    private static BrowserMobProxyServer proxyServer;

    @BeforeClass
    public static void generalSetup(){
        proxyServer = new BrowserMobProxyServer();
        proxyServer.start(4444);

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxyServer);
        seleniumProxy.setHttpProxy("localhost:" + proxyServer.getPort());

        DesiredCapabilities dCaps = new DesiredCapabilities();
        dCaps.setCapability(CapabilityType.PROXY, seleniumProxy);
        ChromeOptions options = new ChromeOptions();
        options.merge(dCaps);

        driver = WebDriverFactory.createDriver(WebDriverType.valueOf("CHROME"), options);
    }

    @AfterClass
    public static void teardown(){
        if (driver != null) {
            driver.quit();
        }

        proxyServer.stop();
    }

    @Test
    public void proxyTest(){
        proxyServer.newHar();

        driver.get("https://www.otus.ru/");

        Har har = proxyServer.getHar();

        File harFile = new File("otus.json"); //можно сохранять и har, но json просматривать удобнее
        try {
            har.writeTo(harFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}