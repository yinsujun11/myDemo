package com.mydemo.activity;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.zh.jqtek.JQ_BT_ACTIVITY;
import com.zh.jqtek.JQ_BT_CTL;
import com.zh.jqtek.JQ_JPL_CMD;
import com.zh.jqtek.JQ_TYPE.ANGLE_ROTATE;

public class AtyPrint extends Activity{
    String mBtAddress = null; // 打印机蓝牙地址
    JQ_BT_CTL mBtManagement = null; // 蓝牙管理类对象
    MY_HANDER mHander = null;
    public final int BT_CONNECTED = 1;
    public final int CONNECT_FAILED = 0;
    public boolean printFlag = false;
    static String mBtName = null;
    final int REQUESTCODE_BLUETOOTH_ADDRESS = 100;
    final int REQUESTCODE_SCAN = 101;
    private AlertDialog.Builder mSimpleAlertDialogFial;
	
	@SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1002) {
                //				//是否打印成功 判断
                if (!(AtyPrint.this.isFinishing())) {
                    AlertDialog mSimpleAlertDialog = new AlertDialog.Builder(AtyPrint.this)
                            .setTitle("温馨提示").setMessage("是否打印单据成功？")
                            .setNegativeButton("否",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,int which) {
                                            AlertDialog mSimpleAlertDialog = new AlertDialog.Builder(
                                                    AtyPrint.this).setTitle("温馨提示")
                                                    .setMessage("是否连接打印机打印单据？")
                                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    // 链接外置打印机
                                                                    if (mBtAddress == null) {
                                                                        Intent myIntent = new Intent(AtyPrint.this, JQ_BT_ACTIVITY.class);
                                                                        startActivityForResult(myIntent, REQUESTCODE_BLUETOOTH_ADDRESS);
                                                                        return;
                                                                    } else {
                                                                        // 创建蓝牙管理类对象
                                                                        mBtManagement = new JQ_BT_CTL();
                                                                        mBtManagement.bt_connect(mBtAddress);
                                                                        SUB_THREAD SubThread = new SUB_THREAD();
                                                                        SubThread.start();
                                                                        mHander = new MY_HANDER(AtyPrint.this);
                                                                    }
                                                                }
                                                            })
                                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).create();
                                            mSimpleAlertDialog.show();
                                        }
                                    })
                            .setPositiveButton("是",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
//                                            setPrintTime();
//                                            bt_order_bluetooth_print.setText("再次打印");
//                                            printFlag = true;
                                        }
                                    }).create();
                    mSimpleAlertDialog.show();
                }
            } 
        };
    };

    // 状态判断
    private class SUB_THREAD extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            int i = 0;
            while (i < 10) {
                if (mBtManagement.bt_status_get() == JQ_BT_CTL.BT_STATUS_CONNECTED) {
                    msg.what = BT_CONNECTED;
                    mHander.sendMessage(msg);
                    return;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    i++;
                    mBtManagement.bt_connect(mBtAddress);
                }
            }
            msg.what = CONNECT_FAILED;
            mHander.sendMessage(msg);
            return;
        }
    }
    
    
    /**
     * 打印单据
     */
    public void printOrder() {
        printView(2);
    }
    
    
 // 消息处理
    @SuppressLint("HandlerLeak") class MY_HANDER extends Handler {
        private WeakReference<Activity> mmAcitivity;
        MY_HANDER(Activity activity) {
            mmAcitivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BT_CONNECTED) {// 连接成功
                System.out.println("连接成功！！！！");
                printOrder();
            } else {// 连接失败
                System.out.println("连接失败！！！！");
                //判断acitivity是否被销毁
                if (!(AtyPrint.this.isFinishing())) {
                    mSimpleAlertDialogFial = new AlertDialog.Builder(AtyPrint.this)
                            .setTitle("温馨提示")
                            .setMessage("连接打印机失败，是否重新连接？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            if (mBtAddress == null) {
                                                Intent myIntent = new Intent(AtyPrint.this, JQ_BT_ACTIVITY.class);
                                                startActivityForResult(myIntent, REQUESTCODE_BLUETOOTH_ADDRESS);
                                                return;
                                            }
                                            Intent myIntent = new Intent(AtyPrint.this, JQ_BT_ACTIVITY.class);
                                            startActivityForResult(myIntent, REQUESTCODE_BLUETOOTH_ADDRESS);
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                    mSimpleAlertDialogFial.create().show();
                }

            }
        }

    }
    //打印布局
    @SuppressLint("SimpleDateFormat")
    public void printView(int number) {
    	//第一张
         JQ_JPL_CMD JPLCmd = new JQ_JPL_CMD();
         JPLCmd.jpl_data_clear();
         JPLCmd.jpl_page_start_ex(10, 0, 576, 650, false);
         int add = 0;
         final int offset = 30;
         add += offset;
         JPLCmd.jpl_page_textout_ex(15, add, "快递兔", 16, true, false, false,
                 false, 1, 1, ANGLE_ROTATE.ROTATE_0);
         add += offset + 5;
         JPLCmd.jpl_page_textout_ex(15, add, "体验最好的寄件平台", 10, true, false, false,
                 false, 0, 0, ANGLE_ROTATE.ROTATE_0);
         add += add + 5;
         //待测 居中
         JPLCmd.jpl_page_textout_ex(15+200, add + offset, "收件凭证：", 16, true, false,
                 false, false, 1, 1, ANGLE_ROTATE.ROTATE_0);
        add += offset + 10;
        //绘制二维码
        Bitmap icon = createQRCodeBitmap("000002");
        char c[] = GetBmpData(icon);
        JPLCmd.jpl_page_bitmap(0, add, icon.getWidth(), icon.getHeight(), c);// 画二维码中心图标
        add += offset + 5;
        String message="本单据为快递兔收件的凭证，快递员录入单号后，会向您下单的微信或APP发送单号，您也可以" +
        		"在我的订单中查看单号信息"; 
        if (message.length() > 15) {// 超过一行
            JPLCmd.jpl_page_textout(15, add,message.substring(0, 15));
            if (message.length() > 36) {// 超过二行
                JPLCmd.jpl_page_textout(15, add + offset, message.substring(16, 36));
                JPLCmd.jpl_page_textout(15, add + 2 * offset, message.substring(36, message.length()));
                add = add + 3 * offset + 9;
            } else {
                JPLCmd.jpl_page_textout(15, add + offset, message.substring(15, message.length()));
                add = add + 2 * offset + 9;
            }
        } else {
            JPLCmd.jpl_page_textout(15, add, message);
            add = add + 1 * offset + 9;
        }
        add += offset + 10;
        String messageRedPacket="给好友发红包，发5元得五元，多发多得";
        JPLCmd.jpl_page_textout(15, add, messageRedPacket);
        JPLCmd.jpl_page_print();
        mBtManagement.bt_write(JPLCmd.jpl_data_get(), JPLCmd.jpl_length_get());
        //第二张
        add = 0;
        JQ_JPL_CMD JPLCmd2 = new JQ_JPL_CMD();
        JPLCmd2.jpl_data_clear();
        JPLCmd2.jpl_page_start_ex(10, 0, 576, 650, false);
        add += offset + 10;
        //快递单号
        JPLCmd2.jpl_page_textout_ex(15, add, "快递兔", 16, true, false, false,
                false, 1, 1, ANGLE_ROTATE.ROTATE_0);
        add += offset + 5;
        JPLCmd2.jpl_page_textout_ex(15, add, "体验最好的寄件平台", 10, true, false, false,
                false, 0, 0, ANGLE_ROTATE.ROTATE_0);
        add += add + 5;
        //绘制二维码
        icon = createQRCodeBitmap("000001");
        char c1[] = GetBmpData(icon);
        JPLCmd2.jpl_page_bitmap(0, add, icon.getWidth(), icon.getHeight(), c1);// 画二维码中心图标
        add += offset + 10;
        String orderNumber="编号：000001";
        JPLCmd2.jpl_page_textout(15+200, add, orderNumber);
        add += offset + 10;
        JPLCmd.jpl_page_textout_ex(15, add + offset, "客户签字：", 16, true, false,
                false, false, 1, 1, ANGLE_ROTATE.ROTATE_0);
        JPLCmd2.jpl_page_print();
        JPLCmd2.jpl_page_feed();
        mBtManagement.bt_write(JPLCmd2.jpl_data_get(), JPLCmd2.jpl_length_get());
        handler.sendEmptyMessage(1002);
    }

    private char[] GetBmpData(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpWidthBytes = (((bmpWidth - 1) / 8) + 1);
        int bmpHeight = bmp.getHeight();
        int[] bmpPixels = new int[bmpWidth * bmpHeight];
        char[] bmpData = new char[bmpWidthBytes * bmpHeight];
        bmp.getPixels(bmpPixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);
        int PixelsIndex = 0, DataIndex = 0;
        for (int y = 0; y < bmpHeight; y++) {
            for (int x = 0; x < bmpWidth; x++) {
                PixelsIndex = y * bmpWidth + x;
                DataIndex = y * bmpWidthBytes + (x >> 3);
                if (IsBlack(bmpPixels[PixelsIndex])) {
                    bmpData[DataIndex] |= (0x01 << (x & 0x07));
                }
            }
        }
        return bmpData;
    }

    private boolean IsBlack(int color) {
        int red = (color >> 16) & 0xff;
        int green = (color >> 8) & 0xff;
        int blue = (color & 0xff);
        int gray = (red * 30 + green * 59 + blue * 11) / 100;
        if (gray > 128) {
            return false;
        }
        return true;
    }
    private final int QRCODE_SIZE = 250;
    /**
     * 创建QR二维码图片
     */
    private Bitmap createQRCodeBitmap(String content) {
        // 用于设置QR二维码参数
        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
        // 设置QR二维码的纠错级别——这里选择最高H级别
        qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, qrParam);
            // 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            int[] data = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y))
                        data[y * w + x] = 0xff000000;// 黑色
                    else
                        data[y * w + x] = -1;// -1 相当于0xffffffff 白色
                }
            }
            // 创建一张bitmap图片，采用最高的效果显示
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            // 将上面的二维码颜色数组传入，生成图片颜色
            bitmap.setPixels(data, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!(resultCode == Activity.RESULT_OK)) {
            return;
        }
        switch (requestCode) {
            case REQUESTCODE_BLUETOOTH_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    mBtName = data.getStringExtra(JQ_BT_ACTIVITY.EXTRA_BLUETOOTH_DEVICE_NAME);
                    mBtAddress = data.getStringExtra(JQ_BT_ACTIVITY.EXTRA_BLUETOOTH_DEVICE_ADDRESS);
                    if (mBtAddress == null || "".equals(mBtAddress)
                            || "No Bonded Devices".equals(mBtAddress) || "No availabel devices!".equals(mBtAddress) || "vailabel devices!".equals(mBtAddress)) {
                        return;
                    }
                    System.out.println(mBtAddress + "===========mBtAddress=============="+mBtName);
                    // 创建蓝牙管理类对象
                    mBtManagement = new JQ_BT_CTL();
                    mBtManagement.bt_connect(mBtAddress);
                    SUB_THREAD SubThread = new SUB_THREAD();
                    SubThread.start();
                    mHander = new MY_HANDER(this);
                }
                break;
        }
    }
}
