//Listens for actions from specific client (depending on MSocket) in P2P
//Made in MyServerthread.java
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.lang.*;


public class MyServerListenerThread implements Runnable{
	private MSocket mSocket;
    //private BlockingQueue eventQueue;
    private PriorityBlockingQueue myPriorityQueue;
    public LamportClock myLamportClock;
    public HashMap<Double, Integer> lamportAcks;
    
    public MyServerListenerThread( MSocket mSocket, PriorityBlockingQueue myPriorityQueue, LamportClock myLamportClock, 
    		HashMap<Double, Integer> lamportAcks){
        this.mSocket = mSocket;
        this.myPriorityQueue = myPriorityQueue;
        this.myLamportClock = myLamportClock;
        this.lamportAcks = lamportAcks;
    }
    
    public void run() {	//read, process and enqueue packet
        MPacket received = null;
        if(Debug.debug) System.out.println("MyServerListenerThread: Starting a listener");
        while(true){
            try{
            	System.out.println("MyServerListenerThread: Going to read from socket");
                received = (MPacket) mSocket.readObject();
                if(Debug.debug) System.out.println("MyServerListenerThread: Read: " + received);
                
                //have Lamport clock stuff here
                if(received.category == 0)
                {	//if 0, an event
	               if(received.lamportClock > myLamportClock.value)  //Updating lamport clock
	                {
	            	    System.out.println("MyServerListenerThread: Updating lamport clock value as new one is HIGHER");
	                	int a = (int) Math.round(received.lamportClock);
	                	Double localLamportClock = new Double(a + "." + myLamportClock.pid).doubleValue();
	            		myLamportClock.value = (double) localLamportClock;
	            		System.out.println("MyServerListenerThread: New lamport clock value is " + myLamportClock.value);
	            	}
	               else
	               {
	            	   System.out.println("MyServerListenerThread: NOT updating lamport clock value -  Lamport clock value is " + myLamportClock.value);
	               	
	               }
	               //only events in priority queue
	               System.out.println("MyServerListenerThread: Putting EVENT in myPriorityQueue");
	               myPriorityQueue.put(received);
                }
                
                else //its an ACK
                {
                	System.out.println("MyServerListenerThread: Got an Ack!");
                	//check who its for and update hash table
                	
                	
                }
                
                
                
            }
            
            catch(IOException e){
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            
        }
    }
    
}


