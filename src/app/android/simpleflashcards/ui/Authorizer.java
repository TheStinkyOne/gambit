package app.android.simpleflashcards.ui;


import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;


public class Authorizer
{
	// Defined by API
	private static final String SPREADSHEETS_AUTH_TOKEN_TYPE = "wise";
	private static final String DOCUMENTS_LIST_AUTH_TOKEN_TYPE = "writely";

	private final Activity activity;

	public static enum ServiceType {
		SPREADSHEETS, DOCUMENTS_LIST
	}

	public Authorizer(Activity activity) {
		this.activity = activity;
	}

	public String getToken(ServiceType serviceType, Account account) {
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());
		Bundle authResponse = new Bundle();
		String authType = authTypeFromServiceType(serviceType);

		try {
			authResponse = accountManager.getAuthToken(account, authType, null, activity, null, null)
				.getResult();
		}
		catch (OperationCanceledException e) {
			throw new AuthorizationCanceledException();
		}
		catch (AuthenticatorException e) {
			throw new AuthorizationFailedException();
		}
		catch (IOException e) {
			throw new AuthorizationFailedException();
		}

		if (!authResponse.containsKey(AccountManager.KEY_AUTHTOKEN)) {
			throw new AuthorizationFailedException();
		}

		return authResponse.getString(AccountManager.KEY_AUTHTOKEN);
	}

	private String authTypeFromServiceType(ServiceType serviceType) {
		switch (serviceType) {
			case SPREADSHEETS:
				return SPREADSHEETS_AUTH_TOKEN_TYPE;

			case DOCUMENTS_LIST:
				return DOCUMENTS_LIST_AUTH_TOKEN_TYPE;

			default:
				throw new RuntimeException("Unknown service type");
		}
	}
}
