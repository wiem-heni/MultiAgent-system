package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.List;

public class NegociateurAgent  extends GuiAgent {

    protected NegociateurGui negociateurGui;
    protected AID[] acheteurs;

    @Override
    protected void setup() {
        if (getArguments().length==1){
            negociateurGui = (NegociateurGui) getArguments()[0];
            negociateurGui.negociateurAgent=this;
        }
        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 9000) {
            @Override
            protected void onTick() {
                // Mettre à jour la liste des agents acheteurs
                DFAgentDescription dfa = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                // recherche des services par type
                sd.setType("transaction");
                // recherche des services par nom
                sd.setName("Vente");
                dfa.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent,dfa);
                    // définir la liste des acheteurs (tableau)
                    acheteurs = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        acheteurs[i] = result[i].getName();
                    }
                } catch (FIPAException fe) { fe.printStackTrace(); }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int compteur=0;
            //pour stocker les réponses des acheteurs
            private List<ACLMessage> replies= new ArrayList<ACLMessage>();
            
            @Override
            public void action() {
                
                MessageTemplate template= MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));

                ACLMessage aclMessage=receive(template);
                if(aclMessage!=null){
                    // filter les messages
                    switch(aclMessage.getPerformative()){
                    case ACLMessage.REQUEST : // de la part de l'artiste
                    
                    	
                        // appel à proposition
                        String article=aclMessage.getContent();
                        ACLMessage aclMessage1=new ACLMessage(ACLMessage.CFP); // envoyer des proposes aux acheteurs
                        aclMessage1.setContent(article);
                       for (AID aid:acheteurs) {
                           aclMessage1.addReceiver(aid);
                       }
                       send(aclMessage1);
                        break;

                   case ACLMessage.PROPOSE : //messages de la part des acheteurs contenant les différents prix proposés
                        ++compteur;
                        replies.add(aclMessage);
                        if(compteur==acheteurs.length){
                            // calculer le maximum prix
                            ACLMessage meilleureOffre=replies.get(0);
                            double max=Double.parseDouble(replies.get(0).getContent());
                            for(ACLMessage offre:replies){
                                double price=Double.parseDouble(offre.getContent());

                                if(price>=max){
                                    max=price;
                                    meilleureOffre=offre;

                                }
                            }
                            //envoyer le meilleur prix à l'artiste
                            ACLMessage msgtoArtist = new ACLMessage(ACLMessage.INFORM);
                            msgtoArtist.setContent(meilleureOffre.getContent());
                            msgtoArtist.addReceiver(new AID("Artist", AID.ISLOCALNAME));
                            send(msgtoArtist);
                            
                            //afficher le meilleur prix sur l'interface de négociateur
                            negociateurGui.logMessage2(meilleureOffre);
                            
                            //envoyer un acceptProposal à l'acheteur sélectionné avec le meilleur prix
                            ACLMessage aclMessageAccept=meilleureOffre.createReply();
                            aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            aclMessageAccept.setContent("Vous êtes sélectionné");
                            System.out.println("meilleure prix est: "+meilleureOffre.getContent());
                            send(aclMessageAccept);
                        }

                        break;
             
                }
                    String article=aclMessage.getContent();
                    //afficher le msg
                    negociateurGui.logMessage(aclMessage);
                  
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
      
    }
}
