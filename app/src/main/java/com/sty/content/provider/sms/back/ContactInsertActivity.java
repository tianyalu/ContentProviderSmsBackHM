package com.sty.content.provider.sms.back;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sty.content.provider.sms.back.bean.Contact;
import com.sty.content.provider.sms.back.util.QueryContactsUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ContactInsertActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private Button btnInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_insert);

        initViews();
        setListeners();
    }

    private void initViews(){
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        btnInsert = findViewById(R.id.btn_insert);
    }

    private void setListeners(){
        btnInsert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_insert:
                insertContact();
                break;
            default:
                break;
        }
    }

    private void insertContact(){

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        //2.获取输入内容
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        //3.在插入联系人ID前先查询一下row_contact表中一共有多少条数据 +1得到插入数据的ID
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int count = cursor.getCount();
        int contact_id = count + 1;

        //4.先插入到row_contact表 contact_id
        ContentValues values = new ContentValues();
        values.put("contact_id", contact_id);
        getContentResolver().insert(uri, values);

        //5.把name据插入到data表
        ContentValues nameValues = new ContentValues();
        nameValues.put("data1", name);
        //☆☆☆插入数据时要告诉数据库属于第几条联系人和数据类型
        nameValues.put("raw_contact_id", contact_id);
        nameValues.put("mimetype", "vnd.android.cursor.item/name");
        getContentResolver().insert(dataUri, nameValues);


        //6.把name据插入到data表
        ContentValues phoneValues = new ContentValues();
        phoneValues.put("data1", phone);
        //☆☆☆插入数据时要告诉数据库属于第几条联系人和数据类型
        phoneValues.put("raw_contact_id", contact_id);
        phoneValues.put("mimetype", "vnd.android.cursor.item/phone_v2");
        getContentResolver().insert(dataUri, phoneValues);

        //7.把name据插入到data表
        ContentValues emailValues = new ContentValues();
        emailValues.put("data1", email);
        //☆☆☆插入数据时要告诉数据库属于第几条联系人和数据类型
        emailValues.put("raw_contact_id", contact_id);
        emailValues.put("mimetype", "vnd.android.cursor.item/email_v2");
        getContentResolver().insert(dataUri, emailValues);

        Toast.makeText(this, "插入联系人成功" , Toast.LENGTH_SHORT).show();
    }

}
