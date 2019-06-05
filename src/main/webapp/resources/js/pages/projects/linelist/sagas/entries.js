import { call, put, take } from "redux-saga/effects";
import {
  fetchMetadataEntries,
  removeMetadataEntriesForField,
  saveMetadataEntryField
} from "../../../../apis/metadata/entry";
import { types as appTypes } from "../../../../redux/reducers/app";
import { actions, types } from "../reducers/entries";
import { FIELDS } from "../constants";
import { showNotification } from "../../../../modules/notifications";

/**
 * Fetch all the metadata entries required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* entriesLoadingSaga() {
  yield take(appTypes.INIT_APP);
  yield call(loadEntries);
}

function* loadEntries() {
  try {
    yield put(actions.load());
    const { data: entries } = yield call(
      fetchMetadataEntries,
      window.project.id
    );
    yield put(actions.success(entries));
  } catch (error) {
    yield put(actions.error(error));
  }
}

/**
 * Saga to handle updating the value of a metadata entry.
 * @returns {IterableIterator<*>}
 */
export function* entryEditedSaga() {
  // Always true, that way it can the listener is set up every time.
  while (true) {
    const { entry, label, field } = yield take(types.EDITED);
    yield call(
      saveMetadataEntryField,
      entry[FIELDS.sampleId],
      entry[field],
      label
    );
  }
}

/**
 * Saga to remove all metadata entries from a metadata field.
 * @returns {IterableIterator<any>}
 */
export function* removeFieldEntriesSaga() {
  while (true) {
    const { field } = yield take(types.REMOVE_DATA);
    const { message } = yield call(removeMetadataEntriesForField, field);
    showNotification({ text: message });
    yield call(loadEntries);
  }
}
