# BottomTopDialog
将dialog显示在底部和顶部

参考的[Android-AlertView](https://github.com/saiwu-bigkoo/Android-AlertView)

![screenshot](/screenshot.gif)



##### 依赖
``````
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
 dependencies {
	        compile 'com.github.jeffreyhappy:BottomTopDialog:v0.1'
	}
``````

##### 使用
简单使用只需要继承BottomTopDialogFragment然后实现BottomTopViewInterface接口的方法



``````
public class ShareDialog extends BottomTopDialogFragment {
    @Override
    public void bindContent(ViewGroup viewGroup) {
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismissWithAnim();
            }
        });
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.layout_alert_share;
    }

}

``````

``````
public interface BottomTopViewInterface {


    /**
     * getContentLayoutId()中布局和数据绑定
     *
     * @param viewGroup
     */
    void bindContent(ViewGroup viewGroup);

    /**
     * 内容所属的layoutId
     *
     * @return
     */
    int getContentLayoutId();


    /**
     * 内容是显示在上方还是下方
     * @param
     * @return
     */
    int getGravity();
}
``````

最后展示
``````
      ShareDialog shareDialog = new ShareDialog();
      //复写默认动画
      shareDialog.overrideAnim(R.anim.fade_in_center,R.anim.fade_out_center);
      shareDialog.show(getSupportFragmentManager(),"shareDialog");
``````


