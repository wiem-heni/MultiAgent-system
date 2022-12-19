package agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jade.core.Runtime;


public class NegociateurGui extends Application {

    protected  ObservableList<String> observableList;
    protected NegociateurAgent negociateurAgent;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Negociateur");
        BorderPane borderPane=new BorderPane();
        VBox vBox=new VBox();
        observableList=
                FXCollections.observableArrayList();
        ListView<String> listView=new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);
        Scene scene=new Scene(borderPane,400,300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl(false);
        profile.setParameter(profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent
                ("Negociateur", "agents.NegociateurAgent", new Object[]{this});
        agentController.start();
    }

    ///loger le message
    public void logMessage(ACLMessage aclMessage){
        // pour éviter les pb de multi-thread
        Platform.runLater(()-> {
            observableList.add(aclMessage.getContent()+", "+
                    aclMessage.getSender().getName());
        });
    }
    ///loger le message
  public void logMessage2(ACLMessage meilleureOffre){
 
            observableList.add("Le meilleur prix proposé est "+meilleureOffre.getContent());
    
  }
}
