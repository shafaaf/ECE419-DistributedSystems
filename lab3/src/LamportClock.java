
public class LamportClock {
	
	public int pid;
	public double value;

	public LamportClock(int pid, double value) {
		this.pid = pid;
		this.value = value;
   }

    public void setValue(double value) {
    	this.value = value;
   }
    
    public void setPid(int pid) {
    	this.pid = pid;
   }
    
    
		 
		
}

/*
MutableDouble a = new MutableDouble(3.0);
MutableDouble b = a;

a.setValue(5.0);
b.getValue(); // equals 5.0
*/