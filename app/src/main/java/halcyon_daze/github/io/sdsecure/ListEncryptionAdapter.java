package halcyon_daze.github.io.sdsecure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chris on 2018-03-18.
 */

public class ListEncryptionAdapter extends BaseAdapter{

    LayoutInflater mInflater;
    ArrayList<SDCard> cardList;

    public ListEncryptionAdapter(Context c, ArrayList<SDCard> cardList) {
        this.cardList = cardList;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.card_info_row, null);
        TextView elementNumTextView = (TextView) v.findViewById(R.id.elementNum);
        TextView latitudeTextView = (TextView) v.findViewById(R.id.latitudeText);
        TextView longitudeTextView = (TextView) v.findViewById(R.id.longitudeText);
        TextView statusTextView = (TextView) v.findViewById(R.id.statusText);
        TextView lastTimeTextView = (TextView) v.findViewById(R.id.lastTimeText);

        elementNumTextView.setText(String.valueOf(position));
        if(cardList.get(position).getLatitude().equals(null) || cardList.get(position).getLongitude().equals("null")
                || cardList.get(position).getLatitude().isEmpty() || cardList.get(position).getLatitude().isEmpty()) {
            latitudeTextView.setText("N/A");
            longitudeTextView.setText("N/A");
        } else {
            latitudeTextView.setText(cardList.get(position).getLatitude());
            longitudeTextView.setText(cardList.get(position).getLongitude());
        }
        if(cardList.get(position).getOperation().equals("1")) {
            statusTextView.setText("Encrypt");
        } else {
            statusTextView.setText("Decrypt");
        }

        lastTimeTextView.setText(cardList.get(position).getLastDayUpdated() + "\n" + cardList.get(position).getLastTimeUpdated());

        return v;
    }
}
