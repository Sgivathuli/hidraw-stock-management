package com.hidraw.stockmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    DB db; LinearLayout root, list; TextView summary;
    int bg=Color.rgb(15,15,17), card=Color.rgb(31,31,35), white=Color.WHITE, muted=Color.LTGRAY, red=Color.rgb(255,85,85);
    public void onCreate(Bundle b){super.onCreate(b); db=new DB(this); draw();}
    TextView text(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(white);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    GradientDrawableCompat unused;
    void draw(){
        ScrollView sc=new ScrollView(this); root=new LinearLayout(this);root.setOrientation(1);root.setPadding(18,22,18,24);root.setBackgroundColor(bg);sc.addView(root);setContentView(sc);
        TextView title=text("HIDRAW",30,true);root.addView(title);TextView sub=text("STOCK MANAGER  •  TATTOO STUDIO",12,true);sub.setTextColor(muted);root.addView(sub);
        summary=text("",15,true);summary.setPadding(0,18,0,18);root.addView(summary);
        Button add=button("+  ADD NEW ITEM");root.addView(add);add.setOnClickListener(v->itemDialog(false,null,0,0));
        Button check=button("🌅  OPENING / PHYSICAL STOCK CHECK");root.addView(check);check.setOnClickListener(v->physicalCheck());
        TextView heading=text("YOUR STOCK",18,true);heading.setPadding(0,24,0,10);root.addView(heading);
        list=new LinearLayout(this);list.setOrientation(1);root.addView(list);refresh();
    }
    Button button(String s){Button b=new Button(this);b.setText(s);b.setTextColor(white);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(55,55,61)));return b;}
    void refresh(){if(list==null)return;list.removeAllViews();SQLiteDatabase d=db.getReadableDatabase();Cursor c=d.rawQuery("SELECT id,name,category,qty,unit,minqty FROM items ORDER BY name COLLATE NOCASE",null);int total=0,low=0,out=0;while(c.moveToNext()){int id=c.getInt(0);String name=c.getString(1),cat=c.getString(2);int qty=c.getInt(3);String unit=c.getString(4);int min=c.getInt(5);total++;if(qty<=min)low++;if(qty<=0)out++;
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(14,12,14,12);box.setBackgroundColor(card);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,-2);bp.setMargins(0,0,0,10);list.addView(box,bp);
        TextView n=text(name,17,true);box.addView(n);TextView detail=text(cat+"  •  Minimum: "+min+" "+unit,12,false);detail.setTextColor(muted);box.addView(detail);
        TextView q=text("Stock: "+qty+" "+unit+(qty<=0?"  • OUT OF STOCK":qty<=min?"  • LOW STOCK":""),15,true);if(qty<=min)q.setTextColor(red);q.setPadding(0,7,0,7);box.addView(q);
        LinearLayout row=new LinearLayout(this);row.setOrientation(0);box.addView(row);Button use=smallButton("− USE");Button in=smallButton("+ RESTOCK");Button edit=smallButton("EDIT");Button del=smallButton("DELETE");row.addView(use,new LinearLayout.LayoutParams(0,-2,1));row.addView(in,new LinearLayout.LayoutParams(0,-2,1));row.addView(edit,new LinearLayout.LayoutParams(0,-2,1));row.addView(del,new LinearLayout.LayoutParams(0,-2,1));use.setOnClickListener(v->quantityDialog(id,name,false));in.setOnClickListener(v->quantityDialog(id,name,true));edit.setOnClickListener(v->itemDialog(true,id,qty,min));del.setOnClickListener(v->new AlertDialog.Builder(this).setMessage("Delete "+name+"?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(a,x)->{db.getWritableDatabase().delete("items","id=?",new String[]{""+id});refresh();}).show());
    }c.close();summary.setText("ITEMS: "+total+"     LOW STOCK: "+low+"     OUT: "+out+"\nDate: "+new SimpleDateFormat("dd MMM yyyy",Locale.getDefault()).format(new Date()));}
    Button smallButton(String s){Button b=new Button(this);b.setText(s);b.setTextSize(10);b.setPadding(0,0,0,0);b.setTextColor(white);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(65,65,72)));return b;}
    EditText input(String hint,String value,int type){EditText e=new EditText(this);e.setSingleLine(true);e.setTextColor(white);e.setHintTextColor(muted);e.setHint(hint);e.setInputType(type);e.setText(value==null?"":value);return e;}
    void itemDialog(boolean editing,Integer id,int qty,int min){LinearLayout form=new LinearLayout(this);form.setPadding(20,0,20,0);form.setOrientation(1);EditText name=input("Item name (e.g. 9RL Cartridge)","",InputType.TYPE_CLASS_TEXT);EditText cat=input("Category (Needles / Ink / Hygiene)","Tattoo Supplies",InputType.TYPE_CLASS_TEXT);EditText unit=input("Unit (pcs / bottle / pack)","pcs",InputType.TYPE_CLASS_TEXT);EditText amount=input("Current stock quantity",editing?""+qty:"0",InputType.TYPE_CLASS_NUMBER);EditText minimum=input("Minimum stock alert level",editing?""+min:"5",InputType.TYPE_CLASS_NUMBER);form.addView(name);form.addView(cat);form.addView(unit);form.addView(amount);form.addView(minimum);
        if(editing&&id!=null){Cursor c=db.getReadableDatabase().rawQuery("SELECT name,category,unit FROM items WHERE id=?",new String[]{""+id});if(c.moveToFirst()){name.setText(c.getString(0));cat.setText(c.getString(1));unit.setText(c.getString(2));}c.close();}
        new AlertDialog.Builder(this).setTitle(editing?"Edit item":"Add stock item").setView(form).setNegativeButton("Cancel",null).setPositiveButton("Save",(a,b)->{try{String nm=name.getText().toString().trim();if(nm.isEmpty())return;ContentValues cv=new ContentValues();cv.put("name",nm);cv.put("category",cat.getText().toString().trim());cv.put("unit",unit.getText().toString().trim().isEmpty()?"pcs":unit.getText().toString().trim());cv.put("qty",Integer.parseInt(amount.getText().toString()));cv.put("minqty",Integer.parseInt(minimum.getText().toString()));if(editing)db.getWritableDatabase().update("items",cv,"id=?",new String[]{""+id});else db.getWritableDatabase().insert("items",null,cv);refresh();}catch(Exception ex){Toast.makeText(this,"Enter valid quantities",Toast.LENGTH_SHORT).show();}}).show();}
    void quantityDialog(int id,String name,boolean restock){EditText e=input("Quantity", "1",InputType.TYPE_CLASS_NUMBER);new AlertDialog.Builder(this).setTitle((restock?"Restock: ":"Record usage: ")+name).setView(e).setNegativeButton("Cancel",null).setPositiveButton("Save",(a,b)->{try{int n=Integer.parseInt(e.getText().toString());if(n<=0)throw new Exception();SQLiteDatabase d=db.getWritableDatabase();Cursor c=d.rawQuery("SELECT qty FROM items WHERE id=?",new String[]{""+id});int current=0;if(c.moveToFirst())current=c.getInt(0);c.close();if(!restock&&n>current){Toast.makeText(this,"Not enough stock",Toast.LENGTH_LONG).show();return;}int next=restock?current+n:current-n;ContentValues cv=new ContentValues();cv.put("qty",next);d.update("items",cv,"id=?",new String[]{""+id});ContentValues log=new ContentValues();log.put("item_id",id);log.put("change_qty",restock?n:-n);log.put("action",restock?"RESTOCK":"USED");log.put("stamp",new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).format(new Date()));d.insert("history",null,log);refresh();}catch(Exception ex){Toast.makeText(this,"Enter a valid quantity",Toast.LENGTH_SHORT).show();}}).show();}
    void physicalCheck(){Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,qty,unit FROM items ORDER BY name",null);if(c.getCount()==0){c.close();Toast.makeText(this,"Add items first",Toast.LENGTH_SHORT).show();return;}LinearLayout form=new LinearLayout(this);form.setOrientation(1);form.setPadding(20,0,20,0);java.util.ArrayList<Integer> ids=new java.util.ArrayList<>();java.util.ArrayList<EditText> fields=new java.util.ArrayList<>();while(c.moveToNext()){ids.add(c.getInt(0));TextView label=text(c.getString(1)+" (system: "+c.getInt(2)+" "+c.getString(3)+")",14,true);form.addView(label);EditText e=input("Count actual stock", ""+c.getInt(2),InputType.TYPE_CLASS_NUMBER);form.addView(e);fields.add(e);}c.close();new AlertDialog.Builder(this).setTitle("Physical stock check").setView(form).setNegativeButton("Cancel",null).setPositiveButton("Save check",(a,b)->{SQLiteDatabase d=db.getWritableDatabase();for(int i=0;i<ids.size();i++){try{int actual=Integer.parseInt(fields.get(i).getText().toString());ContentValues cv=new ContentValues();cv.put("qty",actual);d.update("items",cv,"id=?",new String[]{""+ids.get(i)});}catch(Exception ignored){}}refresh();Toast.makeText(this,"Physical stock saved",Toast.LENGTH_SHORT).show();}).show();}
    static class DB extends SQLiteOpenHelper {DB(Activity a){super(a,"hidraw_stock.db",null,1);}public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE items(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,category TEXT,qty INTEGER NOT NULL,unit TEXT,minqty INTEGER NOT NULL)");d.execSQL("CREATE TABLE history(id INTEGER PRIMARY KEY AUTOINCREMENT,item_id INTEGER,change_qty INTEGER,action TEXT,stamp TEXT)");}public void onUpgrade(SQLiteDatabase d,int o,int n){}}
    static class GradientDrawableCompat {}
}
