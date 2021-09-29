package ISO8583;

import org.apache.log4j.Logger;
import com.magicsoftware.xpi.sdk.SDKException;
import com.magicsoftware.xpi.sdk.UserProperty;
import com.magicsoftware.xpi.sdk.step.IStep;
import com.magicsoftware.xpi.sdk.step.StepGeneralParams;

public class StepRuntime implements IStep {
	
	private final static Logger log = Logger.getLogger("MyLoggerName");
	
	/*
	In order to use the log4j infrastructure you should add your own logger and appender to the log4j.xml found under runtime/java/classes
	
	Please use the following as an example but use your own logger name, appender name and log file name.
	
	Notice that this appender defines its own log file called mylog_${pid}
	
	<appender name="magicxpi-somename-appender" class="org.apache.log4j.RollingFileAppender">
		<param name="Threshold" value="&fileDefThreshold;"/>
     	<param name="File" value="${com.magicsoftware.ibolt.home}/logs/java/mylog_${pid}.log"/>
	 	<param name="MaxFileSize" value="&fileSize;"/>
	 	<param name="MaxBackupIndex" value="&fileBackups;"/>
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="&XpiPattern;"/>
		</layout>	
  	</appender>
	
	In addition you should add a logger that has the same name as the one defined in the class logger declaration. In this logger you can define the requested log level.
	The logger should reference the appender:
	
		<logger name="MyLoggerName" additivity="false">
	  	<level value="debug"/>
		<appender-ref ref="magicxpi-somename-appender"/>
	</logger>
	
	Please note that in order to get debug messages in the log the entry fileDefThreshold in the log4j.xml should be set to debug: <!ENTITY fileDefThreshold "debug">
	
	*/

	@Override
	public void invoke(StepGeneralParams params) throws SDKException {
	
	String payload = new String(params.getPayloadOBject());	
	UserProperty inputExp = params.getUserProperty("inputvalue");
	UserProperty inputStatic = params.getUserProperty("MyAlpha");
	UserProperty result = params.getUserProperty("resultvalue");
	
	try {
		String stringResult = inputExp.getValue().toString()+" "+inputStatic.getValue().toString()+"\n Payload= "+payload;
		log.debug("Setting the following result: "+stringResult);
		result.setAlpha(stringResult);
		
	} catch (Exception e) {
		log.error("Unable to set result",e);
		throw new SDKException(100,"Error");
	}
		
	}
}
