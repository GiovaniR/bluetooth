package com.example.bluetooth;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends Activity implements AdapterView.OnItemClickListener {
    List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    MyAdapter ma ;
    Button select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        getAllContacts(this.getContentResolver());
        ListView lv= (ListView) findViewById(R.id.lv);
        ma = new MyAdapter();
        lv.setAdapter(ma);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);
        // adding
        select = (Button) findViewById(R.id.button1);
        select.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                StringBuilder checkedcontacts= new StringBuilder();

                for(int i = 0; i < name1.size(); i++)

                {
                    if(ma.mCheckStates.get(i)==true)
                    {
                        checkedcontacts.append(name1.get(i).toString());
                        checkedcontacts.append("\n");

                    }
                    else
                    {

                    }


                }

                Toast.makeText(ContactsActivity.this, checkedcontacts, Toast.LENGTH_SHORT).show();
                //Save contacts on sharedprefferences in order to send them to micro
                SharedPreferences sharedPref = ContactsActivity.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Contacts",checkedcontacts.toString());
                editor.commit();
            }
        });


    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        ma.toggle(arg2);
    }

    public  void getAllContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name1.add(name);
            phno1.add(phoneNumber);
        }

        phones.close();
    }
    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
    {  private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView tv1,tv;
        CheckBox cb;
        MyAdapter()
        {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater)ContactsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name1.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi=convertView;
            if(convertView==null)
                vi = mInflater.inflate(R.layout.row, null);
            tv= (TextView) vi.findViewById(R.id.textView1);
            tv1= (TextView) vi.findViewById(R.id.textView2);
            cb = (CheckBox) vi.findViewById(R.id.checkBox1);
            tv.setText("Name :"+ name1.get(position));
            tv1.setText("Phone No :"+ phno1.get(position));
            cb.setTag(position);
            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);

            return vi;
        }
        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub

            mCheckStates.put((Integer) buttonView.getTag(), isChecked);
        }
    }
    /*@Override
    public long getSelectedItemId() {
        return super.getSelectedItemId();
    }

    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    ListView lv;
    Cursor cursorl;*/

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        /*cursorl = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        startManagingCursor(cursorl);

        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID};

        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,cursorl,from, to);

        setListAdapter(listAdapter);

        lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
