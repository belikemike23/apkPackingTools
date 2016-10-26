package javafx.apktools;

import javafx.apktools.model.BuildParams;
import javafx.apktools.model.Data;
import javafx.apktools.model.config.Channel;
import javafx.apktools.model.config.Pac;
import javafx.apktools.model.config.Person;
import javafx.apktools.model.config.Product;
import com.ids.crypt.*;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;



public class MainController extends Controller{

    public static long actionTime;

    public TextArea textArea;
    public ComboBox<Product> product;
    public ComboBox<Pac> pac;
    //public ComboBox<Person> person;
    public TextField versionName,versionCode,gameVersion,sdkVersion,ledouChannle, selectedFile,resFile,ctChannel,mmChannel,cmChannel;
    public ImageView image;

    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;
    public Stage buildStage, addPacStage;
    public GridPane mainPane;
    private static String KEY = "idreamsky2009110";

    private BuildParams buildInfo = new BuildParams();

    public void btnBakSmaliApk(){
        ApkBuildController abc=new ApkBuildController();
        abc.bakSmali(buildInfo);
        String folderName = buildInfo.apkFile.getParent() + File.separator + buildInfo.apkFile.getName().replace(".apk", "").trim();
        versionName.setText(showApkToolYmlVersion(folderName)[0]);
        versionCode.setText(showApkToolYmlVersion(folderName)[1]);
        ledouChannle.setText(showSkynetconfig(folderName)[0]);
        gameVersion.setText(showSkynetconfig(folderName)[1]);
        sdkVersion.setText(showSkynetconfig(folderName)[2]);
        mmChannel.setText(showCarrierInfo(folderName)[0]);
        ctChannel.setText(showCarrierInfo(folderName)[1]);
        cmChannel.setText(showCarrierInfo(folderName)[2]);
    }

    public void btnSignApk(){
        ApkBuildController abc =new ApkBuildController();
        abc.signApk(buildInfo);
    }

    public void btnSmaliApk(){
        //从主界面获取参数
        buildInfo.version=versionName.getText();
        buildInfo.versioncode=versionCode.getText();
        buildInfo.gameVersion=gameVersion.getText();
        buildInfo.ledouChannel=ledouChannle.getText();
        buildInfo.sdkVersion=sdkVersion.getText();
        buildInfo.ctChannel = ctChannel.getText();
        buildInfo.mmChannel = mmChannel.getText();
        if (getBuildStage() != null) {
            if (!buildStage.isShowing()) {
                buildStage.show();
            }
            getController(ApkBuildController.class).Smali(buildInfo);
        }
    }

    public void btnUpdateVersion(){
        buildInfo.version=versionName.getText();
        buildInfo.versioncode=versionCode.getText();
        if(buildInfo.version == null||buildInfo.version.length() <= 0 ) {
            new Alert(Alert.AlertType.WARNING,"请填入正确的versionname", ButtonType.OK).show();
            return ;
        }
        else if(buildInfo.versioncode == null||buildInfo.versioncode.length() <= 0 ){
            new Alert(Alert.AlertType.WARNING,"请填入正确的versioncode", ButtonType.OK).show();
            return ;
        }
        else if(!(buildInfo.versioncode.startsWith("'") ||buildInfo.versioncode.endsWith("'"))){
            new Alert(Alert.AlertType.WARNING,"versioncodeb必须以“ ' ”开始和结束", ButtonType.OK).show();
            return ;
        }
        if (getBuildStage() != null) {
            if (!buildStage.isShowing()) {
                buildStage.show();
            }
            getController(ApkBuildController.class).updateApkVersion(buildInfo);
        }
    }

    public void btnBatchSmaliApk(){
        buildInfo.version=versionName.getText();
        buildInfo.versioncode=versionCode.getText();
        buildInfo.gameVersion=gameVersion.getText();
        buildInfo.ledouChannel=ledouChannle.getText();
        buildInfo.sdkVersion=sdkVersion.getText();
        buildInfo.ctChannel = ctChannel.getText();
        buildInfo.mmChannel = mmChannel.getText();
        if (getBuildStage() != null) {
            if (!buildStage.isShowing()) {
                buildStage.show();
            }
            getController(ApkBuildController.class).batchSmaliApk(buildInfo);
        }
    }

/*  不使用"选择"按钮
  public void btnFileChooser() {
        if (!actionTime()) {
            return;
        }
        if (fileChooser == null) {
            fileChooser = new FileChooser();
        }
        ObservableList<FileChooser.ExtensionFilter> observableList = fileChooser.getExtensionFilters();
        observableList.clear();
        observableList.addAll(new FileChooser.ExtensionFilter("apk文件", "*.apk"));
        List<File> list = fileChooser.showOpenMultipleDialog(Main.stage.getOwner());
        if (list != null && list.size() > 0) {
            File file = list.get(0);
            buildInfo.apkFile = file;
            buildFile.setText(file.getName());
        }
    }*/
    public void setUpGestureTarget(DragEvent event){
        mainPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                } else {
                    event.consume();
                }
            }
        });
        mainPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    List<File> list;
                    list = db.getFiles();
                    if (list != null && list.size() > 0 ) {
                        File file = list.get(0);
                        //System.out.println(file.getPath());
                        if(file.getName().endsWith(".apk")){
                        buildInfo.apkFile = file;
                        //System.out.println("反编译的文件夹路径是"+ buildInfo.apkFile.getParent() + File.separator + buildInfo.apkFile.getName().replace(".apk", "").trim());
                        selectedFile.setText(file.getName());
                        }else{
                            selectedFile.setText(file.getName());
                            buildInfo.directoryFolder=file;
                            versionName.setText(showApkToolYmlVersion(file.getPath())[0]);
                            versionCode.setText(showApkToolYmlVersion(file.getPath())[1]);
                            ledouChannle.setText(showSkynetconfig(file.getPath())[0]);
                            gameVersion.setText(showSkynetconfig(file.getPath())[1]);
                            sdkVersion.setText(showSkynetconfig(file.getPath())[2]);
                            mmChannel.setText(showCarrierInfo(file.getPath())[0]);
                            ctChannel.setText(showCarrierInfo(file.getPath())[1]);
                            cmChannel.setText(showCarrierInfo(file.getPath())[2]);
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }
/*  不使用“选择按钮”
    public void btnFileDirectoryChooser() {
        if (!actionTime()) {
            return;
        }
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
        }
        directoryChooser.setTitle("选择要被编译的文件夹");
        //File defaultDirectory=new File("D:\\WorkPlace\\SubwaySurf");
        //directoryChooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory=directoryChooser.showDialog(Main.stage.getOwner());
        if(selectedDirectory==null){
            return;
        }
        buildDirectory.setText(selectedDirectory.getName());
        buildInfo.directoryFolder=selectedDirectory;
        versionName.setText(showApkToolYmlVersion(selectedDirectory.getPath())[0]);
        versionCode.setText(showApkToolYmlVersion(selectedDirectory.getPath())[1]);
        ledouChannle.setText(showSkynetconfig(selectedDirectory.getPath())[0]);
        gameVersion.setText(showSkynetconfig(selectedDirectory.getPath())[1]);
        sdkVersion.setText(showSkynetconfig(selectedDirectory.getPath())[2]);
        mmChannel.setText(showCarrierInfo(selectedDirectory.getPath())[0]);
        ctChannel.setText(showCarrierInfo(selectedDirectory.getPath())[1]);
        cmChannel.setText(showCarrierInfo(selectedDirectory.getPath())[2]);
    }*/


    /**
     * 显示apktool.jar解包后生成的apktool.yml版本信息
     * @param appFolderName apktool.yml文件所在路径
     */
    public String[] showApkToolYmlVersion(String appFolderName){
        String versioninfo[]=new String[2];
        try{
            File apkToolYmlFile = new File(appFolderName + File.separator + "apktool.yml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(apkToolYmlFile)));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("versionName")) {
                    versioninfo[0]=line.substring(15);
                }
                if (line.contains("versionCode")) {
                    versioninfo[1]=line.substring(15);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return versioninfo;
    }
    /**
     * 显示skynetconfig的sdk版本信息
     * @param appFolderName skynet_config.txt文件所在路径
     */
    public String[] showSkynetconfig(String appFolderName){
        String[] Skynetconfig=new String[3];
        try{
            File gameVersionFile = new File(appFolderName + File.separator + "assets"+File.separator+"skynet_config.txt");
            if(!gameVersionFile.exists()){
                gameVersionFile = new File(appFolderName + File.separator + "assets"+File.separator+"ids_config.txt");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(gameVersionFile)));
            String encodedLine,decodedLine;
            String line;
            StringBuffer strBuffer=new StringBuffer();
            while ((line=reader.readLine())!=null){
                strBuffer.append(line);
            }
            encodedLine=strBuffer.toString();
            //去掉编辑器自作聪明加上的utf-8标记
            //String correctEncodedLine=encodedLine.substring(1);
            //用json的方法获取gameversion
            decodedLine=decode(encodedLine);
            //System.out.println(decodedLine);
            //int start=decodedLine.indexOf("game_version");
            //int end=decodedLine.indexOf(",",start);
            //gameVersion=decodedLine.substring(start+15,end-1);
            buildInfo.skynetconfig=decodedLine;
            JSONObject jsonObject = new JSONObject(decodedLine);
            Skynetconfig[0]= jsonObject.getString("channel_id");
            Skynetconfig[1] = jsonObject.getString("game_version");
            Skynetconfig[2] = jsonObject.getString("sdk_version");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Skynetconfig;
    }


    /**
     * 显示各大运营商渠道号及配置文件是否替换
     * @param appFolderName apktool.yml文件所在路径
     */

    public String[] showCarrierInfo(String appFolderName){
        String[] carrierInfo=new String[3];
        try{
            File ctChannelFile = new File(appFolderName + File.separator + "assets"+File.separator+"egame_channel.txt");
            if(ctChannelFile.exists()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(ctChannelFile)));
                String line;
                StringBuffer strBuffer=new StringBuffer();
                while ((line=reader.readLine())!=null){
                    strBuffer.append(line);
                }
                line=strBuffer.toString();
                carrierInfo[1]=line;
            }else {
                carrierInfo[1]="电信渠道文件不存在！";
            }
            File mmChannelFile = new  File(appFolderName + File.separator + "unknown"+File.separator+"mmiap.xml");
            if(mmChannelFile.exists()) {
                Document document = new SAXReader().read(mmChannelFile);
                Element element = document.getRootElement();
                Element channelElement = element.element("channel");
                String mmChannel = channelElement.getText();
                carrierInfo[0] = mmChannel;
            }else {
                carrierInfo[0] = "mm计费文件不存在！";
            }
            File cmChannelFile = new File(appFolderName + File.separator + "assets"+File.separator+"CHANNEL"+File.separator+"channel.xml");
            if(cmChannelFile.exists()) {
                Document document = new SAXReader().read(cmChannelFile);
                Element element = document.getRootElement();
                Element channelElement = element.element("channelName");
                String cmChannel = channelElement.getText();
                carrierInfo[2] = cmChannel;
            }else {
                carrierInfo[2] = "基地计费文件不存在！";
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return carrierInfo;
    }



/*    public void btnResFileChooser() {
        if (!actionTime()) {
            return;
        }
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
        }
        File directory = directoryChooser.showDialog(Main.stage.getOwner());
        if (directory == null) {
            return;
        }
        resFile.setText(directory.getPath());
        buildInfo.resFolder = directory;
    }*/

    public void btnAddPac() {
        if (!actionTime()) {
            return;
        }
        if (getAddPacStage() != null) {
            getAddPacStage().show();
        }
    }

/*
    public void productAction() {
        buildInfo.product = product.getSelectionModel().getSelectedItem();
    }

   public void personAction() {
        buildInfo.person = person.getSelectionModel().getSelectedItem();
    }*/

    public void pacAction() {
        buildInfo.pac.clear();
        Pac c = pac.getSelectionModel().getSelectedItem();
        if ("All".equals(c.mark)) {
            Pac[] cc = new Pac[pac.getItems().size()];
            pac.getItems().toArray(cc);
            buildInfo.pac.addAll(Arrays.asList(cc));
        } else {
            buildInfo.pac.add(c);
        }
    }

    public void addNewPac(String name, String mark) {
        Pac c = new Pac();
        c.name = name;
        c.mark = mark;
        pac.getItems().remove(c);
        pac.getItems().add(c);
        pac.getSelectionModel().select(c);
    }
    public void setText(String text) {
        Platform.runLater(() -> textArea.appendText(text + "\r\n"));
    }

    @Override
    public void initialized(URL location, ResourceBundle resources) {
        //image.setImage(new Image("/android_heander.png"));

       Data data = new Data();
        /* buildInfo.appName = data.getAppName();
        buildInfo.channelName = data.getChannelName();
        buildInfo.personName = data.getPersonName();*/

        /* 不使用Product
        List<Product> products = data.getProduct();

        list.addAll(products);
        product.getSelectionModel().select(0);
        buildInfo.product = product.getSelectionModel().getSelectedItem();
        */
        buildInfo.pacName = data.getPacName();
        ObservableList list;
        List<Pac> pacs = data.getPac();
        addDefPac(pacs);
        list = pac.getItems();
        list.addAll(pacs);
        pac.getSelectionModel().select(0);
        buildInfo.pac.addAll(pacs);

        /* 不使用
        List<Person> persons = data.getPerson();
        list = person.getItems();
        list.addAll(persons);
        person.getSelectionModel().select(0);
        buildInfo.person = person.getSelectionModel().getSelectedItem();
        */

        buildInfo.manifest = data.getManifest();
        buildInfo.resource = data.getResource();
        /*去掉默认显示，修改为读取当前文件夹配置
        version.setText("1.0.0");
        */
        //buildInfo.version = versionName.getText();

    }

    public void addDefPac(List<Pac> pacs) {
        Pac c = new Pac();
        c.name = "全部包名";
        c.mark = "All";
        pacs.add(0, c);
    }


    public Stage getBuildStage() {
        if (buildStage == null) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("fxml/apk_build.fxml"));
                buildStage = new Stage();
                buildStage.setTitle(Main.TITLE);
                buildStage.setScene(new Scene(root));
                buildStage.initModality(Modality.NONE);
                buildStage.initStyle(StageStyle.UNIFIED);
                buildStage.setResizable(false);
                buildStage.initOwner(Main.stage);
                buildStage.setX(Main.stage.getX());
                buildStage.setY(Main.stage.getY() + Main.stage.getHeight());
                buildStage.setOnShown((event) -> {

                });
            } catch (Exception e) {
                e.printStackTrace();
                buildStage = null;
            }
        }
        return buildStage;
    }


    public Stage getAddPacStage() {
        if (addPacStage == null) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("fxml/add_pac.fxml"));
                addPacStage = new Stage();
                addPacStage.setTitle(Main.TITLE);
                addPacStage.setScene(new Scene(root));
                addPacStage.initModality(Modality.WINDOW_MODAL);
                addPacStage.initStyle(StageStyle.UNIFIED);
                addPacStage.setResizable(false);
                addPacStage.initOwner(Main.stage);
                addPacStage.setOnShown((event) -> getController(AddPacController.class).initialized(null, null));
            } catch (Exception e) {
                e.printStackTrace();
                addPacStage = null;
            }
        }
        return addPacStage;
    }

    public boolean actionTime() {
        if (System.currentTimeMillis() - actionTime > 600) {
            actionTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    static String decode(String st) throws UnsupportedEncodingException {
        st = st.replaceAll("@@", "\"");
        JSONArray data = new JSONArray(st);
        StringBuffer outputStr = new StringBuffer();
        int len = data.length();
        for(int decryptStr = 0; decryptStr < len; ++decryptStr) {
            String inputStr = data.getString(decryptStr);
            outputStr.append(Des.decode(KEY, inputStr));
        }
        String var6 = URLDecoder.decode(outputStr.toString(), "utf-8");
        return var6;
    }

    @Override
    protected Controller getController() {
        return this;
    }
}
