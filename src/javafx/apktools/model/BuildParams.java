package javafx.apktools.model;

import javafx.apktools.Main;
import javafx.apktools.model.config.Channel;
import javafx.apktools.model.config.Pac;
import javafx.apktools.model.config.Person;
import javafx.apktools.model.config.Product;
import javafx.apktools.model.manifest.Manifest;
import javafx.apktools.model.resource.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BuildParams {

    public String keyStoreFilePath;
    public String keyStoreAlias;
    public String keyStorePassword;
    public boolean signerTSA;

    public BuildParams() {
        try {
            Properties properties = new Properties();
            String filepath = java.net.URLDecoder.decode(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "utf-8");
            FileInputStream inputStream = new FileInputStream(filepath.substring(0,filepath.lastIndexOf("/"))+File.separator+"signer.properties");
            properties.load(inputStream);
            keyStoreFilePath =filepath.substring(0,filepath.lastIndexOf("/"))+File.separator+properties.getProperty("KEY_STORE_FILE_PATH", "");
            //System.out.println(filepath.substring(0,filepath.lastIndexOf("/")));
            keyStoreAlias = properties.getProperty("KEY_STORE_ALIAS", "");
            keyStorePassword = properties.getProperty("KEY_STORE_PASSWORD", "");
            signerTSA = Boolean.parseBoolean(properties.getProperty("SIGNER_TSA", "false"));
            inputStream.close();
            properties.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File apkFile;
    public File resFolder;
    public File directoryFolder;
    public Product product;
    public List<Channel> channel = new ArrayList<>();
    public List<Pac> pac = new ArrayList<>();
    public String appName;
    public String channelName;
    public String pacName;
    public Person person;
    public String personName;
    public Manifest manifest;
    public String version;
    public String versioncode;
    public String skynetconfig;
    public String ledouChannel;
    public String sdkVersion;
    public String gameVersion;
    public String ctChannel;
    public String mmChannel;
    public Resource resource;
}
