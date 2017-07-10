package com.ninety8point6.droptoken.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.ninety8point6.droptoken.R;
import com.ninety8point6.droptoken.concepts.ResponseCallback;

/**
 * A {@link DialogFragment} for handling player selection.
 */
public class PlayerSelectionDialogFragment extends DialogFragment {

    private ResponseCallback<Integer, Throwable> mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_player_selection)
               .setItems(R.array.player_selection_list, (dialogInterface, i) -> mCallback.onSuccess(i));

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * A setter for adding a {@link ResponseCallback} to the {@link DialogFragment} for handling the
     * selection results.
     *
     * @param callback a {@link ResponseCallback} for handling the selection results
     */
    protected void setOnPlayerSelectedListener(final ResponseCallback<Integer, Throwable> callback) {
        mCallback = callback;
    }

    /**
     * A static creator for setting up the {@link ResponseCallback} and other {@link Dialog} settings.
     * A {@link DialogFragment} cannot expose a non-default constructor.
     *
     * @param callback a {@link ResponseCallback} for handling the selection results
     *
     * @return a newly created {@link PlayerSelectionDialogFragment}, ready to be shown
     */
    public static PlayerSelectionDialogFragment create(final ResponseCallback<Integer, Throwable> callback) {

        Preconditions.checkArgument(callback != null);

        final PlayerSelectionDialogFragment fragment = new PlayerSelectionDialogFragment();
        fragment.setOnPlayerSelectedListener(callback);
        return fragment;
    }
}
