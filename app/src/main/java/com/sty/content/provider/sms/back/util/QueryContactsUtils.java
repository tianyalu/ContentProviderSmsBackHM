package com.sty.content.provider.sms.back.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sty.content.provider.sms.back.bean.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven.T on 2017/12/15/0015.
 */

public class QueryContactsUtils {

    public static List<Contact> queryContacts(Context context){
        //0.创建一个集合
        List<Contact> contactLists = new ArrayList<>();

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            //1.先查询row_contacts表的contact_id列 可以得知一共有几条联系人
            String contact_id = cursor.getString(0);
            Log.i("Tag", "contact_id: " + contact_id);

            if(contact_id != null) {
                Contact contact = new Contact();
                contact.setId(contact_id);

                //2.根据contact_id去查询data表，查询data1列和mimetype_id
                //※ 在查询data表时，其实查询的是view_data的视图(view_data是由data表和mimetype表的组合)

                Cursor dataCursor = context.getContentResolver().query(dataUri, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{contact_id}, null);
                while (dataCursor.moveToNext()) {
                    String data1 = dataCursor.getString(0);
                    String mimeType = dataCursor.getString(1);
                    Log.i("Tag", "data1: " + data1 + "    mimeType: " + mimeType);
                    //3.根据mimetype 区分data1数据
                    if ("vnd.android.cursor.item/name".equals(mimeType)) {
                        contact.setName(data1);
                    } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
                        contact.setPhone(data1);
                    } else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) {
                        contact.setEmail(data1);
                    }
                }

                contactLists.add(contact);
            }
        }

        return contactLists;
    }
}
