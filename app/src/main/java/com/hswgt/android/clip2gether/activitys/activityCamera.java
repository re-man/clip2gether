package com.hswgt.android.clip2gether.activitys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hswgt.android.clip2gether.CameraPreview;
import com.hswgt.android.clip2gether.HTTPMultipartRequest;
import com.hswgt.android.clip2gether.R;
import com.hswgt.android.clip2gether.Validate;

public class activityCamera extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private ImageButton capture, switchCamera;
    private Context myContext;
    private FrameLayout cameraPreview;
    private boolean cameraFront = false;
    private ProgressBar bar;
    public static Dialog dialogProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Zur sicherheit stellen wir die Orientierung nach dem Sensor wieder an
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);


        // Der Bildschirm soll anbleiben
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Ein Overlay für die Controllbuttton erstellen - Vorlage activity_camera_controll
        // Das sind der auslöse und switch camera button
        LayoutInflater controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.activity_camera_control, null);
        ViewGroup.LayoutParams layoutParamsControl
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        // Context merken, damit wir getApp.. oder getBase.. Context nicht immer aufrufen müssen
        myContext = this;

        // Init Funktion für das wiedergeben des Camera Preview
        initialize();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, getResources().getString(R.string.cameraActivity_error_noCamera), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, getResources().getString(R.string.cameraActivity_error_noFrontCamera), Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
                mCamera = Camera.open(findBackFacingCamera());
            } else {
                mCamera = Camera.open(findFrontFacingCamera());
            }
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }
    }

    public void initialize() {
        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(myContext, mCamera);

        cameraPreview.addView(mPreview);

        capture = (ImageButton) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (ImageButton) findViewById(R.id.button_switch);
        switchCamera.setOnClickListener(switchCameraListener);


    }

    OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //get the number of cameras
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa
                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, getResources().getString(R.string.cameraActivity_error_onlyOneCamera), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    public void chooseCamera() {
        //if the camera preview is the front
        int cameraId;
        if (cameraFront) {
            cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera

        if ((context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) || (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))){
            return true;
        }else{
            return false;
        }
    }


    private void showShareScreen(Bitmap shareImage, String attachID, String desc) {

        final Dialog dialog = new Dialog(activityCamera.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);
        dialog.show();
        //  dialog.setCanceledOnTouchOutside(true);
        // 	dialog.setTitle("Super, gib nur noch \ndeinen Text an die Welt ein");

        // set the custom dialog components - text, image and button

        ImageView image = (ImageView) dialog.findViewById(R.id.imageView2);
        image.setImageBitmap(shareImage);

        ImageView imgViewActivityShare_share = (ImageView) dialog.findViewById(R.id.shareDialogShare);
        ImageView imgViewActivityShare_cancel = (ImageView) dialog.findViewById(R.id.shareDialogCancel);

        Button buttonActivityShare_Take2fie = (Button) dialog.findViewById(R.id.ButtonShareTake2fie);

        TextView editText2 = (TextView) dialog.findViewById(R.id.editText);
        editText2.setText(desc);

        final String strActivityShare_attachID = attachID;
        final String strActivityShare_ShareURL = getResources().getString(R.string.urlConn_shareURL) + strActivityShare_attachID;
        imgViewActivityShare_share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.cameraActivity_dialog_share2fieText) + "\r\n\r\n" + strActivityShare_ShareURL);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        });

        buttonActivityShare_Take2fie.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        imgViewActivityShare_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        });

    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }


    private PictureCallback getPictureCallback() {
        final PictureCallback picture = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File file_ActivityCamera_pictureFile = getOutputMediaFile();
                final String str_ActivityCamera_picturefilefilepath = file_ActivityCamera_pictureFile.getPath();

                if (file_ActivityCamera_pictureFile == null) {
                    return;
                }
                try {

                    final BitmapFactory.Options optActivityCamera_options = new BitmapFactory.Options();

                    //  optActivityCamera_options.inSampleSize = 2;
                    optActivityCamera_options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                    optActivityCamera_options.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

                    Bitmap btmpActivityCamera_dataBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, optActivityCamera_options);
                    Bitmap btmpActivityCamera_dataBitmap_scaled = btmpActivityCamera_dataBitmap;
                    CameraInfo info = new CameraInfo();


                    // Das Bild was wir hochladen müssen, sollte skalliert 799 pixel breit seit
                    if (btmpActivityCamera_dataBitmap.getWidth() > 799) {
                        btmpActivityCamera_dataBitmap_scaled = scaleDown(btmpActivityCamera_dataBitmap, 799, true);
                    }

                    // Preview erstellen mit 40 % größe ( Landscape ) und 50 % Größe ( Potrait ) Orientierung
                    if ((getResources().getDisplayMetrics().widthPixels < getResources().getDisplayMetrics().heightPixels)) {
                        btmpActivityCamera_dataBitmap = scaleDown(btmpActivityCamera_dataBitmap, (getResources().getDisplayMetrics().heightPixels / 100 * 50), true);
                    } else {
                        btmpActivityCamera_dataBitmap = scaleDown(btmpActivityCamera_dataBitmap, (getResources().getDisplayMetrics().widthPixels / 100 * 40), true);
                    }


                    // wenn wir die Rückkamera haben und Potrait Modus sind - drehen wir das Bild um 180 Grad
                    if (!cameraFront && (getResources().getDisplayMetrics().widthPixels < getResources().getDisplayMetrics().
                            heightPixels)) {
                        btmpActivityCamera_dataBitmap = rotate(btmpActivityCamera_dataBitmap, 90);
                        btmpActivityCamera_dataBitmap_scaled = rotate(btmpActivityCamera_dataBitmap_scaled, 90);
                    }

                    // Wenn wir Portrait Modus sind drehen wir das Bild um 90 Grad gegen den Uhrzeiger
                    else if (getResources().getDisplayMetrics().widthPixels < getResources().getDisplayMetrics().
                            heightPixels) {
                        btmpActivityCamera_dataBitmap = rotate(btmpActivityCamera_dataBitmap, 270);
                        btmpActivityCamera_dataBitmap_scaled = rotate(btmpActivityCamera_dataBitmap_scaled, 270);
                    }




                    final Bitmap btmpActivityCamera_realImage = btmpActivityCamera_dataBitmap;

                    FileOutputStream fos = new FileOutputStream(file_ActivityCamera_pictureFile);
                    btmpActivityCamera_dataBitmap_scaled.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    // custom dialog
                    final Dialog dialog = new Dialog(activityCamera.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_upload);
                    dialog.setCanceledOnTouchOutside(true);

                    ImageView image = (ImageView) dialog.findViewById(R.id.image);
                    image.setImageBitmap(btmpActivityCamera_realImage);

                    final Button uploadDialogButtonOK = (Button) dialog.findViewById(R.id.shareDialogWeiters2Fie);
                    final ImageView uploadDialogButtonCancel = (ImageView) dialog.findViewById(R.id.uploadDialogButtonCancel);

                    final EditText UploadText = (EditText) dialog.findViewById(R.id.editText);

                    Bundle bndlActivityCamera_intentExtras = getIntent().getExtras();
                    final String username = bndlActivityCamera_intentExtras.getString("username");

                    dialog.show();

                    if (getResources().getDisplayMetrics().widthPixels < getResources().getDisplayMetrics().
                            heightPixels) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        }
                    });

                    uploadDialogButtonOK.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Leerzeichen entfernen
                            UploadText.setText(UploadText.getText().toString().trim());

                            if (!Validate.isValidDescription(UploadText.getText().toString())) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.validError_description), Toast.LENGTH_LONG).show();
                                return;

                            }

                            dialog.hide();
                            dialogProgress = new Dialog(activityCamera.this);
                            dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogProgress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialogProgress.setContentView(R.layout.dialog_upload_progress);
                            dialogProgress.setCanceledOnTouchOutside(false);
                            dialogProgress.show();


                            /************* Php script path ****************/
                            Thread background = new Thread(new Runnable() {


                                // After call for background.start this run method call
                                public void run() {
                                    String Serverresponse = "";

                                    try {

                                        String charset = "UTF-8";
                                        File uploadFile1 = new File(str_ActivityCamera_picturefilefilepath);
                                        String requestURL = getResources().getString(R.string.urlConn_uploadURL);
                                        try {
                                            HTTPMultipartRequest multipart = new HTTPMultipartRequest(requestURL, charset);

                                            multipart.addFormField("username", username);
                                            multipart.addFormField("photo-name", UploadText.getText().toString());

                                            multipart.addFilePart("contest-photo", uploadFile1);

                                            Serverresponse = multipart.finish();
                                            Serverresponse = Serverresponse.replace("\uFEFF", "");

                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }


                                        threadMsg(Serverresponse);
                                    } catch (Throwable t) {
                                        // just end the background thread
                                        Log.i("Animation", "Thread  exception " + t);
                                    }
                                }

                                private void threadMsg(String msg) {

                                    if (!msg.equals(null) && !msg.equals("")) {
                                        Message msgObj = handler.obtainMessage();
                                        Bundle b = new Bundle();
                                        b.putString("message", msg);
                                        msgObj.setData(b);
                                        handler.sendMessage(msgObj);
                                    }
                                }

                                // Define the Handler that receives messages from the thread and update the progress
                                private final Handler handler = new Handler() {

                                    public void handleMessage(Message msg) {

                                        String aResponse = msg.getData().getString("message");

                                        if ((null != aResponse)) {

                                            if (aResponse != "") {

                                                switch (aResponse) {
                                                    case "306":

                                                        Toast.makeText(
                                                                getBaseContext(),
                                                                getResources().getString(R.string.cameraActivity_error_uploadFailed),
                                                                Toast.LENGTH_SHORT).show();
                                                        dialogProgress.dismiss();
                                                        dialog.show();
                                                        break;

                                                    default:
                                                        dialogProgress.dismiss();
                                                        showShareScreen(btmpActivityCamera_realImage, aResponse, UploadText.getText().toString());
                                                }

                                            } else {
                                                Toast.makeText(
                                                        getBaseContext(),
                                                        getResources().getString(R.string.cameraActivity_error_uploadFailed),
                                                        Toast.LENGTH_SHORT).show();
                                                dialogProgress.dismiss();
                                                dialog.show();
                                            }
                                        } else {

                                            // ALERT MESSAGE
                                            Toast.makeText(
                                                    getBaseContext(),
                                                    getResources().getString(R.string.cameraActivity_error_connectionError),
                                                    Toast.LENGTH_SHORT).show();
                                            dialogProgress.dismiss();
                                            dialog.show();
                                        }

                                    }
                                };

                            });
                            // Start Thread


                            if (isConnected()) {         // Start Thread
                                background.start();  //After call start method thread called run Method

                            } else {

                                // ALERT MESSAGE
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.cameraActivity_error_offlineNotification),
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                    uploadDialogButtonCancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    OnClickListener captrureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.takePicture(null, null, mPicture);
        }
    };


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    //make picture and save to a folder
    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "clip2gether");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + "_IMG.jpg");

        return mediaFile;
    }


    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}