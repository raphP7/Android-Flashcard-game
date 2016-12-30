package com.auvert.raphaela.myproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private String authority;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] fragmentNames;

    public boolean downloadInProgress;
    public int timeForQuestion;

    private long idDeckInUse=-1;
    private String idDeckName;

    public void setIdDeckInUse(long id){
        this.idDeckInUse=id;
    }
    public long getIdDeckInUse(){
        return this.idDeckInUse;
    }
    public void setidDeckName(String idDeckName){
        this.idDeckName=idDeckName;
    }
    public String getidDeckName(){
        return this.idDeckName;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("id", idDeckInUse);
        outState.putString("idDeckName",idDeckName);
        outState.putBoolean("downloadInProgress",downloadInProgress);
        outState.putInt("timeForQuestion",timeForQuestion);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null){
            idDeckInUse=savedInstanceState.getLong("id");
            idDeckName=savedInstanceState.getString("idDeckName");
            downloadInProgress=savedInstanceState.getBoolean("downloadInProgress");
            timeForQuestion=savedInstanceState.getInt("timeForQuestion");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authority = getResources().getString(R.string.authority);

        if(savedInstanceState==null){
            Intent tmp = new Intent(this, BackgroundService.class);
            startService(tmp);
        }

        mTitle = mDrawerTitle = getTitle();
        fragmentNames = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, fragmentNames));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(),0);

                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

            selectItem(position);
        }
    }



    public void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new Question();
        String tag="question";

        switch (position) {
            case 0:
                fragment= new SelectDeck();
                tag="selectDeck";
                break;
            case 1:
                if(idDeckInUse==-1){
                    Toast toast = Toast.makeText(this, getString(R.string.SelectFirstDeck), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    fragment=new SelectDeck();
                    tag="selectDeck";
                    position=0;
                    break;
                }else{
                    fragment = new ListCards();
                    tag="listCards";
                }

                break;
            case 2:
                if(idDeckInUse==-1){
                    Toast toast = Toast.makeText(this, getString(R.string.SelectFirstDeck), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    fragment=new SelectDeck();
                    tag="selectDeck";
                    position=0;
                    break;
                }else{
                    fragment= new Question();
                    tag="question";
                }

                break;
            case 3:
                fragment= new Telecharger();
                tag="download";
                break;
            case 4:
                fragment= new Reglages();
                tag="reglages";
                break;
        }

        if(fragment==null){
            fragment=new Question();
            tag="question";
        }

        Bundle args = new Bundle();
        args.putInt("argQuestion", position);
        fragment.setArguments(args);


        FragmentManager fragmentManager = getFragmentManager();

        if(tag=="reglages" || tag=="download"){
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(tag).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        //fragmentManager.beginTransaction().add(fragment,tag).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);

        if(position<fragmentNames.length){
            setTitle(fragmentNames[position]);
        }

        mDrawerLayout.closeDrawer(mDrawerList);
    }


    public void setDrawerItemCheck(int position){
        mDrawerList.setItemChecked(position, true);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public boolean supprimerFromList(ListView list, String tab , String type) {

        long[] ids = list.getCheckedItemIds();
        if (ids.length == 0){
            Toast toast = Toast.makeText(this,getString(R.string.SelectAtLeast1)+" "+type, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false ;
        }

        for (long id : ids) {
            Log.d("supprimer id =", id + "");
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content")
                    .authority(authority)
                    .appendPath(tab);

            /* id du livre a supprimer a la fin de uri */

            ContentUris.appendId(builder, id);
            Uri uri = builder.build();

            int res = getContentResolver().delete(uri, null, null);
            Log.d("result of delete=", res + "");
        }
        return true;
    }


    public int getDificultyValue(int radioGroupChecked){

        int dificultySelect=-1;

        switch (radioGroupChecked){
            case R.id.Radiobutton0 :
                dificultySelect=0;
                break;
            case R.id.Radiobutton1 :
                dificultySelect=1;
                break;
            case R.id.Radiobutton2 :
                dificultySelect=2;
                break;
            case R.id.Radiobutton3 :
                dificultySelect=3;
                break;
            case R.id.Radiobutton4 :
                dificultySelect=4;
                break;
        }
        return  dificultySelect;
    }

    public void setCheckRadioGroup(RadioGroup radioGroup , int dificulty){

        switch (dificulty){
            case 0 :
                radioGroup.check(R.id.Radiobutton0);
                break;
            case 1 :
                radioGroup.check(R.id.Radiobutton1);
                break;
            case 2 :
                radioGroup.check(R.id.Radiobutton2);
                break;
            case 3 :
                radioGroup.check(R.id.Radiobutton3);
                break;
            case 4 :
                radioGroup.check(R.id.Radiobutton4);
                break;
        }
    }

}

