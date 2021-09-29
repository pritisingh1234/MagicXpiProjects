package ISO8583;

import java.util.HashMap;

import com.magicsoftware.xpi.sdk.SDKException;
import com.magicsoftware.xpi.sdk.trigger.TriggerGeneralParams;
import com.magicsoftware.xpi.sdk.trigger.external.FlowLauncher;

public class Test {
public static void main(String[] args) throws SDKException {
	Runtime run=new Runtime();
	TriggerGeneralParams  triggerGeneralParams=new TriggerGeneralParams(); 
	FlowLauncher fl=new FlowLauncher();
	HashMap<String,String> map=new HashMap<String,String>();
	map.put("IP","10.23.223.101");
	map.put("Port","5012");
	triggerGeneralParams.setServiceObject(map);
	run.load(triggerGeneralParams, fl);
	
}
}
