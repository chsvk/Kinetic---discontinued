package tech.steampunk.kinetic.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tech.steampunk.kinetic.UI.CallsFragment;
import tech.steampunk.kinetic.UI.ChatsFragment;
import tech.steampunk.kinetic.UI.GroupsFragment;

/**
 * Created by Vamshi on 9/8/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private String[] titles = {"Extra", "Chats", "Calls"};

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                CallsFragment callsFragment = new CallsFragment();
                return callsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
