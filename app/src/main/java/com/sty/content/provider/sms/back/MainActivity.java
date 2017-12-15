package com.sty.content.provider.sms.back;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.Normalizer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sty.content.provider.sms.back.bean.Contact;
import com.sty.content.provider.sms.back.util.QueryContactsUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSmsBack;
    private Button btnSmsInsert;
    private Button btnContactQuery;
    private Button btnContactInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setListeners();
    }

    private void initViews(){
        btnSmsBack = findViewById(R.id.btn_sms_back);
        btnSmsInsert = findViewById(R.id.btn_sms_insert);
        btnContactQuery = findViewById(R.id.btn_contact_query);
        btnContactInsert = findViewById(R.id.btn_contact_insert);
    }

    private void setListeners(){
        btnSmsBack.setOnClickListener(this);
        btnSmsInsert.setOnClickListener(this);
        btnContactQuery.setOnClickListener(this);
        btnContactInsert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sms_back:
                backSms();
                break;
            case R.id.btn_sms_insert:
                insertSms();
                break;
            case R.id.btn_contact_query:
                queryContacts();
                break;
            case R.id.btn_contact_insert:
                Intent intent = new Intent(this, ContactInsertActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //点击按钮后把所有的短信备份到Xml文件中去
    private void backSms(){
        try{
            //1.获取XmlSerializer的实例
            XmlSerializer serializer = Xml.newSerializer();
            //1.1创建文件
            String filePathFolder = Environment.getExternalStorageDirectory().getPath() + "/sty/";
            File tmpFile = new File(filePathFolder);
            if(!tmpFile.exists()){
                tmpFile.mkdirs();
            }
            String filePath = filePathFolder + "smsbackup.xml";
            Log.i("Tag", "file path: " + filePath);
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);

            //2.设置序列化器参数
            serializer.setOutput(fos, "utf-8");
            //3.写xml文档开头
            serializer.startDocument("utf-8", true);
            //4.写xml的根节点
            serializer.startTag(null, "smss");
            //5.构造uri
            Uri uri = Uri.parse("content://sms/");

            //6.由于短信的数据库已经通过内容提供者暴露出来，所以可以直接通过内容解析者查询
            Cursor cursor = getContentResolver().query(uri, new String[]{"address", "date", "body"}, null, null, null);
            while(cursor.moveToNext()){
                String address = cursor.getString(0);
                String date = cursor.getString(1);
                String body = cursor.getString(2);

                //7.写sms节点
                serializer.startTag(null, "sms");

                //8.写address节点
                serializer.startTag(null, "address");
                serializer.text(address);
                serializer.endTag(null, "address");

                //8.写date节点
                serializer.startTag(null, "date");
                serializer.text(date);
                serializer.endTag(null, "date");

                //8.写body节点
                serializer.startTag(null, "body");
                Log.i("Tag", "body:" + body);
                serializer.text(body.substring(0, 20));
                serializer.endTag(null, "body");

                serializer.endTag(null, "sms");
            }

            serializer.endTag(null, "smss");
            serializer.endDocument();
            fos.close();

            Toast.makeText(this, "短信备份成功", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void insertSms(){
        //1.由于短信的数据库已经通过内容提供者暴露出来，所以可以直接通过内容解析者查询
        Uri uri = Uri.parse("content://sms/");
        //2.创建ContentValues
        ContentValues values = new ContentValues();
        values.put("address", "95555");
        values.put("body", "您的银行卡余额为：-1000000");
        values.put("date", System.currentTimeMillis());

        Uri uri2 = getContentResolver().insert(uri, values);

        Toast.makeText(this, "插入短信为什么会失败:" + uri2, Toast.LENGTH_SHORT).show();
    }

    private void queryContacts() {
        List<Contact> queryContacts = QueryContactsUtils.queryContacts(this);
        for(Contact contact : queryContacts){
            Log.i("Tag", "contacts: " + contact);
        }
    }
}
