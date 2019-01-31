package jillpena.c323finalproj.com.finalproject;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class Tab1TrendingFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerView rv;
    RVAdapterTab1 adapter;
    ArrayList<Video> videoArrayList = new ArrayList<>();
    VideoView video;
    //TrendingInterface trendingInterface;

       /* public interface TrendingInterface{
            void initializeVideoList(ArrayList<Video> videoArrayList, RecyclerView rv, View rootview, LinearLayoutManager llm);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            try{
                trendingInterface = (TrendingInterface) getActivity();
            } catch(ClassCastException e){
                throw new ClassCastException(getActivity().toString()
                        + " must implement trendingInterface");
            }
        }*/

    public Tab1TrendingFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Tab1TrendingFragment newInstance(int sectionNumber) {
        Tab1TrendingFragment fragment = new Tab1TrendingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addVideosToList();
        View rootView = inflater.inflate(R.layout.fragment_trending, container, false);
        rv = rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        adapter = new RVAdapterTab1(videoArrayList);
        rv.setAdapter(adapter);
        return rootView;
    }

    private void addVideosToList() {
        if (videoArrayList.isEmpty()) {
            videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video1.mp4", 3));
            videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video2.mp4", 2));
            videoArrayList.add(new Video("https://s3.us-east-2.amazonaws.com/android-sample-videos/video3.mp4", 1));
        }
    }

    public class RVAdapterTab1 extends RecyclerView.Adapter<RVAdapterTab1.MyViewHolder> {
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

        public RVAdapterTab1(ArrayList<Video> list) {

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
        public RVAdapterTab1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_list_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final RVAdapterTab1.MyViewHolder holder, int position) {
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
