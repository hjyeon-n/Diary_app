package com.example.jiyeonhyun.ma02_20161016;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jiyeonhyun.ma02_20161016.Adapter.MovieAdapter;
import com.example.jiyeonhyun.ma02_20161016.DTO.MovieDTO;
import com.example.jiyeonhyun.ma02_20161016.Parser.MovieParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {

    public static final String TAG = "MovieActivity";
    final int INSERT_CODE = 200;

    EditText etTarget;
    ListView lvList;
    String apiAddress;
    String query;

    MovieAdapter adapter;
    ArrayList<MovieDTO> resultList;
    MovieParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        etTarget = (EditText)findViewById(R.id.etTarget);
        lvList = (ListView)findViewById(R.id.lvList);

        resultList = new ArrayList();
        adapter = new MovieAdapter(this, R.layout.movie_item, resultList);
        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.api_url);
        parser = new MovieParser();

        /* 리스트 항목을 클릭하면 영화 제목을 받아옴 */
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDTO movie = resultList.get(position);
                String title = movie.getTitle();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", title);

                setResult(INSERT_CODE, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearch:
                query = etTarget.getText().toString();
                Log.d(TAG, query);
                new MovieAsyncTask().execute();
                break;
        }
    }

    /* 추가 점수: 네이버 영화 API를 사용해서 영화제목, 감독, 개봉일자 가지고 오기 */
    class MovieAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MovieActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer response = new StringBuffer();

            // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
            String clientId = getResources().getString(R.string.client_id);
            String clientSecret = getResources().getString(R.string.client_secret);

            try {
                String apiURL = apiAddress + URLEncoder.encode(query, "UTF-8");
                Log.d(TAG, apiURL);
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // response 수신
                Log.d(TAG, url.toString());
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

        /* 파서값을 리스트뷰에 넘겨줌 */
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);

            resultList = parser.parse(result); // xml을 파서에게 전달
            adapter.setList(resultList); // 결과값으로 parsing한 결과를 담고 있는 arrayList를 가지고 옴
            adapter.notifyDataSetChanged(); // getView 호출

            progressDlg.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(MovieActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }
}
