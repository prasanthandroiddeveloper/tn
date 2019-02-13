package com.tripnetra.tripnetra;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class LoadingDialog extends Dialog {

     public LoadingDialog(Context context) {
        super (context);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.activity_customdialog);

        Glide.with(context).load(R.drawable.loadingif).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into((ImageView)findViewById(R.id.loadWV));

    }

}
