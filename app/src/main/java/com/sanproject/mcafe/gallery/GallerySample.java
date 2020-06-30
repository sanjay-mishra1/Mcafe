package com.sanproject.mcafe.gallery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.Touch.image_zoomer;

import java.io.File;
import java.util.ArrayList;

/**
 * The Class GallarySample.
 */
public class GallerySample extends Activity {

    /** The images. */
    private ArrayList<String> images;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);

        GridView gallery = (GridView) findViewById(R.id.galleryGridView);

        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (null != images && !images.isEmpty())
                {    Intent intent=new Intent(GallerySample.this, image_zoomer.class);

                 intent.putExtra("Image", images.get(position));
                 Log.e("Image","path is "+images.get(position)+" uri is "+Uri.parse( new File(images.get(position)).toString()));
                 startActivity(intent);
                 finish();
               // startActivity(new Intent(GallerySample.this,help_activity.class));
                }


            }
        });

    }

    /**
     * The Class ImageAdapter.
     */
    private class ImageAdapter extends BaseAdapter {

        /** The context. */
        private Activity context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext
         *            the local context
         */
        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                Display display;
                display = getWindowManager().getDefaultDisplay();
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView
                        .setLayoutParams(new GridView.LayoutParams((display.getWidth()/3-2), (display.getWidth()/3)));

            } else {
                picturesView = (ImageView) convertView;
            }

              Log.e("Images","getcount "+getCount()+" position "+position);
    try {
        Glide.with(context).applyDefaultRequestOptions(RequestOptions.skipMemoryCacheOf(true)).load(images.get(position)).

                apply(RequestOptions.centerCropTransform()).into(picturesView);
    }catch (Exception IndexOutOfBoundsException){}
           // Glide.with(context).load(images.get(position))
            //        .placeholder(R.drawable.ic_launcher).centerCrop()
           //         .into(picturesView);

            return picturesView;
        }

        /**
         * Getting All Images Path.
         *
         * @param activity
         *            the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data = 0, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaColumns.DATA,
                    MediaStore.Images.Media.DATE_MODIFIED };

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            if (cursor != null) {
                column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            }
            if (cursor != null) {
                column_index_folder_name = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
            }
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    absolutePathOfImage = cursor.getString(column_index_data);

                    listOfAllImages.add(0,absolutePathOfImage);
                }
            }
           // Collections.reverse(listOfAllImages);
            return listOfAllImages;
        }
    }
}