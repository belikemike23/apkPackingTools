package javafx.apktools;

import com.sun.xml.internal.fastinfoset.util.StringArray;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddPacController extends Controller {

    public TextField pacName, pacMark;

    public void btnCancel() {
        MainController controller = getController(MainController.class);
        if (controller.addPacStage != null && controller.addPacStage.isShowing()) {
            controller.addPacStage.close();
        }
    }

    public void btnConfirm() {
        String name = pacName.getText();
        String mark = pacMark.getText();
        if (name == null || name.trim().length() < 1) {
            new Alert(Alert.AlertType.WARNING, "请填写包名", ButtonType.OK).show();
            return;
        }
        if (mark == null || mark.trim().length() < 1) {
            new Alert(Alert.AlertType.WARNING, "请填写渠道标识", ButtonType.OK).show();
            return;
        }
        /*
        /*
        调用MainController中的方法
         */
        MainController controller = getController(MainController.class);
        if (controller.addPacStage != null && controller.addPacStage.isShowing()) {
            controller.addPacStage.close();
            controller.addNewPac(name.trim(), mark.trim());
        }
    }

    @Override
    protected void initialized(URL location, ResourceBundle resources) {
        pacName.setText("");
        pacMark.setText("");
    }

    @Override
    protected Controller getController() {
        return this;
    }
}
