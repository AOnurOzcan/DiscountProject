package com.example.ooar.discountproject.util;

import android.support.v4.app.Fragment;

/**
 * Created by Onur Kuru on 6.3.2016.
 */
//fragment değişimlerinin yönetilmesi için import edilmesi gereken arayüz
public interface FragmentChangeListener {

    public void replaceFragment(Fragment fragment, String tagName);
}
