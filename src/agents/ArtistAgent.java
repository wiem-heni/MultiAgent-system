package agents;

import containers.ArtistContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ArtistAgent extends GuiAgent {
    //interface graphique
    private ArtistContainer gui;

    @Override
    protected void setup() {

        gui = (ArtistContainer) getArguments()[0];
        gui.setArtistAgent(this);
        System.out.println("Creation et initialisation de l'agent: "+this.getAID().getName());

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //POUR FILTERE QUE PAR UNE SEUL TYPE DE MESSAGE
                MessageTemplate messageTemplate =  MessageTemplate.MatchPerformative(ACLMessage.INFORM);

                ACLMessage aclMessage = receive(messageTemplate);
                if(aclMessage != null){
                    switch(aclMessage.getPerformative()) {
                        case ACLMessage.INFORM:
                        	 String acheteur=aclMessage.getContent();
                            
                             gui.logMessage(aclMessage);
                            break;
                    }
                }else block();
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("L'agent : "+this.getAID().getName()+" Taken down!!! ");
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        //type vente si 1 qu'on a passé en parametere dans class ArtistContainer - button.setOnAction
        if(guiEvent.getType()==1){
            //achat
            //MESSAGE Type est request
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            String article = guiEvent.getParameter(0).toString();
            aclMessage.setContent(article);
            aclMessage.addReceiver(new AID("Negociateur", AID.ISLOCALNAME));
            send(aclMessage);

        }
    }


}
