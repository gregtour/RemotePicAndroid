package greg.remotepic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

import static android.R.attr.button;
import android.util.Log;

public class RemotePicActivity extends AppCompatActivity {
    public static String RP = "RemotePicActivity";

    String statusString = "Undefined";

    public Button pushButton;
    public ImageView imageTarget;
    public EditText inputField;
    public TextView statusText;

    public byte[] readStream(InputStream in) {
        byte BUFFER[] = new byte[2048];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.reset();

        int readAmount;
        try {
            while ((readAmount = in.read(BUFFER, 0, BUFFER.length)) > 0) {
                stream.write(BUFFER, 0, readAmount);
            }
            stream.close();
            in.close();
        } catch (Exception e) {
            Log.v(RP, e.getMessage());
            statusString = "Stream error.";
        } finally {
            readAmount = 0;
        }


        return stream.toByteArray();
    }

    // push me button
    private final OnClickListener ButtonClickHandler = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // gray out the button
            pushButton.setClickable(false);

            // get the string value
            String textValue = inputField.getText().toString();

            // attempt to set the image
            setImage(textValue);
        }
    };

    // create the RemotePic activity!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_pic);

        inputField = (EditText)findViewById(R.id.editField);
        pushButton = (Button)findViewById(R.id.loadButton);
        imageTarget = (ImageView)findViewById(R.id.imageDest);
        statusText = (TextView)findViewById(R.id.statusBar);

        statusText.setText("Started");

        pushButton.setOnClickListener(ButtonClickHandler);
    }

    // try to set the image based on a string...
    protected void setImage(String imageString) {
        statusText.setText("Setting image " + imageString + ".jpg...");
        ImageLoaderTask myTask = new ImageLoaderTask();
        myTask.execute(imageString);
    }

    private class ImageLoaderTask extends AsyncTask<String, String, String>
    {
        Bitmap bitmap;

        public ImageLoaderTask() {
            super();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String file = params[0];
            byte[] bytes = null;
            statusString = "";

            bitmap = null;
            try {
                String path = "http://i.imgur.com/" + file + ".jpg";
                //path = "http://brainplex.net/res/art_239.png";
                Log.v(RP, "Opening path " + path);

                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = new BufferedInputStream(connection.getInputStream());

                    bitmap = BitmapFactory.decodeStream(in);

                    //bytes = readStream(in);
                    //bitmap = BitmapFactory.decodeByteArray(bytes, 0, 0);
                } catch (Exception e) {
                    Log.v(RP, "BMP Exception: " + e.getMessage());
                    statusString = "Image or connection problem.";
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                Log.v(RP, "whoops url or request problem");
                statusString = "URL or connection problem.";
            }

            if (statusString.equals("")) {
                statusString = "Success";
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            imageTarget.setImageBitmap(bitmap);

            statusText.setText(statusString);
            pushButton.setClickable(true);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
