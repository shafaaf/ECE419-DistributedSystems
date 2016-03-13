import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ClientQueueExecutionThread implements Runnable {

    private MSocket mSocket  =  null;
    private Hashtable<String, Client> clientTable = null;
    private PriorityBlockingQueue<MPacket> myPriorityQueue = null;
    private MSocket[] client_mSocket;
    private HashMap<Double, Integer> lamportAcks;
    private int maxClients;
    
    public ClientQueueExecutionThread( MSocket mSocket, Hashtable<String, Client> clientTable,
    		PriorityBlockingQueue myPriorityQueue, MSocket[] client_mSocket, 
    			HashMap<Double, Integer> lamportAcks, int maxClients)
    {
        this.mSocket = mSocket;
        this.clientTable = clientTable;
        this.myPriorityQueue = myPriorityQueue;
        this.client_mSocket = client_mSocket;
        this.lamportAcks = lamportAcks;
        this.maxClients = maxClients;
        
        if(Debug.debug) System.out.println("ClientQueueExecutionThread: Instatiating QueueExecutionThread");
    }

    public void run() {
    	
        MPacket received = null;
        MPacket headOfPriorityQueue = null;
        MPacket toBroadcast = null;
        Client client = null;
        if(Debug.debug) System.out.println("ClientQueueExecutionThread: Starting Instatiating QueueExecutionThread");
        
        while(true){
        	if(!myPriorityQueue.isEmpty())
        	{
        		//MyPriorityQueue will only have EVENTS, not acks!
                
                //Send acks for only head of queue, which may be changing
	        	headOfPriorityQueue = (MPacket)myPriorityQueue.peek();
	        	if(headOfPriorityQueue != null)
	        	{
		        	if(headOfPriorityQueue.acks_sent == 0)
		        	{
		        		//Send it to all clients
		        		for(MSocket mSocket: client_mSocket)
		        		{
		        			System.out.println("ClientQueueExecutionThread: Writing ACKS for "
		                			+ headOfPriorityQueue.lamportClock + " to sockets");
		                	toBroadcast = new MPacket(1, headOfPriorityQueue.lamportClock);
		                    mSocket.writeObject(toBroadcast);
		                    headOfPriorityQueue.acks_sent = 1;
		                }
		        	}
	        	}
	        	
	        	/*
	        	System.out.println("headOfPriorityQueue has lamport clock value  " + headOfPriorityQueue.lamportClock + 
	        			" and actual queue peek has lamport clock value " + myPriorityQueue.peek().lamportClock + 
	        			"and number of acks peek has is" + lamportAcks.get(myPriorityQueue.peek().lamportClock));
	        	*/
	        	
	        	if(lamportAcks.get(myPriorityQueue.peek().lamportClock) != null)	//head has some acks at least- need check
	        	{
	        		//Makes sure head  has all acks and has sent out all its acks 
	        		if((lamportAcks.get(myPriorityQueue.peek().lamportClock) == maxClients) 
	        				&& (myPriorityQueue.peek().acks_sent == 1))
	        		{
	        			System.out.println("QueueExecutionThread: CAN execute! - Real Head of queue has lamport clock " + 
	        					myPriorityQueue.peek().lamportClock +" and it has " + 
	        						lamportAcks.get(myPriorityQueue.peek().lamportClock) + " acks!");
		        		
	        				//execution
		        			received = myPriorityQueue.poll();
				        	client = clientTable.get(received.name);
				        	
				        	//Debugging
				        	//System.out.println("QueueExecutionThread: client for this event is: " + received.name);
				        	
				           	//Execute client actions. Here the client class refers to the client who actually caused the movement, fire or projectile movement
				            if(received.event == MPacket.UP){
				                client.forward();
				            }else if(received.event == MPacket.DOWN){
				                client.backup();
				            }else if(received.event == MPacket.LEFT){
				                client.turnLeft();
				            }else if(received.event == MPacket.RIGHT){
				                client.turnRight();
				            }else if(received.event == MPacket.FIRE){
				                client.fire();
				            }else if(received.event == MPacket.PROJECTILE_MOVEMENT){
				                client.myMoveProjectile();
				            }	
				            else{
				                throw new UnsupportedOperationException();
				            }
				            
		        		
	        		}
	        		
	        		else
		        	{	//when dont have enough acks or the guy for which we sent acks above was someone else
	        			/*
	        			System.out.println("QueueExecutionThread: CANT execute for a reason! Real Head "
	        				+ "of queue has lamport clock " + myPriorityQueue.peek().lamportClock + 
	        					" and number of acks peek has is " + lamportAcks.get(myPriorityQueue.peek().lamportClock));
	        					*/
		        	}
	        	
	        	}
                
        	} 
        }
    }
}
