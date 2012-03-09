package app.android.simpleflashcards.googledocs;


public class FailedRequestException extends GoogleDocsException
{
	public FailedRequestException() {
	}

	public FailedRequestException(String message) {
		super(message);
	}

	public FailedRequestException(Throwable cause) {
		super(cause);
	}

	public FailedRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}