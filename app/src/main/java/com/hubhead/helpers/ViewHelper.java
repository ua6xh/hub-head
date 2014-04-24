package com.hubhead.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hubhead.R;
import com.hubhead.contentprovider.Notification;
import com.hubhead.models.CircleModel;
import com.hubhead.models.ContactModel;
import com.hubhead.models.NotificationGroupModel;
import com.hubhead.models.SphereModel;

import java.util.List;
import java.util.Map;

public class ViewHelper {
    public static String TAG = "com.hubhead.ViewHelper";

    public static View createNotificationGroupView(Notification notificationModel, List<NotificationGroupModel> groups, Map<String, ContactModel> contactMap, Map<Long, SphereModel> sphereMap, Map<Long, CircleModel> circleMap, Context context, Typeface mMuseosancyrl) {
        View v = null;
        CircleModel circle = null;

        SphereModel sphereModel = sphereMap.get(notificationModel.sphere_id);
        if (circleMap.containsKey(sphereModel.circle_id)) {
            circle = circleMap.get(sphereModel.circle_id);
        }
        if (circle != null) {

            v = LayoutInflater.from(context).inflate(R.layout.notification, null);

            LinearLayout llNotification = (LinearLayout) v.findViewById(R.id.llNotification);
            TextView tvSphereName = (TextView) llNotification.findViewById(R.id.tvSphereName);
            if (sphereMap.containsKey(notificationModel.sphere_id)) {
                tvSphereName.setText(sphereMap.get(notificationModel.sphere_id).getName());
            } else {
                tvSphereName.setText(context.getResources().getString(R.string.word_notifications) + " " + notificationModel.sphere_id);
            }
            tvSphereName.setTypeface(mMuseosancyrl, Typeface.BOLD);

//            switch (notificationModel.type_notification) {
//                case NotificationModel.TYPE_SPHERE: {
//                    break;
//                }
//                case NotificationModel.TYPE_TASK: {
//                    View taskView = LayoutInflater.from(context).inflate(R.layout.notification_task, null, false);
//                    TextView tvTaskName = (TextView) taskView.findViewById(R.id.tvTaskName);
//                    ImageView imgIconCountMessages = (ImageView) taskView.findViewById(R.id.imgIconCountMessages);
//                    TextView tvCountMessages = (TextView) taskView.findViewById(R.id.tvCountMessages);
//                    tvTaskName.setText(notificationModel.model_name);
//                    if (notificationModel.messages_count > 0) {
//                        imgIconCountMessages.setImageResource(R.drawable.ic_cnt_mess);
//                        tvCountMessages.setText(Integer.toString(notificationModel.messages_count));
//                    } else {
//                        imgIconCountMessages.setVisibility(View.INVISIBLE);
//                        tvCountMessages.setText("");
//                    }
//                    llNotification.addView(taskView);
//                    break;
//                }
//            }

            if (groups.size() > 0) {
                for (NotificationGroupModel group : groups) {
                    View groupView = LayoutInflater.from(context).inflate(R.layout.notification_group, null, false);

                    TextView tvGroupName = (TextView) groupView.findViewById(R.id.text_view_group_name);
                    if (circle != null) {
                        ContactModel contact = contactMap.get(circle.getId() + "_" + group.user_id);
                        tvGroupName.setText(contact.toString());
                    } else {
                        tvGroupName.setText(context.getResources().getString(R.string.word_delete) + " " + group.user_id);
                    }
                    tvGroupName.setTypeface(mMuseosancyrl, Typeface.BOLD);

                    TextView tvGroupDate = (TextView) groupView.findViewById(R.id.text_view_group_date);
                    tvGroupDate.setText(group.getDt());
                    tvGroupDate.setTypeface(mMuseosancyrl);

                    llNotification.addView(groupView);

                    for (int actCnt = 0; actCnt < group.actions.size(); actCnt++) {
                        View actionView = LayoutInflater.from(context).inflate(R.layout.notification_action, null, false);

                        TextView tvActionName = (TextView) actionView.findViewById(R.id.text_view_action_name);
                        ImageView imgIconAction = (ImageView) actionView.findViewById(R.id.icon_action);

                        tvActionName.setText(Html.fromHtml(group.actions.get(actCnt).toString()));
                        tvActionName.setTypeface(mMuseosancyrl);
                        imgIconAction.setImageResource(group.actions.get(actCnt).getImgResource());

                        llNotification.addView(actionView);
                    }
                }
            }
        }
        return v;
    }

}
