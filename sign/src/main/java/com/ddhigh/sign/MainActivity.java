package com.ddhigh.sign;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.library.utils.Logger;
import com.ddhigh.library.utils.MD5;
import com.ddhigh.sign.adapter.PkgAdapter;
import com.ddhigh.sign.entity.PkgEntity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    @ViewInject(R.id.pkg)
    EditText pkg;
    @ViewInject(R.id.get)
    Button get;
    @ViewInject(R.id.hash)
    TextView hash;
    @ViewInject(R.id.copy)
    Button copy;
    @ViewInject(R.id.listView)
    ListView listView;
    PkgAdapter adapter;
    String _pkg;
    DbUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _pkg = pkg.getText().toString().trim();
                if (_pkg.length() == 0) {
                    Toast.makeText(MainActivity.this, "请输入应用包名", Toast.LENGTH_SHORT).show();
                } else {
                    //获取签名
                    try {
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(_pkg, PackageManager.GET_SIGNATURES);
                        Signature[] signs = packageInfo.signatures;
                        Signature sign = signs[0];
                        String md5 = parseSignature(sign.toByteArray());
                        hash.setText(md5);
                        //设置历史记录
                        PkgEntity pkgEntity = new PkgEntity(_pkg);
                        if (!adapter.getList().contains(pkgEntity)) {
                            adapter.getList().add(0, pkgEntity);
                            db.save(pkgEntity);
                            adapter.notifyDataSetChanged();
                        }
                        copy.setVisibility(View.VISIBLE);
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(MainActivity.this, _pkg + "包名不存在", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        Logger.e(e);
                    }
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cbm.setText(hash.getText());
                Toast.makeText(MainActivity.this, "签名已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });

        //设置历史查找
        db = DbUtils.create(this);
        try {
            List<PkgEntity> list = db.findAll(Selector.from(PkgEntity.class).orderBy("id", true));
            if (list == null) {
                list = new ArrayList<PkgEntity>();
            }
            adapter = new PkgAdapter(list, this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    pkg.setText(adapter.getList().get(i).getName());
                }
            });
        } catch (DbException e) {
            Logger.e(e);
            Toast.makeText(MainActivity.this, "历史记录加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String parseSignature(byte[] bytes) {
        return MD5.hexdigest(bytes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Android签名获取工具1.0");
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle("关于");
            builder.setPositiveButton("确定", null);
            builder.show();
            return true;
        } else if (id == R.id.action_clear) {
            try {
                db.deleteAll(PkgEntity.class);
                for (int i = 0; i < adapter.getList().size(); i++) {
                    adapter.getList().remove(i);
                }
                adapter.notifyDataSetChanged();
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
