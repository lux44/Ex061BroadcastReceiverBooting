package com.lux.ex061broadcastreceiverbooting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

//BroadcastReceiver는 반드시 AndroidManifest.xml에 등록해야만 함.
public class BootingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //N(누가) 버전부터는 앱을 설치한 후 적어도 1회 직접 사용자가 launcher화면(앱 목록)에서
        //앱 아이콘을 클릭하여 실행한 앱에 대해서만 Boot_completed에 대한 방송을 수신할 수 있음.

        //수신한 방송 액션값 확인
        String action =intent.getAction();
        
        //수신한 방송이 "Boot completed"인지 확인
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Toast.makeText(context, "boot received", Toast.LENGTH_SHORT).show();

            //보통은 부팅완료됐을때 백그라운드 작업을 수행하는
            //Service를 실행하는 것이 일바적임.
            //ex) 채팅앱, 시스템앱들,,

            //실습 목적으로 부팅 완료되면 MainActivity를 실행하기
            //android 10(api 29)버전부터는 리시버에서 직접 액티비티를 실행하는 것을 금지함.
            //대신에 리시버에서 알림(notification)을 띄우고 이 알림을 사용자가 클릭했을때
            //액티비티가 실행되도록 하는 것을 강제함.
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                //알림을 띄워주는 알림 관리자 객체부터 OS로부터 소환
                NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


                //알림채널 객체 생성
                NotificationChannel channel=new NotificationChannel("ch01","부팅완료 리시버 앱",NotificationManager.IMPORTANCE_HIGH);
                //채널을 알림시스템에 등록하기
                notificationManager.createNotificationChannel(channel);
                //알림 객체를 만들어주는 건축가 객체 생성
                Notification.Builder builder= new Notification.Builder(context,"ch01");

                //건축가에게 원하는 알림에 대한 설정
                builder.setSmallIcon(R.drawable.ic_noti);

                builder.setContentTitle("booting completed");
                builder.setContentText("Ex061앱의 메인 액티비티를 실행 할 수 있음.");
                builder.setSubText("앱실행");

                //알림창을 클릭할때 실행할 액티비티를 실행시켜주는 인텐트 객체 생성
                Intent i=new Intent(context,MainActivity.class);
                //지금 당장 실행하는 것이 아니라 알림창을 클릭할때까지 보류시키기 위해 보류중인 인텐트로 만들기
                PendingIntent pendingIntent=PendingIntent.getActivity(context,100,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
                builder.setContentIntent(pendingIntent);

                builder.setAutoCancel(true);

                Notification notification=builder.build();
                //알림 매니저에게 알림을 요청
                notificationManager.notify(10,notification);
            }else {
                Intent intent1 = new Intent(context, MainActivity.class);
                //단, 새로운 Activity를 기존 Activity가 아닌 곳에서 실행하려면
                //새로운 Task(새로운 메인스레드)에서 실행하도록 해야함.
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }


        }
        
    }
}
