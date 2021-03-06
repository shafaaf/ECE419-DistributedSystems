import java.io.InvalidObjectException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Random;

public class ServerSenderProjectileThread implements Runnable {

    //private ObjectOutputStream[] outputStreamList = null;
    private MSocket[] mSocketList = null;
    private LinkedBlockingQueue eventQueue = null;
    //private int globalSequenceNumber; 
    
    public ServerSenderProjectileThread(MSocket[] mSocketList,
                              LinkedBlockingQueue eventQueue){
        this.mSocketList = mSocketList;
        this.eventQueue = eventQueue;
        //this.globalSequenceNumber = 0;
    }

    public void run() {
        MPacket toBroadcast = null;
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        while(true){
           
            	//Constructor - public MPacket(String name, int type, int event)
            	toBroadcast = new MPacket("from_server",MPacket.ACTION, MPacket.PROJECTILE_MOVEMENT);
               
            	
                if(Debug.debug) System.out.println("ServerSenderProjectileThread: Sending " + toBroadcast);
                
                   try {
					eventQueue.put(toBroadcast);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            
        
	        try{
	        	Thread.sleep(200);	//speed of server broadcasting
	        }
	        
	        catch(Exception e){}
            
        }
    }
}