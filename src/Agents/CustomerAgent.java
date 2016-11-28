package Agents;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import Behaviour.CustomerOrder;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class CustomerAgent extends Agent{
	
	//private Vector<AID> FactoryAgents = new Vector<AID>();
	private AID FactoryAgent;
	
	protected void setup() {
		System.out.println("Customer Agent:"+getAID().getName()+"is Initialized");

		// Fetch the list of available factory agents
		addBehaviour(new OneShotBehaviour(this) {

			@Override
			public void action() {
				// TODO Auto-generated method stub
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Order-Items");
				template.addServices(sd);

				// Fetch Agent List
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					FactoryAgent = result[0].getName();

				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				// PrintAgentList();
			}
		});
		
		addBehaviour(new PlaceOrders());

	}
	
	protected void takeDown(){
		System.out.println("Customer Agent:"+getAID().getName()+"is Terminated");		
	}
	
	private class PlaceOrders extends Behaviour{
		private int step = 0;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			switch (step) {
			case 0: {
				CustomerOrder order = getOrder();
					try {
						// place order to factory agents
						ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
						msg.addReceiver(FactoryAgent);
						msg.setLanguage("English");
						msg.setContentObject(order);
						send(msg);
						step = 1;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case 1:{
				ACLMessage msg = myAgent.receive();
				if(msg != null){
					if (msg.getPerformative() == ACLMessage.INFORM) {
						System.out.println("Delivery completed");
						step = 2;
						myAgent.doDelete();
					}
				}
			}break;
			}

		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return (step == 2);
		}
		
		private CustomerOrder getOrder() {
			String order = null;
			Random r = new Random();
			int Low = 1;
			int High = 3;
			int orderType = r.nextInt(High - Low) + Low;

			switch (orderType) {

			case 1: {
				order = "Bearing";
			}
				break;
			case 2: {
				order = "Bearing-box";
			}
				break;
			default:
				break;
			}
			
			return (new CustomerOrder(this.myAgent.getAID(),order));
		}
		
	}

}