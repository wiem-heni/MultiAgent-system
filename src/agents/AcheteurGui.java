package agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AcheteurGui extends Application {

    protected  ObservableList<String> observableList;
    protected AcheteurAgent acheteurAgent;
    protected AgentContainer agentContainer;
    private AcheteurGui acheteurGui;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        acheteurGui=this;
        primaryStage.setTitle("Acheteur");
        HBox hBox=new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        Label label=new Label("Agent name");
        TextField field=new TextField();
        Button buttonDeploy=new Button("Deploy");
        hBox.getChildren().addAll(label, field,buttonDeploy);
        BorderPane borderPane=new BorderPane();
        VBox vBox=new VBox();
        observableList=
                FXCollections.observableArrayList();
        ListView<String> listView=new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);
        Scene scene=new Scene(borderPane,400,300);
        primaryStage.setScene(scene);
        primaryStage.show();

        buttonDeploy.setOnAction((event) -> {
            try{
                String name=field.getText();
                AgentController agentController =
                        agentContainer.createNewAgent(name, "agents.AcheteurAgent", new Object[]{acheteurGui});
                agentController.start();
            }catch (StaleProxyException e){
                e.printStackTrace();
            }
        });
    }

    private void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl(false);
        profile.setParameter(profile.MAIN_HOST, "localhost");
        agentContainer = runtime.createAgentContainer(profile);
        agentContainer.start();
    }

    ///loger le message
    public void logMessage(ACLMessage aclMessage){
        // pour éviter les pb de multi-thread
        Platform.runLater(()-> {
            observableList.add(aclMessage.getContent()+", "+
                    aclMessage.getSender().getName());
        });
    }
	public void logMessage2(String prix) {
	       observableList.add(prix);
	}
}
