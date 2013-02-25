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

package ru.ming13.gambit.ui.activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.otto.Subscribe;
import ru.ming13.gambit.R;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.DeckRenamedEvent;
import ru.ming13.gambit.ui.bus.DeckRenamingCancelledEvent;
import ru.ming13.gambit.ui.fragment.DeckRenamingFragment;
import ru.ming13.gambit.ui.intent.IntentException;
import ru.ming13.gambit.ui.intent.IntentExtras;
import ru.ming13.gambit.ui.util.FragmentOperator;


public class DeckRenamingActivity extends SherlockFragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operation);

		setUpFragment();
	}

	private void setUpFragment() {
		FragmentOperator.addFragment(this, buildFragment(), R.id.container_operation_fragment);
	}

	protected Fragment buildFragment() {
		return DeckRenamingFragment.newInstance(extractReceivedDeckUri());
	}

	private Uri extractReceivedDeckUri() {
		Uri deckUri = getIntent().getParcelableExtra(IntentExtras.DECK_URI);

		if (deckUri == null) {
			throw new IntentException();
		}

		return deckUri;
	}

	@Subscribe
	public void onDeckRenamed(DeckRenamedEvent deckRenamedEvent) {
		finish();
	}

	@Subscribe
	public void onDeckRenamingCancelled(DeckRenamingCancelledEvent deckRenamingCancelledEvent) {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getInstance().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getInstance().unregister(this);
	}
}
