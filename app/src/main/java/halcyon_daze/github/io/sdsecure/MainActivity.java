package halcyon_daze.github.io.sdsecure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText requestText = (EditText) findViewById(R.id.request);

        Button searchBtn =  findViewById(R.id.sendRequestButton);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestText.setText("");
            }
        });
    }
}
