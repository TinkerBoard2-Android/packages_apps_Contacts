/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.contacts.common.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.android.contacts.common.model.account.AccountWithDataSet;

import junit.framework.Assert;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@MediumTest
public class ContactsPreferencesTest extends AndroidTestCase {

    private static final String ACCOUNT_KEY = "ACCOUNT_KEY";

    @Mock private Context mContext;
    @Mock private Resources mResources;
    @Mock private SharedPreferences mSharedPreferences;

    private ContactsPreferences mContactsPreferences;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        Mockito.when(mContext.getResources()).thenReturn(mResources);
        Mockito.when(mResources.getString(Mockito.anyInt()))
                .thenReturn(ACCOUNT_KEY); // contact_editor_default_account_key

        Mockito.when(mContext.getSharedPreferences(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(mSharedPreferences);
        Mockito.when(mSharedPreferences.contains(ContactsPreferences.SORT_ORDER_KEY))
                .thenReturn(true);
        Mockito.when(mSharedPreferences.contains(ContactsPreferences.DISPLAY_ORDER_KEY))
                .thenReturn(true);

        mContactsPreferences = new ContactsPreferences(mContext);
    }

    public void testGetSortOrderDefault() {
        Mockito.when(mResources.getBoolean(Mockito.anyInt())).thenReturn(
                false, // R.bool.config_sort_order_user_changeable
                true // R.bool.config_default_sort_order_primary
        );
        Assert.assertEquals(ContactsPreferences.SORT_ORDER_PRIMARY,
                mContactsPreferences.getSortOrder());
    }

    public void testGetSortOrder() {
        Mockito.when(mResources.getBoolean(Mockito.anyInt())).thenReturn(
                true // R.bool.config_sort_order_user_changeable
        );
        Mockito.when(mSharedPreferences.getInt(Mockito.eq(ContactsPreferences.SORT_ORDER_KEY),
                Mockito.anyInt())).thenReturn(ContactsPreferences.SORT_ORDER_PRIMARY);
        Assert.assertEquals(ContactsPreferences.SORT_ORDER_PRIMARY,
                mContactsPreferences.getSortOrder());
    }

    public void testGetDisplayOrderDefault() {
        Mockito.when(mResources.getBoolean(Mockito.anyInt())).thenReturn(
                false, // R.bool.config_display_order_user_changeable
                true // R.bool.config_default_display_order_primary
        );
        Assert.assertEquals(ContactsPreferences.DISPLAY_ORDER_PRIMARY,
                mContactsPreferences.getDisplayOrder());
    }

    public void testGetDisplayOrder() {
        Mockito.when(mResources.getBoolean(Mockito.anyInt())).thenReturn(
                true // R.bool.config_display_order_user_changeable
        );
        Mockito.when(mSharedPreferences.getInt(Mockito.eq(ContactsPreferences.DISPLAY_ORDER_KEY),
                Mockito.anyInt())).thenReturn(ContactsPreferences.DISPLAY_ORDER_PRIMARY);
        Assert.assertEquals(ContactsPreferences.DISPLAY_ORDER_PRIMARY,
                mContactsPreferences.getDisplayOrder());
    }

    public void testRefreshSortOrder() throws InterruptedException {
        Mockito.when(mResources.getBoolean(Mockito.anyInt())).thenReturn(
                true // R.bool.config_sort_order_user_changeable
        );
        Mockito.when(mSharedPreferences.getInt(Mockito.eq(ContactsPreferences.SORT_ORDER_KEY),
                Mockito.anyInt())).thenReturn(ContactsPreferences.SORT_ORDER_PRIMARY,
                ContactsPreferences.SORT_ORDER_ALTERNATIVE);

        Assert.assertEquals(ContactsPreferences.SORT_ORDER_PRIMARY,
                mContactsPreferences.getSortOrder());
        mContactsPreferences.refreshValue(ContactsPreferences.SORT_ORDER_KEY);

        Assert.assertEquals(ContactsPreferences.SORT_ORDER_ALTERNATIVE,
                mContactsPreferences.getSortOrder());
    }

    public void testRefreshDisplayOrder() throws InterruptedException {
        Mockito.when(mResources.getBoolean(Mockito.anyInt())).thenReturn(
                true // R.bool.config_display_order_user_changeable
        );
        Mockito.when(mSharedPreferences.getInt(Mockito.eq(ContactsPreferences.DISPLAY_ORDER_KEY),
                Mockito.anyInt())).thenReturn(ContactsPreferences.DISPLAY_ORDER_PRIMARY,
                ContactsPreferences.DISPLAY_ORDER_ALTERNATIVE);

        Assert.assertEquals(ContactsPreferences.DISPLAY_ORDER_PRIMARY,
                mContactsPreferences.getDisplayOrder());
        mContactsPreferences.refreshValue(ContactsPreferences.DISPLAY_ORDER_KEY);

        Assert.assertEquals(ContactsPreferences.DISPLAY_ORDER_ALTERNATIVE,
                mContactsPreferences.getDisplayOrder());
    }

    public void testRefreshDefaultAccount() throws InterruptedException {
        Mockito.when(mResources.getBoolean(Mockito.anyInt())).thenReturn(
                true // R.bool.config_default_account_user_changeable
        );

        Mockito.when(mSharedPreferences.getString(Mockito.eq(ACCOUNT_KEY), Mockito.anyString()))
                .thenReturn(new AccountWithDataSet("name1", "type1", "dataset1").stringify(),
                        new AccountWithDataSet("name2", "type2", "dataset2").stringify());

        Assert.assertEquals("name1", mContactsPreferences.getDefaultAccount());
        mContactsPreferences.refreshValue(ACCOUNT_KEY);

        Assert.assertEquals("name2", mContactsPreferences.getDefaultAccount());
    }
}
