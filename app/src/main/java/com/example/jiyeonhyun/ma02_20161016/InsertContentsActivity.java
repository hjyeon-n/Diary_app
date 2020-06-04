package com.example.jiyeonhyun.ma02_20161016;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;
import com.example.jiyeonhyun.ma02_20161016.Parser.WeatherParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class InsertContentsActivity extends AppCompatActivity {

    final int ACTIVITY_CODE = 200;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 100;
    private final static String TAG = "InsertContentsActivity";

    private final static int REQUEST_TAKE_THUMBNAIL = 100;
    private static final int REQUEST_TAKE_PHOTO = 300;
    private final int GALLERY_CODE = 1111;

    private ImageView mImageView;
    private String mCurrentPhotoPath;

    DBHelper helper;
    EditText etContents, etWeather, etDate, etLocation, etFeeling, etMovie;
    Geocoder geoCoder;
    List<Address> addressList = null;
    double latitude, longitude;
    String baseTime, baseDate;
    String apiAddress, weather;
    String db_month, db_year, db_day;
    String color, type;
    int format;
    WeatherParser parser;
    int nx, ny;

    public static int TO_GRID = 0;

    private LocationManager locManager;
    Intent intent;

    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_contents);

        helper = new DBHelper(this);
        apiAddress = getResources().getString(R.string.weather_api_url);

        etContents = findViewById(R.id.etContents);
        etWeather = findViewById(R.id.etWeather);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etFeeling = findViewById(R.id.etFeeling);
        etMovie = findViewById(R.id.etMovie);
        color = "black";
        type = "normal";

        mImageView = (ImageView)findViewById(R.id.imageView);
        File path = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        geoCoder = new Geocoder(this);

        parser = new WeatherParser();

        /* 이미지 뷰를 클릭했을 때 외부 카메라 호출 */
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        /*날짜 선택 datepicker*/
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(); //현재 년도, 월, 일ty
                int year = cal.get (cal.YEAR);
                int month = cal.get (cal.MONTH);
                int date = cal.get (cal.DATE) ;

                DatePickerDialog dialog = new DatePickerDialog(InsertContentsActivity.this, listener, year, month, date);
                dialog.show();
            }
            private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    db_month = String.valueOf(monthOfYear + 1);
                    db_year = String.valueOf(year);
                    db_day = String.valueOf(dayOfMonth);
                    etDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                }
            };
        });

        /* 오늘의 기분을 선택할 수 있음 - Dialog 구현 */
        etFeeling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(InsertContentsActivity.this);

                // 기분을 %로 나눠서 다이얼로그에서 선택하도록 함 -> 기분은 arrays.xml에 정의해 둠.
                builder.setTitle("오늘의 기분 선택");
                builder.setSingleChoiceItems(R.array.feeling, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedIndex = i;
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] feelings = getResources().getStringArray(R.array.feeling);
                        etFeeling.setText(feelings[selectedIndex]);
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });

        /* 영화 선택 시 새로운 액티비티로 넘어감 */
        etMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(InsertContentsActivity.this, MovieActivity.class);
                startActivityForResult(intent, ACTIVITY_CODE);
            }
        });

        /* Fake Gps를 통해서 현재 위치를 받아옴 -> 지오코더를 사용하여서 주소로 변환 */
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(InsertContentsActivity.this);

                builder.setTitle("위치를 선택하세요");
                builder.setSingleChoiceItems(R.array.location, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedIndex = i;
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedIndex) {
                            case 1:
                                Criteria criteria = new Criteria();
                                criteria.setAccuracy(Criteria.NO_REQUIREMENT);
                                criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
                                criteria.setAltitudeRequired(false);
                                criteria.setCostAllowed(false);

                                //		String bestProvider = locManager.getBestProvider(criteria, true);
                                String bestProvider = LocationManager.PASSIVE_PROVIDER;

                                if (ActivityCompat.checkSelfPermission(InsertContentsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(InsertContentsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(InsertContentsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                                    return;
                                }

                                // 현재 위치 받아오기
                                Location lastLocation = locManager.getLastKnownLocation(bestProvider);

                                if (lastLocation != null) {
                                    latitude = lastLocation.getLatitude(); // 위도
                                    longitude = lastLocation.getLongitude(); // 경도

                                    // 위치 하나만 받아옴
                                    try {
                                        addressList = geoCoder.getFromLocation(latitude, longitude, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    if (addressList == null) {
                                        etLocation.setText("no result");
                                    }
                                    else {
                                        StringBuffer result = new StringBuffer();
                                        for (Address address : addressList) {
                                            result.append(String.format(address.getAddressLine(0).toString()));
                                        }
                                        etLocation.setText(result.toString());
                                    }
                                }
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });

         /*
           날씨를 받아오기 위해서 기상청 동네예보 API를 사용하였습니다.
           동네예보를 받아오기 위해서 현재 위치의 위경도를 받아왔고, 이를 기상청의 격자표대로 변환을 했습니다.
           예보시간을 맞추기 위해서 baseTime 코드 변환을 따로 진행했습니다.
           -> 이를 Dialog를 사용해서 구현하였고 파싱한 결과값을 DB COL_WEATHER에 저장하도록 하였습니다.
        */
        etWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(InsertContentsActivity.this);

                builder.setTitle("날씨를 선택하세요");
                builder.setSingleChoiceItems(R.array.weather, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedIndex = i;
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedIndex) {
                            case 1:
                                Criteria criteria = new Criteria();
                                criteria.setAccuracy(Criteria.NO_REQUIREMENT);
                                criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
                                criteria.setAltitudeRequired(false);
                                criteria.setCostAllowed(false);

                                //		String bestProvider = locManager.getBestProvider(criteria, true);
                                String bestProvider = LocationManager.PASSIVE_PROVIDER;

                                if (ActivityCompat.checkSelfPermission(InsertContentsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(InsertContentsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(InsertContentsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                                    return;
                                }

                                Location lastLocation = locManager.getLastKnownLocation(bestProvider);

                                if (lastLocation != null) {
                                    latitude = lastLocation.getLatitude(); // 위도
                                    longitude = lastLocation.getLongitude(); // 경도

                                    LatXLngY tmp = convertGRID_GPS(TO_GRID, latitude, longitude);

                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA); // baseDate를 맞추기 위해서 포맷팅하였습니다.
                                    SimpleDateFormat formatter2 = new SimpleDateFormat("HH", Locale.KOREA); // baseTime을 맞추기 위해서 포맷팅하였습니다. (24h)

                                    Date currentDate = new Date(); // 현재 일자 받아오기
                                    Date currentTime = new Date(); // 현재 시간 받아오기

                                    baseDate = formatter.format(currentDate); // 포맷에 맞춰주기
                                    String Time = formatter2.format(currentTime);

                                    Log.e("time", Time);
                                    // 예보 시간 맞춰주기 ex) 오전 6시 -> 0500
                                    format = (Integer.valueOf(Time) - 1) * 100;

                                    if (format == 0) {
                                        baseTime = "0000";
                                    }
                                    else if (format != 0 && format < 1000) {
                                        baseTime = '0' + String.valueOf(format);
                                        Log.e("abc", baseTime);
                                    }
                                    else {
                                        baseTime = String.valueOf((Integer.valueOf(Time) - 1) * 100);
                                    }

                                    Log.e("weather", baseTime);

                                    // 기상청 위경도 변환값
                                    nx = (int)tmp.x;
                                    ny = (int)tmp.y;

                                    new weatherAsyncTask().execute();
                                }
                                break;
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });
    }


    /* startActivityForResult의 반환값 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ACTIVITY_CODE:
                if (data != null) {
                    etMovie.setText(data.getStringExtra("title"));
                }
                else {
                    etMovie.setText("");
                }
                break;
            case GALLERY_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        // 선택한 이미지에서 비트맵 생성
                        InputStream in = getContentResolver().openInputStream(data.getData());
                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();
                        // 이미지 표시
                        mImageView.setImageBitmap(img);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_TAKE_THUMBNAIL:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mImageView.setImageBitmap(imageBitmap);
                }
                break;
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    setPic();
                }
                break;
        }
    }

    /* 저장 혹은 전체 목록으로 돌아가기 */
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_add:
                SQLiteDatabase db = helper.getWritableDatabase();

                String contents = etContents.getText().toString();
                String whether = etWeather.getText().toString();
                String date = etDate.getText().toString();
                String year = db_year;
                String month = db_month;
                String day = db_day;
                String location = etLocation.getText().toString();
                String feeling = etFeeling.getText().toString();
                String title = etMovie.getText().toString();

//			DB 메소드를 사용할 경우
                ContentValues row = new ContentValues();
                row.put(DBHelper.COL_IMG, mCurrentPhotoPath);
                row.put(DBHelper.COL_CONTENTS, contents);
                row.put(DBHelper.COL_WHETHER, whether);
                row.put(DBHelper.COL_DATE, date);
                row.put(DBHelper.COL_MONTH, month);
                row.put(DBHelper.COL_LOCATION, location);
                row.put(DBHelper.COL_MOVIE, title);
                row.put(DBHelper.COL_COLOR, color);
                row.put(DBHelper.COL_TYPE, type);
                row.put(DBHelper.COL_YEAR, db_year);
                row.put(DBHelper.COL_DAY, db_day);

                // 기분에 따라 사진이 달라지도록 조건문 설정
                if (feeling.equals("0%")) {
                    row.put(DBHelper.COL_FEELING, R.mipmap.battery_0);
                }
                else if (feeling.equals("25%")) {
                    row.put(DBHelper.COL_FEELING, R.mipmap.battery_25);
                }
                else if (feeling.equals("50%")) {
                    row.put(DBHelper.COL_FEELING, R.mipmap.battery_50);
                }
                else if (feeling.equals("75%")) {
                    row.put(DBHelper.COL_FEELING, R.mipmap.battery_75);
                }
                else if (feeling.equals("100%")) {
                    row.put(DBHelper.COL_FEELING, R.mipmap.battery_100);
                }

                long result = db.insert(DBHelper.TABLE_NAME, null, row);
                helper.close();

                // 저장 유무에 따라 Toast 띄우기
                if (result > 0) {
                    etContents.setText("");
                    etWeather.setText("");
                    etDate.setText("");
                    etLocation.setText("");
                    etFeeling.setText("");
                    etMovie.setText("");
                    Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    /* 옵션 메뉴 */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    //    옵션 메뉴에서 아이템을 선택했을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.textcolor:
                final AlertDialog.Builder builder = new AlertDialog.Builder(InsertContentsActivity.this);

                builder.setTitle("색깔을 선택하세요");
                builder.setSingleChoiceItems(R.array.color, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedIndex = i;
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedIndex) {
                            case 0:
                                etContents.setTextColor(Color.RED);
                                color = "red";
                                break;
                            case 1:
                                etContents.setTextColor(Color.BLUE);
                                color = "blue";
                                break;
                            case 2:
                                etContents.setTextColor(Color.BLACK);
                                color = "black";
                                break;
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.setCancelable(true);
                builder.show();
                break;
            case R.id.texttype:
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(InsertContentsActivity.this);

                builder2.setTitle("타입을 선택하세요");
                builder2.setSingleChoiceItems(R.array.type, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedIndex = i;
                    }
                });
                builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedIndex) {
                            case 0:
                                etContents.setTypeface(null, Typeface.NORMAL);
                                type = "normal";
                                break;
                            case 1:
                                etContents.setTypeface(null, Typeface.ITALIC);
                                type = "italic";
                                break;
                            case 2:
                                etContents.setTypeface(null, Typeface.BOLD);
                                type = "bold";
                                break;
                        }
                    }
                });
                builder2.setNegativeButton("취소", null);
                builder2.setCancelable(true);
                builder2.show();
                break;
            case R.id.now:
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String getTime = sdf.format(date);
                String text = etContents.getText().toString();
                etContents.setText(text + " " + getTime);
                break;
        }
        return true;
    }

    //    외부 카메라 호출
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.jiyeonhyun.ma02_20161016.fileprovider",
                        photoFile);
                Log.e("photoURI", photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("path", mCurrentPhotoPath);
        return image;
    }



    /* 위치 퍼미션 허가 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "위치 권한 획득!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "위치 권한 미획득!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }



    /* 날씨 API 활용 */
    class weatherAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(InsertContentsActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer response = new StringBuffer();

            try {
                StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + "ChBN8Iztazp439IdYhK3TXvmmN%2FvvxkswPES%2Fvm36WG%2FhkceAzC%2FU2zMyPXClZsNQ64ID7n7B6KAPYgaXaxB9g%3D%3D"); /*서비스 인증*/
                urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /*06시 발표(정시단위) -매시각 40분 이후 호출*/
                urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + nx); /*예보지점의 X 좌표값*/
                urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + ny); /*예보지점의 Y 좌표값*/

                URL url = new URL(urlBuilder.toString());
                Log.e(TAG, url.toString());
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");

                // response 수신
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } else {
                    Log.e(TAG, "API 호출 에러 발생 : 에러코드=" + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response.toString(); //xml 반환
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, result);

            weather = parser.parse(result); // xml을 파서에게 전달
            etWeather.setText(weather + "℃"); // 파서 결과값을 TEXT에 넣어주기

            progressDlg.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(InsertContentsActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }

    /* 위경도를 기상청 격자로 변환하는 코드 */
    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        }
        return rs;
    }

    class LatXLngY
    {
        public double lat;
        public double lng;

        public double x;
        public double y;
    }
}
