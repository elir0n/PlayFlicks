package com.example.myapplication.view;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.utils.TokenManager;

import java.net.URI;

public class ToolbarFragment extends Fragment {


    private ImageButton nightLightIcon;
    private ImageButton searchIcon;
    private ImageButton logoutIcon;
    private ImageButton adminIcon;

    public ToolbarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitUI();


        // set admin button only if he is an admin
        if (!TokenManager.getRole(getContext()).equals("admin")) {
            adminIcon.setVisibility(View.INVISIBLE);
        }

        adminIcon.setOnClickListener(v -> adminTouchHandle());
        logoutIcon.setOnClickListener(v -> logoutUser());

        nightLightIcon.setOnClickListener(v -> toggleVisibility());

        searchIcon.setOnClickListener(v -> startActivity(new Intent(requireContext(), Search.class)));
    }

    /**
     * Init UI elements
     */
    private void InitUI() {
        nightLightIcon = requireView().findViewById(R.id.night_light_icon);
        searchIcon = requireView().findViewById(R.id.search_icon);
        logoutIcon = requireView().findViewById(R.id.logout_icon);
        adminIcon = requireView().findViewById(R.id.admin_icon);


        ImageView profilePic = requireView().findViewById(R.id.profileImage);
        profilePic.setClipToOutline(true);

        // displaying the user pic
        Glide.with(requireContext())
                .load(getString(R.string.BASE_URL) + TokenManager.getImage(requireContext()))
                .placeholder(R.drawable.no_image_found)
                .into(profilePic);
    }

    /**
     * handle admin imageButton touch
     */

    private void adminTouchHandle() {
        Intent intent = new Intent(requireContext(), AdminActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_toolbar, container, false);


    }

    /**
     * Toggle UI visibility -  night <-> day
     */
    private void toggleVisibility() {
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), uiMode == Configuration.UI_MODE_NIGHT_YES ? R.color.lightGray : R.color.black));
        AppCompatDelegate.setDefaultNightMode(uiMode == Configuration.UI_MODE_NIGHT_YES ? MODE_NIGHT_NO : MODE_NIGHT_YES);
    }

    /**
     * Handle user logout
     */
    private void logoutUser() {
        Intent intent = new Intent(requireContext(), UnsignedHomeActivity.class);
        TokenManager.deleteToken(requireContext());
        startActivity(intent);
    }
}