package com.boxfox.dsm_boxfox;



import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.boxfox.dsm_boxfox.Server.start;
import com.boxfox.dsm_boxfox.Sub.InfoActivity;
import com.example.dsm_boxfox.R;
import com.boxfox.dsm_boxfox.Survey.FeedListActivity;
import com.boxfox.dsm_boxfox.Survey.MyRecyclerAdapter;


public class ArrayListFragment extends ListFragment {
    int fragNum;
    static int page;
    int check;
    int check_page;
    public static int checks;
    String arrs[];


    public static ArrayListFragment init(int val) {
        ArrayListFragment truitonList = new ArrayListFragment();

        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonList.setArguments(args);
        page=val;
        return truitonList;
    }

    /**
     * Retrieving this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragNum = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    /**
     * The Fragment's UI is a simple text view showing its instance number and
     * an associated list.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layoutView = inflater.inflate(R.layout.fragment_array_list,
                container, false);

        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        check=page;
        check_page = page;

        if(!start.Network){
            arrs = new String[1];
            arrs[0]="네트워크 연결이 실패";
            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrs));
            return;
        }
            arrs= start.survey;
            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrs));

    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_content), arrs[position], Snackbar.LENGTH_INDEFINITE)
                .setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (arrs[position].equalsIgnoreCase("네트워크 오류") || arrs[position].equalsIgnoreCase("글이 존재하지 않습니다")) {
                            return;
                        }
                        checks = check;
                        if (check == 1 || check == 2) {
                            Intent in = new Intent(getContext(), InfoActivity.class);
                            in.putExtra("Position", position);
                            startActivity(in);
                        } else {
                            if (((MainActivity) getContext()).getLogin().islogin()) {
                                FeedListActivity.positionc = position;
                                MyRecyclerAdapter.check = 2;
                                Intent in = new Intent(getContext(), FeedListActivity.class);
                                try {
                                    startActivity(in);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_content), "로그인해 주세요", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("확인", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                })
                .show();
    }
}
