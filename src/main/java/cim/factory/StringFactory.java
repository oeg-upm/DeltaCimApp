package cim.factory;

public class StringFactory {

	private StringFactory() {
		// empty
	}

	
	public static String concatenateStrings(Object arg0, Object arg1) {
		StringBuilder message = new StringBuilder();
		message.append(String.valueOf(arg0)).append(String.valueOf(arg1));
		return message.toString();
	}
	
	
	public static String concatenateStrings(Object arg0, Object arg1, Object arg2) {
		StringBuilder message = new StringBuilder();
		message.append(String.valueOf(arg0)).append(String.valueOf(arg1)).append(String.valueOf(arg2));
		return message.toString();
	}

	public static String concatenateStrings(Object arg0, Object arg1, Object arg2,  Object arg3 ) {
		StringBuilder message = new StringBuilder();
		message.append(String.valueOf(arg0)).append(String.valueOf(arg1)).append(String.valueOf(arg2)).append(String.valueOf(arg3));
		return message.toString();
	}
}
