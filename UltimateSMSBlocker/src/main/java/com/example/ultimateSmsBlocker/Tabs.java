/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.example.ultimateSmsBlocker;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Tabs extends TabActivity
{

    TabHost tabHost;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        tabHost = getTabHost ();

        tabHost.addTab ( tabHost.newTabSpec ( "tab1" )
                .setIndicator ( "Block List" )
                .setContent ( new Intent ( this, MainActivity.class ) ) );

        tabHost.addTab ( tabHost.newTabSpec ( "tab2" )
                .setIndicator ( "Blocked Messages" )
                .setContent ( new Intent ( this, BlockMessagesList.class ) ) );


        tabHost.addTab ( tabHost.newTabSpec ( "tab3" )
                .setIndicator ( "Settings" )
                .setContent ( new Intent ( this, Settings.class )
                        .addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP ) ) );
    }

}
