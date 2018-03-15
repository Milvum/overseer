package com.milvum.overseer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.milvum.overseer.databinding.ActivityMainBinding;
import com.milvum.overseer.models.Notification;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "Overseer";
    static final int SCAN_WALLET_REQUEST = 1;  // The request code

    // values obtained from ZXing Repo, but unsure how to extract them through library
    static final String ZXING = "com.google.zxing.client.android";
    static final String RESULT = "SCAN_RESULT";
    static final String ACTION = ZXING + ".SCAN";

    private ActivityMainBinding binding;
    private Handler handler;
    private boolean handlerBusy = false;
    protected final int NOTIFICATION_TIME = 5000; // in millis

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Button scan = findViewById(R.id.scan_button);
        scan.setOnClickListener(onClickLaunchQRCodeIntent);

        this.binding.executePendingBindings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_WALLET_REQUEST) {
            if (resultCode == RESULT_OK) {
                final String contents = data.getStringExtra(RESULT); //this is the result

                new BegForAddressTask(this, contents).execute();
            } else if (resultCode == RESULT_CANCELED) {

                this.showNotification(new Notification(false));

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.handler = new Handler(getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.handler.removeCallbacksAndMessages(null);
        this.handlerBusy = false;

        hideNotification(false);
    }

    private void showNotification(Notification notification) {
        this.binding.setNotification(notification);
        this.binding.executePendingBindings();

        if(this.handlerBusy) {
            return;
        }

        // Delete notification after some time :)
        this.handlerBusy = true;

        this.handler.removeCallbacksAndMessages(null);

        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideNotification(true);
                handlerBusy = false;
            }
        }, NOTIFICATION_TIME);
    }

    private void hideNotification(boolean render) {
        binding.setNotification(null);

        if (render) {
            binding.executePendingBindings();
        }
    }

    private final View.OnClickListener onClickLaunchQRCodeIntent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ACTION);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); //for Qr code, it's "QR_CODE_MODE" instead of "PRODUCT_MODE"
            intent.putExtra("SAVE_HISTORY", false); //this stops saving ur barcode in barcode scanner app's history

            try {
                startActivityForResult(intent, SCAN_WALLET_REQUEST);
            } catch(ActivityNotFoundException e) {
                Uri marketUri = Uri.parse("market://details?id=" + ZXING);
                Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                startActivity(marketIntent);
            }
        }
    };

    protected class BegForAddressTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<Context> contextRef;
        private Future<Void> begTask;
        private String address;

        protected BegForAddressTask(Context context, String address) {
            super();

            HttpClient client = new HttpClient(context);
            this.begTask = client.beg(address);
            this.address = address;
            this.contextRef = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                begTask.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.wtf(TAG, "You did not wait!", e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Context context = contextRef.get();
            if(context == null) {
                Log.w(TAG, "Cannot show toast as context no longer exists");
                return;
            }
            if(!result) {
                Log.w(TAG, "Beg was unsuccessful");
                return;
            }

            String msg = "Votingpass was requested for: " + address;
            Log.v(TAG, msg);

            showNotification(new Notification(true));
        }
    }
}
