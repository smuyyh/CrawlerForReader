package com.qy.reader.home;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.diql.android.novel.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTest {

    @Rule
    public ActivityTestRule<SplashActivity> activityRule
            = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void text() {
        onView(withId(R.id.tv_splash_skip))
                .check(matches(withText("跳过")));
    }
}