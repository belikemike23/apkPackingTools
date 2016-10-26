package javafx.apktools;

import javafx.apktools.bin.Callback;
import javafx.apktools.bin.Command;
import javafx.apktools.model.BuildParams;
import javafx.apktools.model.config.Channel;
import javafx.apktools.model.config.Pac;
import javafx.apktools.model.manifest.Manifest;
import javafx.apktools.model.manifest.MetaData;
import javafx.apktools.model.resource.Strings;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.net.URL;
import java.sql.Time;
import java.util.List;
import java.util.ResourceBundle;


public class ApkBuildController extends Controller implements Callback {

    public TextArea textArea;
    private Command command;
    Callback callback1,callback2,callback3;
    private boolean build = false;
    private boolean baksmaling =false;
    private boolean Smaling=false;
    private boolean signning = false;
    private boolean batchSmali = false;
    private boolean updating =  false;

    @Override
    public void initialized(URL location, ResourceBundle resources) {
        command = new Command(this);
    }

    @Override
    public void receiver(String message) {
        setText(message);
    }

    public void bakSmali(BuildParams params) {
        if (baksmaling) {
            new Alert(Alert.AlertType.WARNING, "正在反编译中,请稍后...", ButtonType.OK).show();
            return;
        }
        baksmaling =true;
        Command command1 = new Command(callback1);
        File apkFile = params.apkFile;
        String buildApkFolderName = apkFile.getParent() + File.separator + apkFile.getName().replace(".apk", "").trim();
        //setText("1. --> 开始解包" + apkFile.getName());
        if (command1.decodeApk(apkFile.getPath(), buildApkFolderName)) {
            //setText("反编译终止!!!!!!");
            System.out.println("反编译完成");
        }
        baksmaling = false;
        command1.close();
        new Alert(Alert.AlertType.INFORMATION, "反编译完成").show();
        }

     /*
        提升apk版本号
         */
     public void updateApkVersion(BuildParams params){
         if(updating){
             new Alert(Alert.AlertType.WARNING,"正在提升版本中，请稍后...",ButtonType.OK).show();
             return;
         }
         updating = true;
         textArea.clear();
         new Thread(()-> {
             File apkFile = params.apkFile;
             if(apkFile==null){
                 new Alert(Alert.AlertType.WARNING,"请选择需要操作的apk文件",ButtonType.OK).show();
                 return;
             }
             String buildApkFolderName = apkFile.getParent() + File.separator + apkFile.getName().replace(".apk", "").trim();
             String zipalignApkOutputFile = buildApkFolderName + File.separator + "_" + params.version+"_"+params.versioncode + ".apk";
             String buildApkOutputFile = buildApkFolderName + File.separator + apkFile.getName();
             setText("1. --> 开始解包" + apkFile.getName());
             //用apktool解包
             if (!command.decodeApk(apkFile.getPath(), buildApkFolderName)) {
                 setText("打包终止!!!!!!");
                 return;
             }
             setText("2. --> 开始更新版本信息");
             if (!command.updateApkToolYmlVersion(buildApkFolderName, params.version, params.versioncode)) {
                 setText("打包终止!!!!!!");
                 return;
             }
             setText("3. --> 开始打包");
             if (!command.buildApk(buildApkFolderName, buildApkOutputFile)) {
                 setText("打包终止!!!!!!");
                 return;
             }
             System.out.println("打包成功");
             setText("4. --> 开始签名");
             if (!command.signerApk(params.keyStoreFilePath, buildApkOutputFile, params.keyStoreAlias, params.keyStorePassword)) {
                 setText("打包终止!!!!!!");
                 return;
             }
             System.out.println("签名成功");
             setText("5. --> 开始优化");
             //zipalign优化
             if (!command.zipalign(buildApkOutputFile, zipalignApkOutputFile)) {
                 System.out.println("优化不成功");
                 return;
             }
             System.out.println("优化成功");
             command.deleteFile(new File(buildApkOutputFile));
             setText("———————————————————————————————————————————————————");
             setText("|    打包完成，你可以测试是否正确！                 |");
             setText("———————————————————————————————————————————————————");
             updating = false;
             command.close();
         }).start();
     }

    public void signApk(BuildParams params) {
        if(signning){
            new Alert(Alert.AlertType.WARNING,"正在签名中，请稍后...", ButtonType.OK).show();
            return;
        }
        signning = true;
        Command command3 = new Command(callback3);
        File apkFile = params.apkFile;
        String zipalignApkOutputFile =apkFile.getParent()+File.separator + apkFile.getName().replace(".apk", "").trim()+"_idreamskySign" + ".apk";
        if (!command3.signerApk(params.keyStoreFilePath, apkFile.getPath(), params.keyStoreAlias, params.keyStorePassword)) {
            new Alert(Alert.AlertType.INFORMATION,"签名不成功").show();
            return;
        }
        System.out.println("签名成功");
        //setText("5. --> 开始优化");
        //zipalign优化
        if (!command3.zipalign(apkFile.getPath(), zipalignApkOutputFile)) {
            new Alert(Alert.AlertType.INFORMATION,"优化不成功").show();
            return;
        }
        signning = false;
        new Alert(Alert.AlertType.INFORMATION,"签名完成").show();
        command3.close();
    }

    public void batchSmaliApk(BuildParams params) {
        if(batchSmali){
            new Alert(Alert.AlertType.WARNING,"正在批量打包中，请稍后...", ButtonType.OK).show();
            return;
        }
        batchSmali = true;
        textArea.clear();
        new Thread(() -> {
            List<Pac> pacs = params.pac;
            for (Pac pac : pacs) {
                if ("All".equals(pac.mark)) {
                    continue;
                }
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String date = sdf.format(now);
                Command command2 = new Command(callback2);
                String buildApkFolderName = params.directoryFolder.getPath();
                String buildApkOutputFile = buildApkFolderName + File.separator + params.directoryFolder.getName() + ".apk";
                String zipalignApkOutputFile = buildApkFolderName + File.separator + params.directoryFolder.getName() + "_"+pac.getMark() + "_" + date + ".apk";
                //修改apktool.yml中version的值
                setText("1. --> 开始更新版本信息");
                if (!command2.updateApkToolYmlVersion(buildApkFolderName, params.version, params.versioncode)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                setText("2. --> 开始更新包名");
                if (!command2.updatePackageName(buildApkFolderName, pac)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                setText("3. --> 开始更新skynetconfig信息");
                if (!command2.updateSkynetconfig(buildApkFolderName, params.skynetconfig, params.ledouChannel, params.gameVersion, params.sdkVersion)) {
                    return;
                }
                setText("4. --> 开始更新运营商渠道信息");
                if (!command2.updateCarrierInfo(buildApkFolderName, params.ctChannel, params.mmChannel)) {
                    setText("电信与MM渠道号不存在，无需更新");
                    return;
                }
                setText("5. --> 开始打包");
                if (!command2.buildApk(buildApkFolderName, buildApkOutputFile)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                System.out.println("打包成功");
                setText("6. --> 开始签名");
                if (!command2.signerApk(params.keyStoreFilePath, buildApkOutputFile, params.keyStoreAlias, params.keyStorePassword)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                System.out.println("签名成功");
                setText("7. --> 开始优化");
                //zipalign优化
                if (!command2.zipalign(buildApkOutputFile, zipalignApkOutputFile)) {
                    System.out.println("优化不成功");
                    return;
                }
                System.out.println("优化成功");
                command2.deleteFile(new File(buildApkOutputFile));
                command2.close();
            }
            setText("———————————————————————————————————————————————————");
            setText("|    打包完成，你可以测试是否正确！                 |");
            setText("———————————————————————————————————————————————————");
            batchSmali = false;
        }).start();
        }

    public void Smali(BuildParams params) {
        if (Smaling) {
            new Alert(Alert.AlertType.WARNING, "正在打包中,请稍后...", ButtonType.OK).show();
            return;
        }
        Smaling =true;
        textArea.clear();
            new Thread(() -> {
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String date = sdf.format(now);
                String buildApkFolderName = params.directoryFolder.getPath();
                String buildApkOutputFile = buildApkFolderName + File.separator + params.directoryFolder.getName() + ".apk";
                String zipalignApkOutputFile = buildApkFolderName + File.separator + params.directoryFolder.getName() + "_"+date + ".apk";
                //修改apktool.yml中version的值
                setText("1. --> 开始更新版本信息");
                if (!command.updateApkToolYmlVersion(buildApkFolderName, params.version, params.versioncode)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                setText("2. --> 开始更新skynetconfig信息");
                if (!command.updateSkynetconfig(buildApkFolderName, params.skynetconfig, params.ledouChannel, params.gameVersion, params.sdkVersion)) {
                    return;
                }
                setText("3. --> 开始更新运营商渠道信息");
                if(!command.updateCarrierInfo(buildApkFolderName,params.ctChannel,params.mmChannel)){
                    setText("电信与MM渠道号不存在，无需更新");
                    return;
                }
                setText("4. --> 开始打包");
                if (!command.buildApk(buildApkFolderName, buildApkOutputFile)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                System.out.println("打包成功");
                setText("5. --> 开始签名");
                if (!command.signerApk(params.keyStoreFilePath, buildApkOutputFile, params.keyStoreAlias, params.keyStorePassword)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                System.out.println("签名成功");
                setText("6. --> 开始优化");
                //zipalign优化
                if (!command.zipalign(buildApkOutputFile, zipalignApkOutputFile)) {
                    System.out.println("优化不成功");
                    return;
                }
                System.out.println("优化成功");
                command.deleteFile(new File(buildApkOutputFile));
                setText("———————————————————————————————————————————————————");
                setText("|    打包完成，你可以测试是否正确！                 |");
                setText("———————————————————————————————————————————————————");
                Smaling = false;
                command.close();
            }).start();
    }
/*
    public void build(BuildParams params) {
        if (build) {
            new Alert(Alert.AlertType.WARNING, "正在打包中,请稍后...", ButtonType.OK).show();
            return;
        }
        build = true;
        textArea.clear();
        new Thread(() -> {
            File apkFile = params.apkFile;
            String buildApkFolderName = apkFile.getParent() + File.separator + apkFile.getName().replace(".apk", "").trim();
            String buildApkOutputFile = buildApkFolderName + File.separator + apkFile.getName();
            List<Channel> channels = params.channel;
            for (Channel channel : channels) {
                if ("All".equals(channel.mark)) {
                    continue;
                }
                params.manifest.getMetaData().clear();
                params.manifest.getMetaData().add(new MetaData(params.personName, params.person.mark));
                params.manifest.getMetaData().add(new MetaData(params.channelName, channel.mark));
                setText(String.format("\r\n产品：%s\r\n渠道：%s\r\n人员：%s\r\n版本：%s", params.product.name, channel.toString(), params.person.toString(), params.version));
                String zipalignApkOutputFile = "渠道包" + File.separator + channel.name + "-" + params.product.name + "-" + params.version + ".apk";
                setText("1. --> 开始解包" + apkFile.getName());
                //用apktool解包
                if (!command.decodeApk(apkFile.getPath(), buildApkFolderName)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                setText("2. --> 开始定制");
                //替换资源文件
                if (params.resFolder != null) {
                    command.replaceResource(params.resFolder.getPath(), buildApkFolderName);
                }
                //修改AndroidManifest.xml
                if (!command.updateAndroidManifest(buildApkFolderName, params.manifest)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                //修改apktool.yml中version的值
                if (!command.updateApkToolYmlVersion(buildApkFolderName, params.version, params.versioncode)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                //修改res/values文件夹下面的资源
                command.updateResource(buildApkFolderName, params.resource);
                setText("3. --> 开始打包");
                //用apktool重新打包
                if (!command.buildApk(buildApkFolderName, buildApkOutputFile)) {
                    setText("打包终止!!!!!!");
                    return;
                }
                setText("4. --> 开始签名");
                if (params.signerTSA) {
                    //带时间戳的签名
                    if(!command.signerApkByTime(params.keyStoreFilePath, buildApkOutputFile, params.keyStoreAlias, params.keyStorePassword)){
                        setText("打包终止!!!!!!");
                        return;
                    }
                } else {
                    //不带时间戳的签名
                    if(!command.signerApk(params.keyStoreFilePath, buildApkOutputFile, params.keyStoreAlias, params.keyStorePassword)){
                        setText("打包终止!!!!!!");
                        return;
                    }
                }
                setText("5. --> 开始优化");
                //zipalign优化
                new File(zipalignApkOutputFile).getParentFile().mkdirs();
                if(!command.zipalign(buildApkOutputFile, zipalignApkOutputFile)){
                    setText("打包终止!!!!!!");
                    return;
                }
                //删除反编译后的文件夹
                command.deleteFile(new File(buildApkFolderName));
            }
            setText("———————————————————————————————————————————————————");
            setText("|    打包完成，你可以测试是否正确！    |");
            setText("———————————————————————————————————————————————————");
            build = false;
        }).start();
    }*/

    public void setText(String text) {
        Platform.runLater(() -> textArea.appendText(text + "\r\n"));
    }

    @Override
    protected Controller getController() {
        return this;
    }
}
