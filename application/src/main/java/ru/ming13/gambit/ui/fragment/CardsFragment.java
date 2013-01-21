/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.ui.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.provider.ProviderUris;
import ru.ming13.gambit.local.sqlite.DbFieldNames;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.loader.Loaders;


public class CardsFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	private Uri cardsUri;

	private CursorAdapter cardsAdapter;

	public static CardsFragment newInstance(Uri deckUri) {
		CardsFragment cardsFragment = new CardsFragment();

		cardsFragment.setArguments(buildArguments(deckUri));

		return cardsFragment;
	}

	private static Bundle buildArguments(Uri deckUri) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(FragmentArguments.DECK_URI, deckUri);

		return bundle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpCardsUri();

		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	private void setUpCardsUri() {
		Uri deckUri = getArguments().getParcelable(FragmentArguments.DECK_URI);

		cardsUri = ProviderUris.Content.buildCardsUri(deckUri);
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpCardsList();
	}

	private void setUpCardsList() {
		setUpCardsAdapter();
		loadCards();
	}

	private void setUpCardsAdapter() {
		cardsAdapter = buildCardsAdapter();
		setListAdapter(cardsAdapter);
	}

	private CursorAdapter buildCardsAdapter() {
		String[] departureColumns = {DbFieldNames.CARD_FRONT_SIDE_TEXT};
		int[] destinationFields = {R.id.text};

		SimpleCursorAdapter cardsAdapter = new SimpleCursorAdapter(getActivity(),
			R.layout.list_item_one_line, null, departureColumns, destinationFields, 0);

		cardsAdapter.setViewBinder(buildCardsListItemViewBinder());

		return cardsAdapter;
	}

	private SimpleCursorAdapter.ViewBinder buildCardsListItemViewBinder() {
		String cardsListItemTextMask = getString(R.string.mask_card_list_item);

		return new CardsListItemViewBinder(cardsListItemTextMask);
	}

	private static class CardsListItemViewBinder implements SimpleCursorAdapter.ViewBinder
	{
		private final String cardsListItemTextMask;

		public CardsListItemViewBinder(String cardsListItemTextMask) {
			this.cardsListItemTextMask = cardsListItemTextMask;
		}

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			TextView cardsListItemTextView = (TextView) view;
			cardsListItemTextView.setText(buildCardsListItemText(cursor));

			return true;
		}

		private String buildCardsListItemText(Cursor cursor) {
			String cardFrontSideText = cursor.getString(
				cursor.getColumnIndex(DbFieldNames.CARD_FRONT_SIDE_TEXT));
			String cardBackSideText = cursor.getString(
				cursor.getColumnIndex(DbFieldNames.CARD_BACK_SIDE_TEXT));

			return String.format(cardsListItemTextMask, cardFrontSideText, cardBackSideText);
		}
	}

	private void loadCards() {
		getLoaderManager().initLoader(Loaders.CARDS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		String[] projection = {DbFieldNames.ID, DbFieldNames.CARD_FRONT_SIDE_TEXT, DbFieldNames.CARD_BACK_SIDE_TEXT};
		String sort = DbFieldNames.CARD_FRONT_SIDE_TEXT;

		return new CursorLoader(getActivity(), cardsUri, projection, null, null, sort);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cardsLoader, Cursor cursor) {
		cardsAdapter.swapCursor(cursor);

		if (getListAdapter().isEmpty()) {
			setUpNoCardsText();
		}
	}

	private void setUpNoCardsText() {
		TextView emptyDecksListTextView = (TextView) getListView().getEmptyView();
		emptyDecksListTextView.setText(R.string.empty_cards);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cardsLoader) {
		cardsAdapter.swapCursor(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_action_bar_cards, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_create_item:
				callCardCreation();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callCardCreation() {
		Intent intent = IntentFactory.createCardCreationIntent(getActivity(), cardsUri);
		startActivity(intent);
	}
}
