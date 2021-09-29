package ISO8583;

import com.magicsoftware.ibolt.commons.logging.ILogModules;

public enum LogModules
  implements ILogModules
{
  STEP("magicxpi.component.ISO8583.step");
  
  private final String stringValue;
  
  private LogModules(String paramString)
  {
    this.stringValue = paramString;
  }
  
  public String description()
  {
    return this.stringValue;
  }
}
