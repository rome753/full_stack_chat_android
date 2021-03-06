package cc.rome753.fullstack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;
import cc.rome753.fullstack.bean.Login;
import cc.rome753.fullstack.bean.Register;
import cc.rome753.fullstack.callback.HttpHandler;
import cc.rome753.fullstack.main.MainActivity;
import cc.rome753.fullstack.manager.OkhttpManager;
import cc.rome753.fullstack.manager.UserManager;


/**
 * A host screen that offers host via email/password.
 */
public class LoginActivity extends BaseActivity {

    public static void start(BaseActivity activity){
        Intent i = new Intent(activity, LoginActivity.class);
        activity.startActivity(i);
    }

    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_email)
    EditText mEtEmail;

    @BindView(R.id.btn_register)
    Button mBtnRegister;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    @BindView(R.id.btn_switch)
    Button mBtnSwitch;

    @Override
    public int setView() {
        return R.layout.activity_login;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.host, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //切换服务器
        OkhttpManager.setHost(item.getTitle().toString());
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_register)
    void register(){
        String u = mEtUsername.getText().toString().trim();
        String p = mEtPassword.getText().toString().trim();
        String e = mEtEmail.getText().toString().trim();

        if(TextUtils.isEmpty(u) || TextUtils.isEmpty(p) || TextUtils.isEmpty(e)){
            Utils.toast("empty");
            return;
        }

        String json = new Gson().toJson(new Register(u,p,e));
        OkhttpManager.post("register", json, new HttpHandler() {

            @Override
            public void onSuccess(String response) {
                Utils.toast(response);
                switches();
            }

            @Override
            public void onFailure() {
            }
        });
    }

    @OnClick(R.id.btn_login)
    void login(){
        final String u = mEtUsername.getText().toString().trim();
        String p = mEtPassword.getText().toString().trim();
        if(TextUtils.isEmpty(u) || TextUtils.isEmpty(p)){
            Utils.toast("empty");
            return;
        }

        String json = new Gson().toJson(new Login(u,p));
        showDialog();
        OkhttpManager.post("login", json, new HttpHandler() {

            @Override
            public void onSuccess(String response) {
                hideDialog();
                UserManager.getUser().setName(u);
                MainActivity.start(mActivity);
                finish();
            }

            @Override
            public void onFailure() {
                hideDialog();
            }
        });
    }

    @OnClick(R.id.btn_switch)
    void switches(){
        String toLogin = getString(R.string.to_login);
        String toRegister = getString(R.string.to_register);
        boolean isLogin = mBtnSwitch.getText().toString().equals(toRegister);

        setTitle(isLogin ? "注册" : "登录");
        mBtnSwitch.setText(isLogin ? toLogin : toRegister);
        mEtEmail.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        mBtnRegister.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        mBtnLogin.setVisibility(isLogin ? View.GONE : View.VISIBLE);
    }

}

