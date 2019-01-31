package jillpena.c323finalproj.com.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Tab3FriendsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerView rv;
    RVAdapterTab3 adapter;
    ArrayList<Person> personArrayList = new ArrayList<>();
    FloatingActionButton fab;
    FriendsInterface friendsInterface;

    public interface FriendsInterface{
        void onClickFab(ArrayList<Person> plist);
        void onClickPersonProfile(Person p);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            friendsInterface = (FriendsInterface) getActivity();
        } catch(ClassCastException e){
            throw new ClassCastException(getActivity().toString()
                    + " must implement friendsInterface");
        }
    }

    public Tab3FriendsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Tab3FriendsFragment newInstance(int sectionNumber) {
        Tab3FriendsFragment fragment = new Tab3FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addPeopleToList();
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        rv = rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        adapter = new RVAdapterTab3(personArrayList);
        rv.setAdapter(adapter);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsInterface.onClickFab(personArrayList);
            }
        });
        return rootView;
    }

    private void addPeopleToList() {
        if (personArrayList.isEmpty()) {
            personArrayList.add(new Person("Zach", R.drawable.ic_person_24dp));
            personArrayList.add(new Person("Jill", R.drawable.ic_person_24dp));
            personArrayList.add(new Person("Bill", R.drawable.ic_person_24dp));
        }
    }

    public class RVAdapterTab3 extends RecyclerView.Adapter<RVAdapterTab3.MyViewHolder> {
        ArrayList<Person> personList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            ImageView profileImageView;

            public MyViewHolder(View view) {
                super(view);
                nameTextView = view.findViewById(R.id.nameTextView);
                profileImageView = view.findViewById(R.id.profileImageView);
            }
        }

        public RVAdapterTab3(ArrayList<Person> list) {

            personList = list;
        }


        @Override
        public RVAdapterTab3.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.person_list_item, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = rv.getChildLayoutPosition(view);
                    Person p = personArrayList.get(itemPosition);
                    friendsInterface.onClickPersonProfile(p);
                }
            });
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final RVAdapterTab3.MyViewHolder holder, int position) {
            Person current = personList.get(position);
            holder.nameTextView.setText(current.getName());

        }

        @Override
        public int getItemCount() {
            return personList.size();
        }
    }
}
