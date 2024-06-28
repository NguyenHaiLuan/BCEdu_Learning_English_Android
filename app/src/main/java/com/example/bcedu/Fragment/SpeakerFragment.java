package com.example.bcedu.Fragment;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bcedu.Activities.LiveStream.JoinActivity;
import com.example.bcedu.Activities.LiveStream.MeetingActivity;
import com.example.bcedu.Adapter.SpeakerAdapter;
import com.example.bcedu.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;
import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.lib.JsonUtils;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeakerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeakerFragment extends Fragment {

    private static Activity mActivity;
    private static Context mContext;
    private static Meeting meeting;
    private boolean micEnabled = true;
    private boolean webcamEnabled = true;
    private boolean hlsEnabled = false;
    private Button btnHls, btnLeave;
    private ImageButton btnMic, btnWebcam, copy;
    private TextView tvMeetingId, tvHlsState;
    public SpeakerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
            // getting meeting object from Meeting Activity
            meeting = ((MeetingActivity) mActivity).getMeeting();
        }
    }

    // TODO: Rename and change types and number of parameters
    public static SpeakerFragment newInstance(String param1, String param2) {
        SpeakerFragment fragment = new SpeakerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speaker, container, false);
        btnMic = view.findViewById(R.id.btnMic);
        btnWebcam = view.findViewById(R.id.btnWebcam);
        btnHls = view.findViewById(R.id.btnHLS);
        btnLeave = view.findViewById(R.id.btnLeave);

        tvMeetingId = view.findViewById(R.id.tvMeetingId);
        tvHlsState = view.findViewById(R.id.tvHlsState);

        copy = view.findViewById(R.id.copy_id_btn);

        if (meeting != null) {
            tvMeetingId.setText("Meeting Id : " + meeting.getMeetingId());
            setActionListeners();

            meeting.addEventListener(meetingEventListener);

            final RecyclerView rvParticipants = view.findViewById(R.id.rvParticipants);
            rvParticipants.setLayoutManager(new GridLayoutManager(mContext, 2));
            rvParticipants.setAdapter(new SpeakerAdapter(meeting));
        }
        return view;
    }

    private void setActionListeners() {
        btnMic.setOnClickListener(v -> {
            if (micEnabled) {
                meeting.muteMic();
                StyleableToast.makeText(mContext, "Đã tắt mic!", R.style.micOffToast).show();
                btnMic.setImageResource(R.drawable.baseline_mic_off_24);
            } else {
                meeting.unmuteMic();
                StyleableToast.makeText(mContext, "Đã mở mic!", R.style.micOnToast).show();
                btnMic.setImageResource(R.drawable.baseline_mic_24);
            }
            micEnabled = !micEnabled;
        });

        btnWebcam.setOnClickListener(v -> {
            if (webcamEnabled) {
                meeting.disableWebcam();
                StyleableToast.makeText(mContext, "Đã tắt camera!", R.style.camOffToast).show();
                btnWebcam.setImageResource(R.drawable.baseline_videocam_off_24);
            } else {
                meeting.enableWebcam();
                StyleableToast.makeText(mContext, "Đã bật camera!", R.style.camOnToast).show();
                btnWebcam.setImageResource(R.drawable.baseline_videocam_24);
            }
            webcamEnabled = !webcamEnabled;
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = meeting.getMeetingId().toString();
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", text);
                clipboard.setPrimaryClip(clip);

                StyleableToast.makeText(mContext, "Sao chép id lớp học thành công!", R.style.success_toast).show();
            }
        });

        btnLeave.setOnClickListener(v -> meeting.leave());

        btnHls.setOnClickListener(v -> {
            if (!hlsEnabled) {
                JSONObject config = new JSONObject();
                JSONObject layout = new JSONObject();
                JsonUtils.jsonPut(layout, "type", "SPOTLIGHT");
                JsonUtils.jsonPut(layout, "priority", "PIN");
                JsonUtils.jsonPut(layout, "gridSize", 4);
                JsonUtils.jsonPut(config, "layout", layout);
                JsonUtils.jsonPut(config, "orientation", "portrait");
                JsonUtils.jsonPut(config, "theme", "DARK");
                JsonUtils.jsonPut(config, "quality", "high");
                meeting.startHls(config);
            } else {
                meeting.stopHls();
            }
        });
    }

    private final MeetingEventListener meetingEventListener = new MeetingEventListener() {
        @Override
        public void onMeetingLeft() {
            //unpin local participant
            meeting.getLocalParticipant().unpin("SHARE_AND_CAM");
            if (isAdded()) {
                Intent intents = new Intent(mContext, JoinActivity.class);
                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intents);
                mActivity.finish();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onHlsStateChanged(JSONObject HlsState) {
            if (HlsState.has("status")) {
                try {
                    tvHlsState.setText("Current HLS State : " + HlsState.getString("status"));
                    if (HlsState.getString("status").equals("HLS_STARTED")) {
                        hlsEnabled = true;
                        btnHls.setText("Stop HLS");
                    }
                    if (HlsState.getString("status").equals("HLS_STOPPED")) {
                        hlsEnabled = false;
                        btnHls.setText("Start HLS");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        mContext = null;
        mActivity = null;
        if (meeting != null) {
            meeting.removeAllListeners();
            meeting = null;
        }
        super.onDestroy();
    }
}