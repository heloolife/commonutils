package com.wdl.utils.test;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * fragment 的管理类
 *
 * @author wdl
 */
public class FragmentStackManger {
    private static final int ANIM_TYPE_NORMAL = 200;
    private static final int ANIM_TYPE_CUSTOME = 201;
    private static final int ANIM_TYPE_NONE = 202;

    private List<Fragment> fragmentStack;

    private int layout_id;

    private FragmentManager fm;
    private FragmentTransaction transaction;
    private int anim_noraml;  //系统的样式
    private int anim_left_in, anim_left_out, anim_right_in, anim_right_out; //自定义样式
    private int anim_type = ANIM_TYPE_NONE; //动画样式类型


    public FragmentStackManger(int id, FragmentManager fm) {
        this.layout_id = id;
        this.fm = fm;
        fragmentStack = new ArrayList<Fragment>();
    }

    /**
     * 设置统一的自定义切换格式
     *
     * @param enter
     * @param exit
     * @param popEnter
     * @param popExit
     */
    public void setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
        anim_type = ANIM_TYPE_CUSTOME;
        anim_left_in = enter;
        anim_left_out = exit;
        anim_right_in = popEnter;
        anim_right_out = popExit;
    }

    /**
     * 设置统一的系统动画格式
     *
     * @param anim
     */
    public void setAnimations(int anim) {
        anim_type = ANIM_TYPE_NORMAL;
        anim_noraml = anim;
    }

    private void add(Fragment fragment) {
        transaction.add(layout_id, fragment);
    }

    private void show(Fragment fragment) {
        transaction.show(fragment);
    }

    private void hide(Fragment fragment) {
        transaction.hide(fragment);
    }

    private void remove(Fragment fragment) {
        transaction.remove(fragment);
    }

    private void beginTransation() {
        if (anim_type == ANIM_TYPE_NORMAL) {
            beginTransation(anim_noraml);
        } else if (anim_type == ANIM_TYPE_CUSTOME) {
            beginTransation(anim_left_in, anim_left_out, anim_right_in, anim_right_out);
        } else {
            transaction = fm.beginTransaction();
        }
    }

    private void beginTransation(int enter, int exit, int popenter, int popexit) {
        beginTransation(ANIM_TYPE_CUSTOME, -1, enter, exit, popenter, popexit);
    }

    private void beginTransation(int normal) {
        beginTransation(ANIM_TYPE_NORMAL, normal, -1, -1, -1, -1);
    }


    private void beginTransation(int type, int normal, int enter, int exit, int popenter, int popexit) {
        transaction = fm.beginTransaction();
        if (type == ANIM_TYPE_NORMAL) {
            transaction.setTransition(normal);
        } else if (type == ANIM_TYPE_CUSTOME) {
            transaction.setCustomAnimations(enter, exit, popenter, popexit);
        }
    }

    private void commit() {
        transaction.commit();
    }


    private void commitAllowingStateLoss() {
        transaction.commitAllowingStateLoss();
    }

    /**
     * 添加fragment到stack当中
     *
     * @param fragment
     * @param isReplace 如果已经存在，true则删除已经存在的，并在尾部重新添加；false则直接退出
     */
    public void addFragment(Fragment fragment, boolean isReplace) {
        beginTransation();
        if (fragmentStack.contains(fragment) && isReplace) {
            fragmentStack.remove(fragment);
            remove(fragment);
        }
        if (!(fragmentStack.contains(fragment) && !isReplace)) {
            add(fragment);
            fragmentStack.add(fragment);
        }
        commit();
    }


    /**
     * 添加fragment，默认不替换
     *
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        addFragment(fragment, false);
    }

    /**
     * 展示fragment  如果不在栈里边存在 则直接添加 默认隐藏前一个
     *
     * @param fragment
     */
    public void showFragment(Fragment fragment) {
        showFragmentByHide(fragment);
    }

    public void showFragmentAllowingStateLoss(Fragment fragment) {
        showFragmentByHide(fragment, true);
    }

    /**
     * 显示指定fragment 但是隐藏前一个fragment
     *
     * @param fragment
     */
    public void showFragmentByHide(Fragment fragment) {
        showFragmentByHide(fragment, false);
    }

    /**
     * 显示指定fragment 但是隐藏前一个fragment
     *
     * @param fragment
     */
    public void showFragmentByHide(Fragment fragment, boolean isAllowingStateLoss) {
        if (!fragmentStack.contains(fragment))
            addFragment(fragment);

        popUpStack(fragment);

        beginTransation();
        show(fragment);
        int size = fragmentStack.size();
        if (size > 1) {
            hide(fragmentStack.get(size - 2));
        }

        if (isAllowingStateLoss)
            commitAllowingStateLoss();
        else
            commit();
    }

    /**
     * 显示指定fragment 但是remove前一个fragment
     *
     * @param fragment
     */
    public void showFragmentByReplace(Fragment fragment) {
        if (!fragmentStack.contains(fragment))
            addFragment(fragment);

        popUpStack(fragment);

        beginTransation();
        show(fragment);
        int size = fragmentStack.size();
        if (size > 1) {
            remove(fragmentStack.get(size - 2));
            fragmentStack.remove(size - 2);
        }
        commit();
    }

    /**
     * 展示上一个fragment 并remove当前的
     */
    public void showPreFragmentByReplace() {
        int size = fragmentStack.size();
        if (size < 2)
            return;
        showFragmentByReplace(fragmentStack.get(size - 2));
    }

    /**
     * 清空 并去除
     */
    public void clear() {
        int size = fragmentStack.size();
        if (size < 1)
            return;
        beginTransation();
        for (int i = 0; i < size; i++)
            remove(fragmentStack.get(i));
        commit();

        fragmentStack.clear();
    }

    /**
     * 取出到栈顶
     *
     * @param fragment
     */
    public void popUpStack(Fragment fragment) {
        int indexOf = fragmentStack.indexOf(fragment);
        int size = fragmentStack.size();
        for (int i = indexOf; i < size - 1; i++) {
            Collections.swap(fragmentStack, i, i + 1);
        }
    }

    public int getStackSize() {
        return fragmentStack.size();
    }

    public Fragment getCurrentFragment() {
        return fragmentStack.get(getStackSize() - 1);
    }

    public boolean isCurrentFragment(Fragment fragment) {
        boolean result = false;
        if (fragmentStack.indexOf(fragment) == getStackSize() - 1)
            result = true;
        return result;
    }
}
