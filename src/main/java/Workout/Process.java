package Workout;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Process {
    public static String pathContactos;
    public static String pathListadoIps;
    private WebDriver driver;

    private void welcomeMesage() {
        System.out.println("¡Hola! Tras leer este mensaje, por favor, pulsa enter.");
        System.out.println("Ruta del archivo de contactos: " + pathContactos);
        System.out.println("Ruta del archivo de listado de teléfonos a añadir (un telefono por linea): " + pathListadoIps);
        System.out.println("[[INTRODUCE SI Y PULSA ENTER SI TODO ESTÁ OK]]");
        try {
            System.in.read();
        } catch (Exception e) {

        }
    }

    public void getRoutes() {
        String os = System.getProperty("os.name").toLowerCase();
        String path = "";
        if (os.contains("mac")) {
            path = File.separator;
        } else if (os.contains("windows")) {
            path = "C:" + File.separator;
        } else if (os.contains("linux")) {
            path = File.separator;
        }
        pathContactos = path + "contactos.vcf";
        pathListadoIps = path + "listadoips.txt";
    }

    public void configure() {
        this.getRoutes();
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        this.welcomeMesage();
        /*
        Check if there is a driver available. If not, just wait.
         */
        String os = System.getProperty("os.name").toLowerCase();
        String bits = System.getProperty("sun.arch.data.model");
        String basePath = "src/main/resources/drivers/v0.24/geckodriver.";
        String osExtension = "";
        if (os.contains("mac")) {
            osExtension = "mac";
        } else if (os.contains("windows")) {
            osExtension = "win." + bits + ".exe";
        } else if (os.contains("linux")) {
            osExtension = "linux." + bits;
        }
        System.setProperty("webdriver.gecko.driver", basePath + osExtension);
        FirefoxOptions opts = new FirefoxOptions();
        /*
        Si se desean programar múltiples certificados, se hará aquí.
        Se debe cambiar el perfil al deseado con el certificado instalado.
        Para ello, se cogerá de la instalación original y dicha carpeta de perfil se pondrá en
        resources/profiles/firefox/{{profile}}
        indicando abajo su URL. Para múltiples concurrentes, se debería añadir el campo
        certificate a operation y aquí donde pone default coger this.op.getCertificateName()
        siendo por defecto alguno.
         */
        //File firefoxProfileFolder = new
        //  File("src/main/resources/profiles/firefox/default");
        FirefoxProfile profile = new FirefoxProfile(); //firefoxProfileFolder
        profile.setAcceptUntrustedCertificates(true);

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "OFF");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "OFF");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

        opts.setProfile(profile);
        opts.setHeadless(false);
        this.driver = new FirefoxDriver(opts);
        this.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        this.process();
    }

    public void process() {
        FileManager fm = new FileManager();
        List<String> tels = fm.getTelephones();
        for (String ip : tels) {
            this.processSingle(ip);
        }
    }


    public void processSingle(String ip) {
        Scanner scan = new Scanner(System.in);
        String fullUrl = "http://" + ip + "/";
        this.navigate(fullUrl);

        // FILL LOGIN PAGE
        this.driver.findElement(By.xpath("//input[@id='password']")).sendKeys("0000");
        this.driver.findElement(By.xpath("//*[@id=\"content\"]/form/div[1]/table/tbody/tr[7]/td[2]/a")).click();
        this.waitPageLoad();

        /* Check if there are any errors */
        try {
            if (null != this.driver.findElement(By.xpath(("//*[@id=\"content\"]/form/div[1]/table/tbody/tr[7]/td[2]/a")))) {
                System.out.println("Sesión iniciada en otro dispositivo para la IP " + ip);
                return;
            }
        } catch (Exception e) {

        }

        /* PAGE 1.5: ADVICE */
        try {
            WebElement advice = this.driver.findElement(By.xpath(("//input[@id='disable_advice']")));
            if (null != advice) {
                System.out.println("Ignorando aviso de contraseña insegura...");
                advice.click();
                this.driver.findElement(By.xpath("//a[@class='buttonLink100']")).click();
            }
        } catch (Exception e) {

        }

        /* PAGE 2 */
        System.out.println("Navegando por la página...");
        this.driver.findElement(By.xpath("//*[@id=\"div_tabs\"]/table/tbody/tr/td[2]/a")).click();
        this.driver.findElement(By.xpath("//*[@id=\"div_menue\"]/ul/li[5]/div/div/a")).click();
        this.driver.findElement(By.xpath("//*[@id=\"div_menue\"]/ul/li[5]/ul/li[2]/div/div/a")).click();
/* EXTENSION AUTOMATICA
        System.out.println("Por favor, selecciona la extensión que deseas actualizar: ");


        List<WebElement> extensions = this.driver.findElements(By.xpath("//td[@id=\"handsets_section\"]/table"));
        int optIterate = 1;
        String opt = "-1";
        for(WebElement extension:extensions) {
            //System.out.println(page.getByXPath(extension.getCanonicalXPath() + "/tbody/tr/td"));
            System.out.println(optIterate + ": " + extension.getText());
            optIterate += 2;
        }
        opt = scan.next();
        scan.nextLine();
        if(opt.equals("-1") || Integer.parseInt(opt) > optIterate) {
            System.out.println("Opción no válida. Abortando petición.");
            return;
        }
        this.driver.findElements(By.xpath("//td/input"));
        WebElement selectedOpt = extensions.get((Integer.parseInt(opt) - 1)).findElement(By.xpath("//td/input"));
        selectedOpt.click();*/
        this.driver.findElements(By.xpath("//td/input"));
        List<WebElement> extensions = this.driver.findElements(By.xpath("//td[@id=\"handsets_section\"]/table"));
        WebElement selectedOpt = extensions.get(0).findElement(By.xpath("//td/input"));
        selectedOpt.click();
        System.out.println("Eliminando agenda...");

        this.navigate(fullUrl + "settings_phonebook_transfer.html");

        /* Recheck if needed */
        this.driver.findElement(By.xpath("//html/body/div/div[5]/div[2]/form/div[1]/table/tbody/tr[7]/td[2]/div/a")).click();

        this.acceptAlert();
        this.acceptAlert();

        System.out.println("Subiendo nueva agenda...");

        this.driver.findElement(By.xpath("//*[@id=\"save_restore\"]")).sendKeys("value", pathContactos);
        this.driver.findElement(By.xpath("//*[@id=\"content\"]/form/div[1]/table/tbody/tr[5]/td[2]/div/a")).click();
        /*HtmlFileInput newContacts2 = page.getFirstByXPath("//*[@id=\"tdt_file\"]");
        // Mejora: coger este archivo por FTP.
        newContacts2.setFiles(new File("Users/andreigarcia/IdeaProjects/workout_automatizacion_telefonos/contactos.vcf"));

        page.executeJavaScript("document.gigaset.tdt_function.value = '2';\n" +
                "\t      document.gigaset.submit();");
        System.out.println("Javascript executed");

        System.out.println("URL: " + page.getUrl());
        System.out.println(collectedAlerts);
        while(page.getUrl().equals(fullUrl + "status.html")) {
            System.out.println("Subiendo contactos... " + page.getUrl());
            synchronized(page){//page2 should not be null
                page.wait(5000);
            }
            TimeUnit.SECONDS.sleep(5);
        }
        /* DISCONNECT */
        System.out.println("Desconectando...");
        this.navigate(fullUrl + "logout.html");

    }

    private void acceptAlert() {
        int i = 0;
        while (i++ < 10) {
            try {
                Alert alert = driver.switchTo().alert();
                alert.accept();
                break;
            } catch (NoAlertPresentException e) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e2) {

                }
                continue;
            }
        }
    }

    public void navigate(String url) {
        System.out.println("Navegando a " + url);
        this.driver.get(url);
        this.waitPageLoad();
    }

    private void waitPageLoad() {
        ExpectedCondition<Boolean> pageLoadCondition = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
                    }
                };
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(pageLoadCondition);
    }

}
