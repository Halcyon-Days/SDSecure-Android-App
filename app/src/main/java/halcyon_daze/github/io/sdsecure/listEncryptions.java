package halcyon_daze.github.io.sdsecure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class listEncryptions extends AppCompatActivity {

    ArrayList<SDCard> cardList;
    ListView cardListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_encryptions);

        cardList = new ArrayList<SDCard>();

        cardListView = findViewById(R.id.cardList);

        //Creates adapter which shows the details of a bus when it is clicked in the listview
        ListEncryptionAdapter cardListAdapter = new ListEncryptionAdapter(this, cardList);
        cardListView.setAdapter(cardListAdapter);
    }

}
