package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class AcheteurAgent extends GuiAgent {

    protected AcheteurGui acheteurGui;

    @Override
    protected void setup() {
        if (getArguments().length == 1) {
            acheteurGui = (AcheteurGui) getArguments()[0];
            acheteurGui.acheteurAgent = this;
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                // Enregister le service de l'achat des oeuvres d'art
                DFAgentDescription dfd = new DFAgentDescription();
                dfd.setName(getAID());
                ServiceDescription sd = new ServiceDescription();
                sd.setType("transaction");
                sd.setName("Vente");
                dfd.addServices(sd);
                try {
                    DFService.register(myAgent, dfd);
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null) {
                    //afficher le msg apres reception
                    acheteurGui.logMessage(aclMessage);
                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.CFP:
                            ACLMessage reply=aclMessage.createReply();
                            // changer l'acte de la réponse
                            reply.setPerformative(ACLMessage.PROPOSE);
                            // prix aleatoire
                            
                            String prixmin =aclMessage.getContent().substring(aclMessage.getContent().indexOf(" ")+1,aclMessage.getContent().length()); 
                            System.out.println(prixmin);
                            Integer i=Integer.valueOf(prixmin);  
                           String prix= String.valueOf(i+new Random().nextInt(1000));
                            reply.setContent(prix);
                            acheteurGui.logMessage2(prix);
                            send(reply);
                            break;
                        
                            
                    }
                } else block();
            }
        });
    }

    // Avant que l'agent soit détruit
    @Override
    protected void takeDown() {
        try {
            // Suppression d’un service publié
            //désenregistrer l'agent dans les services
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

   

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {

    }


}
