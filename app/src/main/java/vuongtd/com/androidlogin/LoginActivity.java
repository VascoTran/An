package vuongtd.com.androidlogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by vuongtd on 4/5/2016.
 */
public class LoginActivity extends Activity{
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.100.5:3000");

        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data =  args[0].toString();

            if(data == "true"){

                // Launch login activity
                Intent intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Log.d("error", "cant login");
            }
            hideDialog();

        }
    };

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSocket.connect();

        mSocket.on("login", onLogin);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // check for empty data in the form
                if(!email.isEmpty() && !password.isEmpty()){
                    // Login user
                    checkLogin(email, password);
                }else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!" , Toast.LENGTH_LONG).show();
                }
            }
        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void checkLogin(String email, String password) {
        pDialog.setMessage("Logging in ...");
        showDialog();

        mSocket.emit("login", email, password);
    }

    private void showDialog(){
        if (!pDialog.isShowing())
            pDialog.show();
    }

}
