package com.destan.epiandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



public class MainActivity extends AppCompatActivity{
    final Context context = this;
    private ViewFlipper vf, vfContent;
    private EditText username, password;
    private boolean connected;
    private TextView name, cycle, logTime, messages;
    private ImageView viewProfile;
    private RelativeLayout blank;
    private int m_semester;
    private Calendar planning;
    private String token, urlAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        token = "";
        urlAPI = "https://epitech-api.herokuapp.com/";
        connected = false;
        vf = (ViewFlipper) findViewById(R.id.vf);
        vfContent = (ViewFlipper) findViewById(R.id.vfContent);
        blank = (RelativeLayout) findViewById(R.id.contentBlank);
        Button login = (Button) findViewById(R.id.login);
        Button view_planning = (Button) findViewById(R.id.button_show_planning);
        Button view_all_marks = (Button) findViewById(R.id.button_show_all_marks);
        Button view_all_projects = (Button) findViewById(R.id.button_show_all_projects);
        Button view_my_modules = (Button) findViewById(R.id.button_show_all_modules);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        name = (TextView) findViewById(R.id.userNameText);
        cycle = (TextView) findViewById(R.id.userCourseText);
        viewProfile = (ImageView) findViewById(R.id.userPhoto);
        logTime = (TextView) findViewById(R.id.userLogTime);
        messages = (TextView) findViewById(R.id.userMessages);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login();
            }
        });
        view_planning.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                planning = Calendar.getInstance(Locale.FRANCE);
                Planning();
            }
        });
        view_all_marks.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetAllModules(m_semester);
            }
        });
        view_my_modules.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetMyModules(m_semester);
            }
        });
        view_all_projects.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetAllProjects();
            }
        });
    }

    private void affError(String... args)
    {
        disconnect();
        AlertDialog error = new AlertDialog.Builder(this).create();
        error.setTitle(args[0]);
        error.setMessage(args[1]);
        error.setButton(DialogInterface.BUTTON_POSITIVE, args[2], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        error.show();
    }

    private void disconnect(){
        if (connected) {
            token = "";
            m_semester = -1;
            name.setText("");
            cycle.setText("");
            username.setText("");
            password.setText("");
            vf.setDisplayedChild(0);
            vfContent.setDisplayedChild(0);
            blank.removeAllViewsInLayout();
            connected = false;
        }
    }

    private LinearLayout getSemesterMenu2(int selected)
    {
        LinearLayout button_semester = new LinearLayout(this);
        button_semester.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button_semester.setWeightSum(m_semester / 3 + (m_semester % 3 == 0 ? 0 : 1));
        button_semester.setOrientation(LinearLayout.VERTICAL);
        button_semester.setId(R.id.button_semester_id);
        for (int i = 1; i <= m_semester; i = i + 3) {

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.weight = 1;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setWeightSum(3);
            row.setLayoutParams(param);

            for (int j = i; j <= i + 2 && j <= m_semester; j++) {
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.weight = 1;
                Button button = new Button(this);
                if (j == selected) {
                    button.setBackgroundColor(0xFF05B8CC);
                    button.setClickable(false);
                }
                else
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blank.removeAllViewsInLayout();
                            GetMyModules((int) (v.getTag()) - R.id.semester_id);
                        }
                    });
                button.setWidth(0);
                button.setLayoutParams(rlp);
                String textButton = "Semestre " + j;
                button.setText(textButton);
                button.setTextSize(13);
                button.setTag(R.id.semester_id + j);
                row.addView(button);
            }
            button_semester.addView(row);
        }
        return (button_semester);
    }


    private LinearLayout getSemesterMenu(int selected)
    {
        LinearLayout button_semester = new LinearLayout(this);
        button_semester.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button_semester.setWeightSum(m_semester / 3 + (m_semester % 3 == 0 ? 0 : 1));
        button_semester.setOrientation(LinearLayout.VERTICAL);
        button_semester.setId(R.id.button_semester_id);
        for (int i = 1; i <= m_semester; i = i + 3) {

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.weight = 1;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setWeightSum(3);
            row.setLayoutParams(param);

            for (int j = i; j <= i + 2 && j <= m_semester; j++) {
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.weight = 1;
                Button button = new Button(this);
                if (j == selected) {
                    button.setBackgroundColor(0xFF05B8CC);
                    button.setClickable(false);
                }
                else
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blank.removeAllViewsInLayout();
                            GetAllModules((int) (v.getTag()) - R.id.semester_id);
                        }
                    });
                button.setWidth(0);
                button.setLayoutParams(rlp);
                String textButton = "Semestre " + j;
                button.setText(textButton);
                button.setTextSize(13);
                button.setTag(R.id.semester_id + j);
                row.addView(button);
            }
            button_semester.addView(row);
        }
        return (button_semester);
    }

    private  void setProjects(JSONArray res) {
        int j = 0;
        for (int i = 0; i < res.length(); i++) {
            if (res.optJSONObject(i).optString("registered").equals("1") && !res.optJSONObject(i).optString("project").equals("null")) {
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout rl = new RelativeLayout(this);
                if (j != 0)
                    rlp.addRule(RelativeLayout.BELOW, 1 + j - 1);
                else
                    rlp.topMargin = 100;
                rl.setId(1 + j);
                rl.setLayoutParams(rlp);
                TextView textProj = new TextView(this);
                textProj.setText(res.optJSONObject(i).optString("project"));
                textProj.setTextSize(15);
                rl.addView(textProj);
                blank.addView(rl);
                j++;
            }
        }
    }


    private void GetAllProjects()
    {

        try {
            Object obj = new RequestClass().execute(urlAPI + "projects?token=" + token, "GET", "").get(5000, TimeUnit.MILLISECONDS);
            if (obj.getClass() == JSONArray.class)
                setProjects((JSONArray) obj);
            vfContent.setDisplayedChild(1);
        } catch (InterruptedException | ExecutionException | TimeoutException e1) {
            e1.printStackTrace();
        }
    }

    private void GetMyModules(int semester)
    {
        int i = 0;
        String credit;

        try {
            JSONObject result = (JSONObject)new RequestClass().execute(urlAPI + "modules?token=" + token, "GET", "").get(5000, TimeUnit.MILLISECONDS);
            JSONArray modules = result.optJSONArray("modules");
            blank.addView(getSemesterMenu2(semester));
            for (int j = 0; j < modules.length(); j++){
                JSONObject module = modules.optJSONObject(j);
                if (semester == module.optInt("semester"))
                {
                    credit = module.optString("credits");
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (i != 0)
                        rlp.addRule(RelativeLayout.BELOW, R.id.semester_id + i - 1);
                    else
                        rlp.addRule(RelativeLayout.BELOW, R.id.button_semester_id);

                    RelativeLayout rl = new RelativeLayout(this);
                    rl.setBackgroundColor(Color.DKGRAY);
                    if (i != 0)
                        rl.setPadding(0, 3, 0, 0);
                    rl.setId(R.id.semester_id + i);
                    rl.setLayoutParams(rlp);

                    RelativeLayout cell = new RelativeLayout(this);
                    cell.setBackgroundColor(Color.WHITE);
                    cell.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    TextView textModule = new TextView(this);
                    textModule.setText(module.optString("title"));
                    textModule.setTextSize(13);

                    RelativeLayout.LayoutParams paramText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    paramText.topMargin = 35;

                    TextView textCredit = new TextView(this);
                    credit = "Credit disponible: " + credit;
                    textCredit.setText(credit);
                    textCredit.setTextSize(13);
                    textCredit.setGravity(View.FOCUS_RIGHT);
                    textCredit.setLayoutParams(paramText);

                    cell.addView(textCredit);
                    cell.addView(textModule);
                    rl.addView(cell);
                    blank.addView(rl);
                    i++;
                }
            }
            vfContent.setDisplayedChild(1);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            affError("Error", "Connection timed out.", "OK");
        }
    }

    private void GetAllModules(int semester)
    {
        int i = 0;
        String grade;

        try {
            JSONObject result = (JSONObject)new RequestClass().execute(urlAPI + "modules?token=" + token, "GET", "").get(5000, TimeUnit.MILLISECONDS);
            JSONArray modules = result.optJSONArray("modules");
            blank.addView(getSemesterMenu(semester));
            for (int j = 0; j < modules.length(); j++){
                JSONObject module = modules.optJSONObject(j);
                if (semester == module.optInt("semester") && !(grade = module.optString("grade")).equals("-"))
                {
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (i != 0)
                        rlp.addRule(RelativeLayout.BELOW, R.id.semester_id + i - 1);
                    else
                        rlp.addRule(RelativeLayout.BELOW, R.id.button_semester_id);

                    RelativeLayout rl = new RelativeLayout(this);
                    rl.setBackgroundColor(Color.DKGRAY);
                    if (i != 0)
                        rl.setPadding(0, 3, 0, 0);
                    rl.setId(R.id.semester_id + i);
                    rl.setLayoutParams(rlp);

                    RelativeLayout cell = new RelativeLayout(this);
                    cell.setBackgroundColor(Color.WHITE);
                    cell.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    TextView textModule = new TextView(this);
                    textModule.setText(module.optString("title"));
                    textModule.setTextSize(13);

                    RelativeLayout.LayoutParams paramText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    paramText.topMargin = 35;

                    TextView textGrade = new TextView(this);
                    grade = "Grade : " + grade;
                    textGrade.setText(grade);
                    textGrade.setTextSize(13);
                    textGrade.setGravity(View.FOCUS_RIGHT);
                    textGrade.setLayoutParams(paramText);

                    cell.addView(textGrade);
                    cell.addView(textModule);
                    rl.addView(cell);
                    blank.addView(rl);
                    i++;
                }
            }
            vfContent.setDisplayedChild(1);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            affError("Error", "Connection timed out.", "OK");
        }
    }

    private void getInfoUser()
    {

        try {
            JSONObject result = (JSONObject)new RequestClass().execute(urlAPI + "user?token=" + token + "&user=" + URLEncoder.encode(username.getText().toString(), "UTF-8"), "GET", "").get(5000, TimeUnit.MILLISECONDS);
            name.setText(result.optString("title"));
            m_semester = result.optInt("semester");
            String logText = "time active : " + result.optJSONObject("nsstat").optString("active") + "h";
            logTime.setText(logText);
            JSONObject m = ((JSONArray)new RequestClass().execute(urlAPI + "messages?token=" + token, "GET", "").get(5000, TimeUnit.MILLISECONDS)).optJSONObject(0);
            String mes = m.optString("title");
            messages.setText(Html.fromHtml(mes));
            cycle.setText(result.optJSONArray("gpa").getJSONObject(0).optString("cycle"));
            new ImageClass(viewProfile).execute(result.optString("picture"));
            vf.setDisplayedChild(1);
        } catch (InterruptedException | ExecutionException | UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            affError("Error", "Connection timed out.", "OK");
        }
    }

    private LinearLayout getPlanningMenu()
    {
        LinearLayout button_planning = new LinearLayout(this);
        button_planning.setOrientation(LinearLayout.HORIZONTAL);
        button_planning.setWeightSum(3);
        button_planning.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.weight = 1;
        Button button = new Button(this);
        button.setBackgroundColor(0xFF05B8CC);
        button.setClickable(false);
        String textButton;
        textButton = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(planning.getTime());
        button.setText(textButton);
        button.setTextSize(10);
        Button prev = new Button(this);
        prev.setText(R.string.button_prev_planning_menu);
        prev.setTextSize(10);
        Button next = new Button(this);
        next.setText(R.string.button_next_planning_menu);
        next.setTextSize(10);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blank.removeAllViewsInLayout();
                planning.add(Calendar.DAY_OF_MONTH, -1);
                Planning();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blank.removeAllViewsInLayout();
                planning.add(Calendar.DAY_OF_MONTH, +1);
                Planning();
            }
        });
        button.setWidth(0);
        button.setLayoutParams(rlp);
        prev.setWidth(0);
        prev.setLayoutParams(rlp);
        next.setWidth(0);
        next.setLayoutParams(rlp);
        button_planning.addView(prev);
        button_planning.addView(button);
        button_planning.addView(next);
        return (button_planning);
    }

    private void setPlanning(final JSONArray result){
        List<RelativeLayout> list = new ArrayList<>();
        for (int i = 0; i < result.length(); i++)
        {
            if (result.optJSONObject(i).optString("module_registered").equals("true")) {
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout rl = new RelativeLayout(this);
                if (list.size() != 0)
                    rlp.addRule(RelativeLayout.BELOW, 1 + list.size() - 1);
                else
                    rlp.topMargin = 100;
                rl.setId(1 + list.size());
                rl.setLayoutParams(rlp);
                Button textPlanning = new Button(this);

                textPlanning.setGravity(0x03);
                String beg = result.optJSONObject(i).optString("start");
                beg = beg.substring(11, beg.length() - 3) + "-";
                String end = result.optJSONObject(i).optString("end");
                end = end.substring(11, end.length() - 3) + " ";
                String planningTmpText = beg + end + result.optJSONObject(i).optString("acti_title");
                textPlanning.setText(planningTmpText);
                if (result.optJSONObject(i).optString("event_registered").equals("registered") && result.optJSONObject(i).optString("past").equals("true") && result.optJSONObject(i).optBoolean("allow_token")) {
                    final int finalI = i;
                    textPlanning.setOnClickListener(new View.OnClickListener() {
                        String scolarYear = result.optJSONObject(finalI).optString("scolaryear");
                        String codemodule = result.optJSONObject(finalI).optString("codemodule");
                        String codeinstance = result.optJSONObject(finalI).optString("codeinstance");
                        String codeacti = result.optJSONObject(finalI).optString("codeacti");
                        String codeevent = result.optJSONObject(finalI).optString("codeevent");
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder dial = new AlertDialog.Builder(context);
                            final EditText et = new EditText(context);
                            dial.setView(et);
                            final String s = scolarYear;
                            final String cmod = codemodule;
                            final String cins = codeinstance;
                            final String cact = codeacti;
                            final String ceve = codeevent;
                            DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int b)
                                {
                                    String tok = et.getText().toString();
                                    try {
                                        new RequestClass().execute(urlAPI + "token", "POST", "token=" + token + "&scolaryear=" + s + "&codemodule=" + cmod + "&codeinstance="+ cins + "&codeacti=" + cact + "&codeevent=" + ceve + "&tokenvalidationcode=" + tok).get(5000, TimeUnit.MILLISECONDS);
                                        Planning();
                                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                        e.printStackTrace();
                                    }
                                    d.dismiss();
                                }
                            };
                            dial.setCancelable(false).setPositiveButton("OK", l);
                            AlertDialog alertDialog = dial.create();
                            alertDialog.show();
                        }
                    });
                }
                else
                    textPlanning.setBackgroundColor(Color.TRANSPARENT);
            textPlanning.setTextSize(15);
                textPlanning.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rl.addView(textPlanning);
                blank.addView(rl);
                list.add(rl);

            }
        }
    }

    private void Planning() {
        try {
            Object obj = new RequestClass().execute(urlAPI + "planning?token=" + token + "&start=" + new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(planning.getTime()) + "&end=" + new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(planning.getTime()), "GET", "").get(5000, TimeUnit.MILLISECONDS);
            blank.addView(getPlanningMenu());
            if (obj.getClass() == JSONArray.class)
                setPlanning((JSONArray) obj);
            vfContent.setDisplayedChild(1);
        } catch (Exception e){
            e.printStackTrace();
    }
    }

    private void Login(){
        if (username.getText().length() > 0 && password.getText().length() > 0) {
            String user = username.getText().toString();
            String pass = password.getText().toString();
            try {
                JSONObject result = (JSONObject)new RequestClass().execute(urlAPI + "login", "POST", "login=" + URLEncoder.encode(user, "UTF-8") + "&password=" + URLEncoder.encode(pass, "UTF-8")).get(5000, TimeUnit.MILLISECONDS);
                if (result == null)
                    throw new IOException();
                token = result.optString("token");
                connected = true;
                getInfoUser();
            } catch (InterruptedException | ExecutionException | UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                affError("Error", "Connection timed out.", "OK");
            } catch (IOException e) {
                affError("Error", "Cannot log to Epitech.\nCheck your username and password.", "OK");
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (connected) {
            switch (item.getItemId()) {
                case R.id.Planning:
                    blank.removeAllViewsInLayout();
                    planning = Calendar.getInstance();
                    Planning();
                    return true;
                case R.id.Marks:
                    blank.removeAllViewsInLayout();
                    GetAllModules(m_semester);
                    return true;
                case R.id.Project:
                    blank.removeAllViewsInLayout();
                    GetAllProjects();
                    return true;
                case R.id.Module:
                    blank.removeAllViewsInLayout();
                    GetMyModules(m_semester);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
