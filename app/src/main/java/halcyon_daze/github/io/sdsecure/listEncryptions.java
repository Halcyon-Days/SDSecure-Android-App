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
        //FOR TESTING ONLY
        testListCreate();

        cardListView = findViewById(R.id.cardList);

        //Creates adapter which shows the details of a bus when it is clicked in the listview
        ListEncryptionAdapter cardListAdapter = new ListEncryptionAdapter(this, cardList);
        cardListView.setAdapter(cardListAdapter);
    }


    private void testListCreate() {
        cardList.add(new SDCard("1", "1", "1", "2018-03-12T12:00:00"));
        cardList.add(new SDCard("2", "2", "2", "2018-03-13T1:00:00"));
        cardList.add(new SDCard("3", "3", "3", "2018-03-14T2:00:00"));
    }
}
