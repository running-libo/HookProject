package com.example.hookproject;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookHelper {

    /**
     * 在不修改以上代码的情况下，通过Hook把 ((Button) view).getText()内给修改
     *
     * @param view view对象
     */
    public static void hookClickListener(View view) throws Exception {
        //之前的还是用户写的实现代码
        //为了获取ListenerInfo对象，需要执行这个方法 ListenerInfo getListenerInfo() ,才能拿到
        Class<?> viewClass = Class.forName("android.view.View");
        Method getListenerInfoMethod = viewClass.getDeclaredMethod("getListenerInfo");
        getListenerInfoMethod.setAccessible(true);

        Object listenerInfo = getListenerInfoMethod.invoke(view); //获取ListenerInfo对象
        //替换 public OnClickListener mOnClickListener; 替换为我们自己的
        Class<?> listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
        Field field = listenerInfoClass.getField("mOnClickListener");
        Object mOnClickListenerObj = field.get(listenerInfo); //获取该view下的mOnClickListener对象

        //1.监听onClick,当用户点击按钮的时候-->onClick,我们自己要先拦截这个事件,动态代理
        //第一个参数：类加载器
        //第二个参数：要监听的接口，监听什么接口，就返回什么接口
        //第三个参数：监听接口方法里面的回调
        Object onClickListenerProxy = Proxy.newProxyInstance(MainActivity.class.getClassLoader(), new Class[]{View.OnClickListener.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Log.i("minfo", "拦截到了OnClickListener方法了");

                //使用hook的方法，让任何带文字的按钮，文字都被指定性地修改
                if (view instanceof TextView) {
                    ((TextView)view).setText("被hook的textview");
                }

                //让系统片段，正常的执行下去
                return method.invoke(mOnClickListenerObj, view);
            }
        });

        //把系统的mOnClickListener,换成我们自己写的动态代理
        field.set(listenerInfo, onClickListenerProxy);
    }
}
