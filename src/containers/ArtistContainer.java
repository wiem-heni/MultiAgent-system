package containers;

import agents.ArtistAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ArtistContainer extends Application {
    private ArtistAgent artistAgent;
    ObservableList<String> observableList = FXCollections.observableArrayList();

//lorsque l'execution il va executer methode start qui va appeler start container au premier...
    public static void main(String[] args) {
        launch(ArtistContainer.class);
    }

    public void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl(false);
        profile.setParameter(profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Artist", "agents.ArtistAgent", new Object[]{this});
        agentController.start();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();

        primaryStage.setTitle("Artiste");
        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10,10, 10, 10));
        hBox.setSpacing(10);
        Label label = new Label("oeuvre d'art: ");
        TextField textField = new TextField();
        Label label2 = new Label("prix min: ");
        TextField textField2 = new TextField();
        Button button = new Button("vendre");
        hBox.getChildren().add(label);
        hBox.getChildren().add(textField);
        hBox.getChildren().add(label2);
        hBox.getChildren().add(textField2);
        hBox.getChildren().add(button);
        borderPane.setTop(hBox);

        GridPane gridPane = new GridPane();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        ListView<String> stringListView = new ListView<String>(observableList);
        gridPane.add(stringListView,0, 0);
        vBox.getChildren().add(gridPane);
        borderPane.setCenter(vBox);


        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String article = textField.getText().toString()+" "+textField2.getText().toString();
                //demande de vente si evenement type 1
                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter(article);
                artistAgent.onGuiEvent(guiEvent);
               
            }
        });
        Scene scene = new Scene(borderPane, 350, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    ///loger le message
    public void logMessage(ACLMessage aclMessage){
        // pour éviter les pb de multi-thread
        Platform.runLater(()-> {
            observableList.add(aclMessage.getContent()+", "+
                    aclMessage.getSender().getName());
        });
    }

	public ArtistAgent getArtistAgent() {
		return artistAgent;
	}

	public void setArtistAgent(ArtistAgent artistAgent) {
		this.artistAgent = artistAgent;
	}
    
}
