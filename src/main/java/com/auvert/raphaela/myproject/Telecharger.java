package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Raph on 23/12/2016.
 */

public class Telecharger extends Fragment {

    Button newValueButton;
    TextView newValueTxt;
    EditText newValueEnter;
    ProgressBar progressBar;
    RetainedFragment mWorkFragment;


    private FragmentManager mFM;

    public Telecharger() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_download, container, false);
        newValueButton= (Button) rootView.findViewById(R.id.newDeckValueButton);
        newValueTxt= (TextView) rootView.findViewById(R.id.newDeckValueTxt);
        newValueEnter= (EditText) rootView.findViewById(R.id.newDeckValueEnter);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);


        if(getActivity()!=null){
            if(getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)!=null){
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);
            }
        }


        if(!((MainActivity) getActivity()).downloadInProgress){
            newValueButton.setText(R.string.Download);
            newValueButton.setVisibility(View.VISIBLE);
            newValueTxt.setText(R.string.DownloadNewDeck);
            newValueTxt.setVisibility(View.VISIBLE);
            newValueEnter.setHint(R.string.Url);
            newValueEnter.setVisibility(View.VISIBLE);
        }else{
            newValueTxt.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            newValueButton.setVisibility(View.GONE);
            newValueEnter.setVisibility(View.GONE);
        }

        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getActivity()!=null){
                    if(getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)!=null){
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    }
                }

                ((MainActivity) getActivity()).downloadInProgress=true;
                mWorkFragment.doDownload(v);
            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("FRAG","onActivityCreated");

        FragmentManager fm =getFragmentManager();
        // Check to see if we have retained the worker fragment.
        mWorkFragment = (RetainedFragment)fm.findFragmentByTag("work");
        // If not retained (or first time running), we need to create it.
        if (mWorkFragment == null) {
            mWorkFragment = new RetainedFragment();
            // Tell it who it is working with.
            mWorkFragment.setTargetFragment(this, 0);
            fm.beginTransaction().add(mWorkFragment, "work").commit();
        }else{
            mWorkFragment.setTargetFragment(this, 0);
            synchronized (mWorkFragment){
                mWorkFragment.ViewFind=false;
            }
            Log.d("FRAG", "work already exist");
        }
    }


    public static class RetainedFragment extends Fragment {

        private String authority;

        Button newValueButton;
        TextView newValueTxt;
        EditText newValueEnter;
        ProgressBar progressBar;

        boolean mReady = false;
        boolean mQuiting = false;
        boolean ViewFind = false;

        ContentResolver resolver;
        DownloadFileFromURL asyncTask;
        String url;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("RetainFRAG","onCreate");
            setRetainInstance(true);
            authority=getResources().getString(R.string.authority);
            resolver = getActivity().getContentResolver();
        }


        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            synchronized (this) {
                findViews();
                Log.d("onActivityCread", "DU RETAINABLE FRAGMENT");
                mReady = true;
            }
        }

        public void findViews(){
            synchronized (this) {
                if (getTargetFragment() != null && getTargetFragment().getView()!=null) {
                    Log.d("retainFragment", "recuperation layoutss");


                    if(getActivity()!=null){
                        if(getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)!=null){
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getTargetFragment().getView().getWindowToken(),0);

                        }
                    }

                    newValueButton = (Button) getTargetFragment().getView().findViewById(R.id.newDeckValueButton);
                    newValueTxt = (TextView) getTargetFragment().getView().findViewById(R.id.newDeckValueTxt);
                    newValueEnter = (EditText) getTargetFragment().getView().findViewById(R.id.newDeckValueEnter);
                    progressBar = (ProgressBar) getTargetFragment().getView().findViewById(R.id.progressBar2);
                    ViewFind = true;
                }
            }
        }

        public void doDownload(View view) {
            Log.d("RetainFRAG","download");
            findViews();
            if(newValueEnter==null){
                return;
            }
            url=newValueEnter.getText().toString();
            if(url.length()<1){
                Toast toast = Toast.makeText(getActivity(), getString(R.string.PutURL), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            asyncTask= new DownloadFileFromURL();
            asyncTask.execute(url);
        }

        @Override
        public void onAttach(Context context) {
            synchronized (this) {
                getView();
            }
            super.onAttach(context);
        }


        @Override
        public void onDestroy() {
            synchronized (this) {
                mReady = false;
                mQuiting = true;
                ViewFind=false;
            }
            super.onDestroy();
        }


        @Override
        public void onDetach() {
            synchronized (this) {
                Log.d("RETAIN FRAG", "on detach");
                newValueButton=null;
                newValueTxt=null;
                newValueEnter=null;
                progressBar=null;
                mReady = false;
                ViewFind=false;
            }
            super.onDetach();
        }


        private class DownloadFileFromURL extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                Log.d("AsynkTask","on PRE EXECUTE");
                if(mReady && ViewFind ){
                    newValueTxt.setText("DOWNLOAD IN PROGRESS");
                    progressBar.setVisibility(View.VISIBLE);
                    newValueButton.setVisibility(View.GONE);
                    newValueEnter.setVisibility(View.GONE);
                }
                super.onPreExecute();
            }


            @Override
            protected String doInBackground(String... f_url) {
                int count;
                try {
                    URL url = new URL(f_url[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();

                    // this will be useful so that you can show a tipical 0-100%
                    // progress bar
                    long lenghtOfFile = conection.getContentLength();
                    if(lenghtOfFile==-1){
                        final URL uri=new URL(f_url[0]);
                        URLConnection ucon;
                        try
                        {
                            ucon=uri.openConnection();
                            ucon.connect();
                            final String contentLengthStr=ucon.getHeaderField("content-length");
                            lenghtOfFile = Long.parseLong(contentLengthStr);
                        }
                        catch(final Exception e1)
                        {
                        }
                    }

                    Log.d("DONWLOAD","SIZE : "+lenghtOfFile);

                    if(lenghtOfFile==-1){
                        newValueTxtInUI("DOWNLOAD IN PROGRESS\n "+"SIZE UNKNOW SERVER DONT COMUNICATE IT",newValueTxt);
                    }

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream
                    String path=getActivity().getFilesDir().getPath().toString();
                    File file = new File(path+"/downloadedfile.dbApprendre");
                    file.createNewFile();
                    PrintWriter pw = new PrintWriter(file);
                    pw.close();
                    OutputStream output = new FileOutputStream(file);

                    byte data[] = new byte[1024];

                    long total = 0;

                    long lastValue=0;
                    long result;
                    Log.d("AsynkTask","BEFORE READ");

                    while ((count = input.read(data)) != -1) {

                        if(lenghtOfFile!=-1){
                            total += count;
                            result = (total * 100) / lenghtOfFile;
                            if(lastValue!=result){
                                publishProgress(("") +result );
                                newValueTxtInUI("DOWNLOAD IN PROGRESS "+result+"%",newValueTxt);
                            }
                            lastValue=result;
                        }
                        // writing data to file
                        output.write(data, 0, count);
                    }

                    Log.d("AsynkTask","END READ");
                    output.flush();
                    output.close();
                    input.close();
                    toastInUI(getString(R.string.DownloadFinish));

                    inportInDataBase(file);

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                } finally {

                }

                return null;
            }

            /**
             * Updating progress bar
             * */
            protected void onProgressUpdate(String... progress) {

                synchronized (this) {

                    if(!ViewFind){
                        Log.d("RETAIN FRAG", "look for views ");
                        findViews();
                    }
                    if (mReady&& ViewFind) {
                        Log.d("RETAIN FRAG", "onProgressUpdate: ");
                        if(progressBar!=null){
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(Integer.parseInt(progress[0]));
                        }
                        if(newValueButton!=null){
                            newValueButton.setVisibility(View.GONE);
                        }
                        if(newValueEnter!=null){
                            newValueEnter.setVisibility(View.GONE);
                        }
                    }
                }
            }


            /**
             * After completing background task Dismiss the progress dialog
             * **/
            @Override
            protected void onPostExecute(String file_url) {
                super.onPostExecute(file_url);
                Log.d("AsynkTask","ON POST EXEC");

                synchronized (this) {
                    if (mReady && ViewFind) {
                        if(newValueButton!=null){
                            newValueButton.setText(R.string.Download);
                            newValueButton.setVisibility(View.VISIBLE);
                        }
                        if(progressBar!=null){
                            progressBar.setVisibility(View.GONE);
                        }
                        if(newValueTxt!=null){
                            newValueTxt.setText(R.string.DownloadNewDeck);
                            newValueTxt.setVisibility(View.VISIBLE);
                        }

                        if(newValueEnter!=null){
                            newValueEnter.setHint(R.string.Url);
                            newValueEnter.setVisibility(View.VISIBLE);
                        }
                    }
                    ((MainActivity) getActivity()).downloadInProgress=false;
                }
            }

            public int countLines(File aFile) throws IOException {
                LineNumberReader reader = null;
                try {
                    reader = new LineNumberReader(new FileReader(aFile));
                    while ((reader.readLine()) != null);
                    return reader.getLineNumber();
                } catch (Exception ex) {
                    return -1;
                } finally {
                    if(reader != null)
                        reader.close();
                }
            }


            public void inportInDataBase(File file){
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(file));
                } catch (FileNotFoundException e) {
                    Log.e("ERREUR LECTURE FILE","");
                    return;
                }

                int cmpFalseEntry=0;
                int cmpGoodEntry=0;
                String line;
                String [] tab;
                String dbName=null;
                String title=null;
                String question=null;
                String reponse=null;
                String niveau=null;

                int limit=5000;
                ContentValues[] valuesArray=new ContentValues[limit];

                int valuesArrayCmp=0;
                long idDeck=-1;
                try {

                    newValueTxtInUI("IMPORT IN PROGRESS",newValueTxt);
                    publishProgress(""+0);
                    int nbLines=countLines(file);

                    line=br.readLine();

                    tab=line.split(";");

                    if(tab.length>1 && tab[0].equals("bdApprendre")){

                        Log.d("LINE IN DB ->",""+tab[0]);
                        Log.d("LINE IN DB ->",""+tab[1]);

                        dbName=tab[1];
                        ContentValues values = new ContentValues();
                        values.put("nom",dbName);
                        values.put("time",0);

                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("content").authority(authority).appendPath("deck_table");
                        Uri uri = builder.build();

                        try{
                            uri = resolver.insert(uri,values);
                            idDeck = ContentUris.parseId(uri);
                            if(idDeck==-1){
                                toastInUI(getString(R.string.ErrorInsertNewDeck));
                                return;
                            }
                        }catch (android.database.sqlite.SQLiteConstraintException e){
                            toastInUI(getString(R.string.DeckNameAlExist));
                            return;
                        }

                    }else{
                        toastInUI(getString(R.string.MissingDBTitle));
                        return;
                    }

                    int total=0;
                    int result;
                    int lasvalue=0;
                    long valueDate= System.currentTimeMillis()-86400000;


                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("content").
                            authority(authority)
                            .appendPath("card_table");
                    Uri uri = builder.build();

                    while((line=br.readLine())!=null){
                        total++;
                        result=((int) ((total * 100) / nbLines));
                        if(lasvalue!=result){
                            publishProgress(("") + result);
                            newValueTxtInUI("IMPORT IN PROGRESS "+result+"%",newValueTxt);
                        }
                        lasvalue=result;

                        tab=line.split(";");

                        for(int i =0; i<tab.length-1;i++){

                            if(tab[i].equals("Title")){
                                title=tab[i+1];
                                i++;
                                continue;
                            }else if(tab[i].equals("Question")){
                                question=tab[i+1];
                                i++;
                                continue;
                            }else if(tab[i].equals("Answer")){
                                reponse=tab[i+1];
                                i++;
                                continue;
                            }else if(tab[i].equals("Lvl")){
                                niveau=tab[i+1];
                                i++;
                                continue;
                            }
                        }

                        if(title!=null && question!=null && reponse!=null && niveau!=null){

                            if(valuesArrayCmp>limit-1){
                                int tmpResult= resolver.bulkInsert(uri,valuesArray);
                                cmpGoodEntry+=tmpResult;
                                cmpFalseEntry=(limit-1)-tmpResult;
                                valuesArray= new ContentValues[limit];
                                valuesArrayCmp=0;
                            }

                            ContentValues values = new ContentValues();
                            values.put("title",title);
                            values.put("question",question);
                            values.put("reponse",reponse);
                            values.put("niveau",niveau);
                            values.put("deck_id",idDeck);
                            values.put("date",valueDate);

                            valuesArray[valuesArrayCmp]=values;
                            valuesArrayCmp++;
                        }else{
                            cmpFalseEntry++;
                        }

                    }
                    if(valuesArrayCmp>0){
                        int tmpResult= resolver.bulkInsert(uri,valuesArray);
                        cmpGoodEntry+=tmpResult;
                        cmpFalseEntry=valuesArrayCmp-tmpResult;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                String toShow="";

                if(cmpFalseEntry>0){

                    toShow=cmpFalseEntry+" "+"BAD ENTRIES";

                }
                if(cmpGoodEntry>0){
                    toShow+=" | "+cmpGoodEntry+" "+"GOOD ENTRIES";
                }

                toastInUI(toShow);


            }

            public void newValueTxtInUI(final String txt, final TextView view){
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(view!=null){
                                view.setText(txt);
                                view.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }

            public void toastInUI(final String txt){

                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getActivity(),txt, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });
                }
            }
        }
    }
}
