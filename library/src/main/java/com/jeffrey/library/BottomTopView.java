package com.jeffrey.library;

/**
 * Created by Li on 2017/1/3.
 */

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * 只支持在底部弹出,还有个坑，返回键要自己处理
 * Created by Sai on 15/8/9.
 * 精仿iOSAlertViewController控件
 * 点击取消按钮返回 －1，其他按钮从0开始算
 *
 * update 现在支持顶部，底部弹出 2017/4/10
 */
public class BottomTopView {

    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
    );

    protected WeakReference<Context> contextWeak;
    private FrameLayout contentContainer;
    private ViewGroup decorView;//fragment的根View
    private ViewGroup rootView;//AlertView 的 根View

    private DialogFragment dialogFragment;//所依附的dialogfragment

    private boolean isShowing;

    private Animation outAnim;
    private Animation inAnim;
    private int gravity = Gravity.CENTER;
    private BottomTopViewInterface contentInterface;


    public BottomTopView(DialogFragment dialogFragment, Context context, ViewGroup container, BottomTopViewInterface contentInterface) {
        this.contextWeak = new WeakReference<>(context);
        this.decorView = container;
        this.dialogFragment = dialogFragment;
        this.contentInterface = contentInterface;
        this.gravity = contentInterface.getGravity();

        initViews();
        init();
        initEvents();

    }



    protected void initViews() {
        Context context = contextWeak.get();
        if (context == null) return;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.layout_popup_view, decorView, false);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        contentContainer = (FrameLayout) rootView.findViewById(R.id.content_container);

        initActionSheetViews(layoutInflater);


    }


    protected void initActionSheetViews(LayoutInflater layoutInflater) {
        if (contentInterface == null){
            return;
        }
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(contentInterface.getContentLayoutId(), contentContainer);
        params.gravity = contentInterface.getGravity();
        viewGroup.setLayoutParams(params);
        contentInterface.bindContent(viewGroup);

    }

    protected void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    protected void initEvents() {
    }


    public View getView() {
        return rootView;
    }

    /**
     * show的时候调用
     *
     * @param view 这个View
     */
    private void onAttached(View view) {
        isShowing = true;
        contentContainer.startAnimation(inAnim);
    }

    /**
     * 添加这个View到Activity的根视图
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }



    /**
     * 添加这个View到Activity的根视图
     */
    public void showBelow(View view) {
        if (isShowing()) {
            return;
        }

        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);


        /**
         * decorVieLocation的y就是状态栏的高度，getLocationOnScreen是算状态栏的，但是margin是不算状态栏的，所以要加上
         */
        showBelow(viewLocation[1]+view.getHeight()-getStatusBarHeight(view.getContext()));


    }

    public void showBelow(int offsetY) {
        if (isShowing()) {
            return;
        }
        rootView.setPadding(0,offsetY,0,0);

        onAttached(rootView);
    }

    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        return rootView.getParent() != null && isShowing;
    }

    public void dismiss() {
        //消失动画
        outAnim.setAnimationListener(outAnimListener);
        contentContainer.startAnimation(outAnim);
    }

    public void dismiss(Animation.AnimationListener outAnimListener) {
        //消失动画
        outAnim.setAnimationListener(outAnimListener);
        contentContainer.startAnimation(outAnim);
    }

    public void dismissImmediately() {
        dialogFragment.dismissAllowingStateLoss();
    }

    public Animation getInAnimation() {
        Context context = contextWeak.get();
        if (context == null) return null;

        int res = getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    public Animation getOutAnimation() {
        Context context = contextWeak.get();
        if (context == null) return null;

        int res = getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }


    public void overrideAnim(Animation in,Animation out){
        Context context = contextWeak.get();
        if (context == null) {
            return ;
        }

        inAnim = in;
        outAnim = out;
    }

    private  int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private Animation.AnimationListener outAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dialogFragment.dismiss();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    public BottomTopView setCancelable(boolean isCancelable) {
        View view = rootView.findViewById(R.id.outmost_container);

        if (isCancelable) {
            view.setOnTouchListener(onCancelableTouchListener);
        } else {
            view.setOnTouchListener(null);
        }
        return this;
    }


    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };


    static int getAnimationResource(int gravity, boolean isInAnimation) {
        switch (gravity) {
            case Gravity.BOTTOM:
                return isInAnimation ? R.anim.slide_in_bottom : R.anim.slide_out_bottom;
            case Gravity.TOP:
                return isInAnimation ? R.anim.slide_in_top : R.anim.slide_out_top;
            case Gravity.CENTER:
                return isInAnimation ? R.anim.fade_in_center : R.anim.fade_out_center;
        }
        return -1;
    }
}

