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


import android.content.Intent;
import android.support.v4.app.Fragment;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.fragment.DeckOperationFragment;
import ru.ming13.gambit.ui.intent.IntentFactory;


public class DeckCreationActivity extends FragmentWrapperActivity implements DeckOperationFragment.FormCallback
{
	@Override
	protected Fragment buildFragment() {
		return DeckOperationFragment.newCreationInstance();
	}

	@Override
	public <Data> void onAccept(Data data) {
		Deck deck = (Deck) data;

		callCardsActivity(deck);

		finish();
	}

	private void callCardsActivity(Deck deck) {
		Intent intent = IntentFactory.createCardsListIntent(this, deck);

		startActivity(intent);
	}

	@Override
	public void onCancel() {
		finish();
	}
}
