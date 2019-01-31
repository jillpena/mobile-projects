package jillpena.c323finalproj.com.finalproject;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class FriendProfileFragment extends Fragment {
    String name;
    int image;
    ArrayList<Video> videoArrayList = new ArrayList<>();
    RecyclerView rv;
    RVAdapterProfile adapter;


     /*ProfileInterface profileInterface;

   public interface ProfileInterface{
        void changeProfile();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            profileInterface = (ProfileInterface) getActivity();
        } catch(ClassCastException e){
            throw new ClassCastException(getActivity().toString()
                    + " must implement friendsInterface");
        }
    }*/

    TextView personDetailsTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_profile, container, false);
        name = getArguments().getString("name");
        image = getArguments().getInt("image");
        Log.i("FINAL_EXAM", "FRAGMENT FRIEND PROFILE: " + name + " " + image);
        personDetailsTextView = rootView.findViewById(R.id.textViewProfileDetails);
        ImageView imageView = rootView.findViewById(R.id.imageViewFriendProfile);
        personDetailsTextView.setText(name);
        imageView.setImageResource(image);
        rv = rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        adapter = new RVAdapterProfile(videoArrayList);
        rv.setAdapter(adapter);
        return rootView;
    }

    public void setName(String name){
        personDetailsTextView.setText(name);
    }

    public class RVAdapterProfile extends RecyclerView.Adapter<RVAdapterProfile.MyViewHolder> {
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

        public RVAdapterProfile(ArrayList<Video> list) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i).getViews()).append(" ");
            }
            Log.i("FINAL", "BEFORE SORT: " + builder.toString());
            sortList(list);
            videoList = list;
            StringBuilder builder2 = new StringBuilder();
            for (int i = 0; i < videoList.size(); i++) {
                builder2.append(videoList.get(i).getViews()).append(" ");
            }
            Log.i("FINAL", "AFTER SORT: " + builder2.toString());
        }

        public void sortList(ArrayList<Video> list) {
            for (int i = 0; i < list.size() - 1; i++) {
                int maxIndex = i;
                for (int j = i + 1; j < list.size(); j++) {
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
        public RVAdapterProfile.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_list_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final RVAdapterProfile.MyViewHolder holder, int position) {
            try {
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
                            current.setViews(views + 1);
                        }
                    });

                    favoritesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!current.getFavorite()) {
                                Log.i("FINAL", "change to favorite");
                                favoritesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled));
                                current.setFavorite(true);
                            } else {
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
