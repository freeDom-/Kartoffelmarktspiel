package jKMS.exceptionHelper;

@SuppressWarnings("serial")
public class WrongAssistantCountException extends Exception{
	
	public WrongAssistantCountException(){
		super("Assistantcount have to be > 0");
	}

}
