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
public class RegisterActivity extends Activity {

    private static final String TAG= RegisterActivity.class.getSimpleName();

    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mSocket.connect();

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress Dialog

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        mSocket.on("register", onRegister);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    registerUser(name,email,password);
                }else {
                    Toast.makeText(getApplicationContext(),"please enter your details", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */

    private void registerUser(String name, String email, String password) {

        mSocket.emit("register",name , password ,email);

        pDialog.setMessage("Registering...");
        showDialog();

    }

    private void showDialog() {
        if(!pDialog.isShowing())
            pDialog.show();
    }


    private Emitter.Listener onRegister = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data =  args[0].toString();

            if(data == "true"){

                // Launch login activity
                Intent intent = new Intent(
                        RegisterActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                Log.d("error", "cant register");
            }

        }
    };

    private void hideDialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
            pDialog.cancel();
        }
    }
}
