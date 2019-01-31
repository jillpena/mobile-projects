package jillpena.c323finalproj.com.finalproject;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class Tab2VideoFeedFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    ArrayList<Video> videoArrayList = new ArrayList<>();
    RecyclerView rv;
    RVAdapterTab2 adapter;


    public Tab2VideoFeedFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Tab2VideoFeedFragment newInstance(int sectionNumber) {
        Tab2VideoFeedFragment fragment = new Tab2VideoFeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        populateVideoList();
        View view = inflater.inflate(R.layout.fragment_video_feed, container, false);
        rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        adapter = new RVAdapterTab2(videoArrayList);
        rv.setAdapter(adapter);

        return view;
    }

    private void populateVideoList() {
        if (videoArrayList.isEmpty()) {
            videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video1.mp4", 3));
            videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video2.mp4", 2));
            videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video3.mp4", 1));
            //videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video4.mp4", 0));
            //videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video5.mp4", 0));
        }
    }

    public class RVAdapterTab2 extends RecyclerView.Adapter<RVAdapterTab2.MyViewHolder> {
        ArrayList<Video> videoList;
        ImageButton favoritesButton;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView favoritesTextView;
            VideoView videoView;

            public MyViewHolder(View view) {
                super(view);
                favoritesTextView = view.findViewById(R.id.textViewNumberOfFavorites);
                videoView = view.findViewById(R.id.videoView);
                favoritesButton = view.findViewById(R.id.imageButton);
            }
        }

        public RVAdapterTab2(ArrayList<Video> list) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++){
                builder.append(list.get(i).getViews()).append(" ");
            }
            Log.i("FINAL", "BEFORE SORT: " + builder.toString());
            sortList(list);
            videoList = list;
            StringBuilder builder2 = new StringBuilder();
            for (int i = 0; i < videoList.size(); i++){
                builder2.append(videoList.get(i).getViews()).append(" ");
            }
            Log.i("FINAL", "AFTER SORT: " + builder2.toString());
        }

        public void sortList(ArrayList<Video> list){
            for (int i = 0; i < list.size()-1; i++){
                int maxIndex = i;
                for (int j = i+1; j < list.size(); j++){
                    if (list.get(j).getViews() > list.get(maxIndex).getViews())
                        i = j;
                }
                Log.i("FINAL", "New DataSet Minimum: " + list.get(maxIndex).getViews() +
                        "  --To be swapped with: " + list.get(i).getViews());

                Video maxFav = list.get(i);
                Video swapFav = list.get(maxIndex);
                list.set(maxIndex, maxFav);
                list.set(i, swapFav);
                //notifyDataSetChanged();
            }

        }


        @Override
        public RVAdapterTab2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_list_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final RVAdapterTab2.MyViewHolder holder, int position) {
            try{
                final Video current = videoList.get(position);
                if (holder.favoritesTextView == null) {
                    Log.i("FINAL", "favoirtesTextView is Null");
                } else {


                    String views = Integer.toString(current.getViews());
                    holder.favoritesTextView.setText(views);
                    String url = current.getUrl();
                    Uri videoUri = Uri.parse("https://s3.us-east-2.amazonaws.com/android-sample-videos/video4.mp4");
                    holder.videoView.setVideoURI(videoUri);
                    //holder.videoView.requestFocus();
                    //holder.videoView.setMediaController(new MediaController(getContext()));
                    holder.videoView.start();


                    Log.i("FINAL", "video uri:" + videoUri);
                    holder.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            Log.i("FINAL", "What:" + i + " Extra: " + i1);
                            return false;
                        }
                    });
                    holder.videoView.setOnClickListener(new MediaController.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int views = current.getViews();
                            current.setViews(views+1);
                        }
                    });

                    final Boolean favorited = false;
                    favoritesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!current.getFavorite()) {
                                Log.i("FINAL", "change to favorite");
                                favoritesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled));
                                current.setFavorite(true);
                            }else{
                                Log.i("FINAL", "unfavorite");
                                favoritesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
                                current.setFavorite(false);
                            }
                            sortList(videoList);
                            notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }

}



}
